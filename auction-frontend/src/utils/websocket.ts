import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

class WebSocketService {
  private stompClient: Client | null = null;
  private subscriptions: Map<string, { unsubscribe: () => void }> = new Map();

  connect(token = "") {
    if (this.stompClient?.active) {
      return;
    }
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(import.meta.env.VITE_WS_URL || "/ws"),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000
    });
    this.stompClient.activate();
  }

  subscribeBidUpdate(itemId: number, callback: (payload: unknown) => void) {
    if (!this.stompClient) {
      return;
    }
    this.stompClient.onConnect = () => {
      const subscription = this.stompClient!.subscribe(`/topic/bid/${itemId}`, (message) => {
        callback(JSON.parse(message.body));
      });
      this.subscriptions.set(`bid-${itemId}`, subscription);
    };
  }

  disconnect() {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
    this.subscriptions.clear();
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }
}

export default new WebSocketService();
