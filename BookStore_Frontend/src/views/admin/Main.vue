<template>
  <el-container class="app-container">
    <el-aside width="auto">
      <common-aside/>
    </el-aside>
    <el-container>
      <el-header>
        <common-header/>
      </el-header>
      <el-main class="main">
        <!-- Router -->
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import CommonAside from '../../components/admin/AdminCommonAside.vue';
import CommonHeader from '../../components/admin/AdminCommonHeader.vue';

export default {
  data() {
    return {};
  },
  components: {
    CommonAside,
    CommonHeader
  }
};
</script>

<style lang="scss" scoped>
.app-container {
  height: 100vh;
}
.el-header {
  padding: 0;
}

.el-main {
  background-color: #f6f6f6;
}
</style>
