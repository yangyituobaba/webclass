<!--通用布局页面主体内容-->
<template>
  <!-- 路由视图对象 -->
  <router-view v-slot="{ Component }">
    <!--include主要解决关闭tab页时,同时销毁该组件,防止再次重新打开时数据还在的情况。注意:组件name名必须和路由name名一致，否则会导致组件不缓存的情况。-->
    <keep-alive :include="tabsNames">
      <component :is="Component"></component>
    </keep-alive>
  </router-view>
</template>

<script setup name="AppMain">
import useUserStore from "@/store/modules/userStore"
import { computed } from "vue"
const store = useUserStore()
//将路由里面的name取出来作为一个数组
const tabsNames = computed(() => store.tabRouterList.map((item) => item.name))
</script>

<style scoped>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
}
.el-main{

  margin: 0;
  padding: 0;
  height: 100%;
}
</style>

