<!--共用步驟條，用class的 current-step改變樣式-->
<template>
  <div class="forgot-password__steps">
    <header class=" forgot-password__header">
      <p>重設密碼</p>
    </header>
    <ul class="forgot-password__steps-list">
      <li class="forgot-password__steps-item" :class="{'current-step':currentSteps===1}">
        <span class="forgot-password__steps-count ">Ⅰ</span>
        <span class="forgot-password__steps-label">輸入信箱</span>
      </li>
      <li class="forgot-password__steps-item" :class="{'current-step':currentSteps===2}">
        <span class="forgot-password__steps-count">Ⅱ</span>
        <span class="forgot-password__steps-label">安全驗證</span>
      </li>
      <li class="forgot-password__steps-item" :class="{'current-step':currentSteps===3}">
        <span class="forgot-password__steps-count">Ⅲ</span>
        <span class="forgot-password__steps-label">設定密碼</span>
      </li>
    </ul>
  </div>
</template>
<script>
export default {
  props: {
    currentSteps: {
      type: Number,
      required: true
    }
  }
};
</script>

<style scoped lang="scss">
$steps-color: #34816C;

//步驟條
.forgot-password__steps {
  width: 100%;
  padding-bottom: 1rem;

  .forgot-password__header {
    width: 100%;
    padding-bottom: 1rem;

    p {
      font-size: 1.6rem;
      font-weight: 400;
      text-align: center;
    }
  }

  .forgot-password__steps-list {
    display: flex;
    list-style-type: none;
    justify-content: space-around;

    .forgot-password__steps-item {
      display: flex;
      width: 100%;
      flex-direction: column;
      align-items: center;
      position: relative;

      //item之間的線
      &:not(:first-child)::after {
        content: '';
        position: absolute;
        width: 100%;
        height: 2px;
        top: calc(2rem / 2); //讓線在原點中間
        transform: translateX(-50%);
        background-color: $steps-color;
      }

      //步驟數字
      .forgot-password__steps-count {
        width: 2rem;
        height: 2rem;
        margin-bottom: .5rem;
        font-size: 1.3rem;
        font-weight: bold;
        text-align: center;
        line-height: 2rem;
        border-radius: 50%;
        position: relative;
        z-index: 10;
        color: transparent; //隱藏數字，到該步驟時再加上顏色顯示

        //步驟上的圓圈
        &::before {
          content: '';
          width: 1.8rem;
          height: 1.8rem;
          position: absolute;
          left: 50%;
          top: 50%;
          transform: translate(-50%, -50%);
          background-color: $steps-color;
          border-radius: 50%;
          z-index: -10;
        }

        //勾勾圖片
        &::after {
          content: '';
          width: 100%;
          height: 100%;
          left: 50%;
          top: 50%;
          transform: translate(-50%, -50%);
          position: absolute;
          border-radius: 50%;
          background: no-repeat center $steps-color url("@/assets/images/form-checkmark-white.svg");
        }
      }

      .forgot-password__steps-label {
        font-size: .9rem;
      }
    }
  }

}

//步驟樣式-------------------------------------

//目前步驟圓圈的樣式
.current-step .forgot-password__steps-count::before {
  background-color: #f5f5f5 !important;
  border: .1rem solid $steps-color;
}

//目前步驟及之後的步驟勾勾隱藏，因為還沒到達那個步驟
.current-step .forgot-password__steps-count::after,
.current-step ~ .forgot-password__steps-item .forgot-password__steps-count::after {
  display: none;
}


//目前步驟的步驟數字顯示
.current-step .forgot-password__steps-count {
  color: $steps-color !important;
}

//目前步驟之後的圓圈
.current-step ~ .forgot-password__steps-item .forgot-password__steps-count::before {
  width: .8rem !important;
  height: .8rem !important;
}

//目前步驟之後的label變灰
.current-step ~ .forgot-password__steps-item .forgot-password__steps-label {
  color: #9CA3AF
}
</style>