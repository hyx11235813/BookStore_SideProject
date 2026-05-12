<template>
  <el-menu :default-active="activeMenu"
           class="el-menu"
           @select="handleSelect"
           :collapse="isCollapse"
           background-color="#717171"
           text-color="#fff"
           active-text-color="#ffd04b"
  >
    <h3>{{ isCollapse ? "後臺" : "後臺管理" }}</h3>
    <el-menu-item v-for="(item, index) in menu" :key="index" :index="item.name">
      <router-link :to="item.path" tag="div">
        <i :class="'el-icon-' + item.icon"></i>
        <span class="link">{{ item.label }}</span>
      </router-link>
    </el-menu-item>
  </el-menu>
</template>

<style>
.el-menu:not(.el-menu--collapse) {
  width: 200px;
}

.el-menu {
  height: 100%;

  .el-menu-item {
    font-size: 16px;
    transition: font-size 0.3s ease;
  }

  .el-menu-item:hover {
    font-size: 20px
  }

  h3 {
    text-align: center;
    font-size: 20px;
    color: white;
    font-weight: 400;
    margin: 10px 0 10px;
  }
}
</style>

<script>
export default {
  data() {
    return {
      activeMenu: '',
      menuData: [
        {
          path: "/admin",
          name: "adminHomePage",
          label: "數據表",
          icon: "s-data",
        },
        {
          path: "/admin/book",
          name: "adminBookPage",
          label: "書籍管理",
          icon: "notebook-2",
        },
        {
          path: "/admin/deletedBook",
          name: "adminDeletedBookPage",
          label: "下架書籍管理",
          icon: "delete"
        },
        {
          path: "/admin/user",
          name: "adminUserPage",
          label: "用戶管理",
          icon: "user-solid",
        },

      ]
    }
  },
  methods: {
    handleSelect(key) {
      this.activeMenu = key
    },
  },
  computed: {
    menu() {
      return this.menuData
    },
    isCollapse() {
      return this.$store.state.tab.isCollapse
    }
  },
  watch: {
    $route(to) {
      this.activeMenu = to.name
    }
  },
  mounted() {
    this.activeMenu = this.$route.name
  },
}
</script>
