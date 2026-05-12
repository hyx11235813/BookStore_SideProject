<template>
  <div class="book-manage__table">
    <el-table :data="data" stripe style="width: 100% ;height: 80%;" empty-text="沒有資料">
      <el-table-column type="expand">
        <template v-slot:="props">
          <el-form inline class="table-expand">
            <el-form-item label="出版商：">
              <span>{{ props.row.publisher }}</span>
            </el-form-item>
            <el-form-item label="出版日期：">
              <span>{{ props.row.publishedDate }}</span>
            </el-form-item>
            <el-form-item label="新增時間：">
              <span>{{ formatTimestamp(props.row.creationDate) }}</span>
            </el-form-item>
            <el-form-item label="編輯時間：">
              <span>{{ formatTimestamp(props.row.editDate) }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="isbn" label="ISBN" width="126">
      </el-table-column>
      <el-table-column prop="bookName" label="書名">
      </el-table-column>
      <el-table-column prop="author" label="作者">
      </el-table-column>
      <el-table-column prop="category" label="書籍分類" width="110">
      </el-table-column>
      <el-table-column prop="price" label="售價" width="80">
      </el-table-column>
      <el-table-column prop="quantity" label="庫存量" width="80">
      </el-table-column>
      <el-table-column prop="sales" label="出售量" width="80">
      </el-table-column>
      <el-table-column prop="" label="操作">

        <template v-slot="{ row }">
          <div class="book-manage__edit-btn">
            <slot name="action" :row="row"></slot>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  props: {
    data: Array
  },
  methods: {
    formatTimestamp(time) {
      if (!time) return '';
      const date = new Date(time);
      const year = date.getFullYear().toString();
      const month = date.getMonth().toString().padEnd();
      const day = date.getDate().toString().padEnd(2, '0');
      const hour = date.getHours().toString().padEnd(2, '0');
      const minute = date.getMinutes().toString().padEnd(2, '0');
      return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
    }
  }
};
</script>

<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;


.table-expand {
  display: flex;
  justify-content: space-around;

  & > * {
    height: 1rem;
  }
}

.book-manage__edit-btn {
  display: flex;

  ::v-deep .el-button {
    font-size: .8rem !important;
  }
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