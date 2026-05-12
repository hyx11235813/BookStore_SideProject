<template>
  <div>
    <MobileMenu></MobileMenu>
    <DesktopMenu></DesktopMenu>
  </div>
</template>
<script>
import MobileMenu from '@/components/user/MobileMenu.vue';
import DesktopMenu from '@/components/user/DesktopMenu.vue';

export default {
  components: {
    DesktopMenu,
    MobileMenu
  },
  created() {
    this.countCartQuantity();
  },
  methods: {
    countCartQuantity() {
      let cart = JSON.parse(localStorage.getItem('cart')) || {};
      let cartQuantity = Object.keys(cart).length;
      this.$store.commit('setCartProductQuantity', cartQuantity);
    }
  }

};
</script>

<style scoped lang="scss">
@use "@/assets/style/abstracts" as *;

</style>
