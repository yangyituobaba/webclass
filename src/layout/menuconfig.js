export const menulistt = {
    admin: [
        { id: 1, url: "/admin/productlist", meta: { title: "商品列表", icon: "goods" } },
        { id: 2, url: "/admin/productedit", meta: { title: "编辑商品", icon: "edit" } },
        { id: 3, url: "/admin/orders", meta: { title: "订单管理", icon: "order" } },
        { id: 4, url: "/admin/users", meta: { title: "用户管理", icon: "user" } },
        { id: 5, url: "/admin/profile", meta: { title: "管理员资料", icon: "setting" } },
    ],
    client: [
        { id: 1, url: "/client/products", meta: { title: "浏览商品", icon: "search" } },
        { id: 2, url: "/client/orders", meta: { title: "我的订单", icon: "order" } },
        { id: 3, url: "/client/profile", meta: { title: "客户资料", icon: "user" } },
    ],
    delivery: [
        { id: 1, url: "/delivery/orders", meta: { title: "配送订单", icon: "car" } },
        { id: 2, url: "/delivery/profile", meta: { title: "配送员资料", icon: "user" } },
    ],
    default:[]
}
