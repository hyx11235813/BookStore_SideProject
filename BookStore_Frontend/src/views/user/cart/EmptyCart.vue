
<template>
  <div class="cart__empty">
    <div class="cart__empty-wrapper">
      <img class="cart__empty-img" src="@/assets/images/empty-cart-icon.png" alt="">
      <p class="cart__empty-text">你的購物車還是空的</p>
      <router-link class="cart__empty-button" to="/">買點東西</router-link>
    </div>
  </div>
</template>
<style scoped lang="scss">
@use "src/assets/style/abstracts" as *;
.cart__empty {
  width: 100%;
  .cart__empty-wrapper {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    & > * {
      margin-bottom: 1rem;
    }

    .cart__empty-img {
      width: 5rem;
    }

    .cart__empty-text {
      color: $clr-text-gray;
      font-weight: 600;
    }

    .cart__empty-button {
      @include submit-btn--green;
      width: 10rem;
      font-size: 1.1rem;
      font-weight: 400;

    }
  }
}

</style>