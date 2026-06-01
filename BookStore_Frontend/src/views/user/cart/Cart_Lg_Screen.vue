<template>
  <div class="desktop-header__cart">
    <div class="cart__container">
      <div class="cart_table" v-if="products.length!==0">
        <div class="cart__title">
          <div class="cart__title-wrapper">
            <input class="cart__title-checkbox" type="checkbox" id="selectAll" v-model="selectedAll"
                   @change="toggleSelectAll">
            <label class="cart__title-text cart__title-product" for="selectAll">商品</label>
            <div class="cart__title-text cart__title-price">單價</div>
            <div class="cart__title-text cart__title-count">數量</div>
            <div class="cart__title-text cart__title-total">總計</div>
          </div>
        </div>
        <div class="cart__product">
          <div class="cart__product-container" v-for="(product,index) in products" :id="product.id">
            <div class="cart__product__wrapper">
              <input class="cart__product-checkbox" type="checkbox" v-model="product.selected">
              <div class="cart__product-title">
                <a class="cart__product-a">
                  <img class="cart__product-bookCover" :src="'data:image/jpeg;base64,' + product.bookCoverByte" alt=" "
                       @error="onImageError" @click="generateBookUrl(product)">
                </a>
                <div class="cart__products-bookName">
                  <a class="cart__products-bookName-text " @click="generateBookUrl(product)">{{ product.bookName }}
                  </a>
                  <p class="cart__products-author-text">作者：{{ product.author }}</p>
                </div>
              </div>

              <div class="cart__product-price">
                <p>${{ product.price }}</p>
              </div>

              <div class="cart__product-quantity">
                <div class="cart__product-quantity-button-wrapper">
                  <button class="cart__product-quantity-spin-button" @click="decreaseQuantityThrottle(index) ">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#000000" viewBox="0 0 256 256">
                      <path d="M222,128a6,6,0,0,1-6,6H40a6,6,0,0,1,0-12H216A6,6,0,0,1,222,128Z"></path>
                    </svg>
                  </button>
                  <input class="cart__product-quantity-spin-number" v-model="product.orderQuantity" type="text"
                         inputmode="numeric" pattern="[0-9]+">
                  <button class="cart__product-quantity-spin-button" @click="increaseQuantity(index) ">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#000000" viewBox="0 0 256 256">
                      <path
                          d="M222,128a6,6,0,0,1-6,6H134v82a6,6,0,0,1-12,0V134H40a6,6,0,0,1,0-12h82V40a6,6,0,0,1,12,0v82h82A6,6,0,0,1,222,128Z"></path>
                    </svg>
                  </button>
                </div>
              </div>
              <div class="cart__product-sum">
                <p>${{ product.price * product.orderQuantity || 0 }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="summarize">
          <div class="summarize__container">
            <div class="summarize__select">
              <input class="summarize__select-checkbox" type="checkbox" id="selectAll" v-model="selectedAll"
                     @change="toggleSelectAll">
              <label class="summarize__select-text" for="selectAll">全選</label>
            </div>
            <div class="summarize__checkout">
              <div class="summarize__checkout-price">總金額
                <p class=" summarize__checkout-count-text" v-show="selectedProductCount">（{{
                    selectedProductCount
                  }}個商品）：</p>
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
import EmptyCart from '@/views/user/cart/EmptyCart.vue';
import {messagePopupMixin} from '@/mixins/messagePopupMixin';
import MessagePopup from "@/components/MessagePopup.vue";

export default {
  data() {
    return {}
  },
  mixins: [cartUtils, messagePopupMixin],
  components: {MessagePopup, EmptyCart},
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
}
;

</script>

<style scoped lang="scss">
@use "src/assets/style/abstracts" as *;
//refactor
.desktop-header__cart {
  height: 100%;
  background-color: #f5f5f5;

  .cart__container {
    max-width: 1150px;
    height: 100%;
    margin-inline: auto;
  }
}

.cart__title {
  padding-top: 2rem;

  .cart__title-wrapper {
    display: flex;
    align-items: center;
    width: 100%;
    height: 3rem;
    padding: 0 2rem;
    background-color: #ffffff;
    box-shadow: 0 1px 1px 0 rgba(0, 0, 0, .05);

    //checkbox
    & > :first-child {
      flex: 0;
    }

    //商品名稱
    & > :nth-child(2) {
      flex: 3 300px;
      margin-left: 1rem;
      color: #888;
    }

    //單價、數量、總計
    & > :not(:first-child):not(:nth-child(2)) {
      flex: 1 100px;
      text-align: center;
      color: #888;
    }

    & > input,
    & > label {
      cursor: pointer;
    }
  }
}

//產品資訊
.cart__product {
  margin-top: 1rem;
  margin-bottom: 5rem;
  background-color: #ffffff;
  padding: 1rem;

  .cart__product-container {
    width: 100%;
    border: 1px solid #eaeaea;
    padding: 1rem;
    margin-bottom: 1rem;

    .cart__product__wrapper {
      height: 5rem;
      display: flex;
      //書籍封面、書名column
      & > :nth-child(2) {
        flex: 3 300px;
        margin-left: 1rem;
      }

      //除了checkbox跟書籍封面、書名column以外的column
      & > :not(:first-child):not(:nth-child(2)) {
        flex: 1 100px;
        text-align: center;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      //書名、作者
      .cart__product-title {
        display: flex;
        height: 100%;
        line-height: 1rem;

        .cart__product-a {
          width: 80px;
          height: 80px;
          cursor: pointer;

          .cart__product-bookCover {
            height: 100%;
            width: 100%;
            object-fit: contain;
          }
        }


        .cart__products-bookName {
          padding: 5px 10px 0;

          .cart__products-bookName-text {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            cursor: pointer;
            font-size: .9rem;

          }

          .cart__products-author-text {
            padding-top: 4px;
            font-size: .8rem;
            color: #626262;
          }
        }
      }

      //數量
      .cart__product-quantity {
        display: flex;
        flex-direction: column;

        .cart__product-quantity-button-wrapper {
          display: flex;
          border: 1px solid gray;
          border-radius: 4px;

          .cart__product-quantity-spin-button {
            width: clamp(1.5rem, -0.2143rem + 3.5714vw, 2rem);
            height: 2rem;
            border: none;
            line-height: 1rem;
            cursor: pointer;
            border-radius: 4px;
            background-color: #ffffff;

          }

          .cart__product-quantity-spin-number {
            height: 2rem;
            width: clamp(2.1875rem, 1.2589rem + 1.9345vw, 3rem);
            border: none;
            border-left: 1px solid black;
            border-right: 1px solid black;
            outline: none;
            text-align: center;

            &:focus {
              background-color: #f5f5f5;
            }
          }
        }

      }

      //總計
      .cart__product-sum {
        color: #ee4d2d;

      }
    }
  }
}

//結算
.summarize {
  width: 100%;
  height: 5rem;
  max-width: 1150px;
  position: fixed;
  bottom: 0;
  background-color: #ffffff;

  .summarize__container {
    display: flex;
    height: 100%;
    padding: 1rem;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 0 10px 1px darkgrey;

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

        .summarize__checkout-price-text {
          font-size: 1.7rem;
          color: #b41444;
          margin-left: .5rem;
        }
      }

      .summarize__checkout-button {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 10rem;
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