import axios from "axios";
import { ElMessage } from "element-plus";
const request = axios.create({
    baseURL: "http://localhost:8080/api",
    timeout: 10000
});
request.interceptors.request.use((config) => {
    const token = localStorage.getItem("auction_token") || "";
    config.headers = config.headers || {};
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
request.interceptors.response.use((response) => {
    const payload = response.data;
    if (payload && typeof payload === "object" && "code" in payload && payload.code !== 200) {
        ElMessage.error(payload.message || "请求失败");
        return Promise.reject(new Error(payload.message || "请求失败"));
    }
    return payload;
}, (error) => {
    ElMessage.error(error?.message || "网络异常");
    return Promise.reject(error);
});
export default request;
