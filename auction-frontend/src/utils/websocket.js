import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.subscriptions = new Map();
    }
    connect(token = "") {
        if (this.stompClient?.active) {
            return;
        }
        this.stompClient = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
            connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
            reconnectDelay: 5000
        });
        this.stompClient.activate();
    }
    subscribeBidUpdate(itemId, callback) {
        if (!this.stompClient) {
            return;
        }
        this.stompClient.onConnect = () => {
            const subscription = this.stompClient.subscribe(`/topic/bid/${itemId}`, (message) => {
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
