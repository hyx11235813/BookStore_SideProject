<template>
  <div class="reset-password">
    <div class="reset-password__card">
      <!--      步驟條-->
      <steps :current-steps="3"/>
      <div class="card__img"></div>
      <!--      輸入欄-->
      <form class="card__form">
        <div class="card__form-inputs">
          <input ref="firstInput" class="card__form-input" v-model="form.password" type="password"
                 @keyup="checkPassword"
                 placeholder="新密碼">
          <input class="card__form-input" v-model="form.confirmPassword" type="password" @keyup="checkConfirmPassword"
                 placeholder="確認新密碼">
        </div>
        <div class="card__hint">
          <div class="card__hint-item">
            <img class="card__hint-icon--check"
                 :src="passwordValid ? require('@/assets/images/checkmark-active.svg') : require('@/assets/images/checkmark-inactive.svg')"
                 alt="">
            <p class="card__hint-text">密碼需包含字母和數字，長度為8~16。</p>
          </div>
          <div class="card__hint-item">
            <img class="card__hint-icon--check"
                 :src="passwordMatch ? require('@/assets/images/checkmark-active.svg') : require('@/assets/images/checkmark-inactive.svg')"
                 alt="">
            <p class="card__hint-text">兩次輸入的密碼需要一致。</p>
          </div>
        </div>
        <button class="card__form-button" @click.prevent="submit" :disabled="!isSubmitDisable">
          確認
        </button>
        <div class="card__msg" v-show="msg!==''">
          <p>{{ msg }}</p>
        </div>
      </form>
    </div>
  </div>
</template>
<script>
import {resetPassword} from '@/api/user';
import Steps from '@/views/user/resetPassword/ResetPassword_CommonSteps.vue';

export default {
  // 如果沒有email，返回重設密碼第一個步驟
  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (!vm.$store.state.userData.applyResetEmail) {
        vm.$router.push('/forgot-password');
      }
    });
  },
  components: {Steps},
  data() {
    return {
      isSubmitDisable: false,
      form: {
        password: '',
        confirmPassword: ''
      },
      passwordValid: false,
      passwordMatch: false,
      msg: ''
    };
  }, methods: {
    async submit() {
      try {
        const payload = {
          email: this.$store.state.userData.applyResetEmail,
          password: this.form.password
        };
        const resp = await resetPassword(payload);

        if (resp.code !== 200) {
          this.resetErrorForm();
          await this.showMsg('系統錯誤,請稍後再試');
          return;
        }

        this.$router.push('/forgot-password/reset-password-success');
        this.$store.commit('setApplyResetEmail', '');
      } catch (error) {
        console.error(error);
        await this.showMsg('系統錯誤,請稍後再試');
      }
    },
    resetErrorForm() {
      this.msg = '密碼設定失敗';
      this.form.password = '';
      this.form.confirmPassword = '';
      this.passwordValid = false;
      this.passwordMatch = false;
    },
    isValidPassword() {
      //至少包含一個數字和一個字母，不包含空格，長度8~16
      const reg = /^(?=.*[0-9])(?=.*[a-zA-Z])(?!.* ).{8,16}$/;
      return reg.test(this.form.password);
    },
    isPasswordMatch() {
      return this.form.password === this.form.confirmPassword && (this.form.password !== '' || this.form.confirmPassword !== '');
    },
    isSubmitAllow() {
      return this.passwordValid === true && this.passwordMatch === true;
    },

    checkPassword() {
      this.passwordValid = this.isValidPassword();
      this.passwordMatch = this.isPasswordMatch();
      this.isSubmitDisable = this.isSubmitAllow();
    },
    checkConfirmPassword() {
      this.passwordMatch = this.isPasswordMatch();
      this.isSubmitDisable = this.isSubmitAllow();
    },
    async showMsg(msg) {
      this.msg = msg;
      await this.delay(2000);
      this.msg = '';
    },
    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    }

  },
  mounted() {
    this.$refs.firstInput.focus();
  }
};

</script>
<style lang="scss" scoped>
@use "@/assets/style/abstracts" as *;

.reset-password {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: $clr-bg-soft-green;

  .reset-password__card {
    @include flex-card(min(28rem, 98%))
  }

  //輸入欄
  .card__form {
    width: 100%;
    padding-top: 1.4rem;

    .card__form-inputs {
      display: flex;
      flex-direction: column;

      .card__form-input {
        margin-bottom: 1rem;
        padding: .5rem;
        border: 1px solid #c4c4c4;
        border-radius: 4px;
        width: 100%;
        outline: none;
      }
    }

    //輸入提示
    .card__hint {
      margin-bottom: 2rem;

      .card__hint-item {
        display: flex;
        align-items: center;
        margin-top: 10px;

        .card__hint-icon--check {
          width: .9rem;
          margin-right: .4rem;
        }

        .card__hint-text {
          color: $clr-text-gray;
          font-size: .9rem;
        }
      }
    }

    .card__form-button {
      @include confirm-btn--green-round(100%);
    }

    .card__msg {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: white;
      background-color: rgba(0, 0, 0, 0.8);
      padding: .7rem 1rem;
      border-radius: .7rem;
    }
  }
}


@media only screen and (max-width: 375px) {
  .reset-password__card {
    width: 100vw !important;
    height: 100vh !important;
    border-radius: 0 !important;
    padding: 4rem 1rem !important;
  }
}

</style>