export interface AuctionItem {
  id: number;
  title: string;
  description?: string;
  startPrice: number;
  currentPrice: number;
  bidIncrement: number;
  sellerId: number;
  status: number;
  endTime?: string;
}

export interface BidRecord {
  id: number;
  itemId: number;
  userId: number;
  bidAmount: number;
  bidTime: string;
  isWin: number;
}

export interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
}
