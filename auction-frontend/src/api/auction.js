import request from "@/utils/request";
export const getAuctionList = () => request({
    url: "/auction/list",
    method: "get"
});
export const getAuctionDetail = (id) => request({
    url: `/auction/item/${id}`,
    method: "get"
});
export const getBidRecords = (itemId) => request({
    url: `/auction/bids/${itemId}`,
    method: "get"
});
export const sendBid = (itemId, userId, bidAmount) => request({
    url: `/auction/bid/${itemId}`,
    method: "post",
    data: { userId, bidAmount }
});
export const publishAuction = (data) => request({
    url: "/auction/publish",
    method: "post",
    data
});
export const searchAuctions = (params) => request({
    url: "/auction/search",
    method: "get",
    params
});
export const reviewAuction = (itemId, data) => request({
    url: `/auction/review/${itemId}`,
    method: "post",
    data
});
export const endAuction = (itemId) => request({
    url: `/auction/end/${itemId}`,
    method: "post"
});
export const getCategoryList = () => request({
    url: "/category/list",
    method: "get"
});
export const getSessionList = () => request({
    url: "/auction/session/list",
    method: "get"
});
