<template>
  <div class="user-manage">
    <div class="user-manage__header">
      <!-- 搜尋 -->
      <el-row>
        <el-button class="primary" @click="isSearchModalVisible = true"
                   v-if="!this.isSearchMode()">查詢用戶
        </el-button>
        <el-button type="success" @click="leaveSearch()"
                   v-if="this.isSearchMode()">返回顯示所有用戶
        </el-button>
        <span style="padding-left: 20px">總共{{ totalItems }}筆資料</span>
      </el-row>
    </div>
    <!--TABLE-->
    <div class="user-manage__table">
      <el-table :data="tableData" stripe style="width: 100%" ref="dataTable" empty-text="沒有資料">
        <el-table-column type="expand">
          <template v-slot="props">
            <el-form label-position=" middle" inline class="table-expand">
              <el-form-item label="註冊時間：">
                <span>{{ props.row.creationDate }}</span>
              </el-form-item>
              <el-form-item label="編輯時間：">
                <span>{{ props.row.editDate }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column label="" width="70px">
          <template v-slot="scope">
            <el-image v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"
                      style="width:40px;height: 40px; border-radius: 50%;"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="名稱" prop="name">
        </el-table-column>
        <el-table-column label="信箱" prop="email" width="300px">
        </el-table-column>
        <el-table-column label="身份" prop="role">
          <template v-slot="scope">
            {{
              scope.row.role === 'ADMIN' ? '管理員' : '使用者'
            }}
          </template>
        </el-table-column>
        <el-table-column label="狀態" prop="isActive">
          <template v-slot="scope">
            {{ scope.row.isActive === true ? '啓用' : '禁用' }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <div class="user-manage__edit-btn">
              <el-button size="mini" class="primary"
                         @click="clickEdit(scope.row.id)">編輯
              </el-button>
            </div>
          </template>

        </el-table-column>
      </el-table>
      <!-- 分頁 -->
      <div class="user-manage__pagination">
        <el-pagination layout="prev, pager, next" :total="totalItems" :page-size="pageSize"
                       @current-change="handleCurrentPage" :current-page.sync="currentPage">
        </el-pagination>
      </div>
    </div>
    <!-- 編輯視窗 -->
    <el-dialog :visible.sync="isEditModalVisible" width=300px :before-close="closeAddBookModal"
               :close-on-click-modal=true>
      <el-form :model="editForm" ref="form" label-width="60px" class="ruleForm" :inline="true"
               label-position="left">
        <el-form-item label="狀態" prop="isActive">
          <el-select v-model="editForm.isActive" placeholder="請選擇">
            <el-option label="啓用" :value="true"></el-option>
            <el-option label="禁用" :value="false"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="身份" prop="role">
          <el-select v-model="editForm.role" placeholder="請選擇">
            <el-option label="管理員" value="ADMIN"></el-option>
            <el-option label="使用者" value="USER"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="isEditModalVisible = false">取 消</el-button>
                <el-button type="primary" @click="submit()">確 定</el-button>
      </span>
    </el-dialog>
    <!-- 搜尋視窗 -->
    <el-dialog title="查詢" :visible.sync="isSearchModalVisible" width=500px :before-close="closeAddBookModal">
      <el-form ref="searchForm" :model="searchForm" label-width="90px">
        <el-form-item label="信箱：">
          <el-input v-model="searchForm.email" placeholder="開頭匹配"></el-input>
        </el-form-item>
        <el-form-item label="角色：">
          <el-select v-model="searchForm.role" placeholder="請選擇角色">
            <el-option label="使用者" value="USER"></el-option>
            <el-option label="管理員" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="狀態：">
          <el-select v-model="searchForm.isActive" placeholder="請選擇狀態">
            <el-option label="禁用" value="0"></el-option>
            <el-option label="啓用" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="註冊時間：">
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
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="isSearchModalVisible = false">取 消</el-button>
                <el-button class="primary" @click="handleSearch()">確 認</el-button>
            </span>
    </el-dialog>
  </div>
</template>

<script>
import {getAllUsers, getUserById, saveUser, searchUsers} from '@/api/admin';

export default {
  data() {
    return {
      loggedUser: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      isEditModalVisible: false,
      isSearchModalVisible: false,
      searchForm: {},
      lastSearchForm: {},
      editForm: {
        isActive: '',
        role: ''
      },
      tableData: [] //使用者資料表


    };
  },
  methods: {
    isSearchMode() {
      return Object.keys(this.lastSearchForm).length > 0;
    },
    closeAddBookModal() {
      this.searchForm = {};
      this.isEditModalVisible = false;
    },
    leaveSearch() {
      this.lastSearchForm = {};
      this.currentPage = 1;
      this.reloadUsers();
    },
    async clickEdit(id) {
      this.isEditModalVisible = true;
      Object.assign(this.editForm, await getUserById(id).then(res => res.data));
    },
    async handleCurrentPage(val) {
      this.currentPage = val;
      await this.reloadUsers();
    },

    async reloadUsers() {
      if (this.isSearchMode()) {
        await this.loadSearchUsers();
      } else {
        await this.getAllUsers(this.currentPage, this.pageSize);
      }
    },
    async loadSearchUsers() {
      try {
        const result = await searchUsers({...this.lastSearchForm, currentPage: this.currentPage}, 0);
        if (result.code !== 200) {
          this.$message.error('無法獲取資料，請稍後再試！');
          return;
        }
        this.tableData = result.data.content;
        this.isEditModalVisible = false;
      } catch (error) {
        this.$message.error('系統錯誤，請稍後再試！');
      }
    },
    submit() {
      this.$refs.form.validate(async (isValid) => {
        if (!isValid) return;
        try {
          const {code} = await saveUser(this.editForm);
          if (code === 200) {
            await this.reloadUsers();
            this.closeAddBookModal();
          } else {
            this.$message.warning('修改失敗，稍後再試！');
          }
        } catch (error) {
          console.error(error);
          this.$message.error('系統錯誤，請稍後再試！');
        }
      });
    },
    async handleSearch() {
      const noSearchCondition = Object.values(this.searchForm).every(item => item === '');  //檢查是否至少有輸入一個條件
      if (noSearchCondition) {
        this.$message.warning('請輸入搜尋條件');
        return;
      }
      try {
        this.lastSearchForm = {...this.searchForm};  //記錄搜尋的條件
        const {data} = await searchUsers(this.searchForm);
        const {content, totalElements} = data;
        const filteredContent = content.filter(user => user.id !== this.loggedUser);
        if (filteredContent.length === 0) {
          this.$message.info('查無資料');
          this.lastSearchForm = {}; //搜尋結果為空，不保留搜尋條件
        } else {
          this.$message.success('查詢成功');
          this.tableData = filteredContent;
          this.totalItems = totalElements;
          this.currentPage = 1;     // 搜尋結果從第一頁開始
        }
      } catch (error) {
        this.$message.error('搜尋失敗，請稍後再試');
      } finally {
        this.searchForm = {};
        this.isSearchModalVisible = false;
      }
    },
    async getAllUsers(currentPage, pageSize) {
      const users = await getAllUsers(currentPage, pageSize);
      this.totalItems = users.data.totalElements;
      this.tableData = users.data.content.filter(item => item.id !== this.loggedUser);
    }
  },
  mounted() {
    this.loggedUser = this.$store.state.userData.user.id;
    this.getAllUsers(this.currentPage, this.pageSize);
  }
};
</script>

<style lang="scss" scoped>
@use '@/assets/style/abstracts' as *;

.table-expand {
  display: flex;
  justify-content: space-around;

}

.table-expand label {
  width: 90px;
  color: #99a9bf;

}

.table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 25%;

}

.user-manage {
  height: 90%;

  .user-manage__header {
    height: 60px;
    display: flex;
    justify-content: space-between;

  }

  .user-manage__pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;

  }

  .user-manage__edit-btn {
    & > * {
      max-width: 6rem;
      padding: .6rem 1rem;
      font-size: .8rem;
    }
  }

  .primary {
    background-color: $clr-primary;
    color: white;
  }
}
</style>