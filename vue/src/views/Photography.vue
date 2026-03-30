<script>
export default {
  name: "Photography"
}
</script>

<template>
  <div class="photography-page">
    <!-- 卡片容器 -->
    <el-card shadow="hover" class="camera-card">
      <!-- 卡片头部：标题+开关 -->
      <div slot="header" class="card-header">
        <span>摄影管理</span>
        <el-switch
            v-model="isCameraOpen"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="toggleCamera"
        />
      </div>
      <!-- 摄像头预览区域 -->
      <div class="camera-preview">
        <video
            ref="videoRef"
            autoplay
            playsinline
            class="camera-video"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { ElMessage } from 'element-ui'; // 引入Element提示组件

export default {
  name: 'Photography',
  data() {
    return {
      isCameraOpen: false, // 摄像头开关状态
      videoStream: null // 保存视频流（用于关闭摄像头）
    };
  },
  methods: {
    // 开关切换时触发：打开/关闭摄像头
    async toggleCamera(open) {
      if (open) {
        await this.openCamera();
      } else {
        this.closeCamera();
      }
    },

    // 打开摄像头（调用浏览器Media API）
    async openCamera() {
      try {
        // 请求摄像头权限，获取视频流（仅请求视频轨道）
        const stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: 'user' } // 优先前置摄像头（移动设备可用environment后置）
        });
        this.videoStream = stream;
        // 将视频流绑定到video标签
        const videoDom = this.$refs.videoRef;
        videoDom.srcObject = stream;
        await videoDom.play(); // 自动播放视频
      } catch (err) {
        this.isCameraOpen = false; // 开关切回关闭
        ElMessage.error(`摄像头启动失败：${err.message}`);
        console.error('摄像头错误：', err);
      }
    },

    // 关闭摄像头（停止视频流）
    closeCamera() {
      if (this.videoStream) {
        // 停止所有视频轨道
        this.videoStream.getTracks().forEach(track => track.stop());
        this.videoStream = null;
        // 清空video的视频源
        this.$refs.videoRef.srcObject = null;
      }
    }
  },
  // 组件销毁前：确保关闭摄像头
  beforeUnmount() {
    if (this.isCameraOpen) {
      this.closeCamera();
    }
  }
};
</script>

<style scoped>
.photography-page {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.camera-preview {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.camera-video {
  width: 640px;
  height: 480px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
}
</style>