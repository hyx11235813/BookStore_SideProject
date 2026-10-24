<template>
  <div>
    <MobileMenu></MobileMenu>
    <DesktopMenu></DesktopMenu>
  </div>
</template>
<script>
import MobileMenu from '@/components/user/MobileMenu.vue';
import DesktopMenu from '@/components/user/DesktopMenu.vue';
import {guestCart} from '@/utils/storage';

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
      this.$store.commit('setCartProductQuantity', guestCart.count());
    }
  }

};
</script>

<style scoped lang="scss">
@use "@/assets/style/abstracts" as *;

</style>
