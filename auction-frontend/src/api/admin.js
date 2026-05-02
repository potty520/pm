import request from "@/utils/request";
export const getAdminCategories = () => request({
    url: "/admin/categories",
    method: "get"
});
export const createAdminCategory = (data) => request({
    url: "/admin/categories",
    method: "post",
    data
});
export const updateAdminCategory = (data) => request({
    url: "/admin/categories",
    method: "put",
    data
});
export const deleteAdminCategory = (id) => request({
    url: `/admin/categories/${id}`,
    method: "delete"
});
export const getAdminSessions = () => request({
    url: "/admin/sessions",
    method: "get"
});
export const createAdminSession = (data) => request({
    url: "/admin/sessions",
    method: "post",
    data
});
export const updateAdminSession = (data) => request({
    url: "/admin/sessions",
    method: "put",
    data
});
export const deleteAdminSession = (id) => request({
    url: `/admin/sessions/${id}`,
    method: "delete"
});
export const getAdminLogs = (limit = 50) => request({
    url: "/admin/logs",
    method: "get",
    params: { limit }
});
