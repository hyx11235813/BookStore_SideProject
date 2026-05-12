<template>
  <div class="home">
    <div class="home__container">
      <div class="hero">
        <div ref="heroSwiper" class="hero__swiper">
          <div class="swiper-wrapper">
            <div class="swiper-slide" v-for="photo in heroPhotos" :key="photo">
              <img :src="require(`@/assets/images/${photo}`)" alt="">
            </div>
          </div>
          <div class="swiper-button-prev swiper-button"></div>
          <div class="swiper-button-next swiper-button"></div>
        </div>
      </div>

      <BookCarousel ref="bestsellerBookCarousel"
                    :title='"熱門書籍"'
                    :books="bestsellerBooks"
                    :rank="'r1'"
                    @showInfo="showInfo"
      >
        <template #viewMore="{rank}">
          <a class="header-link" @click.prevent="viewMoreBooks(rank)">查看更多
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="#000000" viewBox="0 0 256 256">
              <path
                  d="M181.66,133.66l-80,80a8,8,0,0,1-11.32-11.32L164.69,128,90.34,53.66a8,8,0,0,1,11.32-11.32l80,80A8,8,0,0,1,181.66,133.66Z"></path>
            </svg>
          </a>
        </template>

      </BookCarousel>
      <BookCarousel ref="newSellerBookCarousel"
                    title="新上架書籍"
                    :books="newSellerBooks"
                    :rank="'r2'"
                    @showInfo="showInfo"
      >
        <template #viewMore="{rank}">
          <a class="header-link" @click.prevent="viewMoreBooks(rank)">查看更多
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="#000000" viewBox="0 0 256 256">
              <path
                  d="M181.66,133.66l-80,80a8,8,0,0,1-11.32-11.32L164.69,128,90.34,53.66a8,8,0,0,1,11.32-11.32l80,80A8,8,0,0,1,181.66,133.66Z"></path>
            </svg>
          </a>
        </template>
      </BookCarousel>
      <add-to-cart-modal :visible="isAddToCartModalVisible"
                         @update:visible="isAddToCartModalVisible=$event">
      </add-to-cart-modal>
    </div>
  </div>
</template>
<script>
import Swiper, {Navigation} from 'swiper';
import 'swiper/css/swiper.css';
import {getTop10NewBooks, getTop10SalesBooks} from '@/api/book';
import BookCarousel from '@/views/user/home/BookCarousel.vue';
import SuccessModal from '@/components/user/SuccessModal.vue';

export default {
  components: {AddToCartModal: SuccessModal, BookCarousel},
  data() {
    return {
      name: 'home',
      heroPhotos: [
        'test-hero.jpg',
        'test-photo2.jpg',
        'test-photo3.jpg'
      ],
      bestsellerBooks: [],
      newSellerBooks: [],
      isAddToCartModalVisible: false,
      closeTimer: null //計時器時間
    };
  },
  methods: {
    async fetchBooks(apiFn, targetList, refName) {
      try {
        const res = await apiFn();
        this[targetList] = this.processBooks(res);
        this.$nextTick(() => {
          this.$refs[refName].initSwiper();
        });
      } catch (error) {
        console.error(error);
      }
    },
    processBooks(res) {
      let books = res.data.map(item => ({
        id: item.id,
        bookName: item.bookName,
        author: item.author,
        price: item.price,
        isbn: item.isbn,
        img: item.bookCoverByte
      }));

      // 補滿到 6 筆填滿swiper
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
    showInfo() {
      clearTimeout(this.closeTimer);
      this.isAddToCartModalVisible = true;
      this.closeTimer = setTimeout(() => {
        this.isAddToCartModalVisible = false;
      }, 1400);
    },

    viewMoreBooks(rank) {
      this.$router.push({
        name: 'search',
        query: {q: '', Rank: rank}
      });
    },
    initSwiper() {
      new Swiper(this.$refs.heroSwiper, {
        modules: [Navigation],
        loop: true,
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev'
        },
        autoplay: {
          delay: 2500,
          disableOnInteraction: false
        }
      });
    }
  },
  mounted() {
    this.initSwiper(); // 初始化 Swiper
    this.fetchBooks(getTop10SalesBooks, 'bestsellerBooks', 'bestsellerBookCarousel');
    this.fetchBooks(getTop10NewBooks, 'newSellerBooks', 'newSellerBookCarousel');

  }

};
</script>


<style scoped lang="scss">
@use "@/assets/style/abstracts" as *;

.home {
  min-height: 100vh;
  background-color: $clr-bg-light;
  .home__container {
    max-width: 1150px;
    height: 100%;
    margin: 0 auto;
    border-bottom: 1px solid #000;
  }
}

.hero {
  width: 100%;
  height: clamp(10rem, calc(8vw + 12rem), 20rem);

  .hero__swiper {
    width: 100%;
    height: 100%;
    overflow: hidden;
    position: relative;
    border-radius: 1rem;

    .swiper-wrapper {
      width: 100%;
      height: 100%;
    }

    .swiper-button {
      color: white;
    }

    .swiper-slide {
      border-radius: 1rem;

      img {
        display: block;
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 1rem;
      }
    }
  }
}




@media screen and (max-width: 768px) {
  .swiper-button {
    display: none;
  }
  .swiper-slide {
    img {
      border-radius: 0 !important;
    }
  }
}
</style>
