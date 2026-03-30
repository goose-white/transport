<template>
  <!-- 完全保留你原有的模板内容，时间列显示逻辑不变 -->
  <div style="padding: 20px;">
    <!-- 查询条件区域 -->
    <div style="margin-bottom: 20px; display: flex; align-items: center; gap: 15px;">
      <div>
        <label style="margin-right: 8px;">任务ID：</label>
        <el-input v-model="queryForm.id" placeholder="输入任务ID" style="width: 150px;" />
      </div>
      <div>
        <label style="margin-right: 8px;">任务名称：</label>
        <el-input v-model="queryForm.name" placeholder="输入任务名称" style="width: 180px;" />
      </div>
      <div>
        <label style="margin-right: 8px;">任务类型：</label>
        <el-input v-model="queryForm.type" placeholder="输入任务类型（0-3）" style="width: 150px;" />
      </div>
      <div>
        <label style="margin-right: 8px;">执行无人机：</label>
        <el-input v-model="queryForm.deviceId" placeholder="输入执行无人机ID" style="width: 150px;" />
      </div>
      <el-button type="primary" @click="handleQuery">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 新增按钮 -->
    <el-button type="primary" style="margin-bottom: 20px;" @click="handleAdd">新增任务</el-button>

    <!-- 编辑弹窗：Vue2 + Element UI 正确绑定 -->
    <el-dialog
        title="任务信息"
        :visible.sync="dialogFormVisible"
        width="500px"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="form" label-width="100px" size="small" :rules="formRules">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称"></el-input>
        </el-form-item>
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择任务类型">
            <el-option label="0-道路巡航" value="0"></el-option>
            <el-option label="1-水域巡逻" value="1"></el-option>
            <el-option label="2-事故取证" value="2"></el-option>
            <el-option label="3-隐患排查" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="执行无人机" prop="deviceId">
          <el-input v-model.number="form.deviceId" placeholder="请输入无人机ID" type="number"></el-input>
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择任务状态">
            <el-option label="0-未开始" value="0"></el-option>
            <el-option label="1-执行中" value="1"></el-option>
            <el-option label="2-已完成" value="2"></el-option>
            <el-option label="3-已取消" value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTask">确定</el-button>
      </div>
    </el-dialog>

    <!-- 任务表格：完全保留你原有的时间显示逻辑 -->
    <el-table
        :data="taskList"
        stripe
        border
        style="width: 100%;"
        v-loading="loading"
    >
      <el-table-column prop="id" label="任务ID" width="100" />
      <el-table-column prop="name" label="任务名称" width="140" />
      <el-table-column prop="type" label="任务类型（0 - 道路巡航、1 - 水域巡逻、2 - 事故取证、3 - 隐患排查）" width="160" />
      <el-table-column prop="deviceId" label="执行无人机" width="120" />
      <el-table-column prop="startTime" label="创建时间" width="180">
        <template slot-scope="scope">
          {{ scope.row.startTime || '未设置' }}
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="任务结束时间" width="180">
        <template slot-scope="scope">
          {{ scope.row.endTime || '未设置' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="任务状态（0-未开始，1-执行中，2-已完成,3-已取消）" width="160" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click.stop="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click.stop="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: "Task",
  data() {
    return {
      taskList: [],
      loading: false,
      dialogFormVisible: false,
      form: {},
      queryForm: {
        id: '',
        name: '',
        type: '',
        deviceId: ''
      },
      formRules: {
        name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
        deviceId: [
          { required: true, message: '请输入无人机ID', trigger: 'blur' },
          { type: 'number', message: '无人机ID必须为数字', trigger: 'blur' }
        ],
        status: [{ required: true, message: '请选择任务状态', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadTaskData()
  },
  methods: {
    resetForm() {
      this.form = {}
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },

    // 核心修改1：适配四个独立查询条件（替换原queryKey逻辑）
    async loadTaskData() {
      try {
        this.loading = true
        // 构造四个独立查询参数（空值不传递）
        const params = {}
        // 任务ID：非空转数字传递
        if (this.queryForm.id) params.id = Number(this.queryForm.id)
        // 任务名称：非空去空格传递
        if (this.queryForm.name) params.name = this.queryForm.name.trim()
        // 任务类型：非空转数字传递
        if (this.queryForm.type) params.type = Number(this.queryForm.type)
        // 无人机ID：非空转数字传递
        if (this.queryForm.deviceId) params.deviceId = Number(this.queryForm.deviceId)

        // 调用后端适配后的查询接口
        const res = await request.get("/task/getAllData", { params })
        this.taskList = res.data.data && Array.isArray(res.data.data) ? res.data.data : []
      } catch (error) {
        console.error('加载任务数据失败：', error)
        this.taskList = []
        // 仅保留数据加载失败的必要提示，其他失败提示取消
        this.$message.error('数据加载失败，请重试')
      } finally {
        this.loading = false
      }
    },

    handleQuery() {
      this.loadTaskData()
    },

    handleReset() {
      this.queryForm = {
        id: '',
        name: '',
        type: '',
        deviceId: ''
      }
      this.loadTaskData()
    },

    handleAdd() {
      this.form = { type: "0", status: "0" } // 初始值设为字符串（适配Byte）
      this.dialogFormVisible = true
    },

    handleEdit(row) {
      this.dialogFormVisible = true
      // 保留你原有的type/status转字符串逻辑
      this.form = JSON.parse(JSON.stringify({
        id: row.id,
        name: row.name,
        type: row.type?.toString() || "0", // 转为字符串
        deviceId: row.deviceId || "",
        status: row.status?.toString() || "0" // 转为字符串
      }))
      this.$nextTick(() => {
        this.dialogFormVisible = true
      })
    },

    // 核心修改2：取消失败提示，仅保留成功提示
    async saveTask() {
      try {
        await this.$refs.formRef.validate()

        // 保留你原有的提交数据转换逻辑
        const submitData = {
          id: this.form.id ? Number(this.form.id) : null,
          name: this.form.name,
          type: parseInt(this.form.type) & 0xFF, // 强制转为Byte范围（0-255）
          deviceId: Number(this.form.deviceId),
          status: parseInt(this.form.status) & 0xFF, // 强制转为Byte范围
          startTime: new Date().toISOString(),
          endTime: null
        }

        const apiUrl = "/task/save";
        console.log('提交适配Byte类型的数据：', submitData)

        const res = await request.post(apiUrl, submitData)

        // 仅显示成功提示，取消所有失败提示
        const isSuccess = String(res?.data?.code) === '200' || res?.data?.data?.includes('成功')
        if (isSuccess) {
          const tipMsg = res?.data?.data || (this.form.id ? '编辑任务成功' : '新增任务成功')
          this.$message.success(tipMsg)
          this.dialogFormVisible = false
          this.loadTaskData()
        } else {
          // 失败仅打印日志，不显示前端提示
          console.error('保存失败：', res?.data?.data || '未知错误')
        }
      } catch (error) {
        // 异常仅打印日志，取消前端失败提示
        console.error('保存任务失败详情：', error)
        this.dialogFormVisible = false
        this.loadTaskData()
      }
    },

    // 核心修改3：取消删除失败提示，仅保留成功提示
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该任务？', '提示', { type: 'warning' })
        // 修复删除接口传参方式（适配后端@RequestParam）
        const res = await request.post("/task/delete", {}, {
          params: { id: row.id }
        })

        // 仅显示成功提示，取消失败提示
        const isSuccess = String(res?.data?.code) === '200' || res?.data?.data?.includes('成功')
        if (isSuccess) {
          const tipMsg = res?.data?.data || '删除成功'
          this.$message.success(tipMsg)
        } else {
          // 失败仅打印日志，不显示前端提示
          console.error('删除失败：', res?.data?.data || '未知错误')
        }
        this.loadTaskData()

      } catch (error) {
        // 排除用户取消的情况，仅打印日志，取消前端提示
        if (error !== 'cancel') {
          console.error('删除任务失败：', error)
        }
        this.loadTaskData()
      }
    }
  }
}
</script>

<style scoped>
::v-deep .el-button { margin: 0 4px; }
::v-deep .el-form-item { margin-bottom: 15px; }
</style>