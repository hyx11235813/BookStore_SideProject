<template>
  <div class="desktop-header__cart">
    <div class="cart__container">
      <div class="cart__product" v-if="products.length!==0">
        <div class="cart__product-container" v-for="(product,index) in products" :id="product.id">
          <div class="cart__product__wrapper">
            <div class="checkbox-wrapper">
              <label class=" checkbox-label">
                <input type="checkbox" v-model="product.selected">
              </label>
            </div>

            <a class="cart__product-bookCover-a" href="#">
              <img class="cart__product-bookCover-img" :src="'data:image/jpeg;base64,' + product.bookCoverByte" alt=" "
                   @click="generateBookUrl(product)" @error="onImageError">
            </a>
            <div class="cart__products-info">
              <a class="cart__products-bookName-text " @click="generateBookUrl(product)">{{ product.bookName }}
              </a>
              <p class="cart__products-author-text">作者：{{ product.author }}</p>
              <div class="cart__product-quantity">
                <div class="cart__product-quantity-wrapper">
                  <div class="cart__product-price">
                    <p>${{ product.price }}</p>
                  </div>
                  <div class="cart__product-quantity__button-wrapper">
                    <button class="cart__product-quantity-spin-button" @click="decreaseQuantity(index)">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#000000"
                           viewBox="0 0 256 256">
                        <path d="M222,128a6,6,0,0,1-6,6H40a6,6,0,0,1,0-12H216A6,6,0,0,1,222,128Z"></path>
                      </svg>
                    </button>
                    <input class="cart__product-quantity-spin-number" ref="quantity" v-model="product.orderQuantity"
                           type="text"
                           inputmode="numeric" pattern="[0-9]+">
                    <button class="cart__product-quantity-spin-button" @click="increaseQuantity(index)">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#000000"
                           viewBox="0 0 256 256">
                        <path
                            d="M222,128a6,6,0,0,1-6,6H134v82a6,6,0,0,1-12,0V134H40a6,6,0,0,1,0-12h82V40a6,6,0,0,1,12,0v82h82A6,6,0,0,1,222,128Z"></path>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="summarize">
          <div class="summarize__container">
            <div class="summarize__select">
              <input class="summarize__select-checkbox" type="checkbox" id="_selectAll" v-model="selectedAll"
                     @change="toggleSelectAll">
              <label class="summarize__select-text" for="_selectAll">全選</label>
            </div>

            <div class="summarize__checkout">
              <div class="summarize__checkout-price">總金額
                <p class=" summarize__checkout-count-text" v-show="selectedProductCount">（{{
                    selectedProductCount
                  }}）：</p>
                <p class="summarize__checkout-price-text">${{ totalPrice }}</p>
              </div>
              <div class="summarize__checkout-button" @click="goToCheckout">結 賬</div>
            </div>
          </div>
        </div>
      </div>
      <empty-cart v-else></empty-cart>
    </div>
    <div class="modal" v-if="showInfo">
      <div class="info__exceed-quantity">
        <p class="info__exceed-quantity-text">{{ message }}
        </p>
      </div>
    </div>
    <MessagePopup :message="popupMessage" :isVisible="popupVisible" :type="popupType"></MessagePopup>
  </div>
</template>
<script>
import cartUtils from './cartUtils.js';
import EmptyCart from "@/views/user/cart/EmptyCart.vue";
import {messagePopupMixin} from '@/mixins/messagePopupMixin';
import MessagePopup from "@/components/MessagePopup.vue";

export default {
  mixins: [cartUtils, messagePopupMixin],
  components: {EmptyCart, MessagePopup},
  methods: {
    onImageError() {
      event.target.src = require('@/assets/images/no-image.svg');
    },
    goToCheckout() {
      const selected = this.products.filter(product => product.selected)
      if (selected.length == 0) {
        this.showPopupMessage('請先選擇商品')
        return
      }
      const checkoutPayload = selected.map(product => ({
        id: product.id,
        quantity: product.orderQuantity
      }))
      sessionStorage.setItem('checkout_items', JSON.stringify(checkoutPayload))
      this.$router.push('/checkout')
    }
  }
};

</script>
<style scoped lang="scss">
@use "src/assets/style/abstracts" as *;


.desktop-header__cart {
  height: 100%;
  padding-top: 4rem;
  background-color: #f5f5f5;

  .cart__container {
    width: 100vw;
    height: 100%;
  }
}

.cart__product__wrapper {

  display: flex;
  padding: 10px;
  margin: 0 10px 10px;
  background-color: #ffffff;
  border-radius: 5px;

  .checkbox-wrapper {
    align-self: center;

  }

  .cart__product-checkbox {
    width: 22px;
    height: 22px;
    align-self: center;
    border-color: red;

    &:checked {
      background-color: red;
    }
  }

  .cart__product-bookCover-a {
    min-width: 5rem;
    width: 5rem;
    margin-inline: 10px;
    border: 1px solid #d2d2d2;
    border-radius: 4px;

    .cart__product-bookCover-img {
      width: 100%;
      height: 100%;
    }
  }

  .cart__products-info {
    width: 100%;
    display: flex;
    flex-direction: column;
    height: 7rem;

    .cart__products-bookName-text {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      font-size: 14px;
      margin-bottom: 4px;
    }

    .cart__products-author-text {
      font-size: 12px;
    }

  }

  .cart__product-quantity {
    margin-top: auto;

    .cart__product-quantity-wrapper {
      display: flex;

      .cart__product-price {
        color: #ee4d2d;
      }

      .cart__product-quantity__button-wrapper {
        display: flex;
        height: 26px;
        margin-left: auto;
        border: 1px solid #bbbbbb;
        border-radius: 4px;

        .cart__product-quantity-spin-button {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 24px;
          border: none;
          cursor: pointer;
          background-color: #ffffff;
          border-radius: 4px;
        }

        .cart__product-quantity-spin-number {
          width: 42px;
          border: none;
          border-left: 1px solid #bbbbbb;
          border-right: 1px solid #bbbbbb;
          outline: none;
          text-align: center;
          //z-index: 1;

          &:focus {
            border: 1px solid black;
            box-shadow: 0 0 0 2px #fff, 0 0 0 3px #000;
          }
        }
      }
    }
  }
}

//結算
.summarize {
  width: 100%;
  height: 4rem;
  max-width: 1150px;
  position: fixed;
  bottom: 0;
  background-color: #ffffff;

  .summarize__container {
    display: flex;
    height: 100%;
    padding: 10px;
    justify-content: space-between;
    align-items: center;


    .summarize__select {
      display: flex;

      .summarize__select-checkbox {
        margin-right: 6px;
        cursor: pointer;

      }

      .summarize__select-text {
        cursor: pointer;

      }
    }


    .summarize__checkout {
      display: flex;
      height: 100%;

      .summarize__checkout-price {
        display: flex;
        align-items: center;
        margin-right: 1rem;
        font-size: .9rem;

        .summarize__checkout-price-text {
          font-size: 1.5rem;
          color: #b41444;
          margin-left: .5rem;
        }
      }

      .summarize__checkout-button {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 5rem;
        text-align: center;
        border-radius: 4px;
        background-color: #599178;
        color: #ffffff;
        cursor: pointer;

        &:hover {
          background-color: #649d84;
        }
      }
    }
  }
}

.modal {
  position: fixed;
  top: 0;
  width: 100vw;
  height: 100vh;
  z-index: 999;

  .info__exceed-quantity {
    display: flex;
    align-items: center;
    position: fixed;
    height: 50px;
    top: 20%;
    left: 50%;
    border-radius: 4px;
    transform: translateX(-50%);
    background-color: #494949;
    padding: 10px 30px;

    .info__exceed-quantity-text {
      display: flex;
      color: #f5f5f5;

    }
  }
}

</style>