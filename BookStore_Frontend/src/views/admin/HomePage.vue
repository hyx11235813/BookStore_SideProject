<template>

  <div>
    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
          <el-card class="user-info-card">
            <div class="user-info-card__wrapper">
              <el-upload class="avatar-uploader" action='http://localhost:8080/api/file/upload'
                         :headers='{ token: `${token}` }' :show-file-list="false" :on-success="handleAvatarSuccess"
                         :before-upload="beforeAvatarUpload">
                <img v-if="imageUrl" :src="imageUrl" class="avatar" alt="">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div class="user-info-card__data">
                <P>用戶名：{{ userName }}</P>
                <p>角色權限：{{ role === 'ADMIN' ? '管理員' : '' }} </p>
                <p>上次登入時間： <br><span>{{ lastLoginDate === '' ? '第一次登入' : lastLoginDate }}</span></p>
              </div>
            </div>
          </el-card>
          <!-- echart 柱狀圖 -->
          <div ref="echarts1" style="height: 220px;margin-top: 2rem"></div>
          <!-- echart 折線圖 -->
          <div ref="echarts3" style="height: 200px; "></div>
        </div>
      </el-col>

      <el-col :span="12">
        <div class="grid-content bg-purple-light">
          <div class="sales-card">
            <el-card v-for="item in countData" :key="item.name">
              <div class="sales-card__boxes">
                <div class="sales-card__detail">
                  <p>{{ item.title }}</p>
                  <p>{{ item.value }}本</p>
                </div>
                <div class="sales-card__icon" :class="item.icon"></div>
              </div>
            </el-card>
          </div>
          <!-- echart 餅狀圖 -->
          <div class="circle-chart" ref="echarts2" style="height: 250px;  margin-top: 30px; "></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {getChartData, getUserById} from '@/api/admin';
import {uploadAvatar} from '@/api/user';
import * as echarts from 'echarts';

export default {
  data() {
    return {
      countData: [],
      userId: '',
      token: '',
      userName: '',
      role: '',
      lastLoginDate: '',
      imageUrl: ''
    };
  },
  methods: {
    async handleAvatarSuccess(resp, file) { //同時會調用後端的/file/upload把照片存到本地
      this.imageUrl = URL.createObjectURL(file.raw); //顯示圖片在頁面上
      let formData = new FormData();
      formData.append('id', this.userId);
      formData.append('avatar', resp.data);
      const {code} = await uploadAvatar(formData);
      if (code !== 200) {
        this.$message.error('更改頭像失敗，請稍後再試');
        return;
      }
      this.$store.commit('setUserAvatar', resp.data); //設置state，讓header中的頭像也會跟著改變
    },
    // 圖片選擇限制
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('圖片必須爲.jpg格式');
      }
      if (!isLt2M) {
        this.$message.error('照片大小不得超過20MB');
      }
      return isJPG && isLt2M;
    }
  },
  async mounted() {
    this.token = JSON.parse(localStorage.getItem('token'));
    const echarts1 = echarts.init(this.$refs.echarts1);//柱狀圖
    const echarts2 = echarts.init(this.$refs.echarts2);//餅圖
    const echarts3 = echarts.init(this.$refs.echarts3);//折線圖

    this.userId = this.$store.state.userData.user.id;
    const user = await getUserById(this.userId);
    const {name, role, avatar, lastLoginDate} = user?.data||{};
    this.userName = name || '未設定名稱';
    this.role = role;
    this.avatar = avatar;
    this.imageUrl = avatar;
    this.lastLoginDate = new Date(lastLoginDate).toLocaleString().startsWith('1970') ? '' : new Date(lastLoginDate).toLocaleString();

    //取得數據圖數據
    getChartData().then((resp) => {
          if (resp.code !== 200) return;
          const {countData, barData, pieData} = resp?.data;
          //card數據
          this.countData = countData;
          const echarts1_Option = {
            title: {
              text: '每月銷售數量'
            },
            tooltip: {},
            legend: {},
            xAxis: {
              data: barData.map(item => item.month)
            },
            yAxis: {},
            series: [
              {
                name: '上架數量',
                type: 'bar',
                data: barData.map(item => item.monthly_AddedBooks)
              },
              {
                name: '出售數量',
                type: 'bar',
                data: barData.map(item => item.monthly_Sales)
              }
            ]
          };
          echarts1.setOption(echarts1_Option);


          //餅圖數據
          const pieDataArray = [];
          pieData.forEach(item => {
            pieDataArray.push({
              value: item.salesCount,
              name: item.category
            });
          });
          const echarts2_option = {
            title: {
              text: '銷售類別分佈'
            },
            legend: {
              top: 'bottom'
            },
            toolbox: {
              show: true,
              feature: {}
            },
            series: [
              {
                name: 'Nightingale Chart',
                type: 'pie',
                radius: [30, 90],
                center: ['50%', '40%'],
                roseType: 'area',
                itemStyle: {
                  borderRadius: 8
                },
                data: pieDataArray
              }
            ]
          };
          echarts2.setOption(echarts2_option);

          const echarts3_option = {
            title: {
              text: '每月銷售額'
            },
            xAxis: {
              type: 'category',
              data: barData.map(item => item.month)
            },
            yAxis: {
              type: 'value'
            },
            series: [
              {
                data: barData.map(item => item.monthly_SalesPrice),
                type: 'line'
              }
            ]
          };
          echarts3.setOption(echarts3_option);

        }
    );
  }
};

</script>

<style scoped>
.user-info-card {
  margin-right: 1.6rem;

  .user-info-card__wrapper {
    display: flex;
    align-items: center;

    .user-info-card__data {
      margin: auto;

      p {
        margin: 14px;
        border-bottom: 1px solid #d9d9d9;
      }
    }
  }
}


.sales-card {
  display: flex;
  flex-wrap: wrap;

  .el-card {
    margin: 0 1.6rem 1.6rem 0;

  }
  .sales-card__boxes {
    display: flex;
    width: 10rem;
    justify-content: space-around;
    align-items: center;

    .sales-card__detail {
      p {
        font-size: 20px;
      }
    }

    .sales-card__icon {
      font-size: 30px;
    }
  }
}


/* 上傳頭像 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

@media screen and (max-width: 1024px) {
  .el-row {
    display: flex;
    flex-direction: column;
  }

  .el-col {
    width: 100%;
  }

}
</style>