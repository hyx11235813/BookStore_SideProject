<template>
  <div class="forgot-password">
    <div class="forgot-password__card">
      <!--步驟條-->
      <steps :current-steps="1"/>
      <!--返回鈕-->
      <img class="forgot-password-return-button" title="返回登入頁面" src="@/assets/images/arrow-return.svg"
           @click="returnPage" alt="">
      <!--輸入欄-->
      <form class="forgot-password__form">
        <p class="forgot-password__form-hint">*請輸入要找回密碼的電子信箱</p>
        <input :class="['forgot-password__form-email-input',{error:isEmailError}]" v-model="form.email"
               ref="email-input"
               @blur="checkEmailBlur"
               @input="enableLiveCheck?checkEmail():null"
               placeholder="電子信箱"
        >
        <div :class="['forgot-password__form-msg',{show:isEmailError}]">
          信箱錯誤
        </div>
        <div class="forgot-password__form-captcha">
          <input ref="verifyCodeInput" v-model="form.verifyCode" placeholder="驗證碼">
          <div class="captcha-wrapper">
            <valid-code @update:value="getCode" ref="validCode"/>
          </div>
        </div>
        <div :class="['forgot-password__form-msg',{show:isVerifyCodeError}]">
          驗證碼錯誤
        </div>
        <div class="forgot-password__form-button">
          <button @click.prevent="submit" :disabled="isSubmitDisabled">確認</button>
        </div>
        <div :class="['forgot-password__return-msg',{show:!isEmailExist}]">信箱不存在</div>
      </form>
    </div>
    <div class="animation-wrapper" v-show="isLoading">
      <div class="animation" id="animation"></div>
    </div>
  </div>
</template>
<script>
import {requestResetPassword} from '@/api/user';
import ValidCode from '@/components/ValidCode.vue';
import lottie from 'lottie-web';
import Steps from '@/views/user/resetPassword/ResetPassword_CommonSteps.vue';

export default {
  components: {ValidCode, Steps},
  data() {
    return {
      form: {
        email: '',
        verifyCode: ''
      },
      verifyCode: '', //驗證碼
      isEmailError: false,
      isVerifyCodeError: false,
      isEmailExist: true,
      isLoading: false,
      animationInstance: null,//轉圈動畫實例
      enableLiveCheck: false //是否開啟@input 驗證
    };
  },
  computed: {
    //沒輸入不能發送
    isSubmitDisabled() {
      return this.form.email === '' || this.form.verifyCode === '';
    }
  },
  methods: {
    async submit() {
      this.validateForm();
      if (!this.isFormValid()) {
        return;
      }
      this.loading_animation();
      try {
        const resp = await this.delay(2000).then(() => requestResetPassword(this.form));
        if (resp.code !== 200) {
          this.showEmailNotExistHint();
          return;
        }

        this.$store.commit('setApplyResetEmail', this.form.email);
        this.$router.push('/forgot-password/verify-token');
      } catch (error) {
        console.error(error);
      } finally {
        this.closeAnimation();
      }
    },
    validateForm() {
      this.checkEmail();
      this.checkVerifyCode();
    },
    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    showEmailNotExistHint() {
      this.isEmailExist = false;
      this.delay(2000).then(() => this.isEmailExist = true);

    },
    closeAnimation() {
      this.isLoading = false;
      if (this.animationInstance) {
        this.animationInstance.destroy();
      }
    },
    checkEmailBlur() {
      this.checkEmail();
      if (this.isEmailError) {
        this.enableLiveCheck = true;
      }
    },
    isFormValid() {
      return !(this.isEmailError || this.isVerifyCodeError);
    },
    checkEmail() {
      const EMAIL_REG = /[A-Za-z0-9\._%+\-]+@[A-Za-z0-9\.\-]+\.[A-Za-z]{2,}/; //信箱格式
      this.isEmailError = !EMAIL_REG.test(this.form.email);
    },
    checkVerifyCode() {
      if (this.form.verifyCode !== this.verifyCode) {
        this.isVerifyCodeError = true;
        this.form.verifyCode = '';
        this.$refs['validCode'].refreshCode();
        this.$refs.verifyCodeInput.focus();
      } else {
        this.isVerifyCodeError = false;
      }
    },
    loading_animation() {
      this.animationInstance = lottie.loadAnimation({
        container: document.getElementById('animation'),
        loop: true,
        autoplay: true,
        path: '/animation/loading_circle.json'
      });
      this.isLoading = true;
    },
    //從ValidCode.vue取得驗證碼
    getCode(value) {
      this.verifyCode = value;
    },
    returnPage() {
      this.$router.push('/login');
    }
  },
  mounted() {
  }
};

</script>
<style lang="scss" scoped>
@use "@/assets/style/abstracts" as *;

.forgot-password {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: $clr-bg-soft-green;

  .forgot-password__card {
    @include flex-card(min(28rem, 98%));
    position: relative;

    .forgot-password-return-button {
      position: absolute;
      top: 1rem;
      left: 1rem;
      width: 1.5rem;
      cursor: pointer;
    }

    .forgot-password__form {
      position: relative;
      width: 100%;
      padding-top: 1.5rem;

      .forgot-password__form-hint {
        font-size: .8rem;
        color: $clr-text-gray;
        padding-bottom: .4rem;
      }

      //電子信箱輸入
      .forgot-password__form-email-input {
        width: 100%;
        padding: .5rem .5rem;
        margin-bottom: .2rem;
        outline: none;
        border: 1px solid #c4c4c4;
        border-radius: 4px;

        &.error {
          border: 1px solid $clr-danger;
        }
      }

      //驗證碼輸入
      .forgot-password__form-captcha {
        display: flex;
        padding-bottom: .2rem;

        input {
          max-width: 6rem;
          padding: .5rem .5rem;
          outline: none;
          border: 1px solid #c4c4c4;
          border-radius: 4px;
        }

        .captcha-wrapper {
          margin-left: 1rem;
        }
      }

      .forgot-password__form-msg {
        min-height: 1rem;
        font-size: .8rem;
        margin-bottom: .6rem;
        color: $clr-danger;
        visibility: hidden;

        &.show {
          border: red;
          visibility: visible;
        }
      }


      .forgot-password__form-button {
        padding-top: 1rem;

        button {
          @include confirm-btn--green-round(100%);
        }
      }

      .forgot-password__return-msg {
        position: absolute; //.forgot-password__form為 relative
        top: 10%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: white;
        background-color: rgba(0, 0, 0, 0.8);
        padding: .7rem 1rem;
        border-radius: .7rem;
        visibility: hidden;

        &.show {
          visibility: visible;
        }
      }
    }
  }

  .animation-wrapper {
    position: fixed;
    width: 100%;
    height: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 9999;
    background-color: rgba(0, 0, 0, .2);

    #animation {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 8rem;
    }
  }


  @media only screen and (max-width: 375px) {
    .forgot-password__card {
      width: 100vw !important;
      height: 100vh !important;
      border-radius: 0 !important;
      padding: 4rem 1rem 0 !important;
    }
  }


}
</style>