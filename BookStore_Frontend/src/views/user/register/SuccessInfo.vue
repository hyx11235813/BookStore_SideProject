<template>
  <div class="success-info">
    <div class="container">
      <p>帳號驗證完成</p>
      <router-link to="/login" class="return">返回登入畫面</router-link>
    </div>
  </div>
</template>
<script>
</script>
<style lang="scss" scoped>
@use "src/assets/style/abstracts" as *;

.success-info {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  .container {
    //background-color: green;

    p {
      font-size: 1.8rem;
      margin-bottom: 1rem;
      text-align: center;
    }

    .return {
      display: block;
      width: 100%;
      padding: 1rem 2rem;
      text-align: center;
      background-color:$clr-btn-green;
      color: #f5f5f5;
      border-radius: 5px;
      text-decoration: none;

      &:hover {
        background-color: #307865;
      }
    }
  }
}
</style>