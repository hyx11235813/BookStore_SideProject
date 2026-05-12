<template>
  <div>
    <div class="search-books">
      <div class="search-books__container">
        <aside>
          <SearchBooksCommonAside/>
        </aside>
        <BookList/>
      </div>
    </div>
  </div>
</template>

<script>
import SearchBooksCommonAside from "@/views/user/search/SearchBooksCommonAside.vue";
import BookList from "@/views/user/search/BookList.vue";


export default {
  components: {
    SearchBooksCommonAside,
    BookList
  },
}
</script>

<style scoped lang="scss">

.search-books {
  .search-books__container {
    max-width: 1150px;
    height: 100%;
    margin: auto;
    display: flex;
  }
}

@media screen and (max-width: 768px) {
 .search-books__container{
   flex-direction: column;
 }
}
</style>