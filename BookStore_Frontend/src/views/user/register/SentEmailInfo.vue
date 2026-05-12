<template>
  <div class="modal">
    <div class="container">
      <div class="info-card">
        <div class="content">
          <p>驗證信已發送至信箱，請點擊郵件上的鏈結開通帳號</p>
          <p>如果沒看到請檢查垃圾郵件！</p>
        </div>
        <button @click="toLoginPage">返回登入畫面</button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  methods: {
    toLoginPage() {
      this.$router.push('/login')
    }
  }
}
</script>
<style lang="scss" scoped>
@use "src/assets/style/abstracts" as *;

.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: $clr-bg-soft-green;
  .container {
    margin-bottom: 200px;
    width: min(500px, 96%);

    .info-card {
      @include flex-card(100%);
      z-index: 1000;
      border-radius: 12px;

      .content {
        text-align: center;
        padding-bottom: 1rem;

        p {
          line-height: 2;
          color: $clr-text-gray
        }

      }

      button {
        font-size: 1rem;
        @include confirm-btn--green-round(50%);

        &:hover {
          color: white;
        }
      }
    }
  }
}

@media only screen and (max-width: 480px) {
  p {
    font-size: .8rem;
  }
}
</style>