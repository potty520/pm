import request from "@/utils/request";
import type { ApiResult, AuctionItem, BidRecord } from "@/types/auction";

export const getAuctionList = () =>
  request<unknown, ApiResult<AuctionItem[]>>({
    url: "/auction/list",
    method: "get"
  });

export const getAuctionDetail = (id: number) =>
  request<unknown, ApiResult<AuctionItem>>({
    url: `/auction/item/${id}`,
    method: "get"
  });

export const getBidRecords = (itemId: number) =>
  request<unknown, ApiResult<BidRecord[]>>({
    url: `/auction/bids/${itemId}`,
    method: "get"
  });

export const sendBid = (itemId: number, userId: number, bidAmount: number) =>
  request<unknown, ApiResult<BidRecord>>({
    url: `/auction/bid/${itemId}`,
    method: "post",
    data: { userId, bidAmount }
  });

export const publishAuction = (data: unknown) =>
  request({
    url: "/auction/publish",
    method: "post",
    data
  });

export const searchAuctions = (params: { keyword?: string; categoryId?: number; status?: number }) =>
  request({
    url: "/auction/search",
    method: "get",
    params
  });

export const reviewAuction = (itemId: number, data: { approved: boolean; rejectReason?: string }) =>
  request({
    url: `/auction/review/${itemId}`,
    method: "post",
    data
  });

export const endAuction = (itemId: number) =>
  request({
    url: `/auction/end/${itemId}`,
    method: "post"
  });

export const getCategoryList = () =>
  request({
    url: "/category/list",
    method: "get"
  });

export const getSessionList = () =>
  request({
    url: "/auction/session/list",
    method: "get"
  });
