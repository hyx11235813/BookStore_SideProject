<template>
  <div style="height: 100%">
    <div class="book-manage">
      <div class="book-manage__header">
        <!-- 搜尋鈕 -->
        <HeaderButton :last-search-form="lastSearchForm"
                      :total-items="totalItems"
                      @openSearchForm="openSearchModal"
                      @showAddBookModal="openAddBookModal"
                      @closeSearchForm="showAllBooks">
        </HeaderButton>

      </div>
      <!--資料表-->
      <BookTable :data="tableData">
        <template #action="{row}">
          <el-button size="mini" class="primary" @click="handleEdit(row.id)">編輯</el-button>
          <el-button size="mini" class="danger" @click="handleDelete(row.id)">下架</el-button>
        </template>

      </BookTable>
      <!-- 分頁 -->
      <div class="book-manage__pagination">
        <el-pagination layout="prev, pager, next" :total="totalItems" :page-size="pageSize"
                       :current-page.sync="currentPage"
                       @current-change="handleCurrentPage">
        </el-pagination>
      </div>
    </div>
    <!-- 新增視窗 -->
    <AddBookForm ref="bookForm"
                 :visible.sync="isBookModalVisible"
                 :modalType.sync="modalType"
                 @submit="submit">
    </AddBookForm>
    <!--搜尋視窗-->
    <SearchForm ref="searchForm"
                :visible.sync="isSearchModalVisible"
                @submit="handleSearch">
    </SearchForm>
  </div>
</template>

<script>
import {
  getBooks,
  saveBook,
  softDeleteBook,
  getBookById,
  searchBooks, searchBookByISBN
} from '@/api/admin';
import BookTable from '@/components/admin/BookManage/BookTable.vue';
import HeaderButton from '@/components/admin/BookManage/HeaderButton.vue';
import AddBookForm from '@/components/admin/BookManage/AddBookForm.vue';
import SearchForm from '@/components/admin/BookManage/SearchForm.vue';

export default {
  components: {AddBookForm, SearchForm, HeaderButton, BookTable},
  data() {
    return {
      modalType: '',      // 0:編輯,1:新增
      //Modal
      isBookModalVisible: false,
      isSearchModalVisible: false,
      //Page
      currentPage: 1,  //當前頁數
      pageSize: 10,   //每頁顯示的筆數
      totalItems: 0,  //資料總量
      lastSearchForm: {},//拷貝搜尋表單
      //Form
      tableData: [],//書籍數據表
      bookCoverUrl: '', //使用者選擇的圖片
      api_bookCoverUrl: ''   // 從api取得的圖片

    };
  }, methods: {
    openSearchModal() {
      this.isSearchModalVisible = true;
      this.$refs.searchForm.searchForm = {};
    },
    openAddBookModal() {
      this.isBookModalVisible = true;
      this.modalType = 1;
    },

    closeAddBookModal() {
      this.isBookModalVisible = false;
      this.$refs.bookForm.form = {};
    },
    showAllBooks() {
      this.lastSearchForm = {};
      this.currentPage = 1;
      this.getBooks(this.currentPage, this.pageSize);
    },
    async handleCurrentPage(val) {
      this.currentPage = val;
      //如果有使用搜尋，就用searchBook，如果沒有就根據所有書籍進行分頁
      await this.reloadBook();

    },

    async handleEdit(id) {
      this.isBookModalVisible = true;
      this.modalType = 0;
      this.$refs.bookForm.id = id;
      this.$refs.bookForm.form = (await getBookById(id)).data;
    },
    async handleDelete(id) {
      try {
        await this.$confirm('確定下架此書籍?', '提示', {
          confirmButtonText: '確定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        const {code} = await softDeleteBook(id);
        if (code === 200) {
          await this.reloadBook();
          this.$message.success('書籍已下架');
        } else {
          this.$message.error('刪除失敗，請稍後再試');
        }
      } catch (error) {
        this.$message.error('操作失敗，請稍後再試');
      }
    },
    async handleSearch(searchForm) {
      const noSearchCondition = Object.values(searchForm).every(item => item === '');  //檢查是否至少有輸入一個條件
      if (noSearchCondition) {
        this.$message.warning('請輸入搜尋條件');
        return;
      }
      try {
        this.lastSearchForm = {...searchForm}; //保存搜尋條件，爲了點擊分頁時，能夠根據搜尋條件找到分頁結果
        const {data} = await searchBooks(searchForm, 0);
        const {content, totalElements} = data;
        if (content.length === 0) {
          this.$message.info('查無資料');
          this.lastSearchForm = {}; //搜尋結果為空，不保留搜尋條件
        } else {
          this.$message.success('查詢成功');
          this.tableData = content;
          this.totalItems = totalElements;
          this.currentPage = 1;     // 搜尋結果從第一頁開始
        }
      } catch (error) {
        this.$message.error('搜尋失敗，請稍後再試');
      }
      this.$refs.searchForm.searchForm = {};
      this.isSearchModalVisible = false;
    },
    async submit() {
      this.$refs.bookForm.$refs.modalForm.validate(async (isValid) => {
        if (!isValid) return;
        try {
          const form = this.$refs.bookForm.gerFormData();
          const formData = this.createFormData(form);
          //編輯
          if (this.modalType === 0) {
            await this.editBook(formData);
          }
          //新增
          if (this.modalType === 1) {
            await this.addBook(formData);
          }
        } catch (error) {
          console.error(error);
          this.$message.error('系統錯誤，請稍後再試！');
        }
      });
    },
    async editBook(formData) {
      const currentId = formData.get('id');
      const isbnExists = await this.checkIsbnExists(formData.get('isbn'), currentId);
      if (isbnExists) return;

      const {code} = await saveBook(formData);
      if (code !== 200) {
        this.$message.error('編輯失敗');
        return;
      }
      this.$message.success('編輯成功');
      await this.reloadBook();
      this.closeAddBookModal();
    },

    async addBook(formData) {
      if (await this.checkIsbnExists(formData.get('isbn'))) return;

      const {code} = await saveBook(formData);
      if (code !== 200) {
        this.$message.error('新增失敗，請稍後再試！');
        this.closeAddBookModal();
        return;
      }
      this.$message.success('新增成功');

      const {totalPages} = await getBooks(this.currentPage, this.pageSize);
      this.currentPage = totalPages;
      await this.getBooks(this.currentPage, this.pageSize);  // 請求最後一頁的資料
      this.closeAddBookModal();
    },
    async checkIsbnExists(isbn, excludeId = null) {
      try {
        const existingBook = await searchBookByISBN(isbn);
        const book = existingBook.data;
        if (book && String( book.id) !== String(excludeId)) { //避免編輯時isbn沒更改但判斷為重複
          this.$message.warning('此 ISBN 已經存在');
          return true;
        }
        return false;
      } catch (error) {
        this.$message.error('系統錯誤，請稍後再試！');
        return false;
      }
    },
    createFormData(form) {
      let formData = new FormData();
      const fields = ['id', 'isbn', 'bookName', 'author', 'category', 'publisher', 'publishedDate', 'description', 'quantity', 'price'];
      fields.forEach(fields => {
        if (form[fields] !== undefined) {
          formData.append(fields, form[fields]);
        }
      });
      if (form.uploadBookCover) {
        formData.append('uploadBookCover', form.uploadBookCover);
      }
      if (form.api_bookCoverUrl) {
        formData.append('api_bookCoverUrl', form.api_bookCoverUrl);
      }
      return formData;
    },
    async reloadBook() {
      //判斷是不是搜尋狀態，如果是搜尋狀態，則用搜尋條件去獲取分頁資料
      if (this.isSearchMode()) {
        await this.loadSearchedBooks();
      } else {
        await this.getBooks(this.currentPage, this.pageSize);
      }
    },
    isSearchMode() {
      return Object.keys(this.lastSearchForm).length > 0;
    },
    async loadSearchedBooks() {
      const result = await searchBooks({...this.lastSearchForm, currentPage: this.currentPage}, 0);
      if (result.code !== 200) {
        this.$message.error('無法獲取資料，請稍後再試！');
      }
      const books = result.data.content;
      this.totalItems = result.data.totalElements;
      if (books.length === 0 && this.currentPage > 1) {
        this.currentPage -= 1;
        return this.reloadBook();
      }
      this.tableData = books;
    },

    async getBooks(currentPage, pageSize) {
      const books = await getBooks(currentPage, pageSize);
      const content = books.content || [];

      // 若當前的頁數沒有資料，且不是第一頁，則往前翻頁
      if (content.length === 0 && this.currentPage > 1) {
        this.currentPage -= 1;
        return this.getBooks(this.currentPage, this.pageSize);
      }

      this.tableData = content;
      this.totalItems = books.totalElements;
    }
  },
  mounted() {
    this.getBooks(this.currentPage, this.pageSize);
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
}


//分頁按鈕
.book-manage__pagination {
  display: flex;
  justify-content: center;
  margin: 20px 0 20px;
}

.primary {
  background-color: $clr-primary;
  color: white;
}

.danger {
  background-color: $clr-danger;
  color: white;
}


</style>
