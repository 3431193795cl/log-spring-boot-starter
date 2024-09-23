package org.jerry.log.toolkit.tool;

import cn.hutool.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class IPTool {
    private IPTool() {
    }

    /**
     * 获取客户端IP地址
     * 该方法通过HttpServletRequest请求对象来获取客户端的IP地址
     * 它首先尝试通过各种HTTP头部信息获取IP地址，如果这些头部信息不存在或值为"unknown"，
     * 则尝试直接从请求对象中获取远程地址，并排除本地回环地址
     *
     * @param request HttpServletRequest请求对象，用于获取客户端IP地址
     * @return 客户端的IP地址字符串如果无法获取，则返回null或"localhost"
     */
    public static String getIp(HttpServletRequest request) {
        // 定义一个IP地址的头部信息列表，按照优先级排列
        String[] headers = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        // 遍历头部信息列表，尝试获取IP地址
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.equalsIgnoreCase("unknown")) {
                // 如果是 x-forwarded-for, 取第一个 IP
                if ("x-forwarded-for".equals(header)) {
                    int commaIndex = ip.indexOf(',');
                    if (commaIndex > 0) {
                        ip = ip.substring(0, commaIndex);
                    }
                }
                return ip;
            }
        }
        // 如果通过头部信息无法获取IP地址，则尝试直接从请求对象中获取远程地址
        String remoteAddr = request.getRemoteAddr();
        // 检查是否为本地回环地址
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "127.0.0.1".equals(remoteAddr)) {
            return null; // 或者返回一个默认值，如 "localhost"
        }
        return remoteAddr;
    }

    public static String getIpPossession(String ip) {
        String url = "https://myip.ipip.net/";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ip", ip);
        String string = HttpUtil.get(url, paramMap);
        return string.contains("<")? null : string;
    }

}
