
<template>
  <div id="Checkout">
    <header>
     <home-header/>
    </header>
   <main>
    <checkout/>
   </main>

  </div>
</template>
<script >
import HomeHeader from '@/components/user/MemberCenterHeader.vue';
import Checkout from '@/views/user/cart/Checkout.vue';
export default {
  components:{
    HomeHeader,
    Checkout,
  }
}
</script>

<style scoped lang="scss">

</style>