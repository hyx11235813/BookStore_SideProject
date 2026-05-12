<template>
  <div>
    <div class="verify">
      <div class="verify__card">
        <router-link  class=" verify__card-return" :to="{name:'memberCenter'}">
          <img  src="@/assets/images/arrow-left.svg" alt="" style="width: 1rem">
          返回
        </router-link >
        <div class="verify__card-left">
          <div class="verify__card-user-info-wrapper">
            <img class="verify__card-user-avatar" v-if="userAvatar" :src="userAvatar" alt="">
            <img class="verify__card-user-avatar" v-else src="@/assets/images/circle-user-white.svg" alt=''>
            <div class="verify__card-user-name"> Name</div>
          </div>
        </div>
        <div class="verify__card-right">
          <div class="verify__card-form-title">如要繼續操作，請先驗證您的身分</div>
          <div class="verify__card-form-input-wrapper" :class="{'error':showHint}">
            <input class="verify__card-form-input" v-model="password" ref="verify__card-form-input"
                   :type="isPasswordVisible?'text' : 'password'" placeholder="">
            <label class="verify__card-form-label" :class="{'error':showHint}">請輸入您的密碼</label>
            <img class="verify__card-form-img"
                 :src="isPasswordVisible ? eyeCloseImg:eyeOpenImg"
                 @click="togglePasswordVisibility"
                 alt="">
          </div>
          <p class="verify__hint" :class="{'show': showHint}">
            密碼錯誤，請再試一次，或點擊「忘記密碼」以重設密碼。</p>
        </div>
        <div class="verify__buttons">
          <button class="verify__buttons--forgot-password" @click="forgotPassword()">忘記密碼？</button>
          <button class="verify__buttons--next-step" @click="submit">下一步</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {matchPassword} from '@/api/verification';
import eyeCloseImg from '@/assets/images/eye-hide.svg';
import eyeOpenImg from '@/assets/images/eye-show.svg';

export default {
  data() {
    return {
      userAvatar: '',
      password: '',
      showHint: false,
      isPasswordVisible: false,
      eyeCloseImg,
      eyeOpenImg
    };
  },
  methods: {
    async submit() {
      try {
        const isMatched = await matchPassword({password: this.password});
        this.showHint = false;
        if (isMatched) {
          const target = this.$route.query.target || '/';
          this.$router.push(target);
          return;
        }
        this.password = '';
        this.showHint = true;
        this.$refs['verify__card-form-input'].focus();
        this.showErrorMessage();
      } catch (error) {
        console.log(error);
      }
    },
    forgotPassword() {
      this.$router.push('/forgot-password/reset-password');
    },
    togglePasswordVisibility() {
      this.isPasswordVisible = !this.isPasswordVisible;
    }
  },
  mounted() {
    this.userAvatar = this.$store.state.userData.user.avatar;

  }
};
</script>
<style scoped lang="scss">

@import "@/assets/style/abstracts";
//grid-area
.verify__card-return {
  grid-area: return;
  display: flex;
  margin-bottom: 2rem;
  text-decoration: none;
  justify-self: start;
  color: #000000;
}

.verify__card-left {
  grid-area: userInfo;
}

.verify__card-right {
  grid-area: form;
}

.verify__buttons {
  grid-area: btn;
}

.verify {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: $clr-bg-light;

  .verify__card {
    width: 55vw;
    background-color: white;
    padding: 36px 36px 36px;
    border-radius: 26px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-areas:
        "return ."
        "userInfo form"
        "btn btn";

    .verify__card-left {

      .verify__card-user-info-wrapper {
        display: flex;
        flex-direction: column;

        .verify__card-user-avatar {
          width: 3rem;
          height: 3rem;
          border-radius: 50%;
          margin-bottom: .8rem;
        }

        .verify__card-user-name {
          font-size: 2rem;
        }
      }
    }

    .verify__card-right {
      .verify__card-form-title {
        padding-bottom: 2rem;
        font-size: 14px;
        left: 0;

      }

      .verify__card-form-input-wrapper {
        display: flex;
        height: 3rem;
        position: relative;
        border: 1px solid black;
        border-radius: 4px;
        padding-right: 1rem;

        &.error {
          border: 1px solid #be0000;
        }

        .verify__card-form-label {
          position: absolute;
          left: 1rem;
          top: .6rem;
          padding: 0 .3rem;
          background-color: white;
          pointer-events: none;
          transition: 100ms;

          &.error {
            color: #be0000;
          }
        }

        .verify__card-form-input {
          width: 100%;
          height: 100%;
          padding-left: 1rem;
          border: none;
          outline: none;
          border-radius: inherit; //避免的 border缺角

          &:focus + .verify__card-form-label {
            top: -.8rem;
            left: .3rem;
            transform: scale(.9);
          }
        }

        .verify__card-form-img {
          width: 2rem;
          cursor: pointer;
        }

        //有輸入内容，且離開聚焦，label不回到原位
        .verify__card-form-input:not(:placeholder-shown).verify__card-form-input:not(:focus) + .verify__card-form-label {
          top: -.8rem;
          left: .3rem;
          transform: scale(.9);
        }
      }
    }

    .verify__hint {
      margin-top: .5rem;
      margin-left: .4rem;
      font-size: .8rem;
      color: #be0000;
      min-height: 1.5em;
      visibility: hidden;

      &.show {
        visibility: visible;
      }
    }

    .verify__buttons {
      padding-top: 5rem;
      display: flex;
      justify-content: flex-end;

      .verify__buttons--forgot-password {
        @include cancel-btn--transparent(6rem);
      }

      .verify__buttons--next-step {
        @include confirm-btn--green-round(6rem);
        margin-left: 2rem;
      }
    }
  }
}

@media only screen and (max-width: 900px) {
  .verify__card {
    grid-template-columns: 1fr !important;
    grid-template-areas:
        "return"
        "userInfo"
        "form"
        "btn" !important;
  }
  .verify__buttons--forgot-password {
    margin-right: auto;
  }
}

@media only screen and (max-width: 600px) {
  .verify__card {
    width: 100vw !important;
    height: 100vh;
    border-radius: 0 !important;
    grid-template-rows: 9rem 6rem;
    padding: 5rem 1.2rem !important;
  }
.verify__card-return{
  display: flex;
  align-items: center;
}

}
</style>