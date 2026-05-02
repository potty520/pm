import request from "@/utils/request";

export const getRoles = () =>
  request({
    url: "/system/roles",
    method: "get"
  });

export const createRole = (data: unknown) =>
  request({
    url: "/system/roles",
    method: "post",
    data
  });

export const getPermissions = () =>
  request({
    url: "/system/permissions",
    method: "get"
  });

export const createPermission = (data: unknown) =>
  request({
    url: "/system/permissions",
    method: "post",
    data
  });

export const bindRole = (data: unknown) =>
  request({
    url: "/system/bind-role",
    method: "post",
    data
  });

export const unbindRole = (data: unknown) =>
  request({
    url: "/system/unbind-role",
    method: "post",
    data
  });

export const bindPermission = (data: unknown) =>
  request({
    url: "/system/bind-permission",
    method: "post",
    data
  });

export const getUserRoleAssignments = (userIds?: number[]) =>
  request({
    url: "/system/user-roles",
    method: "get",
    params: userIds?.length ? { userIds: userIds.join(",") } : {}
  });
