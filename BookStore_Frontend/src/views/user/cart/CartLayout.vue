<template>
  <div id="cart">
    <header>
      <HomeHeader/>
    </header>
    <main>
      <Cart_Sm_Screen class="cart_Sm_Screen" v-if="isSmallView"/>
      <Cart_Lg_Screen class="cart_Lg_Screen" v-else/>
    </main>
  </div>
</template>

<script>
import HomeHeader from '@/components/user/HomeCommonHeader.vue';
import Cart_Lg_Screen from '@/views/user/cart/Cart_Lg_Screen.vue';
import Cart_Sm_Screen from '@/views/user/cart/Cart_Sm_Screen.vue';

export default {
  components: {Cart_Lg_Screen, Cart_Sm_Screen, HomeHeader},
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