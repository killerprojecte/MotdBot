package com.xbaimiao.miraibot.listeners

import com.xbaimiao.miraibot.getMotd
import com.xbaimiao.miraibot.module.XConfig
import com.xbaimiao.miraibot.toByteArray
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import studio.trc.minecraft.serverpinglib.API.MCServerSocket
import studio.trc.minecraft.serverpinglib.API.MCServerStatus
import studio.trc.minecraft.serverpinglib.Protocol.ProtocolVersion
import java.io.File

class PingMCServer : SimpleListenerHost() {

    companion object {
        val config = XConfig(File("mirai${File.separator}config.yml"))
    }

    val mgc = config.getStringList("punish")

    @EventHandler
    suspend fun GroupMessageEvent.onMessage() {
        if (!MiniGame.isEnable(group.id)) {
            return
        }
        val message = this.message.contentToString()
        if (!sender.isOperator()) {
            for (s in mgc) {
                if (message.replace(Regex("[,.?:\"“”\\]\\[\\}\\{`;~_\\-=+——]"), "").contains(s)) {
                    sender.mute(config.getInt("time") * 60)
                }
            }
        }
        if (message.startsWith("查服")) {
            val ip = message.replace(Regex("[\\u4e00-\\u9fa5]|§+[0-9]|&+[0-9]| "), "")
            if (ip == "") {
                group.sendMessage("你没有说要查询的ip")
                return
            }
            val info = ServerInfo(ip)
            if (!info.isMember()) {
                group.sendMessage("你输入的端口不是一个数字")
                return
            }

            val socket = MCServerSocket.getInstance(info.ip, info.port.toInt())
            val status: MCServerStatus
            val oldTime = System.currentTimeMillis()
            try {
                status = socket.getStatus(ProtocolVersion.v1_15_2)
            } catch (e: Exception) {
                group.sendMessage("$ip 服务器链接异常")
                return
            }
            val newTime = System.currentTimeMillis()
            if (!status.isMCServer) {
                group.sendMessage("这个ip不是一个MC服务器")
                return
            }
            var serverInfo = """
                    服务器IP: $ip
                    服务器延迟: ${newTime - oldTime}ms
                    在线人数: ${status.onlinePlayers}
                    版本号: ${status.version}
                """.trimIndent()
            group.sendMessage(serverInfo)
            val msg = PlainText("Motd信息") + (group.uploadImage(info.getMotd().toByteArray().toExternalResource()))
            group.sendMessage(msg)
//            if (icon.hasServerIcon()) {
//                val m = msg + (group.uploadImage(icon.imageBytes.toExternalResource()))
//                group.sendMessage(m)
//                return
//            }
            group.sendMessage(msg)
        }
    }

    @EventHandler
    suspend fun MemberJoinEvent.onEvent() {
        if (!MiniGame.isEnable(group.id)) {
            return
        }
        group.sendMessage(config.getStringColor("joinMessage")!!)
    }

    @EventHandler
    suspend fun MemberLeaveEvent.Quit.onEvent() {
        if (!MiniGame.isEnable(group.id)) {
            return
        }
        group.sendMessage(config.getStringColor("levelMessage")!!.replace("{0}", this.member.id.toString()))
    }

}