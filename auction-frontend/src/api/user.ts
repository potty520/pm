import request from "@/utils/request";

export const getMe = () =>
  request({
    url: "/user/me",
    method: "get"
  });

export const updateMe = (data: unknown) =>
  request({
    url: "/user/me",
    method: "put",
    data
  });

export const verifyMe = (data: unknown) =>
  request({
    url: "/user/verify",
    method: "post",
    data
  });

export const depositMe = (data: unknown) =>
  request({
    url: "/user/deposit",
    method: "post",
    data
  });

export const getUserList = () =>
  request({
    url: "/user/list",
    method: "get"
  });

export const registerUser = (data: unknown) =>
  request({
    url: "/user/register",
    method: "post",
    data
  });

export const updateUserById = (userId: number, data: unknown) =>
  request({
    url: `/user/${userId}`,
    method: "put",
    data
  });

export const deleteUserById = (userId: number) =>
  request({
    url: `/user/${userId}`,
    method: "delete"
  });
