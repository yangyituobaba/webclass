<template>
  <!--将菜单列表循环出来-->
  <template v-for="item in menuList" :key="item.id">
    <!--判断菜单里面是否有子菜单-->
    <el-sub-menu
        v-if="item.children && item.children.length > 0"
        :index="item.url"
    >
      <template #title>
        <el-icon><SvgIcon :iconClass="item.meta.icon" /></el-icon>
        <span>{{ item.meta.title }}</span>
      </template>
      <!--调用自身循环显示子菜单，传入当前item的children-->
      <MenuTree :menuList="item.children" />
    </el-sub-menu>
    <!--菜单节点-->
    <el-menu-item
        v-else
        :index="item.url"
        @click="store.addTab(item)"
    >
      <el-icon><SvgIcon :iconClass="item.meta.icon" /></el-icon>
      <span>{{ item.meta.title }}</span>
    </el-menu-item>
  </template>
</template>

<script setup name="MenuTree">
//引入全局状态里面的关于菜单栏列表数据和相关方法
import useUserStore from "@/store/modules/userStore"
const store = useUserStore()

//定义属性给组件接收
const props = defineProps({
  //菜单栏属性
  menuList: {
    type: Array,//类型为数组
    default: () => []
  }
})
</script>
