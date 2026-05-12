<template>
  <div class="desktop-header">
    <div class="header-container">
      <div>
        <router-link to="/home"><img class="header__logo" src="../../assets/images/website-logo.svg"></router-link>
      </div>
      <div class="header__login">
        <span class="header__login-text">已經有帳號了?</span>
        <a class="header__login-link " @click="toLogin">
          登入 →
        </a>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  methods: {
    toLogin() {
      this.$router.push('/login')
    }
  }
}
</script>
<style lang="scss" scoped>
.desktop-header {
  width: 100%;
  z-index: 9999;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.6rem 3rem 1rem;

    .header__logo {
      height: 100%;
      width: 2.4rem;
    }

    .header__login {
      .header__login-text {
        color: #000000;
        margin-right: 6px;
      }

      .header__login-link {
        cursor: pointer;
      }
    }
  }
}

@media only screen and (max-width: 768px) {
  .header-container {
    flex-direction: column;
    align-items: start !important;
    padding: 1.6rem 1rem 1rem !important;

    .header__login {
      padding-top: 1rem;
    }
  }
}
</style>