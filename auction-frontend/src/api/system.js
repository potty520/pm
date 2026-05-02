import request from "@/utils/request";
export const getRoles = () => request({
    url: "/system/roles",
    method: "get"
});
export const createRole = (data) => request({
    url: "/system/roles",
    method: "post",
    data
});
export const getPermissions = () => request({
    url: "/system/permissions",
    method: "get"
});
export const createPermission = (data) => request({
    url: "/system/permissions",
    method: "post",
    data
});
export const bindRole = (data) => request({
    url: "/system/bind-role",
    method: "post",
    data
});
export const unbindRole = (data) => request({
    url: "/system/unbind-role",
    method: "post",
    data
});
export const bindPermission = (data) => request({
    url: "/system/bind-permission",
    method: "post",
    data
});
export const getUserRoleAssignments = (userIds) => request({
    url: "/system/user-roles",
    method: "get",
    params: userIds?.length ? { userIds: userIds.join(",") } : {}
});
