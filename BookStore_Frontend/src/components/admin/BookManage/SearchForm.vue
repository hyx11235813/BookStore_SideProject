<template>
  <div class="searchForm">
    <el-dialog class="searchForm__dialog" width=500px title="查詢" :visible.sync="visible"
               :before-close="closeSearchModal">
      <el-form class="searchForm__form" ref="searchForm" :model="searchForm" label-width="100px">
        <el-form-item label="ISBN：">
          <el-input v-model="searchForm.isbn" placeholder="輸入完整ISBN"></el-input>
        </el-form-item>
        <el-form-item label="書名：">
          <el-input v-model="searchForm.bookName" placeholder="模糊搜尋"></el-input>
        </el-form-item>
        <el-form-item label="作者：">
          <el-input v-model="searchForm.author" placeholder="模糊搜尋"></el-input>
        </el-form-item>
        <el-form-item label="出版商：">
          <el-input v-model="searchForm.publisher" placeholder="模糊搜尋"></el-input>
        </el-form-item>
        <el-form-item label="新增時間：">
          <el-col :span="11">
            <el-date-picker type="date" placeholder="從" v-model="searchForm.creationDateStart"
                            style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
          </el-col>
          <el-col class="line" :span="2">~</el-col>
          <el-col :span="11">
            <el-date-picker type="date" placeholder="到" v-model="searchForm.creationDateEnd"
                            style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
          </el-col>
        </el-form-item>
        <el-form-item label="編輯時間：">
          <el-col :span="11">
            <el-date-picker type="date" placeholder="從" v-model="searchForm.editDateStart"
                            style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
          </el-col>
          <el-col class="line" :span="2">~</el-col>
          <el-col :span="11">
            <el-date-picker type="date" placeholder="到" v-model="searchForm.editDateEnd"
                            style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
          </el-col>
        </el-form-item>
        <el-form-item label="書籍分類：">
          <el-select v-model="searchForm.category" placeholder="請選擇分類">
            <el-option v-for="i in category" :label="i.name" :value="i.value"
                       :key="i.value"></el-option>
          </el-select>
        </el-form-item>

      </el-form>

      <span slot="footer" class="dialog-footer">
                        <el-button @click="closeSearchModal">取 消</el-button>
                        <el-button class="primary" @click="$emit('submit',searchForm)">確定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      searchForm: {},
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

      ]
    };
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  }
  , methods: {
    closeSearchModal() {
      this.searchForm = {};
      this.$emit('update:visible', false);
    }
  }
};
</script>
<style scoped lang="scss">
@use '@/assets/style/abstracts' as *;
.primary {
  background-color: $clr-primary;
  color: white;
}
</style>