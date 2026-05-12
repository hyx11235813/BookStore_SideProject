<template>
  <div class="modal" v-show="visible" @click.self="closeModel">
    <div class="modal__container">
      <div class="modal__header">
        <p class="modal__header-title">已加入購物車！</p>
      </div>

      <div class="modal__body">
        <div class="modal__img">
          <img :src="book.bookCoverByte ? 'data:image/jpeg;base64,' + book.bookCoverByte : require('@/assets/images/no-image.svg')" alt="封面" />
        </div>
        <div class="modal__caption">
          <h3 class="modal__title">{{ book.bookName }}</h3>
          <p class="modal__author">{{ book.author }}</p>
          <p class="modal__publisher">{{ book.publisher }}</p>
          <p class="modal__price">${{ book.price }}</p>
        </div>
      </div>

      <div class="modal__btns">
        <button class="modal__btn modal__btn--view" @click="goToCart" >查看購物車</button>
        <button class="modal__btn modal__btn--continue" @click="closeModel">繼續購物</button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    book: {
      default: () => ({
        bookName: '',
        author: '',
        publisher: '',
        price: 0,
        bookCoverByte: ''
      })

    }
  },
  methods: {
    closeModel() {
      this.$emit('update:visible', false);
    },
    goToCart() {
      this.$router.push('/cart')
    },
  }
};
</script>

<style scoped lang="scss">
@use "@/assets/style/abstracts" as*;

.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, .5);
  z-index: 999;

  .modal__container {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: min(600px, 92vw);
    min-height: 150px;
    z-index: 999;
    background-color: white;
    border-radius: 5px;

    .modal__header {
      font-weight: bold;
      font-size: 1.5rem;
      padding: 1rem;
      text-align: center;
    }

    .modal__body {
      padding: 1rem;
      display: flex;
      gap: 2rem;

      .modal__img {
        flex: 0 0 20%;

        img {
          width: 100%;
          height: auto;
        }
      }

      .modal__caption {
        flex: 1;

        & > * {
          padding-bottom: .5rem;
        }

        .modal__title {
          font-size: 1.5rem;
        }

        .modal__author {
        }

        .modal__publisher {

        }

        .modal__price {
          font-size: 1.2rem;
        }
      }
    }

    .modal__btns {
      height: 8.5rem;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding: 1rem 1rem;

      & > * {
        height: 2.7rem;
        font-size: 1rem;
        border-radius: 5px;
        letter-spacing: 0.05em;
        cursor: pointer;

      }

      .modal__btn--view {
        @include submit-btn--green;
      }

      .modal__btn--continue {
        border: 1px solid $clr-btn-green;
        color: $clr-btn-green;
        background-color: transparent;
        &:hover {
          background-color:  $clr-btn-green;
          color: #f5f5f5;
        }
      }
    }
  }
}
</style>