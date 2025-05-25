<template>
  <div class="back-to-top" @click="backToTop" v-show="isScroll">
    <span class="icon iconfont icon-xiangshang">回到顶部</span>
  </div>
</template>

<script>
export default {
  name: 'backToTop',
  props: {
    // 父组件中要监听的元素选择器
    targetSelector: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      scrollTop: 0,
      targetElement: null,
      scrollThreshold: 300, // 滚动阈值，达到此高度时显示按钮
      duration: 500 // 动画持续时间（毫秒）
    };
  },
  computed: {
    isScroll() {
      return this.scrollTop > this.scrollThreshold;
    }
  },
  mounted() {
    // 获取父组件中的目标元素
    this.targetElement = document.getElementsByClassName(this.targetSelector)[0];
    
    if (!this.targetElement) {
      console.error(`未找到目标元素: ${this.targetSelector}`);
      return;
    }
    
    // 监听目标元素的滚动事件
    this.targetElement.addEventListener('scroll', this.handleScroll);
    // 初始化滚动位置
    this.handleScroll();
  },
  beforeDestroy() {
    if (this.targetElement) {
      // 移除滚动事件监听，防止内存泄漏
      this.targetElement.removeEventListener('scroll', this.handleScroll);
    }
  },
  methods: {
    handleScroll() {
      if (this.targetElement) {
        // 获取目标元素的滚动位置
        this.scrollTop = this.targetElement.scrollTop;
      }
    },
    backToTop() {
      if (!this.targetElement) return;
      
      // 获取当前滚动位置
      const startPosition = this.targetElement.scrollTop;
      const startTime = performance.now();
      
      const scrollToTop = (currentTime) => {
        const elapsedTime = currentTime - startTime;
        // 使用easeOutQuad缓动函数
        const progress = Math.min(elapsedTime / this.duration, 1);
        const easeProgress = 1 - Math.pow(1 - progress, 2);
        
        this.targetElement.scrollTop = startPosition - (startPosition * easeProgress);
        
        if (progress < 1) {
          requestAnimationFrame(scrollToTop);
        }
      };
      
      requestAnimationFrame(scrollToTop);
    }
  }
};
</script>

<style scoped>
.back-to-top {
  position: fixed; /* 固定定位 */
  height: 10vh;
  width: 10vh;
  line-height: 10vh; /* 垂直居中 */;
  text-align: center; /* 水平居中 */;
  bottom: 20px; /* 距离底部的距离 */
  right: 20px; /* 距离右边的距离 */
  background-color: #007bff; /* 背景颜色 */
  color: #fff; /* 文字颜色 */
  border-radius: 50%; /* 圆角 */
  cursor: pointer; /* 鼠标指针样式 */
  z-index: 999; /* 确保按钮在其他元素之上 */
}
.back-to-top:hover {
  background-color: #0056b3; /* 悬停时的背景颜色 */
}
.back-to-top span {
  font-size: 2vh; /* 图标大小 */
}
</style>