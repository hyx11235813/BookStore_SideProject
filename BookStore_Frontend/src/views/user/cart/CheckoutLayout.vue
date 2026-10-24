
<template>
  <div id="Checkout">
    <header>
     <home-header/>
    </header>
   <main>
    <checkout-view/>
   </main>

  </div>
</template>
<script >
import HomeHeader from '@/components/user/MemberCenterHeader.vue';
import CheckoutView from '@/views/user/cart/CheckoutView.vue';
export default {
  components:{
    HomeHeader,
    CheckoutView,
  }
}
</script>

<style scoped lang="scss">

</style>