<template>
  <div id="product-detail">
    <div class="product-detail">
      <div class="product-detail__container">
        <header class="product-detail__header--rwd">
          <div class="product-detail__header-container--rwd">
            <h1 class="product-detail__title--rwd">{{ bookDetail.bookName }}</h1>
            <p class="product-detail__author--rwd">作者:{{ bookDetail.author }}</p>
          </div>
        </header>
        <!--封面-->
        <div class="product-detail__image">
          <div class="product-detail__image-frame">
            <img class="img" :src="'data:image/jpeg;base64,'+bookDetail.bookCoverByte" alt="">
          </div>
        </div>
        <!--内容、加入購物車-->
        <div class="product-detail__info">
          <header class="product-detail__header">
            <div class="product-detail__header-container">
              <h1 class="product-detail__title">{{ bookDetail.bookName }}</h1>
              <p class="product-detail__author">作者:{{ bookDetail.author }}</p>
            </div>
          </header>
          <section class="product-detail__product-detail ">
            <div class="product-detail__description">
              <span class="product-detail__description-text">出版：{{ bookDetail.publisher }}</span>
              <span class="product-detail__description-text">出版日期：{{ bookDetail.publishedDate }}</span>

              <div class="product-detail__description-text delivery-way">
                 <span class="deliver-way-text">運送方式：
                   <ul class="deliver-way__buttons">
                   </ul>
                 </span>
                <ul class="delivery-way__info">
                  <li class="delivery-way__info-text">
                    可配送點： 台灣、蘭嶼、綠島、澎湖、金門、馬祖
                  </li>
                </ul>
              </div>
              <span class="product-detail__description-text product-detail-price">$100</span>
            </div>
            <div class="product-detail__commerce">
              <div class="product-detail__commerce-wrapper">
                <span
                    class="product-detail__commerce-remaining-stock">{{
                    bookDetail.quantity >= 10 ? '庫存 > 10' : '庫存 < 10'
                  }}
                </span>
                <button class="product-detail__commerce-button addCart " @click.stop="addToCart(bookDetail.id)">
                  加入購物車
                </button>
              </div>
            </div>
          </section>
        </div>
      </div>
      <div class="product-detail__product-overview">
        <div class="product-detail__product-overview-wrapper">
          <h3 class="product-detail__product-overview-title "> 簡介</h3>
          <p :class="['product-detail__product-overview-text',{truncate:!isOverViewTextExpanded}] "
             v-html="bookDetail.description"></p>
          <button class="product-detail__product-overview-button" @click=" toggleOverviewText"
                  v-show="!isOverViewTextExpanded">看更多
          </button>
          <button class="product-detail__product-overview-button" @click=" toggleOverviewText"
                  v-show="isOverViewTextExpanded" >收起
          </button>
        </div>
      </div>

      <BookCarousel :books="carouselBooks">
        <template #header>
          <header class="carousel-header--middle">
            <h2 class="carousel-header-text">其他推薦</h2>
          </header>
        </template>
      </BookCarousel>

    </div>
    <add-to-cart-modal :visible="isAddToCartModalVisible"
                       @update:visible="isAddToCartModalVisible=$event">

    </add-to-cart-modal>
    <MessagePopup :message="popupMessage" :isVisible="popupVisible" :type="popupType"></MessagePopup>
  </div>
</template>

<script>
import {getBookDetail, getTop10SalesBooksByCategory} from '@/api/book';
import BookCarousel from '@/views/user/home/BookCarousel.vue';
import SuccessModal from '@/components/user/SuccessModal.vue';
import {messagePopupMixin} from '@/mixins/messagePopupMixin';
import MessagePopup from '@/components/MessagePopup.vue';
import {addToShoppingCart, getUserCartInfo} from '@/api/cart';

export default {
  components: {AddToCartModal: SuccessModal, BookCarousel, MessagePopup},
  mixins: [messagePopupMixin],
  data() {
    return {
      bookDetail: {
        id: '',
        isbn: '',
        bookName: '',
        author: '',
        category: '',
        publisher: '',
        publishedDate: '',
        price: '',
        bookCoverByte: '',
        description: '',
        quantity: ''
      },
      carouselBooks: [],
      isOverViewTextExpanded: false,
      show_Info: false,
      isAddToCartModalVisible: false,//加入購物車訊息
      closeTimer: null
    };
  },

  beforeRouteUpdate(to, from, next) {
    this.fetchBookDetail(to.params.isbn);
    next();
  },
  methods: {
    //通過URL的ISBN獲取書籍資料
    async fetchBookDetail(isbn) {
      try {
        const resp = await getBookDetail(isbn);
        if (resp.code !== 200) {
          this.showErrorMessage();
          return;
        }
        this.bookDetail = {...resp.data};
        this.bookDetail.description = this.processDescriptionText(this.bookDetail.description || '');
        await this.getRecommendBooks(this.bookDetail.category);
      } catch (error) {
        console.error(error);
        this.showErrorMessage();
      }
    },
    processDescriptionText(content) {
      return content
          .replace(/★\s*/g, '&nbsp;★')
          .replace(/……/g, '……<br>')
          .replace(/。/g, '。<br>')
          .replace(/ (?![a-zA-Z])/g, '<br>');
    },
    async getRecommendBooks(category) {
      if (!category) return;
      const resp = await getTop10SalesBooksByCategory(category);
      this.carouselBooks = this.processBooks(resp);
    },
    processBooks(res) {
      let books = res.data.map(item => ({
        id: item.id,
        bookName: item.bookName,
        author: item.author,
        price: item.price,
        isbn: item.isbn,
        img: item.bookCoverByte
      })).filter(book => book.isbn !== this.bookDetail.isbn); //避免當前書籍再次出現在推薦清單中

      //如果書籍數量不到swiper在畫面上顯示的6筆，補上不足的數量
      while (books.length < 6) {
        books.push({
          id: `${Date.now()}-${Math.random()}`,
          bookName: '...',
          author: '',
          price: '',
          isbn: '',
          img: ''
        });
      }
      return books;
    },
    async addToCart(bookId) {
      console.log("run");
      const userId = this.$store.state.userData.user.id || 0;
      if (userId) {
        await this.addToUserShoppingCart(userId, bookId, 1);
      } else {
        const cart = JSON.parse(localStorage.getItem('cart')) || {};
        cart[bookId] = (cart[bookId] || 0) + 1;
        localStorage.setItem('cart', JSON.stringify(cart));

        const cartProductQuantity = Object.keys(cart).length;
        this.$store.commit('setCartProductQuantity', cartProductQuantity);
      }
      this.$emit('showInfo');
    },
    async addToUserShoppingCart(userId, bookId, quantity) {
      try {
        const payload = [{userId, bookId, quantity: quantity}];
        const res = await addToShoppingCart(payload);
        if (res.code === 200) {
          await this.getUserCartQuantity(userId);
        }
      } catch (error) {
        console.log(error);
      }
    },
    async getUserCartQuantity(userId) {
      const res = await getUserCartInfo(userId);
      const cartQuantity = res.data.length || 0;
      this.$store.commit('setCartProductQuantity', cartQuantity);
    },
    showInfo() {
      if (this.closeTimer) {
        clearTimeout(this.closeTimer);   //避免多個計時器同時運行造成顯示錯誤
        this.closeTimer = null;
      }
      this.isAddToCartModalVisible = !this.isAddToCartModalVisible;

      if (this.isAddToCartModalVisible) {
        this.closeTimer = setTimeout(() => {
          this.isAddToCartModalVisible = false;
          this.closeTimer = null;
        }, 1400);
      }
    },
    toggleOverviewText() {
      this.isOverViewTextExpanded = !this.isOverViewTextExpanded;
    }
  },
  mounted() {
    let isbn = this.$route.params.isbn;
    this.fetchBookDetail(isbn);
  }
};

</script>
<style scoped lang="scss">
@use "@/assets/style/abstracts/index" as *;

#product-detail {
  background-color: #f5f5f5;
}

.product-detail {
  max-width: 1150px;
  height: 100%;
  margin: auto;

  .product-detail__container {
    display: flex;
    width: 100%;
    padding-top: 2rem;

  }
}

.product-detail__image {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 20rem;
  flex: 1;

  .product-detail__image-frame {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;

    .img {
      height: 100%;
      object-fit: contain;
    }
  }

}


.product-detail__info {
  flex: 1;
  height: 20rem;
  display: flex;
  flex-direction: column;

  .product-detail__header {
    display: block;
  }

  .product-detail__product-detail {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding-top: 1rem;

    .product-detail__description {
      display: flex;
      flex-direction: column;

      & > .product-detail__description-text {
        font-size: .9rem;
        color: $clr-text-gray;
        line-height: 1.8;
      }

      .product-detail-price {
        font-size: 2rem;
        font-family: Lato, sans-serif
      }
    }

    .delivery-way {
      display: flex;
      flex-direction: column;

      .deliver-way__buttons {
        display: inline;

        .deliver-way__item {
          margin-right: 1rem;
          display: inline;
          cursor: pointer;
        }
      }

      .delivery-way__info {
        display: flex;
        flex-direction: column;

        .delivery-way__info-text {
          list-style-type: none;
          font-size: .8rem;
        }
      }
    }

    .product-detail__commerce {
      width: 12rem;
      border: $clr-border-light;
      margin-top: auto;
      border-radius: 2px;

      .product-detail__commerce-wrapper {
        padding: .5rem 1rem .5rem;
        display: flex;
        flex-direction: column;

        .product-detail__commerce-remaining-stock {
          font-size: .8rem;
          margin-bottom: .5rem;
        }
      }

      .product-detail__commerce-button {
        background: none;
        border: none;
        border-radius: 2px;

        &.addCart {
          background-color: #3d6db5;
          padding: 0.5rem 1rem;
          color: $clr-bg-light;
          margin-bottom: .5rem;
          cursor: pointer;

        }

        &:hover {
          background-color: #3d5fb5;
        }
      }
    }
  }

}

.product-detail__product-overview {
  padding: 2rem;

  .product-detail__product-overview-wrapper {
    height: auto;

    .product-detail__product-overview-title {
      border-bottom: $clr-border-light;
      margin-bottom: 1rem;
    }

    .product-detail__product-overview-text {
      font-size: .9rem;
      line-height: 1.5;

      &.truncate { //隱藏多出的行數
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 5;
        -webkit-box-orient: vertical;
        mask-image: linear-gradient(to bottom, black 65%, transparent 100%);

      }
    }

    .product-detail__product-overview-button {
      border: none;
      border-bottom: 1px solid #0000ab;
      background: none;
      color: #0000ab;
      font-weight: 500;
      cursor: pointer;
    }
  }

}

.product-detail__header--rwd {
  display: none;
  margin-bottom: 1rem;
}

.carousel-header--middle {
  margin-bottom: 2rem;

  .carousel-header-text {
    display: flex;
    text-align: center;
    align-items: center;

    &::before,
    &::after {
      content: "";
      flex: 1; //使線條擴展
      height: 1px;
      background-color: #ccc;
      display: block;
    }

    &::before {
      margin-right: .5rem
    }

    &::after {
      margin-left: .5rem
    }
  }
}

@media screen and (max-width: 576px) {
  .product-detail__container {
    display: flex;
    flex-direction: column;
  }

  .product-detail__header {
    display: none !important;
  }

  .product-detail__header--rwd {
    display: block;
    text-align: center;

    .product-detail__header-container--rwd {
      .product-detail__title--rwd {
        word-break: break-all;
        font-size: 1.4rem;
      }

      .product-detail__author--rwd {
        font-size: .8rem;
      }
    }
  }
  .product-detail__info {
    padding: 1rem;
  }
  .product-detail__commerce {
    width: 100% !important;
  }

}

</style>