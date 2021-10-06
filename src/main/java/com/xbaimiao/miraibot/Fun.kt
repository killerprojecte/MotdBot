package com.xbaimiao.miraibot

import com.xbaimiao.miraibot.listeners.ServerInfo
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Proxy
import java.net.URL
import java.net.URLConnection
import javax.imageio.ImageIO

fun BufferedImage.toByteArray(): ByteArray {
    val out = ByteArrayOutputStream()
    ImageIO.write(this, "png", out)
    return out.toByteArray()
}

fun ServerInfo.getMotd(func: () -> Unit = {}): BufferedImage {
    func.invoke()
    val url = "https://api.loohpjames.com/serverbanner.png?ip=$ip&port=$port"
    return downloadImage(url)
}

fun downloadImage(link: String, proxy: Proxy? = null): BufferedImage {
    val url = URL(link)
    val connection: URLConnection = if (proxy != null) {
        url.openConnection(proxy)
    }else url.openConnection()

    connection.useCaches = false
    connection.defaultUseCaches = false
    connection.addRequestProperty("User-Agent", "Mozilla/5.0")
    connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
    connection.addRequestProperty("Pragma", "no-cache")
    val `in` = connection.getInputStream()
    val image = ImageIO.read(`in`)
    `in`.close()
    return image
}

object Fun {

    @JvmStatic
    fun Group.replay(message: Message) {
        runBlocking {
            this@replay.sendMessage(message)
        }
    }

    @JvmStatic
    fun Group.replay(message: String) {
        runBlocking {
            this@replay.sendMessage(message)
        }
    }

    @JvmStatic
    fun Group.upImage(ex: ExternalResource): Image {
        return runBlocking { this@upImage.uploadImage(ex) }
    }

    @JvmStatic
    fun MessageChain.toMiraiCode(): String {
        val msg = StringBuilder()
        this.forEach { a ->
            if (a !is OnlineMessageSource.Incoming.FromGroup && a !is OnlineMessageSource.Incoming.FromFriend && a !is OnlineMessageSource.Incoming.FromTemp) {
                msg.append(a.toString())
            }
        }
        return msg.toString()
    }

    @JvmStatic
    fun MessageChain.getAt(): Long {
        for (item in this) {
            if (item is At) {
                return item.target
            }
        }
        return -1000L
    }

    @JvmStatic
    fun MessageChain.getAtList(): List<Long> {
        val list = ArrayList<Long>()
        for (item in this) {
            if (item is At) {
                list.add(item.target)
            }
        }
        return list
    }

    /**
     * 发送Post请求
     */
    @JvmStatic
    fun URL.sendPost(param: String): String {
        val result = StringBuilder()
        try {
            // 打开和URL之间的连接
            val conn: URLConnection = this.openConnection()
            conn.setRequestProperty("accept", "*/*")
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
            // 发送POST请求必须设置如下两行
            conn.doOutput = true
            conn.doInput = true
            // 获取URLConnection对象对应的输出流
            val out = PrintWriter(conn.getOutputStream())
            // 发送请求参数
            out.print(param)
            // flush输出流的缓冲
            out.flush()
            val input = BufferedReader(InputStreamReader(conn.getInputStream()))
            var line: String?
            while (input.readLine().also { line = it } != null) {
                result.append(line)
            }
        } catch (e: Exception) {
            println("[POST请求]向地址：$this 发送数据：$param 发生错误!")
        }
        return result.toString()
    }

    /**
     * 发送Get请求
     */
    @JvmStatic
    fun URL.sendGet(): String {
        val result = StringBuilder()
        try {
            val conn = this.openConnection() // 打开和URL之间的连接
            conn.setRequestProperty("accept", "*/*") // 设置通用的请求属性
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
            conn.connectTimeout = 4000
            conn.connect() // 建立实际的连接
            val input =
                BufferedReader(InputStreamReader(conn.getInputStream(), "UTF-8")) // 定义BufferedReader输入流来读取URL的响应
            var line: String?
            while (input.readLine().also { line = it } != null) {
                result.append(line)
            }
        } catch (e: Exception) {
            println("发送GET请求出现异常！$this")
        }
        return result.toString()
    }

    @JvmStatic
    fun URL.sendPost(param: HashMap<String, String>): String {
        val post = StringBuilder()
        val size = param.keys.size - 1
        var num = 0
        param.forEach { (k, v) ->
            post.append(k).append("=").append(v)
            if (num++ != size) {
                post.append("&")
            }
        }
        return this.sendPost(post.toString())
    }

}