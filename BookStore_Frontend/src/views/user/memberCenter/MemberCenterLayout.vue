<template>
  <div>
    <header>
      <Header/>
    </header>
    <main class="main">
      <aside class="sidebar">
        <MemberCenterAside/>
      </aside>
      <div class="content">
        <PersonalInfo_Nav/>
        <router-view/>
      </div>
    </main>
  </div>
</template>
<script>
import Header from '@/components/user/MemberCenterHeader.vue';
import MemberCenterAside from '@/components/user/MemberCenterAside.vue';
import PersonalInfo_Nav from './PersonalInfo_Nav.vue';

export default {
  components: {
    Header,
    MemberCenterAside,
    PersonalInfo_Nav
  }
};

</script>
<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.main {
  display: flex;
  height: 100vh;

  .sidebar {
    width: 280px;
  }

  .content {
    width: 300px;
    flex: 1;
  }
}

@media (max-width: 1024px) {
  aside {
    display: none;
  }
}

</style>