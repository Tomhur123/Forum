<script setup>
defineProps({
  data: Object
})
</script>

<template>
  <div style="height: 160px" v-loading="!data.success">
    <div style="display: flex;justify-content: space-between; margin: 10px 20px"
         v-if="data.success">
      <div style="font-size: 45px;color: grey">
        <i :class="`qi-${data.now.icon}-fill`"></i>
      </div>
      <div style="font-weight: bold;text-align: center">
        <div style="font-size: 25px">{{data.now.temp}}℃</div>
        <div style="font-size: 15px">{{data.now.text}}</div>
      </div>
      <div style="margin-top: 13px">
        <div style="font-size: 15px">{{ `${data.location.country} ${data.location.adm1}`}}</div>
        <div style="font-size: 14px; color: grey">{{ `${data.location.adm2} ${data.location.name}` }}</div>
      </div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <div style="display: grid;grid-template-columns: repeat(5, 1fr);text-align: center">
      <div v-for="item in data.hourly">
        <div style="font-size: 13px">{{new Date(item.fxTime).getHours()}}时</div>
        <div style="font-size: 23px">
          <i :class="`qi-${item.icon}-fill`"></i>
        </div>
        <div style="font-size: 12px">{{item.temp}}℃</div>
      </div>
    </div>
  </div>

</template>

<style scoped>

</style>