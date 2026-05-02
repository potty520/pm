import request from "@/utils/request";

export const getMyOrders = () =>
  request({
    url: "/order/my",
    method: "get"
  });

export const payOrder = (orderId: number, paymentMethod: string) =>
  request({
    url: `/order/${orderId}/pay`,
    method: "post",
    data: { paymentMethod }
  });

export const shipOrder = (orderId: number) =>
  request({
    url: `/order/${orderId}/ship`,
    method: "post"
  });

export const completeOrder = (orderId: number) =>
  request({
    url: `/order/${orderId}/complete`,
    method: "post"
  });

export const cancelOrder = (orderId: number) =>
  request({
    url: `/order/${orderId}/cancel`,
    method: "post"
  });
