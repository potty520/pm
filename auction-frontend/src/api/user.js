import request from "@/utils/request";
export const getMe = () => request({
    url: "/user/me",
    method: "get"
});
export const updateMe = (data) => request({
    url: "/user/me",
    method: "put",
    data
});
export const verifyMe = (data) => request({
    url: "/user/verify",
    method: "post",
    data
});
export const depositMe = (data) => request({
    url: "/user/deposit",
    method: "post",
    data
});
export const getUserList = () => request({
    url: "/user/list",
    method: "get"
});
export const registerUser = (data) => request({
    url: "/user/register",
    method: "post",
    data
});
export const updateUserById = (userId, data) => request({
    url: `/user/${userId}`,
    method: "put",
    data
});
export const deleteUserById = (userId) => request({
    url: `/user/${userId}`,
    method: "delete"
});
