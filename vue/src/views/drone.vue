<template>
  <div>
    <!-- 固定查询区域 -->
    <div style="padding: 12px; background: #f8f9fa; border-radius: 4px; margin-bottom: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
      <el-form inline>
        <el-form-item label="唯一标识：">
          <el-input v-model="queryForm.id" placeholder="输入唯一标识" style="width: 180px;" />
        </el-form-item>
        <el-form-item label="设备类型：">
          <el-input v-model="queryForm.model" placeholder="输入设备类型" style="width: 180px;" />
        </el-form-item>
        <!-- 设备状态下拉：添加change事件转数值 -->
        <el-form-item label="设备状态：">
          <el-select
              v-model="queryForm.status"
              placeholder="选择设备状态"
              style="width: 180px;"
              @change="val => queryForm.status = val === '' ? '' : Number(val)">
            <el-option label="待命" value="0" />
            <el-option label="飞行" value="1" />
            <el-option label="维修" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="电池电量：">
          <el-input v-model="queryForm.battery" placeholder="输入电池电量" style="width: 180px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadByColumn">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域：拆分编辑、删除为两列 -->
    <el-table
        :data="drone"
        style="width: 100%"
        :row-class-name="tableRowClassName"
        empty-text="当前条件下无匹配数据，请调整查询条件"
        v-loading="loading"> <!-- 添加加载状态 -->
      <!-- 原有列 -->
      <el-table-column
          prop="id"
          label="唯一标识（主键）"
          width="180">
        <template #header>
          <span>唯一标识（主键） <i class="el-icon-search" style="color: #4299E1; cursor: default;"></i></span>
        </template>
      </el-table-column>
      <el-table-column
          prop="model"
          label="设备类型"
          width="180">
        <template #header>
          <span>设备类型 <i class="el-icon-search" style="color: #4299E1; cursor: default;"></i></span>
        </template>
      </el-table-column>
      <el-table-column
          prop="status"
          label="设备状态 (0 - 待命，1 - 飞行，2 - 维修)"
          width="180">
        <template #header>
          <span>设备状态 (0 - 待命，1 - 飞行，2 - 维修) <i class="el-icon-search" style="color: #4299E1; cursor: default;"></i></span>
        </template>
        <!-- 状态文字化显示，提升体验 -->
        <template #default="scope">
          <span v-if="scope.row.status === 0">待命</span>
          <span v-else-if="scope.row.status === 1">飞行</span>
          <span v-else-if="scope.row.status === 2">维修</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
          prop="battery"
          label="电池电量"
          width="180">
        <template #header>
          <span>电池电量 <i class="el-icon-search" style="color: #4299E1; cursor: default;"></i></span>
        </template>
      </el-table-column>
      <el-table-column
          prop="maxSpeed"
          label="最快飞行速度"
          width="180">
      </el-table-column>
      <el-table-column
          prop="maxFlightTime"
          label="最大续航时间（分钟）"
          width="180">
      </el-table-column>

      <!-- 拆分：编辑列 -->
      <el-table-column label="编辑" width="80">
        <template v-slot="scope">
          <el-button type="success" size="mini" @click="handleEdit(scope.row)">
            编辑 <i class="el-icon-edit"></i>
          </el-button>
        </template>
      </el-table-column>

      <!-- 拆分：删除列 -->
      <el-table-column label="删除" width="80">
        <template v-slot="scope">
          <el-button type="danger" size="mini" @click="handleDelete(scope.row.id)">
            删除 <i class="el-icon-delete"></i>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog title="设备信息" v-model="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small" ref="editFormRef">
        <el-form-item label="设备状态" prop="status">
          <!-- 编辑时用下拉选择，确保数值正确 -->
          <el-select
              v-model="form.status"
              placeholder="选择设备状态"
              @change="val => form.status = Number(val)">
            <el-option label="待命" value="0" />
            <el-option label="飞行" value="1" />
            <el-option label="维修" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="电池电量" prop="battery">
          <el-input v-model.number="form.battery" autocomplete="off" placeholder="请输入电池电量"></el-input>
        </el-form-item>
        <el-form-item label="设备类型" prop="model">
          <el-input v-model="form.model" autocomplete="off" placeholder="请输入设备类型"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 引入请求工具（适配项目中常用的axios封装）
import request from '@/utils/request'

export default {
  name: "Drone", // 组件名规范：首字母大写
  created() {
    this.load()
  },
  data() {
    return {
      drone: [], // 确保初始化为空数组
      form: {},
      dialogFormVisible: false,
      loading: false, // 添加加载状态
      queryForm: {
        id: "",
        model: "",
        status: "", // 初始为空字符串，避免类型混乱
        battery: ""
      }
    }
  },
  methods: {
    // 保存编辑数据（增加异常处理和数据验证）
    save() {
      // 基础验证
      if (!this.form.model) {
        return this.$message.warning("请输入设备类型")
      }
      if (this.form.status === undefined || this.form.status === null) {
        return this.$message.warning("请选择设备状态")
      }
      if (this.form.battery === undefined || this.form.battery < 0) {
        return this.$message.warning("请输入有效的电池电量")
      }

      this.loading = true
      request.post("/drone/save", this.form)
          .then(res => {
            // 适配后端常见的返回格式
            const success = res.data?.code === 200 || res.data
            if (success) {
              this.$message.success("保存成功!")
              this.dialogFormVisible = false
              this.load() // 保存后刷新列表
            } else {
              this.$message.error("保存失败，请重试")
            }
          })
          .catch(err => {
            console.error("保存失败：", err)
            this.$message.error("保存失败，服务器异常")
          })
          .finally(() => {
            this.loading = false
          })
    },
    // 加载全部数据（核心修复：确保数据是数组）
    load() {
      this.loading = true
      request.get("/drone/getAllData")
          .then(res => {
            // 兼容不同的后端返回格式，确保drone始终是数组
            if (Array.isArray(res.data)) {
              this.drone = res.data
            } else if (res.data?.data && Array.isArray(res.data.data)) {
              this.drone = res.data.data
            } else {
              this.drone = []
            }
          })
          .catch(err => {
            console.error("加载数据失败：", err)
            this.$message.error("数据加载失败，请重试")
            this.drone = [] // 异常时置为空数组，避免Table报错
          })
          .finally(() => {
            this.loading = false
          })
    },
    // 条件查询（优化过滤逻辑，增加类型兼容）
    loadByColumn() {
      this.loading = true
      request.get("/drone/getAllData")
          .then(res => {
            let list = []
            // 先确保原始数据是数组
            if (Array.isArray(res.data)) {
              list = res.data
            } else if (res.data?.data && Array.isArray(res.data.data)) {
              list = res.data.data
            }

            // 唯一标识：转为数值后匹配（空值不过滤）
            if (this.queryForm.id) {
              const id = Number(this.queryForm.id)
              list = list.filter(item => item.id === id)
            }
            // 设备类型：模糊匹配（忽略大小写）
            if (this.queryForm.model) {
              const model = this.queryForm.model.trim().toLowerCase()
              list = list.filter(item => item.model?.toLowerCase().includes(model))
            }
            // 设备状态：数值匹配（空值不过滤）
            if (this.queryForm.status !== "") {
              list = list.filter(item => item.status === this.queryForm.status)
            }
            // 电池电量：转为数值后匹配（空值不过滤）
            if (this.queryForm.battery) {
              const battery = Number(this.queryForm.battery)
              list = list.filter(item => item.battery === battery)
            }

            this.drone = list
            // 无数据提示
            if (list.length === 0) {
              this.$message.info("未查询到匹配的设备数据")
            }
          })
          .catch(err => {
            console.error("查询失败：", err)
            this.$message.error("查询失败，请重试")
            this.drone = []
          })
          .finally(() => {
            this.loading = false
          })
    },
    // 重置查询条件
    resetQuery() {
      this.queryForm = { id: "", model: "", status: "", battery: "" }
      this.load() // 重置后加载全部数据
    },
    // 编辑设备（优化数据赋值，确保类型正确）
    handleEdit(row) {
      this.dialogFormVisible = true
      // 深拷贝，避免修改原数据
      this.form = { ...row }
      // 确保status是数值类型（兼容后端返回的字符串）
      this.form.status = row.status ? Number(row.status) : 0
      // 确保battery是数值类型
      this.form.battery = row.battery ? Number(row.battery) : 0
    },
    // 删除设备（增加加载状态和异常处理）
    handleDelete(id) {
      this.$confirm('此操作将永久删除该设备，是否继续？', '提示', {
        type: 'warning'
      }).then(() => {
        this.loading = true
        request.post("/drone/delete", { id: Number(id) })
            .then(res => {
              const success = res.data?.code === 200 || res.data
              if (success) {
                this.$message.success('删除成功!')
                this.load() // 删除后刷新列表
              } else {
                this.$message.error('删除失败，请重试')
              }
            })
            .catch(() => {
              this.$message.error('删除失败，请重试')
            })
            .finally(() => {
              this.loading = false
            })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    // 表格行样式
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex === 1) return 'warning-row';
      if (rowIndex === 3) return 'success-row';
      return '';
    }
  }
}
</script>

<style scoped>
.el-form-item {
  margin-bottom: 0 !important;
  margin-right: 15px;
}

/* 表格行样式（可选） */
:deep(.warning-row) {
  background: #fff7e6;
}
:deep(.success-row) {
  background: #e6f7ee;
}
</style>