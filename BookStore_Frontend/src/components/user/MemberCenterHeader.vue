<template>
  <div class="desktop-header">
    <div class="desktop-header__container">
      <div class="header__container--left">
        <div>
          <router-link to="/home">
            <img class="header__logo-img" src="../../assets/images/website-logo.svg">
          </router-link>
        </div>
      </div>
      <div class="header__container--right">
        <img class="header__container-avatar" :src=avatar v-if="avatar" alt=' '>
        <img class="personal-info__table-avatar-img" v-else src="../../assets/images/circle-user-white.svg" alt=''>
        <el-dropdown @command="handleCommand">
                    <span class="header__userName" style="color:black">{{ userName }}<i
                        class="el-icon-arrow-down el-icon--right"></i></span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="personalInfo">我的帳號</el-dropdown-item>
            <el-dropdown-item command="logout">登出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>

</template>
<script>
export default {
  data() {
    return {
      userInfo: {
        userName: '',
        avatar: ''
      },
    }
  }, methods: {
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token')
        this.$router.push('/login')
      }
      if (command === 'personalInfo' && this.$route.path !== '/member-center/personal-info') {
        this.$router.push('/member-center/personal-info')
      }
    },

  }, computed: {
    avatar() {
      return this.$store.state.userData.user.avatar
    },
    userName() {
      return this.$store.state.userData.user.name
    }
  }, mounted() {

  }
}
</script>

<style lang="scss" scoped>
.desktop-header {
  height: 60px;
  width: 100%;
  display: flex;
  justify-content: center;

  .desktop-header__container {
    height: 100%;
    width: 98%;
    padding: 8px;
    display: flex;
    justify-content: space-between;

    .header__container--right {
      margin-right: 8px;
      display: flex;
      align-items: center;

      .header__container-avatar {
        height: 2.2rem;
        width: 2.2rem;
        border-radius: 50%;
      }

      .header__userName {
        margin-left: 8px;
        cursor: pointer;

      }
    }

    .header__logo-img {
      height: 100%;
      width: 3rem;
    }
  }
}

</style>