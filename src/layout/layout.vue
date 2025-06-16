<template>
  <div>
    <el-container>
      <!--侧边栏,height: 100vh;设置高度为视口高度-->
      <el-aside style="width: 200px;height: 100vh;">
        <SliderBar></SliderBar>
      </el-aside>
      <el-container>
        <!--头部-->
        <el-header>
          <Navbar></Navbar>
        </el-header>
        <!--主体内容-->
        <el-main>
          <!--主体内容-->
          <AppMain>
          </AppMain>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { Navbar, SliderBar, AppMain } from './index.js'

export default {
  name: "layout",
  components: {
    Navbar,
    SliderBar,
    AppMain
  }
}
</script>

<style scoped>

</style>
