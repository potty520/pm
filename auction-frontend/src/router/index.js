import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: "/", redirect: "/dashboard/user" },
        { path: "/login", component: () => import("@/views/auth/Login.vue") },
        {
            path: "/dashboard",
            component: () => import("@/views/layout/MainLayout.vue"),
            children: [
                { path: "", redirect: "/dashboard/user" },
                { path: "user", component: () => import("@/views/user/UserManage.vue") },
                { path: "item", component: () => import("@/views/item/ItemManage.vue") },
                { path: "auction", component: () => import("@/views/auction/AuctionManage.vue") },
                { path: "order", component: () => import("@/views/order/OrderManage.vue") },
                { path: "system", component: () => import("@/views/admin/AdminManage.vue") }
            ]
        },
        { path: "/auction/list", component: () => import("@/views/auction/AuctionList.vue") },
        { path: "/auction/live/:id", component: () => import("@/views/auction/LiveBidding.vue") },
        { path: "/admin/manage", redirect: "/dashboard/system" }
    ]
});
router.beforeEach((to) => {
    if (to.path === "/login")
        return true;
    const userStore = useUserStore();
    if (!userStore.isLoggedIn) {
        return { path: "/login" };
    }
    return true;
});
export default router;
