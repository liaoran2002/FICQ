<template>
  <div class="loading-overlay">
    <div class="loading-spinner"></div>
    <h3>加载中<span class="dots">{{ dots }}</span></h3>
  </div>
</template>

<script>
export default {
  name: 'Loading',
  data() {
    return {
      dotCount: 1,
      dots: '.'
    };
  },
  mounted() {
    this.dotInterval = setInterval(() => {
      this.dotCount = (this.dotCount % 3) + 1;
      this.dots = '.'.repeat(this.dotCount);
    }, 500);
  },
  beforeUnmount() {
    clearInterval(this.dotInterval);
  }
};
</script>

<style scoped>
.loading-overlay {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-spinner {
  border: 4px solid #676767;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>