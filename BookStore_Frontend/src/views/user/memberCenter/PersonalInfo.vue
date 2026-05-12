<template>
  <div>
    <div class="personal-info">
      <div class="personal-info__container">
        <div class="personal-info__title">
          <h1>個人資訊</h1>
        </div>
        <div class="personal-info__banner-wrap">
          <div class="personal-info__banner--left">
            <h2>您的個人資料</h2>
            <p>個人資訊管理，您可以在這裡修改個人資料</p>
          </div>
          <figure class="personal-info__banner--right">
            <img class="personal-info__banner-img" src="@/assets/images/circle-user-green.svg" alt="">
          </figure>
        </div>
        <div class="personal-info__table">
          <div class="personal-info__table-title">
            <h2>基本資訊</h2>
          </div>
          <!--個人相片修改欄位-->
          <div class="personal-info__table-avatar" @click="openAvatarModal">
            <div class="personal-info__table-avatar-container">
              <div class="personal-info__table-avatar-title">個人相片</div>
              <div class="personal-info__table-avatar-data">設置個人資料相片</div>
            </div>
            <figure>
              <img class="personal-info__table-avatar-img" v-if="userAvatar" :src="userAvatar"
                   style="border-radius: 50%;" alt="">
              <img class="personal-info__table-avatar-img" v-else src="@/assets/images/circle-user-white.svg" alt=' '>
            </figure>
          </div>
          <!--使用者資料修改欄位-->
          <div class="personal-info__table-list-wrap" v-for=" i in infoList" :key="i.name">
            <div class="personal-info__table-list-div" @click="handleEdit(i.item)">
              <div class="personal-info__table-list-container">
                <div class="personal-info__table-list-row">
                  <div class="personal-info__table-list-title">{{ i.name }}</div>
                  <div class="personal-info__table-list-data">{{ i.data }}</div>
                </div>
                <figure>
                  <img src="../../../assets/images/checkmark-inactive.svg" style="width: 20px ; height: 20px;" alt="">
                </figure>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 頭像變更的Modal -->
      <transition>
        <div class="modal-overlay" v-if="isAvatarModalVisible"  @mousedown.self="closeModal">
          <div class="avatar-modal" v-if="isAvatarModalVisible_step1">
            <div class="avatar-modal__wrap">
              <header class="avatar-modal__header">
                <h1>
                  <span>個人資料相片</span>
                </h1>
                <div class="avatar-modal__close-btn">
                  <img class="avatar-modal__close-icon" src="@/assets/images/form-cross-black.svg" alt=""
                       @click="isAvatarModalVisible = false;">
                </div>
              </header>
              <main class="avatar-modal__main">
                <div class="avatar-modal__main-desc">
                  <p>您可以設置屬於自己的個人相片</p>
                </div>
              </main>
              <div>
                <div class="avatar-modal__avatar-wrap" v-if="userAvatar">
                  <img class="avatar-modal__avatar-img" :src=userAvatar alt=" ">
                </div>
                <div class="avatar-modal__avatar-wrap" v-else>
                  <img class="avatar-modal__avatar-img" src="@/assets/images/circle-user-white.svg" alt=' '>
                </div>
              </div>

              <div class="avatar-modal__change-avatar-btn-wrap">
                <button class="avatar-modal__change-avatar-btn" @click="triggerFileInput"
                        :style="userAvatar ? {} : { 'flex-grow': '1' }">
                  {{ userAvatar ? '變更' : '新增頭像' }}
                </button>
                <input type="file" ref="fileInput" @change="selectFile" style="display: none;"/>
                <button class="avatar-modal__change-avatar-btn" @click="openAvatarRemoveModal"
                        v-if="userAvatar">移除
                </button>
              </div>
            </div>
          </div>
          <!-- 移除頭像確認 modal -->
          <div class="avatar-modal__card-remove-avatar" v-if="isAvatarModalVisible_step2">
            <div class="avatar-modal__card-remove-wrapper">
              <img class="avatar-modal__card-remove-avatar-img" alt="" :src=userAvatar>
              <h1>要移除個人資料相片嗎？</h1>
            </div>
            <div class="avatar-modal__card-remove-btn-wrap">
              <button class="avatar-modal__card-remove-cancel-btn" @click=cancelRemoveAvatar>取消</button>
              <button class="avatar-modal__card-remove-confirm-btn" @click="removeAvatar">移除</button>
            </div>
          </div>
        </div>
      </transition>
      <!--更改名稱-->
      <div class="modal-overlay" v-if="isRenameModalVisible"   @mousedown.self="closeModal">
        <div class="name-modal">
          <div class="name-modal__card">
            <div class="name-modal__title-wrapper">
              <h2 class="name-modal__title"> 修改名稱</h2>
            </div>
            <div class="name-modal__div">
              <input v-model="userName" type="text" ref="name-modal_input" class="name-modal__input"
                     placeholder=" " @keyup="checkNameValid">
              <label class="name-modal__label">名稱</label>
            </div>
            <div class="name-modal__hint-div">
              <p class="name-modal__hint" ref="name-modal__hint" :class="{'shake-animation':isShaking}"
                 :style="isUserNameValid?{color:'black'}:{color:'red'}">
                只允許輸入中文、數字、英文，長度爲2~16個字</p>
            </div>
            <div class="name-modal__btn-wrapper">
              <button class="name-modal__cancel-btn" @click="isRenameModalVisible = false">取消</button>
              <button class="name-modal__confirm-btn" @click="updateUserNameSubmit">確認</button>
            </div>
          </div>
        </div>
      </div>
      <MessagePopup :message="popupMessage" :isVisible="popupVisible" :type="popupType"></MessagePopup>
    </div>
  </div>

</template>

<script>
import {removeAvatar, uploadImage, updateUserName, uploadAvatar, getCurrentUser} from '@/api/user';
import {messagePopupMixin} from '@/mixins/messagePopupMixin';
import MessagePopup from '@/components/MessagePopup.vue';


export default {
  components: {MessagePopup},
  mixins: [messagePopupMixin],
  data() {
    return {
      infoList: [
        {name: '名稱', item: 'name', data: ''},
        {name: '密碼', item: 'password', data: '******'}
      ],
      userName: '',
      userAvatar: '',
      selectedMenu: '',
      userId: '',
      isAvatarModalVisible: false, //選擇頭像圖片的modal
      isAvatarRemoveModalVisible: false,//移除頭像的modal
      isRenameModalVisible: false,//修改名稱的modal
      token: '',//使用者token
      isShaking: false, //搖晃動畫
      isAvatarModalVisible_step1: false,
      isAvatarModalVisible_step2: false
    };
  },
  computed: {
    isUserNameValid() {
      const name = this.userName;
      return /^[\u4e00-\u9fa5a-zA-Z0-9]{2,16}$/.test(name);
    }
  },
  methods: {
    openAvatarModal() {
      this.isAvatarModalVisible_step1 = true;
      this.isAvatarModalVisible = true;
    },
    openAvatarRemoveModal() {
      this.isAvatarModalVisible_step1 = false;
      setTimeout(() => {
        this.isAvatarModalVisible_step2 = true;
      }, 100);
    },
    closeModal() {
      this.isAvatarModalVisible_step1 = false;
      this.isAvatarModalVisible_step2 = false;
      this.isAvatarModalVisible = false;
      this.isRenameModalVisible = false;
    },
    checkNameValid() {
      return this.isUserNameValid;
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    cancelRemoveAvatar() { //按下取消按鈕觸發
      this.isAvatarModalVisible_step2 = false;
      setTimeout(() => {
        this.isAvatarModalVisible_step1 = true;
      }, 100);
    },
    async updateUserNameSubmit() {
      if (!this.isUserNameValid) {
        this.isShaking = true; //名稱錯誤時的搖晃動畫
        setTimeout(() => {
          this.isShaking = false;
        }, 500);
        return;
      }

      let payload = {
        id: this.userId,
        name: this.userName
      };

      const {code} = await updateUserName(payload);
      if (code !== 200) {
        this.showErrorMessage()
        return;
      }

      this.isRenameModalVisible = false;
      this.showPopupMessage('名稱已更新', 'info');

      await this.getUserInfo(this.userId);
    },

    async selectFile() {
      const file = this.$refs.fileInput.files[0];
      if (!file) return;

      const MAX_SIZE = 2 * 1024 * 1024;  // 20MB
      const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];

      if (!ALLOWED_TYPES.includes(file.type)) {
        this.showPopupMessage('只支援 JPG、PNG、WEBP 格式', 'warning');
        return;
      }

      if (file.size > MAX_SIZE) {
        this.showPopupMessage('檔案太大，請上傳小於 2MB 的圖片', 'warning');
        return;
      }

      let formData = new FormData();
      formData.append('file', file);  //後端MultiPartFile接收

      try {
        //將圖片上傳至本地，並且取得圖片路徑
        const uploadResponse = await uploadImage(formData);
        const avatarPath = uploadResponse.data;
        //更新使用者頭像
        await uploadAvatar({id: this.userId, avatar: avatarPath});
        this.userAvatar = URL.createObjectURL(file); //生成臨時的圖片網址，用來直接顯示圖片
        this.$store.commit('setUserAvatar', this.userAvatar);//設置state，讓header中的頭像也會跟著改變
        this.showPopupMessage('個人相片已更新', 'info');
      } catch (error) {
        console.log(error);
        this.showErrorMessage()
      }
    },
    async removeAvatar() {
      try {
        const resp = await removeAvatar(this.userId);

        if (resp.code !== 200) {
          this.showErrorMessage()
          return;
        }

        await this.getUserInfo(this.userId);

        this.isAvatarModalVisible_step2 = false;
        this.isAvatarModalVisible_step1 = false;
        this.isAvatarModalVisible = false;

        this.showPopupMessage('個人相片已移除', 'info');
      } catch (error) {
        console.error(error);
        this.showErrorMessage()
      }
    },
    async handleEdit(item) {
      try {
        switch (item) {
          case 'name':
            await this.getUserInfo(this.userId);
            this.isRenameModalVisible = true;
            break;
          case 'password':
            await this.$router.push({path: '/verify-password', query: {target: '/change-password'}});
            break;
          default:
            console.error(`未知編輯選擇:${item}`);
        }
      } catch (error) {
        console.error(error);
        this.showErrorMessage()
      }
    },
    async getUserInfo(userId) {
      const resp = await getCurrentUser(userId);
      let {name, avatar} = resp;
      this.$store.commit('setUserAvatar', avatar); //設置state，讓header中的頭像也會跟著改變
      this.$store.commit('setUserName', name);
      this.userAvatar = avatar;
      this.userName = name;
      this.infoList[0].data = name;
    }

  },
  mounted() {
    this.userId = this.$store.state.userData.user.id;
    this.getUserInfo(this.userId);
    const {popupMessage, popupType} = this.$route.query;

    if (popupMessage) {
      this.showPopupMessage(popupMessage, popupType || 'info');

      // 清除 URL 中的 query 參數
      this.$router.replace({
        path: this.$route.path,
        query: {}
      });
    }
  }
}
;

</script>

<style lang="scss" scoped>
@use '@/assets/style/abstracts' as *;

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  text-decoration: none;
  list-style: none;
}


.v-enter-active {
  transition: all 0.2s ease-in-out;
}

.v-enter, //vue2 用 enter而不是.v-enter-from!!
.v-leave-to {
  opacity: 0;

}

.v-enter-active .avatar-modal,
.v-leave-active .avatar-modal {
  transition: all 0.2s ease-in-out;
}


.v-enter .avatar-modal,
.v-leave-to .avatar-modal {
  transform: scale(.7);
  opacity: 0;
}

.personal-info {
  padding: 0 48px;

  .personal-info__container {
    margin-top: 24px;
    width: 800px;

    .personal-info__banner-wrap {
      display: flex;
      padding-top: 40px;
      margin: 32px 0 28px;

      .personal-info__banner--left {
        flex-grow: 2;
        flex-shrink: 1;

        h2 {
          font-weight: 400;
          margin-bottom: 12px;
        }

        p {
          font-size: .9rem;
        }
      }

      .personal-info__banner--right {
        .personal-info__banner-img {
          width: 360px;
          height: 128px;
        }
      }
    }

    .personal-info__title {
      text-align: center;

      h1 {
        font-weight: 500;
        font-size: 28px;
      }

    }

    .personal-info__table {
      display: flex;
      flex-direction: column;
      border: 1px solid #ccc;
      border-radius: 8px;

      .personal-info__table-title {
        height: 70px;
        padding: 24px 24px 8px 24px;

        h2 {
          font-weight: 400;
          color: $clr-text-gray;
        }

      }


      .personal-info__table-avatar {
        display: flex;
        align-items: center;
        padding: 16px 24px;
        width: 100%;
        cursor: pointer;

        &:hover {
          background-color: $clr-hover-gray
        }

        .personal-info__table-avatar-container {
          width: 90%;
          display: flex;
          align-items: center;
        }

        .personal-info__table-avatar-title {
          min-width: 200px;
          color: $clr-text-gray;
          padding-top: 4px;

        }

        .personal-info__table-avatar-data {
          color: $clr-text-gray;
          flex: 1;
          white-space: nowrap;
          padding-top: 4px;
          font-size: 14px;
        }

        .personal-info__table-avatar-img {
          height: 60px;
          width: 60px;
          padding-top: 4px;

        }
      }

      .personal-info__table-list-wrap {
        position: relative;
        border-top: $clr-border-light;

        &:hover {
          background-color: $clr-hover-gray
        }

        .personal-info__table-list-div {
          cursor: pointer;

          .personal-info__table-list-container {
            padding: 16px 24px;
            display: flex;
            align-items: center;
            position: relative;

            .personal-info__table-list-row {
              display: flex;
              align-items: center;
              width: 100%;

              .personal-info__table-list-title {
                min-width: 200px;
                white-space: nowrap;
                color: $clr-text-gray;
              }

              .personal-info__table-list-data {
                color: $clr-text-gray;
              }
            }
          }
        }
      }
    }
  }
}

.modal-overlay {
  @include overlay();


}

.avatar-modal {
  width: min(500px, 100%);

  .avatar-modal__wrap {
    @include flex-card(100%);

    .avatar-modal__header {
      width: 100%;
      display: flex;
      justify-content: center;
      position: relative;

      .avatar-modal__close-btn {
        position: absolute;
        right: 18px;
        cursor: pointer;

        .avatar-modal__close-icon {
          width: 18px;
          height: 18px;
        }
      }
    }

    .avatar-modal__main {
      width: 86%;
      padding: 16px 0 26px;

      h2 {
        font-weight: 400;
        margin-bottom: 6px;
      }

      p {
        font-size: 0.9rem;
        text-align: center;
        color: $clr-text-gray;
      }
    }

    .avatar-modal__avatar-wrap {
      width: 234px;
      height: 234px;

      .avatar-modal__avatar-img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
      }
    }

    .avatar-modal__change-avatar-btn-wrap {
      display: flex;
      justify-content: space-between;
      margin-top: 32px;
      width: 98%;

      .avatar-modal__change-avatar-btn {
        @include confirm-btn--green-round(30%);
      }
    }
  }
}

.avatar-modal__card-remove-avatar {
  width: min(400px, 98%);
  padding: 28px 16px;
  border-radius: 26px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: white;

  .avatar-modal__card-remove-wrapper {
    width: 100%;
    height: 100%;
    max-height: 450px;
    display: flex;
    flex-direction: column;
    align-items: center;

    .avatar-modal__card-remove-avatar-img {
      width: 194px;
      height: 194px;
      border-radius: 50%;
    }

    h1 {
      margin: 24px 0 36px;
      font-weight: 400;
    }
  }

  .avatar-modal__card-remove-btn-wrap {
    width: 100%;
    margin-top: auto;
    display: flex;
    justify-content: space-between;

    .avatar-modal__card-remove-confirm-btn {
      @include confirm-btn--green-round(30%);
    }

    .avatar-modal__card-remove-cancel-btn {
      @include cancel-btn--transparent(30%);
    }
  }
}

.name-modal {
  display: flex;
  flex-direction: column;
  align-items: center;

  .name-modal__card {
    @include flex-card(min(400px, 96vw));

    .name-modal__title-wrapper {
      .name-modal__title {
        font-weight: 400;
        margin-bottom: 2rem;
      }
    }

    .name-modal__hint-div {
      width: 90%;
      margin: 1.5rem 0;
      font-size: .8rem;

    }

    .name-modal__div {
      position: relative;
      width: 100%;


      .name-modal__input {
        width: 100%;
        height: 40px;
        padding: 0 1rem;
        outline: none;
        border: 1px solid black;
        border-radius: 4px;
        font-size: 20px;
        font-weight: 400;
        background-color: #f5f5f5;

        &:focus + .name-modal__label {
          left: .9rem;
          top: -.7rem;
        }

      }

      //有輸入值且離開focus時label固定在上方
      .name-modal__input:not(:placeholder-shown).name-modal__input:not(:focus) + .name-modal__label {
        left: .9rem;
        top: -.7rem;
      }

      .name-modal__label {
        position: absolute;
        left: 1rem;
        top: .45rem;
        background-color: #f5f5f5;
        transition: 100ms;
        border-style: none;


      }
    }

    .name-modal__btn-wrapper {
      width: 100%;
      display: flex;
      justify-content: space-between;

      .name-modal__cancel-btn {
        @include cancel-btn--transparent(30%);
      }

      .name-modal__confirm-btn {
        @include confirm-btn--green-round(30%);
      }
    }
  }

}


.personal-info {
  padding: 0 1rem;

  .personal-info__table-avatar {
    width: 80%;

    .personal-info__table-avatar-container {
      flex-wrap: wrap;

    }
  }

  .personal-info__table-list-wrap {
    .personal-info__table-list-container {
      .flex-container {
        flex-wrap: wrap;

      }
    }
  }

  .personal-info__banner--right {
    display: none;
  }
}

.shake-animation {
  animation: shake 0.5s;
}

@keyframes shake {
  0% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-3px);
  }
  50% {
    transform: translateX(3px);
  }
  75% {
    transform: translateX(-3px);
  }
  100% {
    transform: translateX(0);
  }
}

@media (max-width: 1024px) {
  .personal-info .personal-info__container {
    width: auto;
  }

}


@media (max-width: 660px) {
  .personal-info__title {
    display: none;
  }

}

</style>
