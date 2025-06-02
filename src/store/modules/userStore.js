//使用pinia来管理全局状态
import { defineStore } from "pinia"
import axios from "axios";

/*defineStore 是需要传参数的，其中第一个参数是id，就是一个唯一的值，
简单点说就可以理解成是一个命名空间.
第二个参数就是一个对象，里面有三个模块需要处理，第一个是 state，
第二个是 getters，
第三个是 actions。
*/
//声明了一个useUserStore方法
const useUserStore = defineStore('user', {
    //准备state——用于存储数据
    state: () => {
        return {
            username:'',
            role:'',
            token: '',           // 新增，存储登录返回的 token
            deviceType: 'WEB',   // 新增，默认设备类型
            menulist:[],
            //当前激活菜单的index
            activeMenu: '',
            //绑定值，选中选项卡的name
            editableTabsValue: '',
            //tab标签选项卡内容
            editableTabs: [],
            //tab页路由地址及参数
            tabRouterList: []
        }
    },
    //使用persist插件对state里面属性进行缓存
    persist: {
        enabled: true,//开启缓存，默认缓存所有state里面的属性，默认key为defineStore里面的id值,这里id值为user,所以默认key为user
        //自定义持久化参数，指定以下state里面的属性进行缓存，未指定的不进行缓存
        strategies: [
            {
                key: 'username',
                storage: sessionStorage,
                paths: ['username']
            },
            { key: 'token', storage: sessionStorage, paths: ['token'] },        // 新增
            { key: 'deviceType', storage: sessionStorage, paths: ['deviceType'] },// 新增
            {
                key:'role',
                storage:sessionStorage,
                path:['role']
            },
            {
                key: 'menulist',
                storage: sessionStorage,
                paths: ['menulist']
            },
            {
                // 自定义key
                key: 'activeMenu',
                // 自定义存储方式，默认sessionStorage
                storage: sessionStorage,
                // 指定要持久化的数据
                paths: ['activeMenu']
            },
            {
                key: 'editableTabsValue',
                storage: sessionStorage,
                paths: ['editableTabsValue']
            },
            {
                key: 'editableTabs',
                storage: sessionStorage,
                paths: ['editableTabs']
            },
            {
                key: 'tabRouterList',
                storage: sessionStorage,
                paths: ['tabRouterList']
            }
        ]
    },
    //准备actions——用于响应组件中的动作和用于操作数据（state）,pinia中只有state、getter、action，抛弃了Vuex中的Mutation
    actions: {
        /**
         * 修改state中数据的方法
         * @param name 需要修改的属性名
         * @param value 修改值
         */
        updateState([name, value]) {
            this[name] = value
        },
        setToken(newToken) {
            this.token = newToken
        },

        setDeviceType(newDeviceType) {
            this.deviceType = newDeviceType
        },

        logout() {
            this.username = ''
            this.role = ''
            this.token = ''
            this.deviceType = 'WEB'
            this.menulist = []
            this.activeMenu = ''
            this.editableTabsValue = ''
            this.editableTabs = []
            this.tabRouterList = []
        },
        //动态添加tab标签,item为当前点击的菜单项
        addTab(item) {
            const newTab = {
                title: item.meta.title,
                name: item.url,
                iconClass: item.meta.icon,
            }
            // 判断当前editableTabs中是否存在该tab标签
            if (this.editableTabs.findIndex(item => item.title === newTab.title) === -1) {
                this.editableTabs.push(newTab);
                this.editableTabsValue = newTab.name;
                this.activeMenu = newTab.name;
            }
        },
        //移除tab标签
        removeTab(targetName) {
            let tabs = this.editableTabs
            let activeName = this.editableTabsValue
            if (activeName === targetName) {
                tabs.forEach((tab, index) => {
                    if (tab.name === targetName) {
                        let nextTab = tabs[index + 1] || tabs[index - 1]
                        if (nextTab) {
                            activeName = nextTab.name
                        }
                    }
                })
            }
            this.activeMenu = activeName
            this.editableTabsValue = activeName
            this.editableTabs = tabs.filter(tab => tab.name !== targetName)
            this.tabRouterList = this.tabRouterList.filter(item => item.path !== targetName)
        },
        setUsername(loginname){
            this.username = loginname
        },
        setRole(newRole){
            this.role = newRole
        },
        setMenuList(menu) {
            this.menulist = menu
        }
    },
    getters: {

    }
});

axios.interceptors.request.use(config => {
    if (userStore.token) {
        config.headers['Authorization'] = userStore.token
    }
    if (userStore.deviceType) {
        config.headers['Device-Type'] = userStore.deviceType
    }
    return config
}, error => {
    return Promise.reject(error)
})

export default useUserStore
