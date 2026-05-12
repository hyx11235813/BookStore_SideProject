<template>
  <div class="change-password">
    <Header/>
    <div class="change-password__return-container">
      <div class="change-password__return-button-wrapper">
         <span class="change-password__return-button" @click="$router.back()">
              <img class="change-password__return-button-img" src="@/assets/images/arrow-left.svg" alt="">
              <h1 class="change-password__return-button-text">返回</h1>
         </span>
      </div>
    </div>
    <main class="change-password__main">
      <form class="change-password__form">
        <div class="change-password__input-wrapper">
          <div :class="['change-password__input-outer',{error:newPasswordError} ]">
            <input class="change-password__input" v-model="form.newPassword"
                   :type="isPasswordVisible?'text' : 'password'"
                   @blur="checkNewPasswordBlur"
                   @input="enableLiveCheck ? checkNewPassword() : null"
                   placeholder="">
            <label :class="['password-input__label',{error:newPasswordError}]">新密碼</label>
            <img src="@/assets/images/eye-hide.svg" :src="isPasswordVisible ? eyeCloseImg:eyeOpenImg"
                 @click="togglePasswordVisibility('isPasswordVisible')"
                 alt="">
          </div>
          <p :class="['change-password__error-hint',{show:newPasswordError}]">密碼格式錯誤</p>

          <div :class="['change-password__input-outer',{error:confirmPasswordError} ]">
            <input class="change-password__input" v-model="form.confirmPassword"
                   :type="isConfirmPasswordVisible?'text' : 'password'"
                   placeholder="">
            <label :class="['password-input__label',{error:confirmPasswordError}]">確認新密碼</label>
            <img :src="isConfirmPasswordVisible ? eyeCloseImg:eyeOpenImg" class="confirm-password-img"
                 @click="togglePasswordVisibility('isConfirmPasswordVisible')"
                 alt="">
          </div>
          <p :class="['change-password__error-hint',{show:confirmPasswordError}]">
            密碼不符</p>
        </div>

        <div class="change-password__input-hint">
          <p>密碼需包含字母和數字，且長度為8~16個字</p>
        </div>

        <div class="change-password__button-wrapper">
          <button class="change-password__submit" @click.prevent="submit">變更密碼</button>
        </div>
      </form>
    </main>
    <MessagePopup :message="popupMessage" :isVisible="popupVisible" :type="popupType"></MessagePopup>
  </div>
</template>
<script>
import Header from '@/components/user/MemberCenterHeader.vue';
import {messagePopupMixin} from '@/mixins/messagePopupMixin';
import MessagePopup from '@/components/MessagePopup.vue';
import eyeCloseImg from '@/assets/images/eye-hide.svg';
import eyeOpenImg from '@/assets/images/eye-show.svg';
import {changePassword} from '@/api/user';

export default {
  components: {MessagePopup, Header},
  mixins: [messagePopupMixin],
  data() {
    return {
      form: {
        newPassword: '',
        confirmPassword: ''
      },
      eyeCloseImg,
      eyeOpenImg,
      isPasswordVisible: false,
      isConfirmPasswordVisible: false,
      newPasswordError: false,
      confirmPasswordError: false,
      showSuccessHint: false,
      enableLiveCheck: false //為true時，改為透過@input來驗證密碼格式
    };
  },
  methods: {
    async submit() {
      this.checkConfirmPasswordMatch();
      if (!this.isFormValid()) return;

      let payload = {
        id: this.$store.state.userData.user.id,
        password: this.form.confirmPassword
      };

      await this.changePassword(payload);
    },
    checkConfirmPasswordMatch() {
      this.confirmPasswordError = this.form.confirmPassword !== this.form.newPassword;
    },
    async changePassword(payload) {
      try {
        const resp = await changePassword(payload);
        if (resp.code !== 200) {
          this.showErrorMessage();
          return;
        }
        this.$router.push({
          path: '/member-center/personal-info',
          query: {
            popupMessage: '密碼修改成功',
            popupType: 'info'
          }
        });
      } catch (error) {
        console.error(error);
        this.showErrorMessage();
      }
    },
    isFormValid() {
      this.checkNewPassword();
      this.checkConfirmPasswordMatch();
      return !(this.newPasswordError || this.confirmPasswordError);
    },
    togglePasswordVisibility(field) {
      this[field] = !this[field];
    },

    checkNewPasswordBlur() {
      this.checkNewPassword();
      if (this.newPasswordError) {  //@blue後如果驗證格式錯誤，才打開@input驗證，避免一開始輸入就顯示錯誤
        this.enableLiveCheck = true;
      }
    },
    checkNewPassword() {
      const reg = /^(?=.*[0-9])(?=.*[a-zA-Z])(?!.* ).{8,16}$/;
      this.newPasswordError = !reg.test(this.form.newPassword);
    }
  }
};


</script>

<style scoped lang="scss">
@use "@/assets/style/abstracts" as * ;

.change-password__return-container {
  height: 4rem;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #c9c9c9;

  .change-password__return-button-wrapper {
    width: 840px;
    display: flex;
    margin: auto;

    .change-password__return-button {
      margin-left: 1rem;
      cursor: pointer;

      .change-password__return-button-img {
        transition: transform 200ms ease-out;
      }

      &:hover > img {
        transform: translateX(-.5rem);
      }

      .change-password__return-button-text {
        font-weight: 400;
        display: inline;
      }
    }
  }
}


//表單
.change-password__main {
  display: flex;
  justify-content: center;
  max-width: 840px;
  margin: 4rem auto 0;

  .change-password__form {
    height: 100%;
    width: min(550px, 90%);
    display: flex;
    flex-direction: column;
    padding: 0 2rem;
    border: 1px solid #c9c9c9;
    border-radius: 10px;

    .change-password__input-wrapper {
      .change-password__input-outer {
        width: 100%;
        height: 3rem;
        position: relative;
        margin-top: 2.1rem;
        display: flex;
        outline: none;
        border: 1px solid  #c9c9c9;
        border-radius: 4px;
        padding-right: 1rem;

        &.error {
          border: 1px solid $clr-danger;
        }

        & > img {
          cursor: pointer;
          width: 2rem;
        }

        .password-input__label {
          position: absolute;
          left: 1rem;
          top: .7rem;
          background-color: white;
          transition: 100ms;
          padding: 0 .3rem;
          pointer-events: none;

          &.error {
            color: $clr-danger;
          }
        }

        & > .change-password__input {
          width: 100%;
          height: 100%;
          border-radius: 3px;
          padding-left: 1rem;
          outline: none;
          border: none;
          font-size: 1rem;

        }

      }

      .change-password__input:not(:placeholder-shown).change-password__input:not(:focus) + .password-input__label {
        top: -.7rem;
        left: .4rem;
        scale: (.85);
      }

      .change-password__input:focus {
        & + label {
          top: -.7rem;
          left: .4rem;
          scale: (.85);
        }
      }
    }

    .change-password__error-hint {
      padding: .3rem 1rem 0;
      font-size: .8rem;
      color: $clr-danger;
      visibility: hidden;

      &.show {
        visibility: visible;
      }
    }
  }

  .change-password__input-hint {
    padding-top: 2rem;

    p {
      font-size: .9rem;
      color: rgb(95, 99, 104);
    }
  }

  .change-password__button-wrapper {
    display: flex;
    justify-content: flex-end;
    padding-bottom: 2rem;

    .change-password__submit {
      @include confirm-btn--green-round(7rem);
      margin-top: 2rem;
    }
  }
}

@media only screen and (max-width: 768px) {
  .change-password__return-container {
    border-bottom: none;

    .change-password__return-button-wrapper {
      h1 {
        font-size: 1.5rem;
      }

      img {
        scale: 0.7;
      }
    }
  }
}

.errorText {
  color: $clr-danger !important;
}

.errorBorder {
  border: 1px solid #c41b1b !important;
}

@media only screen and (max-width: 400px) {
  .change-password__form {
    padding: 0 !important;
    border: none !important;

    .change-password__input-hint {
      p {
        font-size: .8rem;
      }
    }
  }
}
</style>


