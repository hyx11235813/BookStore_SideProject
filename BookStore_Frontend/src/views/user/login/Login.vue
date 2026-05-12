<template>
  <div>
    <div class="desktop-header__dropdown-menu-login">
      <div class="login__wrap">
        <div class="login__card">
          <div class="login__card-title">
            <p class="desktop-header__category-title">會員登入</p>
          </div>
          <form class="login__card-container" @submit.prevent="submit">
            <div class="login__card-input">
              <input class="login__card-input-account" v-model="form.loginIdentifier" placeholder="帳號或電子郵件">
              <div class="login__card-input-password-wrapper">
                <input class="login__card-input-password"
                       v-bind:type="passwordVisible?'text':'password'"
                       v-model="form.password" type="password" placeholder="密碼">
                <img class="login__card-input-password-img"
                     @click="togglePasswordVisibility"
                     :src="passwordVisible? eyeCloseImg : eyeOpenImg"
                     alt="">
              </div>
            </div>

            <div class="login__card-checkbox-wrapper">
              <input class="login__card-checkbox-input"
                     type="checkbox" id="rememberMe"
                     v-bind:checked="rememberAccount"
                     @click="rememberMe">
              <label class="login__card-checkbox-label" for="rememberMe">記住帳號</label>
              <span class="error-msg" v-show="showLoginFalse">{{ errorMessage }}</span>
            </div>
            <button class="login__card-button--submit" type="submit" @click="submit" :disabled="isSubmitDisabled">登入
            </button>
            <div class="login__card-links">
              <router-link to="/forgot-password" class="link">忘記密碼？</router-link>
              <router-link to="/register" class="link">會員註冊</router-link>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {login} from '@/api/user';
import {addToShoppingCart, getUserCartInfo} from '/src/api/cart';
import eyeCloseImg from '@/assets/images/eye-hide.svg';
import eyeOpenImg from '@/assets/images/eye-show.svg';
export default {
  data() {
    return {
      eyeCloseImg,
      eyeOpenImg,
      form: {
        loginIdentifier: '',
        password: ''
      },
      passwordVisible: false,
      rememberAccount: false,
      showLoginFalse: false,
      errorMessage: ''
    };
  },
  computed: {
    isSubmitDisabled() {
      return this.form.loginIdentifier === '' || this.form.password === '';
    }
  },
  methods: {
    async submit() {
      try {
        const resp = await login(this.form);
        const {code, data} = resp;
        if (code === 401) return this.handleError('帳號或密碼錯誤');
        if (!data?.isActive) return this.handleError('請點擊郵件中的連結完成驗證');

        if (this.rememberAccount) {
          localStorage.setItem('remember-login', this.form.loginIdentifier);
        }

        const {token, role} = data;
        const route = role === 'ADMIN' ? '/admin' : '/';

        localStorage.setItem('token', JSON.stringify(token)); //存儲token，用於請求時放到header中，後端驗證權限
        await this.$router.push(route).catch(() => {});
        await this.getUserCartInfo();

      } catch (error) {
        console.error(error);
        this.handleError('登入失敗，請稍後再試');
      }

    },
    handleError(msg) {
      this.errorMessage = msg;
      this.showLoginFalse = true;
    },
    async getUserCartInfo() {
      const userId = this.$store.state.userData.user.id || '';
      if (!userId) return;
      const localStorageCart = localStorage.getItem('cart');
      const cart = localStorageCart ? JSON.parse(localStorageCart) : null;

      if (cart) { //登入後把storage的資料存到資料庫
        await this.setStorageToCart(userId, cart);
      }

      try {
        const resp = await getUserCartInfo(userId);
        const cartQuantity = resp.data.length;
        this.$store.commit('setCartProductQuantity', cartQuantity);

      } catch (err) {
        console.error('取得購物車資訊失敗:', err);
      }
    },

    async setStorageToCart(userId, cart) {
      const payload = Object.entries(cart).map(([bookId, quantity]) => ({
        userId: userId,
        bookId: bookId,
        quantity: quantity
      }));

      try {
        await addToShoppingCart(payload);
        localStorage.removeItem('cart');
      } catch (err) {
        console.error( err);
      }

    },
    rememberMe() {
      this.rememberAccount = !this.rememberAccount;
      if (this.rememberAccount) {
        localStorage.setItem('remember-login', this.form.loginIdentifier);
      } else {
        localStorage.removeItem('remember-login');
      }
    },
    togglePasswordVisibility() {
      this.passwordVisible = !this.passwordVisible;
    }
  },
  mounted() {
    const remembered = localStorage.getItem('remember-login');
    if (remembered) {
      this.form.loginIdentifier = remembered;
      this.rememberAccount = true;
    }

  }
};
</script>
<style lang="scss" scoped>
@use "@/assets/style/abstracts" as *;

.desktop-header__dropdown-menu-login {
  height: 100vh;

  .login__wrap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: $clr-bg-soft-green;

    .login__card {
      width: 480px;
      background-color: $clr-bg-light;
      border-radius: 10px;

      //標題
      .login__card-title {
        width: 100%;
        border-top-left-radius: inherit;
        border-top-right-radius: inherit;
        border-bottom: $clr-border-light;

        .desktop-header__category-title {
          text-align: center;
          font-size: 1.6rem;
          padding: 1.2rem 0 1rem;
        }
      }

      //內容
      .login__card-container {
        padding: 1rem 3rem;

        .login__card-input {
          display: flex;
          flex-direction: column;
          align-items: center;

          & > * {
            width: 100%;
            height: 2.5rem;
            margin: 1rem 0;
            background-color: $clr-bg-light;

          }

          //帳號
          .login__card-input-account {
            border: none;
            border-bottom: 1px solid black;
            outline: none;
            font-size: 1.1rem;

            &::placeholder {
              font-size: .9rem;
            }
          }

          //密碼
          .login__card-input-password-wrapper {
            display: flex;
            border-bottom: 1px solid black;

            .login__card-input-password {
              width: 100%;
              height: 100%;
              border: none;
              outline: none;
              font-size: 1.1rem;
              background-color: $clr-bg-light;

              &::placeholder {
                font-size: .9rem;
              }
            }

            .login__card-input-password-img {
              width: 2rem;
              cursor: pointer;
            }
          }
        }

        //記住帳號 //錯誤訊息
        .login__card-checkbox-wrapper {
          padding: 1rem 0 2rem;
          position: relative;

          .login__card-checkbox-input {
            color: $clr-bg-light;
            cursor: pointer;
          }

          .login__card-checkbox-label {
            cursor: pointer;
            padding-left: .2rem;
            color: $clr-text-gray;
            font-size: .9rem;
          }

          .error-msg {
            position: absolute;
            right: 0;
            color: $clr-danger;
            font-size: .9rem;
          }
        }

        //登入
        .login__card-button--submit {
          @include confirm-btn--green-round(100%);
          margin: 0 0 2rem;


        }

        //連結
        .login__card-links {
          display: flex;
          justify-content: space-between;

          .link {
            font-size: .9rem;
            color: $clr-text-gray;
            text-decoration: none;
          }
        }
      }
    }
  }
}

@media only screen and (max-width: 440px) {
  .login__card {
    margin-bottom: 8rem;
  }
  .card {
    width: 100vw !important;
  }
  .content {
    width: 100% !important;
  }
}

</style>