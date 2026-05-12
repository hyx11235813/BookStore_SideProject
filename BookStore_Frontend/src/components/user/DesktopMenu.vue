<template>
  <div class="desktop-header">
    <div class="desktop-header__container">

      <img class="desktop-header__logo" src="@/assets/images/book-store-logo.svg" alt="" @click="home">
      <!--    Menu-->
      <div :class="['desktop-header__category-button',{active:this.isMenuOpen}]" @click.stop="toggleMenu">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="#000000" viewBox="0 0 256 256">
          <path
              d="M231.65,194.55,198.46,36.75a16,16,0,0,0-19-12.39L132.65,34.42a16.08,16.08,0,0,0-12.3,19l33.19,157.8A16,16,0,0,0,169.16,224a16.25,16.25,0,0,0,3.38-.36l46.81-10.06A16.09,16.09,0,0,0,231.65,194.55ZM136,50.15c0-.06,0-.09,0-.09l46.8-10,3.33,15.87L139.33,66Zm6.62,31.47,46.82-10.05,3.34,15.9L146,97.53Zm6.64,31.57,46.82-10.06,13.3,63.24-46.82,10.06ZM216,197.94l-46.8,10-3.33-15.87L212.67,182,216,197.85C216,197.91,216,197.94,216,197.94ZM104,32H56A16,16,0,0,0,40,48V208a16,16,0,0,0,16,16h48a16,16,0,0,0,16-16V48A16,16,0,0,0,104,32ZM56,48h48V64H56Zm0,32h48v96H56Zm48,128H56V192h48v16Z"></path>
        </svg>
        <p class="desktop-header__category-button-name"> 書籍分類</p>
      </div>

      <div ref="menuRef"
           :class="['desktop-header__categories',{active:this.isMenuOpen}] ">
        <div class="desktop-header__categories-container ">
          <div class="desktop-header__category-wrapper">
            <div class="desktop-header__category-title">小說</div>
            <ul class="desktop-header__category-list">
              <li class="desktop-header__category-item" v-for="(cat,index) in categories.fiction"
                  @click="goToSearchPage(cat)"
                  :key="index">
                {{ cat }}
              </li>
            </ul>
          </div>
          <div class="desktop-header__category-wrapper">
            <div class="desktop-header__category-title">非小說</div>
            <ul class="desktop-header__category-list">
              <li class="desktop-header__category-item" v-for="(cat,index) in categories.nonfiction"
                  @click="goToSearchPage(cat)"
                  :key="index">
                {{ cat }}
              </li>
            </ul>
          </div>
        </div>
      </div>


      <!--      Search Bar-->
      <div class="desktop-header__search">
        <div class="desktop-header__search-wrapper">
          <span class="desktop-header__search-icon"></span>
          <input class="desktop-header__search-bar" type="text" v-model="searchValue"
                 placeholder="搜尋：作者、書名、出版商、ISBN"
                 @input="updateQueryParams">
          <div class="desktop-header__search-clear-button" v-if="searchValue" @click="clearSearch"></div>
        </div>
      </div>
      <div ref="userMenuRef"
           class="desktop-header__user-info">
        <img class="desktop-header__user-info-icon" :src="avatarSrc" alt="">
        <div class="desktop-header__user-info__wrapper" @click.stop="toggleUserMenu">
          <span class="desktop-header__user-info-name">{{ displayName }}</span>

          <img :class="['desktop-header__user-info-arrow-down-img',{active:isUserMenuOpen}]"
               src="@/assets/images/arrow-down.svg"
               alt="">
          <div class="desktop-header__user-info-dropdown">
            <div :class="['desktop-header__dropdown-menu',{active:isUserMenuOpen}]">
              <div class="desktop-header__dropdown-menu__container">
                <div class="desktop-header__dropdown-menu-wrapper">
                  <router-link to="/login" class="desktop-header__dropdown-menu-login " v-if="!isLogin">登入
                  </router-link>
                  <router-link to="/register" class="desktop-header__dropdown-menu-create-account secondary-button "
                               v-if="!isLogin">
                    創建帳號
                  </router-link>
                  <router-link to="/member-center/" class=" secondary-button" v-if="isLogin">會員中心</router-link>
                  <a class="logout " v-if="isLogin" @click="logout">登出</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <router-link to="/cart" class="desktop-header__cart">
        <img class="desktop-header__cart-icon" src="@/assets/images/cart.svg" alt="">
        <span class="desktop-header__cart-count" :data-count="cartQuantity"></span>
      </router-link>
    </div>
  </div>
</template>
<script>
import {getUserCartInfo} from '@/api/cart';

export default {
  data() {
    return {
      searchValue: '',
      userInfo: {
        userId: '',
        name: '',
        avatar: ''
      },
      categories: {
        fiction: [
          '文學小說', '科幻小說', '懸疑小說', '浪漫小說', '奇幻小說', '恐怖小說'
        ],
        nonfiction: [
          '傳記', '歷史書籍', '科學', '心理學', '哲學', '藝術', '健康', '旅遊', '烹飪', '財經', '教育', '宗教'
        ]
      },
      isMenuOpen: false,
      isUserMenuOpen: false
    };
  },
  watch: {
    userInfoStore() {
      this.getUserInfoFromStore();
    }
  },
  computed: {
    cartQuantity() {
      return this.$store.state.userData.cartProductQuantity;
    },
    userInfoStore() {
      return this.$store.state.userData.user;
    },
    avatarSrc() {
      return this.userInfo.avatar || require('@/assets/images/circle-user-white.svg');
    },
    displayName() {
      if (!this.userInfo.userId) return '未登入';
      return this.userInfo.name.length > 5
          ? this.userInfo.name.substring(0, 5) + '...'
          : this.userInfo.name;
    },
    isLogin() {
      return !!this.userInfo.userId;
    }
  }
  ,
  methods: {
    goToSearchPage(cat) {
      if (cat === this.$route.query.Cat) return;
      this.searchValue = '';
      this.$router.push(`/search?Cat=${cat}`);
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
        this.$router.replace({query: currentQuery});
      }
    },
    logout() {
      this.$store.dispatch('logout');
      if (this.$route.path !== '/') {
        this.$router.push('/');
      }
    },
    home() {
      if (this.$route.path !== '/') {
        this.$router.push('/');
      }
    },
    clearSearch() {
      this.searchValue = '';
      const {q, ...query} = {...this.$route.query};
      console.log(query);
      this.$router.replace({query});
    },
    async getUserCartQuantity(userId) {
      //使用者購物車數量
      const res = await getUserCartInfo(userId);
      const userCartQuantity = res.data?.length ?? 0;

      //localStorage購物車數量
      const localCart = JSON.parse(localStorage.getItem('cart') || '{}');
      const localCartQuantity = Object.keys(localCart).length;

      const finalShowQuantity = userCartQuantity || localCartQuantity;
      this.$store.commit('setCartProductQuantity', finalShowQuantity);
    },
    getUserInfoFromStore() {
      this.userInfo.userId = this.$store.state.userData.user.id || 0;
      this.userInfo.avatar = this.$store.state.userData.user.avatar;
      this.userInfo.name = this.$store.state.userData.user.name;

    },
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
      this.bindClickOutside('menuRef', 'isMenuOpen');
    },
    toggleUserMenu() {
      this.isUserMenuOpen = !this.isUserMenuOpen;
      this.bindClickOutside('userMenuRef', 'isUserMenuOpen');
    },
    bindClickOutside(ref, state) {
      const handler = (event) => {
        if (this.$refs[ref] && !this.$refs[ref].contains(event.target)) {
          this[state] = false;
        }
        window.removeEventListener('click', handler);
      };

      if (this[state]) {
        window.addEventListener('click', handler);
      }
    }
  }, mounted() {
    this.getUserInfoFromStore();
    this.getUserCartQuantity(this.userInfo.userId);
    this.searchValue = this.$route.query.q;
  }

};
</script>
<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;

.desktop-header {
  width: 100%;
  background-color: #ffffff;
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.2);

  .desktop-header__container {
    position: relative;
    display: flex;
    align-items: center;
    max-width: 1150px;
    padding: .5rem 1rem;
    margin: auto;
  }
}

.desktop-header__logo {
  width: clamp(9rem, 100%, 13rem);
  margin-right: 2rem;
  cursor: pointer;
}

.desktop-header__category-button {
  display: flex;
  align-items: center;
  padding: 4px 5px;
  cursor: pointer;
  border-radius: 5px;
  transition: all 50ms linear;
  border: 1px solid #dcdcdc;

  &.active {
    box-shadow: inset 0 2px 6px 0 rgba(166, 166, 166, 0.68);
    background-color: rgba(246, 246, 246, 0.82);
    transform: scale(0.95);
  }

  .desktop-header__category-button-name {
    user-select: none;
    text-wrap: nowrap;
    font-size: .9rem;
  }


}

.desktop-header__categories {
  position: absolute;
  top: 100%;
  left: 250px;
  border: 1px solid #dcdcdc;
  border-radius: 4px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, .175);
  z-index: 9;
  display: none;

  &.active {
    display: block;
  }

  .desktop-header__categories-container {
    display: flex;
    gap: 1rem;
    background-color: #ffffff;

    .desktop-header__category-wrapper {
      margin: 1rem 1rem;

      .desktop-header__category-title {
        font-size: 1.2rem;
        padding: 0 0 5px;
        border-bottom: 1px solid #dcdcdc;
      }

      .desktop-header__category-list {
        flex-wrap: wrap;

        .desktop-header__category-item {
          position: relative;
          cursor: pointer;
          list-style: none;
          padding: 5px;
          line-height: 1;

          &:hover {
            background-color: #f5f5f5;
            color: $clr-hover-green
          }

        }
      }
    }
  }
}

.desktop-header__search {
  width: clamp(10rem, 100%, 36rem);
  padding: 0 2rem;

  .desktop-header__search-wrapper {
    display: flex;
    width: 100%;
    height: 2.4rem;
    border: 1.2px solid #aaa;
    border-radius: 1.2rem;
    background-color: #ffffff;

    .desktop-header__search-icon {
      position: relative;
      width: 2.4rem;
      height: 100%;

      &::before {
        position: absolute;
        content: '';
        background-image: url("@/assets/images/search.svg");
        background-size: cover;
        width: 1.2rem;
        height: 1.2rem;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
    }

    .desktop-header__search-bar {
      width: 100%;
      height: 100%;
      border: none;
      outline: none;
      border-radius: 0 1.2rem 1.2rem 0;
      background-color: #ffffff;
    }

    .desktop-header__search-clear-button {
      position: relative;
      width: 2.4rem;
      height: 100%;
      cursor: pointer;

      &::before {
        position: absolute;
        content: '';
        background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0NCIgaGVpZ2h0PSI0NCIgZmlsbD0iIzU0NTQ1NCIgdmlld0JveD0iMCAwIDI1NiAyNTYiPjxwYXRoIGQ9Ik0yMDUuNjYsMTk0LjM0YTgsOCwwLDAsMS0xMS4zMiwxMS4zMkwxMjgsMTM5LjMxLDYxLjY2LDIwNS42NmE4LDgsMCwwLDEtMTEuMzItMTEuMzJMMTE2LjY5LDEyOCw1MC4zNCw2MS42NkE4LDgsMCwwLDEsNjEuNjYsNTAuMzRMMTI4LDExNi42OWw2Ni4zNC02Ni4zNWE4LDgsMCwwLDEsMTEuMzIsMTEuMzJMMTM5LjMxLDEyOFoiPjwvcGF0aD48L3N2Zz4=");
        background-size: cover;
        width: 1.2rem;
        height: 1.2rem;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
    }
  }

}

.desktop-header__user-info {
  display: flex;
  align-items: center;
  margin-right: 1rem;
  padding-right: 1rem;
  border-right: 1px solid #aaa;

  .desktop-header__user-info-icon {
    width: 2rem;
    height: 2rem;
    margin-right: 1rem;
    border-radius: 50%;
  }

  .desktop-header__user-info__wrapper {
    position: relative;
    display: flex;
    width: 6rem;
    gap: 10px;
    border-bottom: 2px solid transparent;

    .desktop-header__user-info-name {
      cursor: pointer;
      font-size: .9rem;
    }

    .desktop-header__user-info-arrow-down-img {
      width: 10px;
      transition: transform 100ms ease;

      &.active {
        transform: rotate(-180deg);
      }
    }

    .desktop-header__user-info-dropdown {
      position: absolute;
      top: 40px;
      z-index: 999;

      .desktop-header__dropdown-menu {
        display: flex;
        flex-direction: column;
        width: 8rem;
        visibility: hidden;

        &.active {
          visibility: visible;
        }

        .desktop-header__dropdown-menu__container {
          display: flex;
          flex-direction: column;
          border: 1px solid #dcdcdc;
          border-radius: 4px;
          box-shadow: 0 6px 12px rgba(0, 0, 0, .175);
          background-color: #ffffff;

          & > .link {
            padding: 5px 20px;
            border-left: 2px solid transparent;
            text-decoration: none;
            color: #111111;
            cursor: pointer;
          }

          .desktop-header__dropdown-menu-wrapper {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin: 7px 0;

            .desktop-header__dropdown-menu-login {
              width: 75%;
              padding: 6px 12px;
              margin-bottom: 7px;
              background-color: #346250;
              color: white;
              text-align: center;
              cursor: pointer;
              transition: background-color 300ms ease;
              text-decoration: none;

              &:hover {
                background-color: #4d7a67;
              }
            }

            .desktop-header__dropdown-menu-create-account {
              border-bottom: 1px solid #aaa;
              cursor: pointer;
              color: #111111;
              text-decoration: none;

              &:hover {
                color: #346250;
              }
            }

          }



          .secondary-button {
            font-size: .85rem;
            color: #000000;
            text-decoration: none;
            &:hover{
              color: #346250;
            }
          }

          .logout {
            width: 75%;
            padding: 6px 12px;
            border-top: 1px solid #aaa;
            margin: 10px 0 5px;
            align-self: center;
            background-color: #346250;
            color: white;
            text-align: center;
            cursor: pointer;
            transition: background-color 300ms ease;

            &:hover {
              background-color: #4d7a67;
            }
          }
        }
      }
    }
  }
}

.desktop-header__cart {
  position: relative;
  border-bottom: 2px solid transparent;
  cursor: pointer;

  .desktop-header__cart-icon {
    width: 2.5rem;
    margin-right: 1rem;
  }

  .desktop-header__cart-count {
    position: absolute;
    top: 0;
    right: 10px;

    &::after {
      content: attr(data-count); // 顯示購物車數量
      display: flex;
      justify-content: center;
      align-items: center;
      width: 1.5rem;
      height: 1.5rem;
      background-color: #4d7a67;
      color: #ffffff;
      font-weight: 700;
      border-radius: 50%;
    }
  }
}


@media screen and (max-width: 768px) {
  .desktop-header {
    display: none;
  }
}

</style>