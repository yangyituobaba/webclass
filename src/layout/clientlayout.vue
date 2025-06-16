<template>
  <el-container>
    <el-aside style="width: 200px; height: 100vh;">
      <SliderBar />
    </el-aside>
    <el-container>
      <el-header>
        <Navbar />
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
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
