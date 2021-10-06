package com.xbaimiao.miraibot.setu

import com.google.gson.Gson
import com.xbaimiao.miraibot.Fun.sendGet
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL
import java.util.*

object Data {

    val list = arrayListOf<String>()

}

fun main() {
    ProxyPool.getProxysFromAPIs()
    for ((a, p) in ProxyPool.proxys.withIndex()) {
        if (a > 100) {
            break
        }
        Thread {
//            val _p = p.split(":")
//            val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(_p[0], _p[1].toInt()))
            while (true) {
                val file = randomFile()
                try {
                    val info = getSetuInfo()
                    println("Thread/$a: 开始下载 ${info.data[0].pid}")
                    info.download(file)
                    println("Thread/$a: ${info.data[0].pid} 下载完成")
                    Thread.sleep(5000)
                } catch (e: Exception) {
                    file.delete()
                }
            }
        }.start()
    }

}

fun getSetuInfo(): SetuJson {
    val gson = Gson()
    val url =
        URL("https://api.lolicon.app/setu/v2")
    val data = url.sendGet()
    return gson.fromJson(data, SetuJson::class.java)
}

val args = listOf(
    "A",
    "B",
    "C",
    "D",
    "E",
    "F",
    "G",
    "H",
    "I",
    "J",
    "K",
    "L",
    "M",
    "N",
    "O",
    "P",
    "Q",
    "R",
    "S",
    "T",
    "U",
    "V",
    "W",
    "X",
    "Y",
    "Z"
)

val random = Random()

fun randomFile(): File {
    var name = ""
    for (a in 0..10) {
        name += args[random.nextInt(args.size - 1)]
    }
    name += ".png"
    return File("D:\\色图2\\$name")
}