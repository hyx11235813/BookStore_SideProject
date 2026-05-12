<template>
  <div class="headerContainer">
    <div class="l-contain">
      <el-button @click="handleMenu" icon="el-icon-menu"></el-button>
      <div class="breadcrumb" v-show="showBreadcrumb">
        <router-link v-for="(item,index) in breadcrumbList" :key="index" :to="item.path">
          {{ item.label }}
        </router-link>
      </div>
    </div>
    <div class="r-contain">
      <el-avatar size="medium" :src=changeAvatar style="border-radius: 50%;"></el-avatar>
      <el-dropdown @command="handleCommand">
        <span style="color:black">
          {{ userInfo.userName }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu>
          <el-dropdown-item command="memberCenter">會員中心</el-dropdown-item>
          <el-dropdown-item command="logout">登出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import {getUserById} from '@/api/admin';
export default {
  data() {
    return {
      showBreadcrumb: false,
      userInfo: {
        id: '',
        userName: '',
        role: '',
        lastLoginDate: ''
      }
    }
  },
  computed: {
    changeAvatar() {
      return this.$store.state.userData?.user?.avatar || '';
    },
    breadcrumbList() {
      const routeMap = {
        '/admin': '數據表',
        '/admin/book': '書籍管理',
        '/admin/deletedBook': '下架書籍管理',
        '/admin/user': '用戶管理',
      }
      return Object.keys(routeMap).map(path => ({path, label: routeMap[path]}))
    },
    userId() {
      return this.$store.state.userData?.user?.id || null;
    },
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token')
        this.$router.push('/login')
      }
      if (command === 'memberCenter') {
        this.$router.push('/member-center')
      }
    },
    //側邊menu控制
    handleMenu() {
      this.$store.commit("collapseMenu")
      this.showBreadcrumb = !this.showBreadcrumb
    },
    async fetchUserData() {
      if (!this.userId) return
      try {
        const resp = await getUserById(this.userId);
        if (resp.code !== 200) return;
        const {name, role, lastLoginDate} = resp.data;
        this.userInfo.userName = name || '未設定名稱';
        this.userInfo.role = role || '';
        // 最後登入時間,如果是1970則代表沒有登入過
        this.userInfo.lastLoginDate = new Date(lastLoginDate).toLocaleString().startsWith('1970') ? '' : new Date(lastLoginDate).toLocaleString();
      } catch (error) {
        console.error('獲取用戶資訊失敗:', error);
      }
    }
  },
  watch: {
    userId: 'fetchUserData',
  },
  mounted() {
    this.fetchUserData()
  },

}
</script>

<style lang="scss" scoped>
.headerContainer {
  background-color: #f6f6f6;
  height: 60px;
  padding: 0;
  display: flex;
  justify-content: space-between;

}

.l-contain {
  display: flex;
  align-items: center;
  margin-left: 20px;


  .breadcrumb {
    a {
      margin: 0 10px 0;
      color: black;
      font-size: 18px;
      text-decoration: none;
    }


    .router-link-exact-active {
      color: #dfa500;
    }
  }

}

.r-contain {
  display: flex;
  align-items: center;
  margin-right: 20px;

  span {
    color: white;
    font-size: 18px;
    margin-left: 10px;
    cursor: pointer;
  }

  .el-icon-user {
    font-size: 24px;
    color: white;
  }
}

a {
  text-decoration: none;
}
</style>
