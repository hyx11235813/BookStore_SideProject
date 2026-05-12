<script>
export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    },
  },
  methods: {
    closeModel() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<template>
  <div class="modal" v-show="visible" @click.self="closeModel">
    <div class="modal__container" >
      <div class="info__addToCar">
        <img class="info__addToCar-img" src="@/assets/images/circle-checkmark.svg" alt="">
        <p class="info__addToCar-text">商品已加入購物車</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, .05);
  z-index: 999;

  .modal__container {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 300px;
    height: 150px;
    z-index: 999;
    background-color: rgba(0, 0, 0, 0.65);
    border-radius: 4px;
  }

  .info__addToCar {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;

    .info__addToCar-img {
      width: 20%;
      margin-bottom: 1rem;
    }

    .info__addToCar-text {
      color: #ffffff;
    }

  }
}
</style>