<template>
  <div class="checkout">
    <form class="checkout__form" @submit.prevent="handleOrderSubmit">
      <section class="checkout__section checkout__section--left">
        <p class="checkout__title">收件人資訊</p>

        <div class="checkout__field-group">
          <div class="checkout__field " style="grid-area: name">
            <input class="checkout__input" type="text" id="name" placeholder="" v-model="orderForm.name" required>
            <label class="checkout__label">全名</label>
          </div>
          <div class="checkout__field " style="grid-area: phone">
            <input class="checkout__input" type="tel" id="phone" placeholder="" v-model="orderForm.phone" required>
            <label class="checkout__label">手機號碼</label>
          </div>
          <div class="checkout__field " style="grid-area: city">
            <input class="checkout__input" type="text" id="city" placeholder="" v-model="orderForm.city" required>
            <label class="checkout__label">城市、區</label>
          </div>
          <div class="checkout__field " style="grid-area: postcode">
            <input class="checkout__input" type="number" id="postcode" placeholder="" v-model="orderForm.postcode"
                   required>
            <label class="checkout__label">郵遞區號</label>
          </div>
          <div class="checkout__field " style="grid-area: street">
            <input class="checkout__input" type="text" id="address" placeholder="" v-model="orderForm.address" required>
            <label class="checkout__label">地址</label>
          </div>

        </div>
      </section>

      <aside class="checkout__section checkout__section--right">
        <div class="checkout__summary">
          <p class="checkout__title">數量({{ quantity }})</p>
          <div v-for="item in cartItems" :key="item.id" class="checkout__item">
            <img class="checkout__item-photo" :src="'data:image/jpeg;base64,' + item.bookCover">
            <div class="checkout__item-detail">
              <span class="checkout__item-name">{{ item.bookName }} </span>
              <span class="checkout__item-name">{{ item.author }} </span>
              <span class="checkout__item-quantity">數量{{ item.quantity }} </span>
            </div>
            <div class="checkout__item-price">
              <span>${{ item.price }} </span>
            </div>
          </div>
        </div>
        <div class="checkout__total">
          <span>總計金額</span>
          <span class="checkout__total-amount">${{ totalPrice }}</span>
        </div>
        <button type="submit"
                class="checkout__submit-btn"
                :class="{ 'checkout__submit-btn--disabled': isSubmitting }"
                :disabled="isSubmitting">
          確認下單
        </button>
      </aside>

    </form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      orderForm: {
        name: '',
        phone: '',
        city: '',
        postcode: '',
        address: ''
      },
      cartItems: [
        {
          id: 1,           // 重要：後端存入 order_items 表需要此 ID
          bookCover: 'test',   // 與你之前的組件屬性名稱保持一致
          bookName: 'Java 入門',
          author: 'author',
          price: 100,
          quantity: 1
        },
        {
          id: 2,           // 重要：後端存入 order_items 表需要此 ID
          bookCover: 'test',   // 與你之前的組件屬性名稱保持一致
          bookName: 'Java 入門2',
          author: 'author2',
          price: 1002,
          quantity: 12
        },
        {
          id: 2,           // 重要：後端存入 order_items 表需要此 ID
          bookCover: 'test',   // 與你之前的組件屬性名稱保持一致
          bookName: 'Java 入門2',
          author: 'author2',
          price: 1002,
          quantity: 12
        }
      ],
      quantity: 1,
      isSubmitting: false,
      totalPrice: 0
    };
  }
};
</script>


<style scoped lang="scss">
@use "@/assets/style/abstracts" as *;
//todo:刻畫面
.checkout {
  .checkout__form {
    display: flex;
    gap: 2rem;
    padding: 2rem 6rem;
    margin: 0 auto;

  }


  .checkout__section {
    width: 100%;


    .checkout__title {
      font-size: 0.9rem;
      margin-bottom: 1rem;
    }

    .checkout__field-group {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
      grid-template-areas:

    "name  phone"
    "city  postcode"
    "street street ";

      .checkout__field {
        margin-bottom: 1rem;
        position: relative;;

        .checkout__input {
          width: 100%;
          padding: 20px 5px 5px;
          font-size: 1.2rem;
          outline: none;
          border-radius: 4px;
          border: 1px solid lightgray;

          &:focus + .checkout__label,
          &:not(:placeholder-shown) + .checkout__label {
            transform: translateY(-14px) scale(0.8);
            color: #505050;
          }
        }

        .checkout__label {
          position: absolute;
          line-height: 47px;
          left: 10px;
          pointer-events: none;
          transition: all 0.2s ease;
          transform-origin: left;
          color: $clr-text-gray;
        }
      }
    }

    &--left {
      padding: 1rem;

      flex: 0 0 65%;
      background-color: transparent;
      border: 1px solid lightgray;
      border-radius: 4px;

    }

    &--right {
      flex: 1;

      .checkout__summary {
        max-height: 200px;
        padding: 1rem;
        overflow-y: auto;
        border: 1px solid lightgray;
        border-radius: 4px;

        .checkout__item {
          display: flex;
          height: 100px;

          .checkout__item-photo {
            flex: 0 0 80px;
          }

          .checkout__item-detail {
            flex: 3;
            display: flex;
            flex-direction: column;

          }

          .checkout__item-price {
            flex: 1;
          }

        }
      }

      .checkout__total {
        padding: 1rem;
      }

      .checkout__submit-btn {
        width: 100%;
        height: 54px;
        border: none;
        border-radius: 54px;
      }
    }
  }
}

@media only screen and (max-width: 768px) {
  .checkout__form {
    padding: 2rem 1rem !important;
  }
}

@media only screen and (max-width: 1200px) {
  .checkout__form {
    display: block !important;
  }
  .checkout__section--left {
    margin-bottom: 1rem;

  }
  .checkout__section--left,
  .checkout__section--right {
  }
}
</style>