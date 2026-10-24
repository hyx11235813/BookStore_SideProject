<template>
  <nav class="nav">
    <ul>
      <li v-for="i in asideList" :key="i.name">
        <div :class="{ active:  $route.path === i.path }"
             @click="clickMenu(i)">{{ i.label }}
        </div>
      </li>
    </ul>
  </nav>
</template>
<script>

export default {
  data() {
    return {
      asideList: [
        {name: 'personalInfo', label: '個人資料', path: '/member-center/personal-info'},
        {name: 'borrowRecord', label: '購買記錄', path: '/member-center/borrowRecord'}
      ],
    };
  },
  methods: {
    clickMenu(item) {
      if (item.path !== this.$route.path) {
        this.$router.push(item.path);
      }
    },
  },
};
</script>
<style lang="scss" scoped>
@use '@/assets/style/abstracts' as *;

.nav {
  border-bottom: $clr-border-light;
  display: none;

  ul {
    display: flex;
    justify-content: space-around;
    list-style-type: none;

    li {
      padding: 16px 24px 8px;
      position: relative;
      cursor: pointer;

      div {
        color: $clr-text-gray;

        &:hover,
        &.active {
          color: $clr-hover-green;

          &::after {
            display: block;
          }

        }

        &::after {
          content: '';
          position: absolute;
          display: none;
          text-align: center;
          border-bottom: 4px solid $clr-hover-green;
          border-radius: 10px 10px 0 0;
          bottom: 0;
          left: 26px;
          right: 26px;
        }
      }
    }
  }
}

@media (max-width: 1024px) {
  .nav {
    display: block;
  }
}
</style>