<template>
  <div class="container">
    <div >
      <header >
        <LoginCommonHeader/>
      </header>
      <main >
        <!-- Router -->
        <router-view></router-view>
      </main>
    </div>
    <!--        <div id="animation-container"></div>-->
  </div>
</template>
<script>
import LoginCommonHeader from '@/components/user/LoginCommonHeader.vue'
export default {
  data() {
    return {};
  },
  components: {
    LoginCommonHeader
  }
}
</script>
<style lang="scss" scoped>
@use "src/assets/style/abstracts" as *;
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  background-color: $clr-bg-soft-green;
}

</style>