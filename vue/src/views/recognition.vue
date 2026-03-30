<template>
  <div style="padding: 20px;">
    <!-- 新增：查询区域（匹配前四列） -->
    <div style="padding: 12px; background: #f8f9fa; border-radius: 4px; margin-bottom: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
      <el-form inline>
        <el-form-item label="识别记录标识：">
          <el-input v-model="queryForm.id" placeholder="输入识别记录标识" style="width: 180px;" />
        </el-form-item>
        <el-form-item label="识别时间：">
          <el-input v-model="queryForm.time" placeholder="输入识别时间（模糊匹配）" style="width: 180px;" />
        </el-form-item>
        <el-form-item label="采集设备：">
          <el-input v-model="queryForm.deviceId" placeholder="输入采集设备ID" style="width: 180px;" />
        </el-form-item>
        <el-form-item label="所属任务：">
          <el-input v-model="queryForm.taskId" placeholder="输入所属任务ID" style="width: 180px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadByColumn">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog
        title="设备信息"
        v-model="dialogFormVisible"
        width="500px"
        @close="resetForm"
    >
      <el-form
          ref="formRef"
          :model="form"
          label-width="100px"
          size="small"
          :rules="formRules"
      >
        <el-form-item label="识别时间" prop="time">
          <el-input v-model="form.time" autocomplete="off" placeholder="请输入识别时间"></el-input>
        </el-form-item>
        <el-form-item label="识别结果详情" prop="content">
          <el-input v-model="form.content" autocomplete="off" placeholder="请输入识别结果详情"></el-input>
        </el-form-item>
        <el-form-item label="存储路径" prop="url">
          <el-input v-model="form.url" autocomplete="off" placeholder="请输入存储路径"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 数据表格 -->
    <el-table
        :data="recognition"
        stripe
        border
        style="width: 100%;"
        v-loading="loading"
        empty-text="当前条件下无匹配数据，请调整查询条件"
    >
      <el-table-column
          prop="id"
          label="识别记录标识"
          width="120"
      />
      <el-table-column
          prop="time"
          label="识别时间"
          width="180"
      />
      <el-table-column
          prop="deviceId"
          label="采集设备"
          width="120"
      />
      <el-table-column
          prop="taskId"
          label="所属任务"
          width="120"
      />
      <el-table-column
          prop="content"
          label="识别结果详情"
          min-width="200"
      />
      <el-table-column
          prop="url"
          label="存储路径"
          min-width="200"
      />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
          >
            编辑 <i class="el-icon-edit"></i>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
// 请确保引入你的请求工具
import request from '@/utils/request'

export default {
  name: "recognition",
  data() {
    return {
      recognition: [], // 表格数据源（初始为空数组）
      form: {}, // 编辑表单数据
      dialogFormVisible: false, // 弹窗显示状态
      loading: false, // 加载状态
      // 新增：查询表单数据（对应前四列）
      queryForm: {
        id: "",
        time: "",
        deviceId: "",
        taskId: ""
      },
      // 表单校验规则
      formRules: {
        time: [{ required: true, message: '请输入识别时间', trigger: 'blur' }],
        content: [{ required: true, message: '请输入识别结果详情', trigger: 'blur' }],
        url: [{ required: true, message: '请输入存储路径', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.load() // 页面创建时加载数据
  },
  methods: {
    // 重置表单（弹窗关闭时调用）
    resetForm() {
      this.form = {}
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    },

    // 保存编辑数据
    async save() {
      try {
        // 表单校验
        await this.$refs.formRef.validate()

        // 调用保存接口
        const res = await request.post("/recognition/save", this.form)

        // 接口返回判断（适配常见的后端返回格式）
        if (res && (res.code === 200 || res.success)) {
          this.$message.success("保存成功!")
          this.dialogFormVisible = false
          this.load() // 重新加载表格数据
        } else {
          this.$message.error(res?.msg || '保存失败，请重试')
        }
      } catch (error) {
        console.error('保存识别数据失败：', error)
        this.$message.error('保存失败，请检查数据格式或网络状态')
      }
    },

    // 加载全部表格数据
    async load() {
      try {
        this.loading = true // 显示加载状态

        // 调用获取数据接口
        const res = await request.get("/recognition/getAllData", {})

        // 核心逻辑：兼容不同接口返回格式，确保最终是数组
        let tableData = []
        if (res && res.data) {
          tableData = Array.isArray(res.data) ? res.data : (res.data.data || [])
        }

        // 最终兜底：确保是数组
        this.recognition = Array.isArray(tableData) ? tableData : []
      } catch (error) {
        console.error('加载识别数据失败：', error)
        this.recognition = [] // 失败时重置为空数组，避免表格报错
        this.$message.error('数据加载失败，请刷新页面重试')
      } finally {
        this.loading = false // 关闭加载状态
      }
    },

    // 新增：按条件查询（前四列过滤）
    async loadByColumn() {
      try {
        this.loading = true

        // 先获取全部数据
        const res = await request.get("/recognition/getAllData", {})
        let tableData = []
        if (res && res.data) {
          tableData = Array.isArray(res.data) ? res.data : (res.data.data || [])
        }
        let filterList = Array.isArray(tableData) ? tableData : []

        // 1. 识别记录标识：转为数值后精确匹配
        if (this.queryForm.id) {
          const id = Number(this.queryForm.id)
          filterList = filterList.filter(item => item.id === id)
        }

        // 2. 识别时间：模糊匹配（忽略大小写）
        if (this.queryForm.time) {
          const time = this.queryForm.time.trim().toLowerCase()
          filterList = filterList.filter(item => item.time?.toLowerCase().includes(time))
        }

        // 3. 采集设备：转为数值后精确匹配
        if (this.queryForm.deviceId) {
          const deviceId = Number(this.queryForm.deviceId)
          filterList = filterList.filter(item => item.deviceId === deviceId)
        }

        // 4. 所属任务：转为数值后精确匹配
        if (this.queryForm.taskId) {
          const taskId = Number(this.queryForm.taskId)
          filterList = filterList.filter(item => item.taskId === taskId)
        }

        // 更新表格数据
        this.recognition = filterList

        // 无数据提示
        if (filterList.length === 0) {
          this.$message.info("未查询到匹配的识别记录，请调整查询条件")
        }
      } catch (error) {
        console.error('查询识别数据失败：', error)
        this.recognition = []
        this.$message.error('查询失败，请刷新页面重试')
      } finally {
        this.loading = false
      }
    },

    // 新增：重置查询条件
    resetQuery() {
      // 清空查询表单
      this.queryForm = {
        id: "",
        time: "",
        deviceId: "",
        taskId: ""
      }
      // 重新加载全部数据
      this.load()
    },

    // 编辑行数据（深拷贝避免联动表格）
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row)) // 深度拷贝，避免修改表单联动表格
      this.dialogFormVisible = true
    }
  }
}
</script>

<style scoped>
/* 优化按钮样式 */
:deep(.el-button) {
  margin: 0 2px;
}

/* 弹窗表单间距优化 */
:deep(.el-form-item) {
  margin-bottom: 15px;
}

/* 表格加载状态样式优化 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8);
}

/* 新增：查询区域表单样式优化 */
:deep(.el-form-inline .el-form-item) {
  margin-bottom: 0 !important;
  margin-right: 15px;
}
</style>