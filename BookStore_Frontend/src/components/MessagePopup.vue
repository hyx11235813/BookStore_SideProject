<script>
export default {
  data() {
    return {
      popupVisible: false, //彈出提示
      popupMessage: '',
      popupType: ''//彈出提示類型
    };
  },
  props: {
    message: {
      type: String,
      default: ''
    },
    isVisible: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: '' //info、warning
    }
  },
  computed: {
    typeClass() {
      return `message-popup--${this.type}`;
    }
  }
};
</script>

<template>
  <div v-if="isVisible" class="message-popup" :class="typeClass">
    <div class="message-popup__content">
      <p class="message-popup-text type" :class="typeClass">{{ message }}</p>
    </div>
  </div>
</template>

<style scoped lang="scss">
.message-popup {
  z-index: 9;
  position: fixed;
  width: 300px;
  left: 50%;
  bottom: 10px;
  transform: translateX(-50%);
  padding: 16px;
  border-radius: 6px;
  background-color: #f5f5f5;

  .message-popup__content {
    .message-popup-text {
      font-size: 14px;

    }
  }
}

.message-popup--info {
  background-color: #f5f5f5;
  color: #000000;


}

.message-popup--warning {
  background-color: #C0392B;
  color: #ffffff;
}
</style>