<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
const editor = ref(false)

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth()} 月 ${date.getDay()+1} 日`
})

navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
},error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时, 请检查网络设置')
  get('api/forum/weather?longitude=116.40529&latitude=39.90499', data => {
    Object.assign(weather, data)
    weather.success = true
  })
},{
  timeout: 3000,
  enableHighAccuracy: true
})
</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen/></el-icon> 点击发表主题...
        </div>
      </light-card>
      <light-card style="margin-top: 10px;height: 30px">

      </light-card>
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px" >
        <light-card style="height: 150px" v-for="item in 10">

        </light-card>
      </div>
    </div>
    <div style="width: 280px">
      <div style="position: sticky; top: 20px">
        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag/></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;margin: 10px;color: grey">
            近日，1998年出生的哈尔滨工业大学（深圳）教授、博士生导师杨朔走红网络，网友们纷纷表示不可思议，“98年硕士还没毕业，人家已经是博导了”“感觉自己是来凑数的”。

            哈尔滨工业大学（深圳）官网显示，杨朔为计算机学院长聘教授、博士生导师，入选2024年国家级高层次青年人才计划、国家优秀青年科学基金项目。主要研究方向为机器学习与计算机视觉，研究兴趣侧重于具身智能、可信智能、多模态智能等。长期担任人工智能领域顶会/顶刊审稿专家。
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon><Calendar/></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <weather :data="weather"/>
        </light-card>
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <el-icon><Link/></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div style="display: grid;grid-template-columns: repeat(2, 1fr);grid-gap: 10px;margin: 10px">
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/mele-banner.png"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/vform-banner.png"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/jnpfsoft.png"/>
          </div>
        </div>
      </div>

    </div>
    <topic-editor :show="editor" @close="editor = false"/>
  </div>
</template>

<style lang="less" scoped>
.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}
.friend-link {
  border-radius: 5px;
  overflow: hidden;
}
.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  color: grey;
  height: 40px;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}
</style>