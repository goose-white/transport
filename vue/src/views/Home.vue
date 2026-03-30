<template>
  <div style="padding: 20px;">
    <!-- 顶部数据卡片 -->
    <el-row :gutter="10" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card style="color: #fff; height: 100px;" class="card-container1">
          <div><<i class="el-icon-s-data" /> 执勤无人机</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold;">{{ droneCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #fff; height: 100px;" class="card-container2">
          <div><<i class="el-icon-success" /> 待办任务</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold;">{{ todoTaskCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #fff; height: 100px;" class="card-container4">
          <div><<i class="el-icon-remove" /> 日志记录</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold;">{{ logCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #fff; height: 100px;" class="card-container3">
          <div><<i class="el-icon-warning" /> 异常运行无人机</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold;">{{ errorDroneCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表：左右布局（1:1分栏） -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="12">
        <div id="bar" style="width: 100%; height: 300px;"></div>
      </el-col>
      <el-col :span="12">
        <div id="line" style="width: 100%; height: 300px;"></div>
      </el-col>
    </el-row>

    <!-- 数据下钻弹窗（周维度） -->
    <el-dialog
        title="周任务详情"
        :visible.sync="detailDialogVisible"
        width="50%"
        @open="initDetailChart"
        @close="disposeDetailChart"
    >
      <div id="detailChart" style="width: 100%; height: 300px;"></div>
    </el-dialog>

    <!-- 无人机系统配置模块 -->
    <el-row>
      <el-col :span="24">
        <el-card style="border-radius: 8px;">
          <div style="font-size: 16px; font-weight: bold; margin-bottom: 15px;">无人机系统配置</div>
          <el-form
              ref="configForm"
              :model="config"
              label-width="120px"
              size="small"
              :rules="configRules"
          >
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="config.systemName" style="width: 300px;" />
            </el-form-item>
            <el-form-item label="巡检频率" prop="inspectFreq">
              <el-input v-model.number="config.inspectFreq" suffix="分钟" style="width: 300px;" />
            </el-form-item>
            <el-form-item label="日志保留天数" prop="logRetention">
              <el-input v-model.number="config.logRetention" suffix="天" style="width: 300px;" />
            </el-form-item>
            <el-form-item label="最大飞行高度" prop="maxHeight">
              <el-input v-model.number="config.maxHeight" suffix="米" style="width: 300px;" />
            </el-form-item>
            <el-form-item label="单次续航时长" prop="maxBattery">
              <el-input v-model.number="config.maxBattery" suffix="分钟" style="width: 300px;" />
            </el-form-item>
            <el-form-item label="异常预警阈值" prop="alertThreshold">
              <el-input v-model.number="config.alertThreshold" suffix="次/小时" style="width: 300px;" />
            </el-form-item>
          </el-form>
          <div style="margin-top: 20px;">
            <el-button type="primary" icon="el-icon-check" @click="saveConfig">保存配置</el-button>
            <el-button type="default" icon="el-icon-refresh" style="margin-left: 10px;" @click="resetConfig">重置</el-button>
            <el-button type="default" icon="el-icon-upload" style="margin-left: 10px;" @click="importConfig">导入配置</el-button>
            <el-button type="default" icon="el-icon-download" style="margin-left: 10px;" @click="exportConfig">导出配置</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from '@/utils/request'

export default {
  name: "Home",
  data() {
    return {
      // 顶部卡片数据（从接口获取，默认值兜底）
      droneCount: 5,
      todoTaskCount: 57,
      logCount: 1000,
      errorDroneCount: 2,

      // 系统配置
      config: {
        systemName: "无人机管理系统V1.0",
        inspectFreq: 30,
        logRetention: 90,
        maxHeight: 120,
        maxBattery: 45,
        alertThreshold: 5
      },
      // 配置表单校验规则
      configRules: {
        systemName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
        inspectFreq: [
          { required: true, message: '请输入巡检频率', trigger: 'blur' },
          { type: 'number', min: 1, max: 120, message: '巡检频率需在1-120分钟之间', trigger: 'blur' }
        ],
        logRetention: [
          { required: true, message: '请输入日志保留天数', trigger: 'blur' },
          { type: 'number', min: 7, max: 365, message: '日志保留天数需在7-365天之间', trigger: 'blur' }
        ],
        maxHeight: [
          { required: true, message: '请输入最大飞行高度', trigger: 'blur' },
          { type: 'number', min: 10, max: 500, message: '最大飞行高度需在10-500米之间', trigger: 'blur' }
        ],
        maxBattery: [
          { required: true, message: '请输入单次续航时长', trigger: 'blur' },
          { type: 'number', min: 10, max: 120, message: '单次续航时长需在10-120分钟之间', trigger: 'blur' }
        ],
        alertThreshold: [
          { required: true, message: '请输入异常预警阈值', trigger: 'blur' },
          { type: 'number', min: 1, max: 20, message: '异常预警阈值需在1-20次/小时之间', trigger: 'blur' }
        ]
      },

      // 弹窗相关
      detailDialogVisible: false,
      currentWeek: "",
      weekDetailData: {
        "12月第1周": [8, 10, 12],
        "12月第2周": [10, 12, 15],
        "12月第3周": [9, 11, 13],
        "12月第4周": [12, 14, 16]
      },

      // ECharts实例存储（避免重复创建）
      barChart: null,
      lineChart: null,
      detailChart: null
    }
  },
  methods: {
    // 获取顶部卡片数据
    async getDashboardData() {
      try {
        // 替换为实际接口地址
        const res = await request.get('/dashboard/statistics');
        if (res.data) {
          this.droneCount = res.data.droneCount || this.droneCount;
          this.todoTaskCount = res.data.todoTaskCount || this.todoTaskCount;
          this.logCount = res.data.logCount || this.logCount;
          this.errorDroneCount = res.data.errorDroneCount || this.errorDroneCount;
        }
      } catch (error) {
        console.error('获取仪表盘数据失败：', error);
        this.$message.error('获取数据失败，使用默认值展示');
      }
    },

    // 初始化图表
    async initCharts() {
      // 确保DOM渲染完成
      await this.$nextTick();

      // 12月各周数据（可替换为接口获取）
      const taskData = [30, 37, 33, 42];
      const errorData = [3, 2, 4, 1];
      const weeks = ['12月第1周', '12月第2周', '12月第3周', '12月第4周'];

      // 1. 左侧柱状图（12月各周执行任务数）
      const barDom = document.getElementById('bar');
      if (!barDom) return;
      this.barChart = echarts.init(barDom);
      const barOption = {
        title: { text: '12月各周无人机执行任务数', textStyle: { fontSize: 16 } },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            const item = params[0];
            return `
              <div style="padding: 5px;">
                <div>${item.name}</div>
                <div style="color: ${item.color};">执行任务数：${item.value} 次</div>
                <div>环比增长：${Math.round(Math.random()*10)}%</div>
              </div>
            `;
          }
        },
        legend: { data: ['执行任务数'], right: 20 },
        xAxis: {
          type: 'category',
          data: weeks,
          axisLine: { lineStyle: { color: '#999' } },
          axisLabel: { rotate: 0 }
        },
        yAxis: {
          type: 'value',
          name: '任务数（次）',
          axisLine: { lineStyle: { color: '#999' } },
          splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
        },
        series: [{
          name: '执行任务数',
          data: taskData,
          type: 'bar',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#4299E1' },
              { offset: 1, color: '#2B6CB0' }
            ])
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#66B1FF' },
                { offset: 1, color: '#337ECC' }
              ]),
              shadowBlur: 10,
              shadowColor: 'rgba(0,0,0,0.3)'
            }
          }
        }]
      };
      this.barChart.setOption(barOption);

      // 点击柱状图下钻
      this.barChart.on('click', (params) => {
        this.currentWeek = params.name;
        this.detailDialogVisible = true;
      });

      // 2. 右侧折线图（12月各周异常次数）
      const lineDom = document.getElementById('line');
      if (!lineDom) return;
      this.lineChart = echarts.init(lineDom);
      const lineOption = {
        title: { text: '12月各周无人机异常次数', textStyle: { fontSize: 16 } },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        legend: { data: ['异常次数', '预警阈值'], right: 20 },
        xAxis: {
          type: 'category',
          data: weeks,
          axisLine: { lineStyle: { color: '#999' } }
        },
        yAxis: {
          type: 'value',
          name: '次数（次）',
          axisLine: { lineStyle: { color: '#999' } },
          splitLine: { lineStyle: { type: 'dashed', color: '#eee' } },
          markLine: {
            data: [{
              name: '预警阈值',
              yAxis: this.config.alertThreshold,
              lineStyle: { type: 'dashed', color: '#F56C6C' }
            }]
          }
        },
        series: [
          {
            name: '异常次数',
            data: errorData,
            type: 'line',
            smooth: true,
            lineStyle: { width: 3, color: '#4299E1' },
            itemStyle: {
              color: '#2B6CB0',
              borderWidth: 2,
              borderColor: '#fff'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(66, 153, 225, 0.5)' },
                { offset: 1, color: 'rgba(66, 153, 225, 0.1)' }
              ])
            },
            emphasis: {
              lineStyle: { width: 4 },
              itemStyle: {
                color: '#66B1FF',
                shadowBlur: 10
              }
            }
          },
          {
            name: '预警阈值',
            type: 'line',
            data: [this.config.alertThreshold, this.config.alertThreshold, this.config.alertThreshold, this.config.alertThreshold],
            lineStyle: { type: 'dashed', color: '#F56C6C' },
            symbol: 'none'
          }
        ]
      };
      this.lineChart.setOption(lineOption);

      // 窗口自适应
      window.addEventListener('resize', this.resizeCharts);
    },

    // 周维度详情图表
    initDetailChart() {
      this.$nextTick(() => {
        const detailDom = document.getElementById('detailChart');
        if (!detailDom || !this.currentWeek) return;

        // 先销毁旧实例
        if (this.detailChart) {
          this.detailChart.dispose();
        }

        this.detailChart = echarts.init(detailDom);
        const detailOption = {
          title: { text: `${this.currentWeek} 任务类型分布`, textStyle: { fontSize: 16 } },
          tooltip: { trigger: 'item', formatter: '{b}: {c} 次 ({d}%)' },
          legend: { orient: 'vertical', left: 10, data: ['巡逻任务', '巡检任务', '应急任务'] },
          series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            data: [
              { name: '巡逻任务', value: this.weekDetailData[this.currentWeek][0] },
              { name: '巡检任务', value: this.weekDetailData[this.currentWeek][1] },
              { name: '应急任务', value: this.weekDetailData[this.currentWeek][2] }
            ],
            itemStyle: {
              color: (params) => {
                const colors = ['#4299E1', '#67C21A', '#F56C6C'];
                return colors[params.dataIndex];
              },
              emphasis: {
                shadowBlur: 10,
                shadowColor: 'rgba(0,0,0,0.3)'
              }
            },
            label: { show: false },
            labelLine: { show: false }
          }]
        };
        this.detailChart.setOption(detailOption);
      });
    },

    // 销毁详情图表实例
    disposeDetailChart() {
      if (this.detailChart) {
        this.detailChart.dispose();
        this.detailChart = null;
      }
    },

    // 图表自适应窗口大小
    resizeCharts() {
      if (this.barChart) this.barChart.resize();
      if (this.lineChart) this.lineChart.resize();
      if (this.detailChart) this.detailChart.resize();
    },

    // 保存配置
    async saveConfig() {
      try {
        // 表单校验
        await this.$refs.configForm.validate();

        // 调用保存接口
        const res = await request.post('/system/config/save', this.config);
        if (res.data) {
          this.$message.success('配置保存成功！');
          // 重新初始化折线图（预警阈值可能变化）
          this.initCharts();
        }
      } catch (error) {
        console.error('保存配置失败：', error);
        if (error.message) {
          this.$message.error(error.message);
        } else {
          this.$message.error('配置保存失败，请重试！');
        }
      }
    },

    // 重置配置
    resetConfig() {
      this.$refs.configForm.resetFields();
      this.$message.info('配置已重置为初始值');
    },

    // 导入配置
    importConfig() {
      // 创建文件选择器
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.json';
      input.onchange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = async (event) => {
          try {
            const configData = JSON.parse(event.target.result);
            // 合并配置（避免缺失字段）
            this.config = { ...this.config, ...configData };
            this.$message.success('配置导入成功！');
          } catch (error) {
            this.$message.error('配置文件格式错误，请检查！');
          }
        };
        reader.readAsText(file);
      };
      input.click();
    },

    // 导出配置
    exportConfig() {
      // 将配置转为JSON字符串
      const configStr = JSON.stringify(this.config, null, 2);
      const blob = new Blob([configStr], { type: 'application/json' });

      // 创建下载链接
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `无人机系统配置_${new Date().getTime()}.json`;
      a.click();

      // 释放URL
      URL.revokeObjectURL(a.href);
      this.$message.success('配置导出成功！');
    }
  },
  mounted() {
    // 获取仪表盘数据
    this.getDashboardData();
    // 初始化图表
    this.initCharts();
  },
  beforeDestroy() {
    // 销毁ECharts实例，避免内存泄漏
    if (this.barChart) {
      this.barChart.dispose();
      this.barChart = null;
    }
    if (this.lineChart) {
      this.lineChart.dispose();
      this.lineChart = null;
    }
    if (this.detailChart) {
      this.detailChart.dispose();
      this.detailChart = null;
    }
    // 移除窗口resize监听
    window.removeEventListener('resize', this.resizeCharts);
  }
}
</script>

<style scoped>
/* 卡片样式 */
.card-container1 {
  background-image: linear-gradient(to bottom right,#401EFF,#2c44bd);
  border-radius: 8px;
  overflow: hidden;
}
.card-container2 {
  background-image: linear-gradient(to bottom right,#67C21A,#204e09);
  border-radius: 8px;
  overflow: hidden;
}
.card-container4 {
  background-image: linear-gradient(to bottom right,#901669,#424997);
  border-radius: 8px;
  overflow: hidden;
}
.card-container3 {
  background-image: linear-gradient(to bottom right,#F16C6C,#751111);
  border-radius: 8px;
  overflow: hidden;
}

/* 图表容器适配 */
#bar, #line, #detailChart {
  width: 100% !important;
  height: 300px !important;
}

/* 卡片内文字居中优化 */
.el-card__body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  padding: 15px !important;
}
</style>