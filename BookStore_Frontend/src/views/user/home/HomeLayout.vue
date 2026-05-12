<template>
  <div >
    <header>
      <home-header/>
    </header>
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>
<script>
import HomeHeader from "@/components/user/HomeCommonHeader.vue"

export default {
  components: {HomeHeader},
  name: 'MainView'
}
</script>
<style scoped lang="scss">

</style>