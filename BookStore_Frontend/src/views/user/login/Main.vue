<template>
  <div>
    <!-- Router -->
    <home-header></home-header>
    <router-view></router-view>
  </div>
</template>
<script>
import HomeHeader from "@/components/user/HomeCommonHeader.vue"
import {defineComponent} from "vue";

export default defineComponent({
  components: {HomeHeader}
})

</script>
