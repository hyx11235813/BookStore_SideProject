<template>
  <main class="books">
    <div class="books__container">
      <div class="books__search-result">
        {{ resultText }}
      </div>
      <div class="books__grid">
        <div class="books__grid-wrapper">
          <div class="books__card" v-for="(book,index) in books" :key="index" @click="generateBookUrl(book)"
               ref="cards">
            <div class="books__img">
              <a>
                <img :src="'data:image/jpeg;base64,'+book.bookCover" alt="書籍封面" @error="onImageError">
              </a>
            </div>
            <div class="books__caption">
              <h3 class="books__title">{{ book.bookName }}</h3>
              <p class="books__author">{{ book.author }}</p>
              <p class="books__price">$ {{ book.price }}</p>
              <button class="books__button" @click.stop="addToCart(book.id)">
                <p class="books__button-text">
                  加入購物車
                </p>
              </button>
            </div>
          </div>
        </div>
      </div>
      <add-to-cart-modal :visible="isAddToCartModalVisible"
                         :book="addBookModalData"
                         @update:visible="isAddToCartModalVisible=$event">
      </add-to-cart-modal>
      <MessagePopup :message="popupMessage" :isVisible="popupVisible" :type="popupType"></MessagePopup>

    </div>
  </main>
</template>
<script>
import {getTop100NewlyReleasedBooks, getTop100SalesBooks, getBooks, getBookById} from '@/api/book';
import {addToShoppingCart, getUserCartInfo} from '/src/api/cart';
import AddToCartModal from '@/components/user/addToCartModal.vue';
import MessagePopup from '@/components/MessagePopup.vue';
import {messagePopupMixin} from '@/mixins/messagePopupMixin';

export default {
  mixins: [messagePopupMixin],
  components: {MessagePopup, AddToCartModal: AddToCartModal},
  data() {
    return {
      query: '',//關鍵字搜尋
      rank: '',//書籍排行
      cat: '',//書籍分類
      books: [],
      addBookModalData: [],
      currentPage: 1,
      pageSize: 10,
      totalPages: null,
      hasMoreData: true,
      isAddToCartModalVisible: false,
      cardObserver: null,
      resultText: ''
    };
  },
  watch: {
    '$route.query':
        function () {
          this.getBooks(true);
        }
  },
  methods: {
    onImageError() {
      event.target.src = require('@/assets/images/no-image.svg');
    },
    initObservers() {
      this.cardObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('show');
            this.cardObserver.unobserve(entry.target);
          }
        });
      }, {rootMargin: '-100px'});

      this.infiniteObserver = new IntersectionObserver((entries) => {
        const entry = entries[0];
        if (entry.isIntersecting && this.hasMoreData) {
          this.loadMoreBooks();
          this.infiniteObserver.unobserve(entry.target);
        }
      }, {threshold: 0.5});
    },
    async getBooks(reset = false) {
      if (reset) {
        this.currentPage = 1;
        this.books = [];
        this.hasMoreData = true;
      }
      if (!this.hasMoreData) return;

      try {
        const res = await this.processSearch();
        this.setModel(res);
        await this.getResultText();
      } catch (error) {
        console.log(error);
        this.showErrorMessage();
      } finally {
        this.$nextTick(() => {
          this.observeCard();
          this.observeLastCard();
        });
      }
    },
    async loadMoreBooks() {
      this.currentPage++;
      await this.getBooks();
    },
    observeCard() {
      const cards = this.$refs.cards || [];
      cards.forEach(card => this.cardObserver.observe(card));
    },
    observeLastCard() {
      const cards = this.$refs.cards || [];
      const lastCard = cards[cards.length - 1];
      if (lastCard) {
        this.infiniteObserver.observe(lastCard);
      }
    },
    setModel(res) {
      const booksData = res.data.content;
      this.totalPages = res.data.totalPages;
      if (this.currentPage > this.totalPages) {
        this.hasMoreData = false;
        return;
      }
      booksData.forEach(book => {
        this.books.push({
          id: book.id,
          bookName: book.bookName,
          isbn: book.isbn,
          author: book.author,
          price: book.price,
          bookCover: book.bookCoverByte
        });
      });
    },
    async processSearch() {
      this.setUrlParams();
      if (!this.rank && !this.length && !this.query) {
        return getTop100SalesBooks(this.cat, this.query, this.currentPage, this.pageSize);
      }
      if (this.rank === 'r1') {
        return getTop100SalesBooks(this.cat, this.query, this.currentPage, this.pageSize);
      } else if (this.rank === 'r2') {
        return getTop100NewlyReleasedBooks(this.cat, this.query, this.currentPage, this.pageSize);
      }
      return getBooks(this.cat, this.query, null, this.currentPage, this.pageSize);
    },
    setUrlParams() {
      this.query = this.$route.query.q || '';
      this.rank = this.$route.query.Rank || '';
      this.cat = this.$route.query.Cat || '';
    },
    generateBookUrl(book) {
      if (book.bookName === undefined) return;
      let slug = this.slugify(book.bookName);
      let url = `/product/detail/${slug}/${book.isbn}`;
      this.$router.push(url);
    },
    slugify(title) {
      return title
          .toLowerCase()
          .replace(/ /g, '-');  // 將空格替換為連字符
    },
    getResultText() {
      if (this.books.length > 0 && this.query) {
        this.resultText = `搜尋"${this.query}"的結果：`;
      } else if (this.books.length === 0 && this.query) {
        this.resultText = `搜尋不到"${this.query}"的資料`;
      } else if (this.books.length === 0) {
        this.resultText = '沒有任何資料';
      } else {
        this.resultText = '搜尋結果：';
      }
    },
    async addToCart(bookId) {
      try {
        const book = await getBookById(bookId);
        const userId = this.$store.state.userData.user.id || 0;
        //如果有登入，更新資料庫
        if (userId) {
          await this.addToUserShoppingCart(userId, book);
          return;
        }
        //如果沒登入，更新localStorage
        const cart = JSON.parse(localStorage.getItem('cart')) || {};
        const bookInventory = book.data.quantity || 0;
        const cartBookQuantity = cart[bookId] || 0;

        if (cartBookQuantity < bookInventory) {
          cart[bookId] = cartBookQuantity + 1;
        }
        localStorage.setItem('cart', JSON.stringify(cart));
        let cartProductQuantity = Object.keys(cart).length;
        this.openAddToCartModal(book);
        this.$store.commit('setCartProductQuantity', cartProductQuantity);
      } catch (error) {

      }

    },
    async addToUserShoppingCart(userId, book) {
      try {
        let payload = [{
          userId: userId,
          bookId: book.data.id,
          quantity: 1
        }];
        const res = await addToShoppingCart(payload);
        if (res.code !== 200) {
          this.showErrorMessage();
          return;
        }
        await this.getUserCartQuantity(userId);
        this.openAddToCartModal(book);
      } catch (error) {
        this.showErrorMessage();
        console.warn(error);
      }
    },
    async getUserCartQuantity(userId) {
      try {
        const userCartProductQuantity = await getUserCartInfo(userId).then(res => res.data?.length || 0);
        this.$store.commit('setCartProductQuantity', userCartProductQuantity);
      } catch (error) {
        console.warn(error);
      }
    },
    openAddToCartModal(book) {
      this.isAddToCartModalVisible = true;
      const {bookName, author, publisher, price, bookCoverByte} = book.data;
      this.addBookModalData = {
        bookName: bookName,
        author: author,
        publisher: publisher,
        price: price,
        bookCoverByte: bookCoverByte
      };
    }
  }
  ,

  mounted() {
    this.getBooks(true);
    this.initObservers();
  }
};
</script>
<style scoped lang="scss">
@use "@/assets/style/abstracts" as *;

.books {
  width: 100%;
  min-height: calc(100% - 5rem);

  .books__container {
    display: flex;
    flex-direction: column;
    padding-left: 2rem;
    padding-bottom: 2rem;
    height: 100%;

    .books__search-result {
      padding: 1.5rem 0 2rem;

      .books__search-result-text {
        font-weight: bold;
        font-size: 1.2rem;
      }
    }

    .books__grid {
      height: 100vh;
      min-height: 100%;

      .books__grid-wrapper {
        display: grid;
        grid-template-columns:  repeat(auto-fill, 10rem);
        //grid-template-columns:  repeat(2, 1fr);
        grid-auto-rows: 27rem; //限制每個books__card的高度
        gap: 1.4rem;

        .books__card {
          display: flex;
          flex-direction: column;
          opacity: 0;
          transition: opacity 500ms ease;

          &.show {
            opacity: 1;
          }

          .books__img {
            height: 50%;
            background-color: #f5f5f5;

            a {
              display: block;
              height: 100%;

              img {
                width: 100%;
                height: 100%;
              }
            }
          }

          .books__caption {
            display: flex;
            flex-direction: column;
            flex-grow: 1;
            padding-top: .4rem;
            justify-content: space-between;

            .books__title {
              word-break: break-word;
              padding-bottom: .2rem;
              min-height: 4rem;
            }

            .books__author {
              min-height: 3rem;
              font-size: .8rem;
            }

            .books__price {
              padding: .4rem 0;
              font-weight: bold;
              font-size: 1.2rem;
            }

            .books__button {
              @include submit-btn--green;

              .books__button-text {
                pointer-events: none;
                text-align: center;
                letter-spacing: 2px;
              }
            }
          }

        }

      }
    }
  }
}

@media screen and (max-width: 576px) {
  .books__container {
    padding: 0 1rem !important;

    .books__grid-wrapper {
      grid-template-columns:  repeat(2, 1fr) !important;
      gap: 1rem !important;
    }
  }
}
</style>