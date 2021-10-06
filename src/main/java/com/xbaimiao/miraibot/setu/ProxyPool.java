package com.xbaimiao.miraibot.setu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ProxyPool {
    public static final List<String> proxys = new ArrayList<String>();

    public static void getProxysFromAPIs() {
        Utils.log("Proxy"+"正在使用API获取代理..");

        getProxysFromAPI("https://www.89ip.cn/tqdl.html?api=1&num=9999");

        Utils.log("Proxy"+"代理更新完成!数量:" + proxys.size());
    }

    public static void getProxysFromAPI(String url) {
        String ips = Utils.sendGet(url);
        Matcher matcher = Utils.matches(ips, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}");
        synchronized (proxys) {
            while (matcher.find()) {
                String ip = matcher.group();
                if (!proxys.contains(ip)) {
                    proxys.add(ip);
                }
            }
        }
    }

    public static void getProxysFromFile() {
        try {
            File file = new File("http.txt");
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                proxys.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.log("Proxy", "从本地读取代理完成!数量:" + proxys.size());
    }

    public static void runUpdateProxysTask(final int time) {
        new Thread(() -> {
            while (true) {
                Utils.sleep(time * 1000);
                synchronized (proxys) {
                    proxys.clear();
                }
                getProxysFromAPIs();
            }
        }).start();
    }
}
