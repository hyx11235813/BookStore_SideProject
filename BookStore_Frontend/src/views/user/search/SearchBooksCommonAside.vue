<template>
  <aside class="sidebar">
    <div class="sidebar__container">
      <div class="sidebar__control">
        <div class="sidebar__control-reset" @click="resetFilter">
          <a >重置篩選</a>
        </div>
        <div :class="['sidebar__control-filter',{active:isFilterOpen}]" @click="toggleFilter">
          <a class="filter-button">篩選...</a>
        </div>
      </div>
      <div class="sidebar__wrapper" v-show="isFilterOpen">
        <div class="sidebar__header">
          <p>排行</p>
        </div>
        <div class="sidebar__lists-wrapper">
          <ul class="sidebar__lists-ul">
            <li class="sidebar__lists-li" v-for="(rank,index) in rankTags" :key="index">
              <a class="sidebar__lists-li-a">
                <span class="sidebar__lists-li-text" :class="{'selected':isSelected('selectedRank',rank.value)}"
                      @click="toggleTag('selectedRank',rank.value)">
                  {{ rank.name }}
                </span>
              </a>
            </li>
          </ul>
        </div>
        <div class="sidebar__header">
          <p>書籍分類</p>
        </div>
        <div class="sidebar__lists-wrapper">
          <ul class="sidebar__lists-ul">
            <li class="sidebar__lists-li" v-for="(category,index) in categories" :key="index">
              <a class="sidebar__lists-li-a">
                <span class="sidebar__lists-li-text" :class="{'selected':isSelected('selectedCategory',category.value)}"
                      @click="toggleTag('selectedCategory',category.value)">
                  {{ category.name }}
                </span>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </aside>
</template>
<script>
//todo 修改樣式間距

export default {
  data() {
    return {
      rankTags: [
        {name: '熱銷 Top100', value: 'r1'},
        {name: '新上架 Top100', value: 'r2'}
      ],
      categories: [
        {name: '文學小說', value: '文學小說'},
        {name: '科幻小說', value: '科幻小說'},
        {name: '懸疑小說', value: '懸疑小說'},
        {name: '浪漫小說', value: '浪漫小說'},
        {name: '奇幻小說', value: '奇幻小說'},
        {name: '恐怖小說', value: '恐怖小說'},
        {name: '傳記', value: '傳記'},
        {name: '歷史書籍', value: '歷史書籍'},
        {name: '科學', value: '科學'},
        {name: '心理學', value: '心理學'},
        {name: '哲學', value: '哲學'},
        {name: '藝術', value: '藝術'},
        {name: '健康', value: '健康'},
        {name: '旅遊', value: '旅遊'},
        {name: '烹飪', value: '烹飪'},
        {name: '財經', value: '財經'},
        {name: '教育', value: '教育'},
        {name: '宗教', value: '宗教'}
      ],
      selectedRank: '',
      selectedCategory: '',
      isFilterOpen: false
    };
  },
  watch: {
    '$route.query.Rank': {
      handler(newRank) {
        this.selectedRank = newRank || '';
      }
    },
    '$route.query.Cat': {
      handler(newCat) {
        this.selectedCategory = newCat || '';
      }
    }
  },

  methods: {
    isSelected(field, value) {
      return this[field].includes(value);
    },
    toggleTag(field, value) {
      this[field] = this[field] === value ? '' : value;
      this.updateQueryParams();
      this.isFilterOpen = false;
    },
    updateQueryParams() {
      const query = {
        q: this.$route.query.q || '',
        Rank: this.selectedRank || '',
        Cat: this.selectedCategory || ''
      };
      //先轉爲Array排除掉空的value，再轉回物件
      const filteredQuery = Object.fromEntries(Object.entries(query).filter(([_, value]) => value));
      this.$router.replace({
        name: 'search',
        query: filteredQuery //透過watch來監聽
      });
    },
    toggleFilter() {
      this.isFilterOpen = !this.isFilterOpen;
    },
    resetFilter() {
      this.selectedRank = '';
      this.selectedCategory = '';
      if (this.$route.query.Rank || this.$route.query.Cat) {
        this.$router.replace({
          name: 'search'
        });
      }
    }
  },
  mounted() {
    this.selectedRank = this.$route.query.Rank || '';
    this.selectedCategory = this.$route.query.Cat || '';
  }
};

</script>

<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;
.sidebar {
  width: 12rem;
  min-height: calc(100vh - 5rem);

  .sidebar__container {
    width: 100%;
    height: 100%;
    padding: 1.5rem .7rem 0;

    .sidebar__control {
      width: 100%;
      display: flex;
      justify-content: space-between;

      & > div {
        padding: .2rem 0;
        margin-bottom: 1rem;
        border-radius: 2px;
        text-align: center;
        cursor: pointer;
      }

      .sidebar__control-reset {
        flex-basis: 5rem;
        border: 1px solid $clr-btn-green;

        a {
          width: 100%;
          color: $clr-btn-green;
          text-decoration: none;
          pointer-events: none;
        }

        &:hover {
          background-color: $clr-btn-green;

          a {
            color: white;
          }
        }
      }


      .sidebar__control-filter {
        flex-basis: 5rem;
        display: none;
        background-color: gray;
        justify-content: center;
        align-items: center;

        .filter-button {
          width: 100%;
          color: black;
          text-decoration: none;
          user-select: none;
          pointer-events: none;

          &.active {
            content: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0iIzAwMDAwMCIgdmlld0JveD0iMCAwIDI1NiAyNTYiPjxwYXRoIGQ9Ik0yMDguNDksMTkxLjUxYTEyLDEyLDAsMCwxLTE3LDE3TDEyOCwxNDUsNjQuNDksMjA4LjQ5YTEyLDEyLDAsMCwxLTE3LTE3TDExMSwxMjgsNDcuNTEsNjQuNDlhMTIsMTIsMCwwLDEsMTctMTdMMTI4LDExMWw2My41MS02My41MmExMiwxMiwwLDAsMSwxNywxN0wxNDUsMTI4WiI+PC9wYXRoPjwvc3ZnPg==");
            width: 1rem;
            height: 1rem;
          }


        }

        &:hover {
          a {
            color: blue
          }
        }
      }


    }

    .sidebar__wrapper {

      .sidebar__header {
        background-color: #3a594d;
        border-radius: 5px;
        padding: .5rem;

        p {
          font-weight: bold;
          color: white;
          cursor: default;
        }
      }

      //Sidebar 選項清單-------
      .sidebar__lists-wrapper {
        padding: 0.5rem 0;
        .sidebar__lists-ul {
          list-style-type: none;

          .sidebar__lists-li {
            line-height: 2rem;
            font-size: .8rem;
            padding: 0 8px;

            .sidebar__lists-li-a {
              .sidebar__lists-li-text {
                font-size: .9rem;
                cursor: pointer;
                user-select: none;

                &.selected {
                  padding: .2rem .5rem;
                  color: #ffffff;
                  background-color: $clr-btn-green;
                  border-radius: 5px;
                }

                &.selected::after { //加上X符號
                  content: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0iI2ZmZmZmZiIgdmlld0JveD0iMCAwIDI1NiAyNTYiPjxwYXRoIGQ9Ik0yMDguNDksMTkxLjUxYTEyLDEyLDAsMCwxLTE3LDE3TDEyOCwxNDUsNjQuNDksMjA4LjQ5YTEyLDEyLDAsMCwxLTE3LTE3TDExMSwxMjgsNDcuNTEsNjQuNDlhMTIsMTIsMCwwLDEsMTctMTdMMTI4LDExMWw2My41MS02My41MmExMiwxMiwwLDAsMSwxNywxN0wxNDUsMTI4WiI+PC9wYXRoPjwvc3ZnPg==');
                  width: 1rem;
                  height: 1rem;
                  position: relative;
                  top: 3px;
                  left: 5px;
                }

                &:not(.selected):hover {
                  color: $clr-btn-green;
                }
              }

            }
          }
        }
      }
    }
  }
}

@media screen and (min-width: 768px) {
  .sidebar__wrapper {
    display: block !important;
  }
}

@media screen and (max-width: 768px) {
  .sidebar {
    min-height: 100%;
    width: 100%;

    .sidebar__control {
      flex-direction: row-reverse;

      .sidebar__control-filter {
        display: flex !important;
      }
    }
  }

}
</style>