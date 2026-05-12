<template>
  <div class="register">
    <div class="register__form">
      <div class="register__container">
        <div class="register__card">
          <form>
            <p> 會員註冊</p>
            <template v-for="field in fields">
              <div v-if="field.show" :key="field.label" class="label">
                <label :for="field.id">{{ field.label }}</label>
                <div class="input-wrapper" v-if="field.show">
                  <div class="input-item">
                    <img :src="changeInputIcon(field.name)" class="input-icon" alt="">
                    <input
                        class="input" v-model="register[field.name]"
                        :type="field.name === 'password' ? 'password' : 'text'"
                        :id="field.id"
                        @focus="focusField = field.name"
                        @input="checkInput(field.name, $event.target.value)"
                        @keydown.enter.prevent="confirm(field.name)"
                    />
                  </div>
                  <button
                      class=" confirmBtn"
                      @click.prevent="confirm(field.name)"
                      :disabled="!field.ok"
                      :style="{ visibility: focusField === field.name ? 'visible' : 'hidden' }"
                  >{{ isAllFieldsOk ? '送出' : '確認' }}
                  </button>
                </div>
              </div>
            </template>
          </form>
        </div>
        <!-- 顯示填寫資料的錯誤訊息 -->
        <div class="hint">
          <p class="error-msg">{{ errorMessage }}</p>
        </div>
      </div>
      <!-- 發送驗證信動畫 -->
      <div id="animation" v-show="showAnimation"></div>
    </div>
  </div>
</template>

<script>
import {checkEmail, register, checkAccount} from '@/api/user';
import lottie from 'lottie-web';

export default {
  data() {
    return {
      errorMessage: '',//錯誤訊息
      focusField: '',
      register: {
        account: '',
        email: '',
        password: '',
        name: ''
      },
      fields: [  //show: 是否顯示欄位, ok: 欄位資料是否正確
        {name: 'account', label: '帳號', show: true, ok: false, ref: 'accountInput', id: 'account'},
        {name: 'email', label: '信箱', show: false, ok: false, ref: 'emailInput', id: 'email'},
        {name: 'password', label: '密碼', show: false, ok: false, ref: 'passwordInput', id: 'password'},
        {name: 'name', label: '用戶名', show: false, ok: false, ref: 'nameInput', id: 'name'}
      ],
      showAnimation: false
    };
  },
  computed: {
    isAllFieldsOk() {
      return this.fields.every(field => field.ok === true);
    }
  },
  methods: {
    confirm(name) {
      try {
        const field = this.getField(name);
        if (!field.ok) return;

        const lastOkIndex = this.fields.findLastIndex(field => field.ok === true);
        const nextField = this.fields[lastOkIndex + 1];
        if (nextField) {
          nextField.show = true;
          this.$nextTick(() => document.getElementById(nextField.id).focus());
        }

        if (this.fields.every(field => field.ok)) {
          this.sentEmail_animation();
          register(this.register).then(resp => {
            if (resp.code === 200) {
              this.$router.push('/sent-email-info');
            }
          });
        }
      } catch (error) {
        console.warn(error);
      }

    },
    async checkInput(name, val) {
      const REGEX = {
        account: /^[a-zA-Z0-9]{6,16}$/,//帳號需為字母或數字，且長度6~16
        email: /[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}/,//信箱格式
        password: /^(?=.*\d)(?=.*[a-zA-Z]).{8,16}$/, //密碼需包含英文字母和數字，且長度為8~16個字
        name: /^[\u4e00-\u9fa5a-zA-Z0-9]{1,16}$///只能輸入 中文、英文或數字，長度 1～16 個字元，不允許特殊符號或空白
      };

      if (!val) return this.setOk(name, false);

      switch (name) {
        case 'account': {
          if (!REGEX.account.test(val)) {
            this.setOk(name, false);
            this.setError('帳號只允許字母及數字，且長度介於6~16個字');
            return;
          }
          const resp = await checkAccount(val);
          const isAlreadyUsed = resp.data.isExist;
          this.setOk(name, !isAlreadyUsed);
          this.setError(isAlreadyUsed ? '帳號錯誤或已被使用' : '');
          break;
        }
        case 'email': {
          if (!REGEX.email.test(val)) {
            this.setOk(name, false);
            this.setError('信箱錯誤或已被使用');
            return;
          }
          const resp = await checkEmail(val);
          const isAlreadyUsed = resp.isExist;
          this.setOk(name, !isAlreadyUsed);
          this.setError(isAlreadyUsed ? '信箱錯誤或已被使用' : '');
          break;
        }
        case 'password': {
          if (!REGEX.password.test(val)) {
            this.setOk(name, false);
            this.setError('密碼需包含字母及數字，長度 1～16 個字元');
            return;
          }
          this.setOk(name, true);
          break;
        }
        case 'name': {
          if (!REGEX.name.test(val)) {
            this.setOk(name, false);
            this.setError('只能輸入中文、英文或數字，長度 1～16 個字元');
            return;
          }
          this.setOk(name, true);
          break;
        }
      }
    },
    setOk(name, isOk) {
      const field = this.getField(name);
      if (field) {
        field.ok = isOk;
      }
    },
    setError(msg) {
      this.errorMessage = msg;
    },
    changeInputIcon(fieldName) {
      const value = this.register[fieldName];
      const field = this.getField(fieldName);
      if (!value) return require('@/assets/images/arrow-right.svg');
      return field.ok
          ? require('@/assets/images/form-checkmark-green.svg')
          : require('@/assets/images/form-cross-red.svg');
    },
    getField(name) {
      return this.fields.find(field => field.name === name);
    },
    sentEmail_animation() {
      this.showAnimation = true;
      lottie.loadAnimation({
        container: document.getElementById('animation'),
        loop: true,
        autoplay: true,
        path: '/animation/sentEmail.json'
      });
    }
  },
  mounted() {
    //focus第一個輸入框
    this.$nextTick(() => {
      document.getElementById(this.fields[0].id).focus();
    });
  }
};
</script>
<style lang="scss" scoped>
@use "src/assets/style/abstracts" as *;

.register {
  height: 100vh;

  .register__form {
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    padding-top: 5rem;

    .register__container {
      height: 100%;
      width: min(500px, 96%);


      .register__card {
        z-index: 1000;
        border-radius: 12px;
        background-color: $clr-bg-light;
        padding: 1rem;

        p {
          text-align: center;
        }

        .label {
          margin-top: 1rem;
        }

        .input-wrapper {
          display: flex;
          align-items: center;
          margin: .5rem;

          .input-item {
            width: 100%;
            display: flex;
            align-items: center;

            .input-icon {
              width: 1.4rem;
              margin-right: .7rem;
            }

            .input {
              width: 100%;
              height: 1.9rem;
              margin-right: 6px;
              border-radius: 6px;
              border: none;
              outline: none;
              background-color: inherit;
              font-size: 1rem;
            }

            .input:focus {
              border: 1.4px solid #599178;
            }
          }

          .confirmBtn {
            min-width: 5rem;
            height: 1.9rem;
            border-radius: 6px;
            border: 1px solid rgba(0, 102, 208, 0.95); // 確認這行樣式是否正確
            cursor: pointer;
            color: white;
            background-color: $clr-primary;

            &:disabled {
              border: none;
              background-color: #a7c4e6;
              color: #e0e0e0;
              cursor: not-allowed;
            }
          }
        }
      }


      .hint {
        margin-top: 1rem;
        margin-left: 1.2rem;

        .error-msg {
          font-size: 1rem;
          color: $clr-danger;
        }
      }
    }
  }

  #animation {
    position: fixed;
    width: 100%;
    height: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 9999;
    background-color: rgba(0, 0, 0, .3);
  }
}


@media only screen and (max-width: 500px) {
  .input-wrapper {
    flex-wrap: wrap;
  }
  .confirmBtn {
    width: 100%;
    margin-top: 10px;
  }
}

@media only screen and (max-width: 768px) {
  .register__form {
    padding-top: 1rem !important;
  }

  .hint {
    .error-msg {
      font-size: .9rem !important;
    }

  }
}
</style>
