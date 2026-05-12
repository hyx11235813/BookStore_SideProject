<template>
  <div class="book-manage__header">
    <el-row>
      <el-button class="primary" @click="$emit('openSearchForm')"
                 v-if="Object.keys(lastSearchForm).length===0">查詢書籍
      </el-button>
      <el-button type="success" @click="$emit('closeSearchForm')"
                 v-if="Object.keys(lastSearchForm).length>0">返回顯示所有書籍
      </el-button>
      <span style="padding-left: 20px">總共{{ totalItems }}筆資料</span>
    </el-row>
    <el-row>
      <el-button class="addBtn primary" @click="$emit('showAddBookModal')"
                 v-if="Object.keys(lastSearchForm).length===0">新增書籍</el-button>
    </el-row>
  </div>
</template>
<script>
export default {
  props: {
    lastSearchForm: Object,
    totalItems: Number
  }
};
</script>

<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;
.book-manage__header {
  height: 60px;
  display: flex;
  justify-content: space-between
}

.book-manage__edit-btn {
  display: flex;
  flex-direction: column;

  & > * {
    max-width: 6rem;
    margin: 3px 0;
    font-size: .8rem;
    color: white;

  }
}
.primary {
  background-color: $clr-primary;
  color: white;
}
</style>