<template>
  <div class="verify-token">
    <div class="verify-token__card">
      <!--      步驟條-->
      <steps :current-steps="2"/>
      <div class="card__info">
        <p class="card__info-text">請輸入信件上的驗證碼</p>
      </div>
      <!--        輸入欄-->
      <form class="card__form">
        <div class="card__form-input-field">
          <input
              v-for="(_,index) in form.token"
              v-model="form.token[index]"
              :ref="'input'+index"
              @input="onInput(index)"
              @keydown.backspace="onBackspace(index)"
              @paste="handlePaste"
              maxlength="1"
          >
        </div>
        <div :class="['card__msg',{show:showMsg}]">
          <p>{{ msg }}</p>
        </div>
        <button class="card__form-resent-button" @click.prevent="resendEmail">重新發送驗證碼</button>
        <button class="card__form-next-button" @click.prevent="submit" :disabled="isDisable">下一步</button>
      </form>
    </div>
    <div class="animation-wrapper" v-show="animationInstance!==null">
      <div class="animation" ref="animation"></div>
    </div>
  </div>
</template>
<script>
import {requestResetPassword, verifyToken} from '@/api/user';
import Steps from '@/views/user/resetPassword/ResetPassword_CommonSteps.vue';
import lottie from 'lottie-web';


export default {
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
      form: {
        email: this.$store.state.userData.applyResetEmail,
        token: Array(6).fill('')  //輸入的驗證碼
      },
      msg: '',//根據請求回應顯示的訊息
      animationInstance: null,//lottie動畫實例
      showMsg: false
    };
  },
  computed: {
    isDisable() {
      return this.form.token.filter(item => item !== '').length < 6;
    }
  },
  methods: {
    async submit() {
      try {
        this.startLoading();
        const resp = await verifyToken({email: this.form.email, token: this.form.token.join('')});
        await this.delay(3000);
        if (resp.code !== 200) {
          this.form.token = Array(6).fill('');
          this.focusInput(0);
          this.showMessage('驗證碼錯誤');
          return;
        }
        this.$router.push('/forgot-password/reset-password');
      } catch (error) {
        console.error(error);
      } finally {
        this.stopLoading();
      }
    },
    onInput(index) {
      const val = this.form.token[index];
      if (/^\d$/.test(val)) {
        if (index < this.form.token.length - 1) {
          this.focusInput(index + 1);
        }
      } else {
        this.form.token[index] = '';
      }
    },
    onBackspace(index) {
      if (this.form.token[index] === '' && index > 0) {
        this.focusInput(index - 1);
      }
    },
    focusInput(index) {
      const input = this.$refs['input' + index];
      if (input && input[0]) {
        input[0].focus();
      }
    },
    async resendEmail() {
      try {
        this.startLoading();
        let payload = JSON.stringify({email: this.form.email});
        const resp = await requestResetPassword(payload);
        if (resp.code !== 200) return;

        await this.delay(1000);

        this.showMessage('驗證碼已發送');
      } catch (error) {
        console.error(error);
      } finally {
        this.stopLoading();
      }
    },

    showMessage(msg, duration = 2000) {
      this.msg = msg;
      this.showMsg = true;
      setTimeout(() => {
        this.msg = '';
        this.showMsg = false;
      }, duration);
    },
    startLoading() {
      if (!this.animationInstance) {
        this.animationInstance = lottie.loadAnimation({
          container: this.$refs.animation,
          loop: true,
          autoplay: true,
          path: '/animation/loading_circle.json'
        });
      }
    },

    stopLoading() {
      if (this.animationInstance) {
        this.animationInstance.destroy();
        this.animationInstance = null;
      }
    },

    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    handlePaste(e) {
      e.preventDefault();
      const past = (e.clipboardData.getData('text/plain'));
      const char = past.replace(/\D/g, '').split('').splice(0, 6);
      char.forEach((char, index) => {
        this.$set(this.form.token, index, char);
      });

      const nextIndex = char.length < 6 ? char.length : 5; //index最多不大於5,因為驗證碼只有6碼
      this.focusInput(nextIndex);
    }
  },


  mounted() {
    this.focusInput(0);
   // 確定是否重新整理的提示
    window.addEventListener('beforeunload', (event) => {
      event.preventDefault();
      event.returnValue = '';
    });
  }
};
</script>
<style lang="scss" scoped>
@use "@/assets/style/abstracts" as *;

.green-bg {
  background-color: #4d7a67;
}

.verify-token {
  background-color: $clr-bg-soft-green;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  .verify-token__card {
    @include flex-card(min(28rem, 98%));
    position: relative;

    .card__header {
      padding-bottom: 1rem;
    }

    .card__info {
      margin: 1rem 0 2rem;
      display: flex;
      flex-direction: column;
      align-items: center;

      .card__info-text {
        font-size: 1.2rem;
        color: $clr-text-gray;
      }
    }

    //輸入欄
    .card__form {
      width: 100%;
      display: flex;
      flex-direction: column;
      //驗證碼
      .card__form-input-field {
        display: flex;
        justify-content: space-between;

        input {
          width: 3rem;
          height: 3rem;
          border: 1px solid #a8a8a8;
          border-radius: 4px;
          outline: none;
          text-align: center;
          font-size: 2rem;
          background-color: $clr-bg-light;

          &:disabled {
            cursor: not-allowed;
          }
        }

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
        visibility: hidden;

        &.show {
          visibility: visible;
        }
      }


      .card__form-resent-button {
        align-self: center;
        min-height: 1.3rem;
        margin: 1.4rem 0 1.4rem;
        border: none;
        background: transparent;
        cursor: pointer;
        color: $clr-text-gray;

        &:hover {
          border-bottom: 1px solid $clr-text-gray;
        }
      }

      .card__form-next-button {
        @include confirm-btn--green-round(100%);
      }
    }
  }
}


/* 針對 WebKit不要有箭頭按鈕*/
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* 針對 Firefox不要有箭頭按鈕 */
input[type="number"] {
  -moz-appearance: textfield;
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

  .animation {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 8rem;
  }
}

@media only screen and (max-width: 375px) {
  .verify-token__card {
    width: 100% !important;
    height: 100vh;
    padding: 4rem .8rem !important;
    border-radius: 0 !important;
  }
}


</style>