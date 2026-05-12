<template>
  <div class="carousel">
    <div class="new-seller carousel__container">
      <template>
        <slot name="header"></slot>
      </template>
      <div v-if="this.title" class="desktop-header">
        <h2 class="header-title">{{ title }}</h2>
        <slot name="viewMore" :rank="rank"></slot>
      </div>
      <div ref="booksSwiper" class="carousel__swiper">
        <div class="swiper-wrapper" >
          <div class="swiper-slide book-item " v-for="book in books" :key="book.id"
               @click="goToBookDetail(book)" >
            <div class="img-wrapper">
              <a><img :src="'data:image/jpeg;base64,' + book.img" alt="" @error="onImageError"></a>
              <button class="addToCartButton" @click.stop="addToCart(book.id)" v-show="book.isbn">
                <p class="button-text">加入購物車</p>
              </button>
            </div>
            <div class="caption">
              <h3 class="desktop-header__category-title">{{ book.bookName }}</h3>
              <p class="author">{{ book.author }}</p>
              <p class="price" v-show="book.isbn">${{ book.price }}</p>
            </div>
          </div>
        </div>
        <div class="swiper-button-prev  swiper-button"></div>
        <div class="swiper-button-next  swiper-button"></div>
      </div>

    </div>
  </div>
</template>
<script>
import Swiper, {Navigation} from 'swiper';
import 'swiper/css/swiper.css';
import {addToShoppingCart, getUserCartInfo} from '/src/api/cart';
export default {
  data() {
    return {
    };
  },
  props: {
    title: {
      type: String,
      required: false
    },
    rank: {
      type: String,
      required: false
    },
    books: {
      type: Array,
      required: true
    }
  },
  watch: {
    books: {
      handler(newVal) {
        if (newVal && newVal.length && this.$refs.booksSwiper) {
          this.$nextTick(() => {
            this.initSwiper();
          });
        }
      }
    }
  },
  methods: {
    onImageError(){
      event.target.src = require('@/assets/images/no-image.svg');
    },
    goToBookDetail(book) {
      if (!book.bookName || !book.isbn) return;
      const slug = this.slugify(book.bookName);
      window.location.href = `/product/detail/${slug}/${book.isbn}`;
    },
    slugify(title) {
      return title
          .toLowerCase()
          .replace(/ /g, '-');  // 將空格替換為連字符
    },

    initSwiper() {
      if (this.swiper) {
        this.swiper.destroy(true, true);
        this.swiper = null;
      }
      if (!this.$refs.booksSwiper) return;

      // const isDesktop = window.innerWidth >= 576;
      const config = {
        modules: [Navigation],
        loop: true,
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev'
        },
        // simulateTouch: !isDesktop, // 滑鼠拖拽
        breakpoints: {
          320: {slidesPerView: 2, spaceBetween: 10},
          375: {slidesPerView: 2, spaceBetween: 10},
          576: {slidesPerView: 3, spaceBetween: 10},
          768: {slidesPerView: 5, spaceBetween: 20},
          1024: {slidesPerView: 6, spaceBetween: 20}
        }
      };
      this.swiper = new Swiper(this.$refs.booksSwiper, config);
    },

    async addToCart(bookId) {
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

    mounted() {
    }

  }
};

</script>

<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;

.carousel {
  .carousel__container {
    width: 100%;
    padding: 2rem;
    position: relative; //讓prev、next按鈕定位
    .desktop-header {
      display: flex;
      justify-content: space-between;
      padding: 1rem 0 2em;

      .header-title {
        color: #1f1f1f;
        user-select: none;
      }

      .header-link {
        display: flex;
        align-items: center;
        color: #1f1f1f;
        text-decoration: none;
        cursor: pointer;
      }
    }


    .carousel__swiper {
      width: 96%;
      height: 100%;
      margin: 0 auto;
      overflow: hidden;

      .swiper-wrapper {
        .swiper-slide.book-item {
          padding-right: 4px; //只是爲了給shadow留空間
          &:hover {
            .addToCartButton {
              top: calc(100% - 2.5rem) !important;

            }
          }

          .img-wrapper {
            position: relative;
            height: 200px;
            background-color: gainsboro;


            img {
              display: block;
              box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .addToCartButton {
              position: absolute;
              top: 100%;
              width: 100%;
              height: 2.5rem;
              display: flex;
              justify-content: center;
              align-items: center;
              margin-top: auto;
              padding: 1rem;
              transition: top 300ms ease-out;
              border: 2px solid #4d7a67;
              background-color: #ffffff;
              cursor: pointer;

              &:hover {
                background-color: $clr-bg-light;
              }

              &:active {
                background-color: #efefef;
              }

              .button-text {
                color: #3a594d;
                pointer-events: none;
              }
            }

          }

          .caption {
            position: relative;
            z-index: 1;
            display: flex;
            flex-direction: column;
            min-height: 8rem;
            padding: .5rem 0;
            background-color: #f5f5f5;

            .desktop-header__category-title {
              width: 100%;
              min-height: 3.5rem;
              font-size: .92rem;
            }

            .author {
              min-height: 3.5rem;
              text-overflow: ellipsis;
              font-size: .8rem;
            }

            .price {
              font-size: 1.4rem;
              margin-top: auto;
            }
          }
        }
      }

      .swiper-button-prev, .swiper-button-next {
        color: #9b9b9b;
        scale: .7;
      }
    }
  }


}
@media screen and (max-width: 768px) {
  .swiper-button {
    display: none;
  }
}
@media screen and (max-width: 576px) {
  .carousel__container {
    padding: .3rem !important;
  }
}
</style>