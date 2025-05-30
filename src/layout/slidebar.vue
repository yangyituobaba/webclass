<!--通用布局侧边栏内容-->
<template>
  <el-row>
    <el-col>
      <div class="header">
        <!--系统logo,随便找一个图片示例用-->
        <SvgIcon iconClass="systemManagement" />
        <span class="icon-text">后台管理系统</span>
      </div>
      <!--router表示为启动路由模式,路由模式下index为你的页面路由路径-->
      <!--通过设置default-active属性点击tab页时,自动选中左边菜单栏选项-->
      <div>
        <el-menu
            active-text-color="#1B68B6"
            background-color="#FFFFFF"
            :default-active="store.activeMenu"
            text-color="#333333"
            @select="handleSelect"
            :router="true"
            class="menu-items"
        >
          <!--引用菜单树组件将路由的菜单栏循环显示出来-->
          <MenuTree :menuList="menuTreeList"/>
        </el-menu>
      </div>
    </el-col>
  </el-row>
</template>

<script setup name="SliderBar">
//引入菜单列表组件
import MenuTree from "./menutree.vue"
//引入全局状态里面的关于菜单栏列表数据和相关方法
import useUserStore from "@/store/modules/userStore"

//使用useUserStore里面的属性
const store = useUserStore()
const menuTreeList = store.menulist


//菜单激活回调函数,当tab页已经打开的情况下,再次点击菜单项,对应的tab页也跟着切换
function handleSelect(key) {
  store.updateState(["editableTabsValue", key])
  store.updateState(["activeMenu", key])
}

</script>

<style lang="less" scoped>
.header {
  height: 64px;
  display: flex;
  align-items: center; //垂直居中
  justify-content: left; //水平居左
  //logo样式
  .svg-icon {
    width: 64px;
    height: 32px;
  }
  .icon-text {
    font-size: 16px;
    color: #1b68b6;
    margin-left: -5px;
  }
}

//普通菜单悬浮样式
:deep(.el-menu-item:hover) {
  background-color: #E8EFF7;//背景颜色
  color: #1B68B6;//字体颜色
}

//子菜单悬浮样式,子菜单的图标颜色需要修改svg图片里面的fill值,由fill="#333333"改为fill="currentColor"后,图标悬浮样式颜色才会一起变化
:deep(.el-sub-menu__title:hover) {
  background-color: #E8EFF7;//背景颜色
  color: #1B68B6;//字体颜色
}

//菜单被选中的样式
:deep(.el-menu .el-menu-item.is-active) {
  background-color: #E8EFF7; //背景颜色
  color: #1B68B6; //字体颜色
  border-right: 3px solid #1B68B6;//右边框颜色
}

//子菜单被选中的样式
:deep(.el-sub-menu.is-active .el-sub-menu__title){
  color: #1B68B6; //字体颜色
}

//菜单栏样式
.menu-items {
  height: 100%; //设置高度为父容器高度
  border-right: none;//去掉菜单栏右边框
}
</style>

