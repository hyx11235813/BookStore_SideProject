<template>
  <div class="reset-password-success">
    <div class="reset-password-success__card">
      <div class="card__form">
        <!--步驟條-->
        <steps :current-steps="999"/>
        <div class="card__info">
          <div class="card_info-text">
            <p>您的新密碼已設定成功</p>
          </div>
        </div>
        <button class="card__button" @click.prevent="submit">返回登入頁面</button>
      </div>
    </div>
  </div>
</template>
<script>
import Steps from "@/views/user/resetPassword/ResetPassword_CommonSteps.vue";

export default ({
  components: {Steps},
  data() {
    return {}
  },
  methods: {
    submit() {
      this.$router.push('/login')
    }
  }
})
</script>

<style lang="scss" scoped>
@use "@/assets/style/abstracts" as *;

.reset-password-success {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: $clr-bg-soft-green;

  .reset-password-success__card {
    @include flex-card(min(28rem, 98%));

    .card__form {
      width: 100%;

      .card__info {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 2rem 0 2.5rem;

        .card_info-text {
          font-size: 1.2rem;
          color: $clr-text-gray;
        }
      }

      .card__button {
        @include confirm-btn--green-round(100%)
      }
    }
  }
}

@media only screen and (max-width: 375px) {
  .reset-password-success__card {
    width: 100vw !important;
    height: 100vh !important;
    border-radius: 0 !important;
    padding: 4rem 1rem 0 !important;
  }
}

@media only screen and (max-width: 320px) {
  .reset-password-success__card {
    width: 100vw !important;
    height: 100vh !important;
  }
}
</style>