import { defineStore } from "pinia";
function parseUserId(token) {
    const m = /^uid:(\d+)$/.exec(token.trim());
    return m ? Number(m[1]) : null;
}
export const useUserStore = defineStore("user", {
    state: () => ({
        token: localStorage.getItem("auction_token") || "",
        userId: Number(localStorage.getItem("auction_userId") || 0),
        isLoggedIn: !!localStorage.getItem("auction_token")
    }),
    actions: {
        setToken(token) {
            this.token = token;
            this.isLoggedIn = !!token;
            const uid = token ? parseUserId(token) : null;
            this.userId = uid || 0;
            if (token) {
                localStorage.setItem("auction_token", token);
                localStorage.setItem("auction_userId", String(this.userId));
            }
            else {
                localStorage.removeItem("auction_token");
                localStorage.removeItem("auction_userId");
            }
        },
        logout() {
            this.setToken("");
        }
    }
});
