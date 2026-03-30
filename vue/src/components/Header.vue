<template>
  <div style="line-height: 60px;display: flex;">
    <div style="flex:1;font-size:18px">
        <span :class="collapseBtnclass" style="cursor:pointer" @click="collapse"></span>
        <el-breadcrumb separator="/" style="display:inline-block; margin-left: 10px;">
        <!-- //添加表头 -->
        <el-breadcrumb-item :to="'/'">无人机管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>{{currentPathName}}</el-breadcrumb-item>
      </el-breadcrumb>
      </div>
  </div>
</template>

<script>
export default {
  name: "Header",
  props:{
    collapseBtnclass:String,
    collapse:Function,
  },
  watch:{ // 监听路由变化
    '$route':function(){
      this.currentPathName = localStorage.getItem("currentPathName")//去除route中设置的当前路由信息
    }
  },
  data(){
    return{
      user:localStorage.getItem("user")?JSON.parse(localStorage.getItem("user")):{},
      currentPathName:''
    }
  },
  methods:{
  
  }
}
</script>                                                                                                                                                                                                                                                                                                                                                

<style scoped>

</style>