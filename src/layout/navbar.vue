<!--通用布局头部内容-->
<template>
  <el-row>
    <el-col :span="20">
      <el-tabs
          v-model="store.editableTabsValue"
          type="border-card"
          closable
          @tab-remove="handleTabRemove"
          @tab-click="handleTabClick"
          v-if="store.editableTabs.length !== 0">
        <el-tab-pane v-for="item in store.editableTabs" :key="item.name" :name="item.name" :label="item.title">
          <!-- 右键菜单开始：自定义标签页显示名称，保证每个标签页都能实现右键菜单 -->
          <template #label>
            <el-dropdown
                trigger="contextmenu"
                :id="item.name"
                @visible-change="handleChange($event, item.name)"
                ref="dropdownRef">
              <span style="font-size: 16px;color: #909399;"
                    :class="store.editableTabsValue === item.name ? 'label' : ''">
                <SvgIcon :iconClass="item.iconClass"></SvgIcon>{{ item.title }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="closeCurrent(item.name)">
                    <el-icon>
                      <Close />
                    </el-icon>关闭当前标签页
                  </el-dropdown-item>
                  <el-dropdown-item @click="closeLeft(item.name)" v-if="show(item.name, 'left')">
                    <el-icon>
                      <DArrowLeft />
                    </el-icon>关闭左侧标签页
                  </el-dropdown-item>
                  <el-dropdown-item @click="closeRight(item.name)" v-if="show(item.name, 'right')">
                    <el-icon>
                      <DArrowRight />
                    </el-icon>关闭右侧标签页
                  </el-dropdown-item>
                  <el-dropdown-item @click="closeOther(item.name)" v-if="store.editableTabs.length > 1">
                    <el-icon>
                      <Operation />
                    </el-icon>关闭其他标签页
                  </el-dropdown-item>
                  <el-dropdown-item @click="closeAll()">
                    <el-icon>
                      <Minus />
                    </el-icon>关闭全部标签页
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <!-- 右键菜单结束 -->
        </el-tab-pane>
      </el-tabs>
    </el-col>
    <el-col :span="4">
      <div class="header">
        <!-- 用户信息 -->
        <!--trigger="click"通过点击下标触发-->
        <div style="cursor: pointer;">
          <el-dropdown trigger="click">
          <span>
            {{ username }}
            <SvgIcon iconClass="arrowDown" />
          </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup name="navbar">
//引入全局状态里面的关于菜单栏列表数据和相关方法
import useUserStore from '@/store/modules/userStore'
import { useRouter, useRoute } from "vue-router"
import { onMounted, ref, computed } from 'vue'
import {
  Close,
  DArrowLeft,
  DArrowRight,
  Operation,
  Minus
} from '@element-plus/icons-vue'

//接手全局状态里面的属性和方法
const store = useUserStore();
//使用路由相当于$router,系统路由方法
const router = useRouter()
//使用路由相当于$route,点击菜单栏时当前点击的路由页面里面的属性值
const route = useRoute()
//用户名
const username = '超级管理员'

//触发右键菜单标签页为第一个时，不展示【关闭左侧标签页】
//触发右键菜单标签页为最后一个时，不展示【关闭右侧标签页】
const show = (name, type) => {
  const index = store.editableTabs.findIndex((item) => name === item.name)
  return type === 'left' ? index !== 0 : index !== store.editableTabs.length - 1
}

//右键菜单ref
const dropdownRef = ref()

//在触发右键菜单后，关闭其他tab页上的右键菜单
const handleChange = (visible, name) => {
  if (!visible) return
  dropdownRef.value.forEach((item) => {
    if (item.id === name) return
    item.handleClose()
  })
}

//关闭当前tab页
const closeCurrent = (targetName) => {
  handleTabRemove(targetName)
}

//关闭左侧tab页
const closeLeft = (targetName) => {
  //查找当前点击的tab页所在位置
  let currentIndex = store.editableTabs.findIndex(
      (item) => item.name === targetName
  )
  //查找当前激活标签页index
  const activeIndex = store.editableTabs.findIndex((item) => item.name === store.editableTabsValue)
  //关闭左侧tab页
  store.editableTabs.splice(0, currentIndex)
  //删除对应的左侧历史路由
  store.tabRouterList.splice(0, currentIndex)
  //如果当前关闭点击的tab页包含激活的tab页,则将激活tab页重置为当前点击的tab
  if (activeIndex < currentIndex) {
    //将当前激活的tab页改为当前点击的
    store.updateState(['editableTabsValue', targetName])
    //将激活菜单改为当前点击的
    store.updateState(['activeMenu', targetName])
    //路由跳转到当前点击的tab页
    //查询当前点击的tab页缓存路由参数
    let result = store.tabRouterList.find(item => item.path === targetName);
    //路由跳转且带上对应tab页的参数
    router.push({ path: targetName, query: result.query })
  }
}

//关闭右侧tab页
const closeRight = (targetName) => {
  //查找当前点击的tab页所在位置
  let currentIndex = store.editableTabs.findIndex(
      (item) => item.name === targetName
  )
  //查找当前激活标签页index
  const activeIndex = store.editableTabs.findIndex((item) => item.name === store.editableTabsValue)
  //关闭右侧tab页
  store.editableTabs.splice(currentIndex + 1)
  //删除对应的右侧历史路由
  store.tabRouterList.splice(currentIndex + 1)
  //如果当前关闭点击的tab页包含激活的tab页,则将激活tab页重置为当前点击的tab
  if (activeIndex > currentIndex) {
    //将当前激活的tab页改为当前点击的
    store.updateState(['editableTabsValue', targetName])
    //将激活菜单改为当前点击的
    store.updateState(['activeMenu', targetName])
    //路由跳转到当前点击的tab页
    //查询当前点击的tab页缓存路由参数
    let result = store.tabRouterList.find(item => item.path === targetName);
    //路由跳转且带上对应tab页的参数
    router.push({ path: targetName, query: result.query })
  }
}

//关闭其他tab页
const closeOther = (targetName) => {
  //查找当前点击的tab页所在位置
  let currentIndex = store.editableTabs.findIndex(
      (item) => item.name === targetName
  )
  //关闭其他标签页
  store.editableTabs = [store.editableTabs[currentIndex]]
  //删除除当前点击外的历史路由
  store.tabRouterList = [store.tabRouterList[currentIndex]]
  //如果当前点击的不等于当前激活的
  if (targetName !== store.editableTabsValue) {
    //将当前激活的tab页改为当前点击的
    store.updateState(['editableTabsValue', targetName])
    //将激活菜单改为当前点击的
    store.updateState(['activeMenu', targetName])
    //路由跳转到当前点击的tab页
    //查询当前点击的tab页缓存路由参数
    let result = store.tabRouterList.find(item => item.path === targetName);
    //路由跳转且带上对应tab页的参数
    router.push({ path: targetName, query: result.query })
  }
}

//关闭全部tab页
const closeAll = () => {
  //清空tabs数组
  store.editableTabs.length = 0
  //清空历史路由
  store.tabRouterList.length = 0
  //当前选中tab页设置为空
  store.updateState(['editableTabsValue', ''])
  //当前激活菜单设置为空
  store.updateState(['activeMenu', ''])
  //跳转到首页
  router.push('home')
}

//处理tab标签x按钮的移除
function handleTabRemove(targetName) {
  //如果editableTabs列表不为空数组
  if (store.editableTabs.length > 0) {
    //如果当前所在的tab页路由地址与移除的tab页名一样,则移到前面一个tab页且路由跳转
    if (route.path === targetName) {
      let tabs = store.editableTabs
      tabs.forEach((tab, index) => {
        if (tab.name === targetName) {
          //获取当前tab的后一个或者前一个
          let nextTab = tabs[index + 1] || tabs[index - 1]
          //如果有值就移到它上面，没有就是最后一个跳转到首页
          if (nextTab) {
            //根据name属性进行查询当前tab页的缓存路由参数
            let result = store.tabRouterList.find(item => item.path === nextTab.name);
            //路由跳转且带上对应tab页的参数
            router.push({ path: nextTab.name, query: result.query })
          } else {
            // 更改tab标签绑定值，选中选项卡的name
            store.updateState(['editableTabsValue', ''])
            // 更改当前激活的菜单
            store.updateState(['activeMenu', ''])
            //当删除的是最后一个tab页的时候,跳转到首页
            router.push('home')
          }
        }
      })
      //从editableTabs中移除当前tab标签
      store.removeTab(targetName)
    } else {
      //从editableTabs中移除当前tab标签
      store.removeTab(targetName)
    }
  }
}

//tab标签被选中时触发的事件
function handleTabClick(tab) {
  store.updateState(['activeMenu', tab.props.name])
  store.updateState(['editableTabsValue', tab.props.name])
  // 判断当前url地址和即将跳转的是否一致，不一致进行跳转，防止跳转多次
  if (tab.props.name !== route.path) {
    // 根据name属性进行查询
    let result = store.tabRouterList.find(item => item.path === tab.props.name);
    //路由跳转且带上对应tab页的参数
    router.push({ path: tab.props.name, query: result.query })
  }
}

//退出登录方法
function logout() {

}
</script>

<style lang="less" scoped>
//设置高度
:deep(.el-tabs__nav-scroll) {
  height: 60px;
}

//去掉el-tabs的边框
:deep(.el-tabs) {
  border: none;
}

.header {
  height: 62px;
  position: absolute;
  right: 30px;
  top: 0px;
  z-index: 1; //不设置这个,el-down点击出不来,被tab标签页长度挡住了
  display: flex;
  align-items: center; //垂直居中
}

//tab标签页里面字体设置
.label {
  color: #1B68B6 !important; //激活标签页高亮
  font-size: 16px;
}

:deep(.el-tabs__item) {
  &:hover {
    span {
      color: #1B68B6 !important; //鼠标移到标签页高亮
    }
  }
}
</style>
