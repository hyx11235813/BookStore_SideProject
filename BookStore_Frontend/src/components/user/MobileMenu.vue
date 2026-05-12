<template>
  <div class="mobile-header">
    <div class="mobile-header__menu">
      <button class="menu-button">
        <img v-if="!isMobileMenuOpen" class="menu-icon" src="@/assets/images/hamburger.svg" alt="" @click="openMenu">
        <img v-else class="close-icon" src="@/assets/images/form-cross-black.svg" alt=""
             @click="closeMenu">
      </button>
      <div :class="['mobile-header__menu-wrapper',{active:isMobileMenuOpen}]">
        <aside :class="['main-menu no-scrollbar',{active:isMainMenuOpen}]">
          <ul class="menu-list">
            <li class="menu-list-item" @click="goToMemberCenter">
              <div class="member-center">
                會員中心
                <img src="@/assets/images/arrow-right.v2.svg" alt="">
              </div>
            </li>

            <li class="menu-list-item">
              <div class="menu-list__title " @click="selectMenu('fiction')">小說
                <img src="@/assets/images/arrow-right.v2.svg" alt="">
              </div>
            </li>

            <li class="menu-list-item">
              <div class="menu-list__title" @click="selectMenu('nonfiction')">非小說
                <img src="@/assets/images/arrow-right.v2.svg" alt="">
              </div>
            </li>
          </ul>
        </aside>

        <aside :class="['secondary-menu no-scrollbar',{active:isSecondaryMenuOpen}]">
          <ul class="secondary-manu-ul ">
            <li class=" secondary-manu-title" @click="backToMainMenu">
              <img src="@/assets/images/arrow-left.v2.svg" alt="">
              {{ currentCategories.name }}
            </li>
            <li class="secondary-manu-li" v-for="category in currentCategories.items" :key="category"
                @click="goToSearchPage(category)">
              <a>{{ category }}</a>
            </li>
          </ul>
        </aside>
      </div>
    </div>


    <div class="mobile-header__logo">
      <img class="desktop-header__logo" src="@/assets/images/book-store-logo.svg" alt="" @click="home">
    </div>


    <router-link to="/cart" class="mobile-header__cart">
      <img class="desktop-header__cart-icon" src="../../assets/images/cart.svg" alt="" style="fill: red">
      <span class="desktop-header__cart-count" :data-count="cartQuantity"></span>
    </router-link>

    <div class="mobile-header__search">
      <span class="desktop-header__search-icon"></span>
      <input class="desktop-header__search-bar" type="text" v-model="searchValue" placeholder="搜尋：作者、書名、出版商、ISBN"
             @input="updateQueryParams">
      <div class="desktop-header__search-clear-button" v-show="searchValue" @click="clearSearch"></div>
    </div>
  </div>

</template>
<script>
export default {
  data() {
    return {
      userInfo: {
        userId: '',
        name: '',
        avatar: ''
      },
      categories: {
        fiction: {
          name: '小說',
          items: [
            '文學小說', '科幻小說', '懸疑小說', '浪漫小說', '奇幻小說', '恐怖小說'
          ]
        },
        nonfiction: {
          name: '非小說',
          items: [
            '傳記', '歷史書籍', '科學', '心理學', '哲學', '藝術',
            '健康', '旅遊', '烹飪', '財經', '教育', '宗教'
          ]
        }

      },
      isMobileMenuOpen: false,
      isMainMenuOpen: false,
      isSecondaryMenuOpen: false,
      currentCategories: {},
      searchValue: ''

    };
  },
  computed: {
    //觀察store是否有更新購物車數量
    cartQuantity() {
      return this.$store.state.userData.cartProductQuantity;
    }


  },
  methods: {
    goToMemberCenter() {
      window.location.href = `/member-center`;
    },
    goToSearchPage(category) {
      window.location.href = `/search?Cat=${category}`;
    },
    backToMainMenu() {
      this.isMainMenuOpen = true;
      this.isSecondaryMenuOpen = false;
    },
    selectMenu(menu) {
      this.isMainMenuOpen = false;
      this.isSecondaryMenuOpen = true;
      this.currentCategories = this.categories[menu];
    },
    openMenu() {
      this.isMobileMenuOpen = true;
      this.isMainMenuOpen = true;
      //避免滾動到menu後面的父元素
      document.body.style.overflow = 'hidden';

    },
    closeMenu() {
      this.isSecondaryMenuOpen = false;
      this.isMainMenuOpen = false;
      this.isMobileMenuOpen = false;
      document.body.style.overflow = '';
    },
    home() {
      if (this.$route.path !== '/') {
        this.$router.push('/');
      }
    },
    updateQueryParams() {
      const path = this.$route.path;
      const currentQuery = {...this.$route.query};
      if (this.searchValue) {
        currentQuery.q = this.searchValue;
      } else {
        delete currentQuery.q;
      }

      if (path !== '/search') {
        this.$router.push({path: '/search', query: currentQuery});
        return;
      }

      if (JSON.stringify(currentQuery) !== JSON.stringify(this.$route.query)) {
        this.$router.replace({ query: currentQuery });
      }
    },
    clearSearch() {
      this.searchValue = '';
      if (!this.$route.query.q) return;
      const {q, ...restQuery} = this.$route.query;
      this.$router.replace({query: restQuery}); //保留除了關鍵字以外的查詢條件
    }

  },
  mounted() {
  }
};
</script>

<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;

$header-height: 70px;
$border: 1px solid #e1e1e3;

.mobile-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  padding-top: .3rem;
}

.mobile-header__menu {
  width: 3rem;

  .menu-button {
    width: 2rem;
    height: 2rem;
    border: none;
    margin-left: 1rem;
    background-color: transparent;
    cursor: pointer;

    .menu-icon {
      width: 100%;
    }
  }

  .mobile-header__menu-wrapper {
    position: fixed;
    transform: translateX(-100%);
    transition: transform ease-in-out .3s;
    top: $header-height;
    width: 100%;
    height: calc((100% - #{$header-height})); //要減掉header高度 滾輪才會正常計算
    z-index: 999;
    border-top: $border;
    background-color: #ffffff;

    &.active {
      transform: translateX(0);
    }

    .main-menu {
      transform: translateX(-100%);
      transition: transform ease-in-out .3s;

      &.active {
        transform: translateX(0);
      }

      .menu-list {
        display: flex;
        flex-direction: column;

        .menu-list-item {
          border-bottom: $border;
          font-size: 1.1rem;

          & > * {
            padding: 1rem 1.357rem;
          }

          .member-center {
            display: flex;
            justify-content: space-between;
            text-decoration: none;
            color: #000000;
          }

          .menu-list__title {
            display: flex;
            justify-content: space-between;
          }

        }
      }
    }

    .secondary-menu {
      position: absolute;
      top: 0;
      width: 100%;
      height: 100%;
      transform: translateX(100%);
      transition: transform ease-in-out .3s;
      visibility: hidden;

      &.active {
        visibility: visible;
        transform: translateX(0);
      }

      .secondary-manu-ul {
        font-size: 1.1rem;
        display: flex;
        flex-direction: column;

        .secondary-manu-title {
          display: flex;
          font-size: 1.1rem;
          padding: 1rem 1rem;
          border-bottom: $border;
          font-weight: 600;

          img {
            margin-right: .58rem;
          }
        }

        .secondary-manu-li {
          border-bottom: $border;
          padding: 1rem 2rem;
        }
      }
    }


  }
}

.mobile-header__logo {
  width: 10rem;
  cursor: pointer;

  .desktop-header__logo {
    width: 100%
  }
}

.mobile-header__cart {
  position: relative;
  width: 2.5rem;
  margin-right: .7rem;

  .desktop-header__cart-icon {
    width: 100%;
  }

  .desktop-header__cart-count {
    position: absolute;
    right: -5px;
    top: -5px;

    &::after {
      content: attr(data-count);
      display: flex;
      justify-content: center;
      align-items: center;
      width: 1.5rem;
      height: 1.5rem;
      color: #ffffff;
      background-color: #4d7a67;
      border-radius: 50%;
    }
  }
}

.mobile-header__search {
  position: relative;
  flex-basis: 100%;
  padding: 1rem;
  background-color: rgba(114, 147, 67, 0.25);

  .desktop-header__search-icon {
    position: absolute;
    width: 1.2rem;
    height: 1.2rem;
    left: 25px;
    top: 50%;
    transform: translateY(-50%);
    background-image: url("@/assets/images/search.svg");
    background-size: cover;
    z-index: 9;
  }

  .desktop-header__search-bar {
    width: 100%;
    height: 2rem;
    border-radius: 1.2rem;
    background-color: #ffffff;
    border: 1.2px solid #aaa;
    outline: none;
    padding: 0 2rem;

  }

  .desktop-header__search-clear-button {
    position: absolute;
    top: 50%;
    right: 25px;
    transform: translateY(-50%);
    width: 1.2rem;
    height: 1.2rem;
    display: flex;
    justify-content: center;
    align-items: center;

    &::after {
      position: absolute;
      content: '';
      background-image: url("@/assets/images/form-cross-black.svg");
      width: 1rem;
      height: 1rem;
      background-size: cover;
    }
  }
}


.no-scrollbar {
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.no-scrollbar::-webkit-scrollbar {
  display: none;
}

@media screen and (min-width: 768px) {
  .mobile-header {
    display: none;
  }
}
</style>