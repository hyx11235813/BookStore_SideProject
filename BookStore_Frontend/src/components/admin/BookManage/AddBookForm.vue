<template>
  <div class="manage-dialog">
    <el-dialog class="manage-dialog__wrapper" width=700px
               :visible.sync="visible" :before-close="handleClose"
    >
      <div class="manage-dialog__hint" style="margin-bottom: 1rem">
        <div>測試ISBN: 9789573270126</div>
        <div style="color: red;">"查詢"能從Google Books API 及資料庫查找書籍，已新增過可直接選擇編輯；已下架可以選擇重新上架</div>
      </div>
      <el-row :gutter="10">
        <el-form :model="form" :rules="rules" ref="modalForm" label-width="80px" class="manage-dialog__modal"
                 label-position="left">
          <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
            <div class="manage-dialog__form">
              <el-form-item label="ISBN" prop="isbn">
                <div style="display: flex">
                  <el-input v-model="form.isbn" placeholder="填入ISBN自動帶入書籍資訊"
                  ></el-input>
                  <el-button @click="autoFillBookInfo(form.isbn)"
                             style="margin-left: 14px">查詢
                  </el-button>
                </div>
              </el-form-item>
              <el-form-item label="書名" prop="bookName">
                <el-input v-model="form.bookName"></el-input>
              </el-form-item>
              <el-form-item label="作者" prop="author">
                <el-input v-model="form.author"></el-input>
              </el-form-item>
              <el-form-item label="書籍分類" prop="category">
                <el-select v-model="form.category" placeholder="請選擇書的種類">
                  <el-option v-for="item in category" :label=item.name :value="item.value"
                             :key="item.value"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
            <div class="r-form">
              <el-form-item label="出版商" prop="publisher">
                <el-input v-model="form.publisher"></el-input>
              </el-form-item>
              <el-form-item label="出版日期" prop="publishedDate">
                <el-date-picker v-model="form.publishedDate" type="date" placeholder="選擇日期"
                                value-format="yyyy-MM-dd" style="width: 100%">
                </el-date-picker>
              </el-form-item>
              <el-form-item label="庫存量" prop="quantity">
                <el-input v-model="form.quantity"
                          @input="checkNumber('quantity',form.quantity)"></el-input>
              </el-form-item>
              <el-form-item label="售價" prop="price">
                <el-input v-model="form.price"
                          @input="checkNumber('price',form.price)"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <div class="textarea">
            <el-input type="textarea" :rows="3" placeholder="請輸入書籍内容簡介" v-model="form.description">
            </el-input>
          </div>
          <!-- 圖片顯示 -->
          <div class="manage-dialog__img">
            <div class="manage-dialog__img--show"
                 style="text-align: center;border: 1px solid gainsboro; border-radius: 5px;margin-top: 10px"
                 v-if="form.bookCoverUrl || form.api_bookCoverUrl">
              <img :src="form.api_bookCoverUrl" v-if="form.api_bookCoverUrl" alt="">
              <img :src="form.bookCoverUrl" v-if="form.bookCoverUrl" alt="">
            </div>
          </div>
        </el-form>
      </el-row>
      <span slot="footer" class="dialog-footer">
            <el-button class="primary" @click="triggerFileInput"
                       style="float: left;">選擇書籍封面</el-button>
            <input type="file" ref="fileInput" @click="resetFileInput" @change="selectFile" style="display: none"/>
            <el-button @click="handleClose">取消</el-button>
            <el-button class="primary" @click="$emit('submit')">確定</el-button>
          </span>
    </el-dialog>
  </div>
</template>
<script>
import {relistBook, searchBookByISBN} from '@/api/admin';
import axios from 'axios';

export default {
  data() {
    return {
      apiKey: 'AIzaSyBeOXpP-jqPR5Q-fwymMr3fMSJjEnKbIKI',    //googleBook api
      form: {},
      rules: {
        isbn: [
          {required: true, message: '請輸ISBN', trigger: 'blur'}
        ],
        bookName: [
          {required: true, message: '請輸入書名', trigger: 'blur'}
        ],
        author: [
          {required: true, message: '請輸入作者', trigger: 'blur'}
        ],
        category: [
          {required: true, message: '請輸入種類', trigger: 'blur'}
        ],
        quantity: [
          {required: true, message: '請輸入數量', trigger: 'blur'}
        ],
        publisher: [
          {required: true, message: '請輸入出版商', trigger: 'blur'}
        ],
        publishedDate: [
          {required: true, message: '請輸入出版日期', trigger: 'blur'}
        ],
        price: [
          {required: true, message: '請輸入售價', trigger: 'blur'}
        ]
      },
      category: [
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

    };
  },
  props: {
    visible: {
      type: Boolean
    }
  },
  methods: {
    gerFormData() {
      return this.form;
    },
    checkNumber(inputType, val) {
      this.form[inputType] = val.replace(/[^0-9]/g, '');
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    }, resetFileInput() {
      //先清空才能觸發@chenge
      this.$refs.fileInput.value = '';
    },
    selectFile() {
      const fileInput = this.$refs.fileInput;
      const maxSizeInBytes = 20 * 1024 * 1024; // 20MB
      const file = fileInput.files[0];
      if (file) {
        if (file.size > maxSizeInBytes) {
          this.$message('照片大小超過（最大20MB）');
        } else {
          this.form.api_bookCoverUrl = false;
          // 顯示預覽圖片
          this.$set(this.form, 'bookCoverUrl', URL.createObjectURL(file));
          // 要上傳圖片
          this.form.uploadBookCover = file;
        }
      }
    },
    handleClose() {
      this.form = {};
      this.$emit('update:visible', false);
    },
    //調用Google的Api獲取書籍資料
    async autoFillBookInfo(isbn) {
      if (!isbn) {
        return;
      }
      //先檢查資料庫有沒有資料，如果沒有再使用API查詢
      const isExist = await this.handleIsbnExistence(isbn);
      if (!isExist) {
        axios({
          method: 'get',
          url: `https://www.googleapis.com/books/v1/volumes?q=isbn:${isbn} & key=${this.apiKey}`
        }).then((resp) => {
          const {totalItems, items} = resp.data;
          if (totalItems !== 0) {
            const {volumeInfo} = items[0];
            const {title, authors, publisher, publishedDate, description} = volumeInfo;
            const thumbnail = volumeInfo.imageLinks?.thumbnail || '';
            this.setBookInfo(title, authors, publisher, publishedDate, description, thumbnail);
            this.form.bookCoverUrl = '';
            this.form.uploadBookCover = '';
            this.$refs.fileInput.value = '';
          } else {
            this.setBookInfo('', [], '', '', '', '');
            this.$message('查無此書籍，請手動輸入書籍資訊！');
          }
        });
      }
    },
    async handleIsbnExistence(isbn) {
      const book = await searchBookByISBN(isbn);
      const bookData = book?.data;
      if (bookData) {
        if (bookData.isDeleted) {
          await this.handleBookReList(bookData);
        } else {
          await this.handleBookEdit(bookData);
        }
        return true;
      }
      return false;
    },
    async handleBookReList(bookData) {
      this.$confirm('此書籍已被下架，是否重新上架？', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        relistBook(bookData.id);
        this.form = bookData;
      }).catch(() => {
        this.form = {};
      });
    },
    async handleBookEdit(bookData) {
      this.$confirm('此書籍已經新增過，是否進行編輯？', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$emit('update:modalType', 0);
        this.form = bookData;
      }).catch(() => {
        this.form = {};
      });
    },

    //使用set才能立即更新顯示DOM
    setBookInfo(title, authors, publisher, publishedDate, description, thumbnail) {
      this.$set(this.form, 'bookName', (title || '').toString());
      this.$set(this.form, 'author', (authors || []).join(', '));
      this.$set(this.form, 'publisher', (publisher || '').toString());
      this.$set(this.form, 'publishedDate', (publishedDate || '').toString());
      this.$set(this.form, 'description', (description || '').toString());
      this.$set(this.form, 'api_bookCoverUrl', (thumbnail || '').toString());
    }
  }
};
</script>
<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;
.manage-dialog__img {
  .manage-dialog__img--show {
    img {
      max-width: 100%;
      height: 300px;
    }
  }
}
.primary {
  background-color: $clr-primary;
  color: white;
}
</style>