<template>
  <div style="height: 100%">
    <div class="book-manage">
      <div class="book-manage__header">
        <!-- 搜尋鈕 -->
        <el-row>
          <el-button class="primary" @click="isSearchModalVisible = true"
                     v-if=!this.isSearchMode()>查詢書籍
          </el-button>
          <el-button type="success" @click="leaveSearch()"
                     v-if=this.isSearchMode()> 返回顯示所有書籍
          </el-button>
          <span style="padding-left: 20px">總共{{ totalItems }}筆資料</span>
        </el-row>
      </div>
      <!--資料表-->
      <BookTable :data="bookDataArray">
        <template #action="{row}">
          <div class="book-manage__edit-btn">
            <el-button size="mini" class="primary" @click="relist(row.id)">重新上架</el-button>
          </div>
        </template>
      </BookTable>
      <!-- 分頁 -->
      <div class="book-manage__pagination">
        <el-pagination layout="prev, pager, next" :total="totalItems" :page-size="pageSize"
                       @current-change="handleCurrentPage" :current-page.sync="currentPage">
        </el-pagination>
      </div>
      <!--搜尋視窗-->
      <SearchForm ref="searchForm"
                  :visible.sync="isSearchModalVisible"
                  @submit="handleSearch">
      </SearchForm>
    </div>
  </div>
</template>


<script>
import {searchBooks, getDeletedBooks, relistBook} from '@/api/admin';
import SearchForm from '@/components/admin/BookManage/SearchForm.vue';
import BookTable from '@/components/admin/BookManage/BookTable.vue';

export default {
  components: {BookTable, SearchForm},
  data() {
    return {
      isSearchModalVisible: false,
      lastSearchForm: {},
      bookDataArray: [],
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      category: [
        {name: '文學小說', value: '文學小說'},
        {name: '商業理財', value: '商業理財'},
        {name: '藝術設計', value: '藝術設計'},
        {name: '人文社科', value: '人文社科'},
        {name: '心理勵志', value: '心理勵志'},
        {name: '宗教命理', value: '宗教命理'},
        {name: '自然科普', value: '自然科普'},
        {name: '醫療保健', value: '醫療保健'}
      ]
    };

  }, methods: {
    leaveSearch() {
      this.lastSearchForm = {};
      this.currentPage = 1;
      this.getDeletedBooks(this.currentPage, this.pageSize);
    },
    closeSearchForm() {
      this.isSearchModalVisible = false;
      this.$refs.searchForm.searchForm = {};
    },
    async handleSearch(searchForm) {
      const noSearchCondition = Object.values(searchForm).every(item => item === '');  //檢查是否至少有輸入一個條件
      if (noSearchCondition) {
        this.$message.warning('請輸入搜尋條件');
        return;
      }
      this.lastSearchForm = {...searchForm}; //爲了點擊分頁時能夠找到搜尋結果
      const books = await searchBooks(searchForm, 1);
      const {content, totalElements} = books?.data;
      if (content.length === 0) {
        this.$message.info('查無此書籍');
        this.lastSearchForm = {};
      } else {
        this.$message.success('查詢成功');
        this.bookDataArray = content;
        this.totalItems = totalElements;
        this.currentPage = 1;
      }
      this.$refs.searchForm.searchForm = {};
      this.isSearchModalVisible = false;
    },

    async handleCurrentPage(val) {
      this.currentPage = val;
      //如果有使用搜尋，就用搜尋條件找出分頁，如果沒有就直接獲取所有書籍
      if (this.isSearchMode()) {
        const books = await searchBooks({...this.lastSearchForm, currentPage: this.currentPage}, 1);
        this.bookDataArray = books.data.content;
      } else {
        await this.getDeletedBooks(this.currentPage, this.pageSize);
      }
    },
    isSearchMode() {
      return Object.keys(this.lastSearchForm).length > 0;
    },

    async relist(id) {
      try {
        await this.$confirm('確定重新上架此書籍?', '提示', {
          confirmButtonText: '確定',
          cancelButtonText: '取消'
        });
        const {code} = await relistBook(id);
        if (code === 200) {
          await this.reloadBook(this.currentPage, this.pageSize);
          this.$message.success('書籍已重新上架');
        }
      } catch (error) {
        //取消不提示
      }
    },
    async reloadBook() {
      //判斷是不是搜尋狀態，如果是搜尋狀態，則用搜尋條件去獲取分頁資料
      if (this.isSearchMode()) {
        await this.loadSearchedBooks();
      } else {
        await this.getDeletedBooks(this.currentPage, this.pageSize);
      }
    },
    async loadSearchedBooks() {
      const result = await searchBooks({...this.lastSearchForm, currentPage: this.currentPage}, 1);
      if (result.code !== 200) {
        this.$message.error('無法獲取書籍，稍後再試');
      }
      const books = result.data.content;
      this.totalItems = result.data.totalElements;
      if (books.length === 0 && this.currentPage > 1) {
        this.currentPage -= 1;
        return this.loadSearchedBooks();
      }
      this.bookDataArray = books;
    },
    async getDeletedBooks(currentPage, pageSize) {
      const books = await getDeletedBooks(currentPage, pageSize);
      const {content} = books;
      if (content.length === 0 && this.currentPage > 1) {
        this.currentPage -= 1;
        return this.getDeletedBooks(this.currentPage, this.pageSize);
      }
      this.totalItems = books.totalElements;
      this.bookDataArray = books.content;

    }
  },
  mounted() {
    this.getDeletedBooks(this.currentPage, this.pageSize);
  }
};
</script>

<style lang="scss" scoped>
@use '@/assets/style/abstracts' as *;

.table-expand {
  display: flex;
  justify-content: space-around;

  & > * {
    height: 1rem;
  }
}

.book-manage {
  height: 90%;

  .book-manage__header {
    height: 60px;
    display: flex;
    justify-content: space-between
  }

  .book-manage__edit-btn {
    & > * {
      max-width: 6rem;
      padding: .6rem .8rem;
      font-size: .8rem;
    }
  }
}

.book-manage__pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

 .primary {
  background-color: $clr-primary;
  color: white;
}

</style>