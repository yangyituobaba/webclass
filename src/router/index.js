import {createRouter,createWebHashHistory} from 'vue-router'

const routes = [
    { path: '/login', component: errorcatch},
    { path: '/login', component: LoginView },
    { path: '/register', component: RegisterView },
    {
        path: '/client',
        component: ClientLayout,
        children: [
            { path: 'products', component: ClientProductView },
            { path: 'orders', component: ClientOrderView },
            { path: 'profile', component: ProfileView },
        ]
    },
    {
        path: '/admin',
        component: AdminLayout,
        children: [
            { path: 'products', component: AdminProductView },
            { path: 'orders', component: AdminOrderView },
            { path: 'users', component: UserManageView },
            { path: 'profile', component: ProfileView },
        ]
    },
    {
        path: '/delivery',
        component: DeliveryLayout,
        children: [
            { path: 'orders', component: DeliveryOrderView },
            { path: 'profile', component: ProfileView },
        ]
    },
]

const router = createRouter({
    history:createWebHashHistory(),
    routes,
})


const handleToParams = (to)=>{
    const route={
        fullPath: to.fullPath,
        meta: to.meta,
        name: to.name,
        params: to.params,
        path: to.path,
        query: to.query,
    }
    return route
}

function  handleRouteInEditableTabs(to,store) {
    //判断当前路由的标题是否已经在editableTabs里,如果不在则动态添加tab页
    const indexInEditableTabs = store.editableTabs.findIndex(
        (item) => item.title === to.meta.title
    )
    //当前路由的标题已经在editableTabs里
    if (indexInEditableTabs !== -1) {
        //判断tabRouterList是否已经存在相同的路由
        const indexInTabRouterList = store.tabRouterList.findIndex(
            (item) => item.name === to.name
        )
        //当前路由的name已经在tabRouterList里面
        if (indexInTabRouterList !== -1) {
            //根据当前路由名称找到对应的历史路由
            let result = store.tabRouterList.find(item => item.name === to.name)
            //在name相同但是路由的query参数不一样,则替换为这个最新的(将对象转为string字符串比较,即可判断2个对象属性与值是否完全一样)
            let queryMatched=JSON.stringify(result.query) === JSON.stringify(to.query)
            //如果为false,则替换当前路由参数
            if (!queryMatched) {
                //若存在，则从原始数组中移除该对象
                store.tabRouterList = store.tabRouterList.filter(
                    (item) => item.name !== to.name
                )
                //重新添加这个新路由
                store.tabRouterList.push(handleToParams(to))
            }
        } else {
            //点击菜单栏时,如果不在则添加该路由
            store.tabRouterList.push(handleToParams(to))
        }
    } else {
        //判断该路由是否在黑名单里面,不在则动态添加tab页
        if (!blackList.includes(to.path)) {
            //如果不在editableTabs里面,那么就在editableTabs里面添加这个tab页
            store.editableTabs.push({
                title: to.meta.title,
                name: to.path,
                iconClass: to.meta.icon,
            })
            //点击页面中的某个按钮进行页面跳转的时候,如果不在则添加该路由里面部分字段
            store.tabRouterList.push(handleToParams(to))
        }
    }
}

//路由前置守卫
router.beforeEach((to, from, next) => {
    //如果没有匹配到路由,则跳转到404页面
    if (to.matched.length === 0) {
        next("/404")
    } else {
        //路由发生变化修改页面title
        document.title = to.meta.title
        //使用pinia里面的全局状态属性
        const store = useUserStore()
        //更改tab标签绑定值，选中选项卡的name
        store.updateState(["editableTabsValue", to.path])
        //更改当前激活的菜单
        store.updateState(["activeMenu", to.path])
        //动态添加tab页及tab页切换时参数也跟着切换
        handleRouteInEditableTabs(to,store)
        next()
    }
})
//导出路由
export default router
