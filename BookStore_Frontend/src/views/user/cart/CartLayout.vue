<template>
  <div id="cart">
    <header>
      <HomeHeader/>
    </header>
    <main>
      <CartSmallScreen class="cart_Sm_Screen" v-if="isSmallView"/>
      <CartLargeScreen class="cart_Lg_Screen" v-else/>
    </main>
  </div>
</template>

<script>
import HomeHeader from '@/components/user/HomeCommonHeader.vue';
import CartLargeScreen from '@/views/user/cart/CartLargeScreen.vue';
import CartSmallScreen from '@/views/user/cart/CartSmallScreen.vue';

export default {
  components: {CartLargeScreen, CartSmallScreen, HomeHeader},
  data() {
    return {
      isSmallView: window.innerWidth <= 576
    };
  },
  methods: {
    updateView() {
      this.isSmallView = window.innerWidth <= 576;
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateView);
  },
  mounted() {
    window.addEventListener('resize', this.updateView);
  }
};
</script>
<style scoped lang="scss">


#cart {
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  header {
    position: sticky;
    top: 0;
    z-index: 1000;
    flex: 0 0 auto;
  }

  main {
    flex: 1;
    background-color: #f5f5f5;
  }
}


@media screen and (min-width: 576px) {

}

@media screen and (max-width: 576px) {


}
</style>