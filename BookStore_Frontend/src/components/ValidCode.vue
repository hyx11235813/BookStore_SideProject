<template>
  <div class="ValidCode disabled-select" style="width: 100%; height: 100%" @click="refreshCode">
    <span v-for="(item, index) in codeList" :key="index" :style="getStyle(item)">{{ item.code }}</span>
    <span class="refreshCode" @click="refreshCode">更換驗證碼</span>
  </div>
</template>

<script>
export default {
  name: 'validCode',
  data() {
    return {
      length: 4, //驗證碼長度
      codeList: []
    }
  },
  methods: {
    refreshCode() {
      this.createdCode()
    },
    createdCode() {
      let len = this.length,
          codeList = [],
          chars = '0123456789',
          charsLen = chars.length
      for (let i = 0; i < len; i++) {
        let rgb = [Math.round(Math.random() * 180), Math.round(Math.random() * 180), Math.round(Math.random() * 180)]
        codeList.push({
          code: chars.charAt(Math.floor(Math.random() * charsLen)),
          color: `rgb(${rgb})`,
          padding: `7px`,
          transform: `rotate(${Math.floor(Math.random() * 70) - Math.floor(Math.random() * 70)}deg)`
        })
      }
      this.codeList = codeList
      this.$emit('update:value', codeList.map(item => item.code).join(''))
    },
    getStyle(data) {
      return `color: ${data.color}; font-size: ${data.fontSize}; padding: ${data.padding}; transform: ${data.transform}`
    }
  },
  mounted() {

    this.createdCode()
  },
}
</script>

<style>
.ValidCode {
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.ValidCode span {
  display: inline-block;
  margin-right: .2rem;
}

.refreshCode {
  margin-left: 1rem;
  font-size: .85rem !important;
  color: rgb(0, 66, 208);
}
</style>
