package com.xbaimiao.miraibot.listeners

import com.xbaimiao.miraibot.Bot
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import java.util.*
import java.util.regex.Pattern

class MiniGame : SimpleListenerHost() {

    val map = HashMap<Long, XAxB>()

    companion object {
        fun isEnable(id: Long): Boolean {
            return PingMCServer.config.getStringList("gourps").contains(id.toString())
        }

        fun Friend.isAdmin(): Boolean {
            return PingMCServer.config.getStringList("owner").contains(this.id.toString())
        }
    }

    @EventHandler
    suspend fun FriendMessageEvent.onEvent() {
        if (sender.isAdmin()){
            for (singleMessage in message) {
                if (singleMessage is Image) {
                    sender.sendMessage(Mirai.queryImageUrl(Bot.miraiBot, singleMessage))
                }
            }
        }
    }

    @EventHandler
    suspend fun GroupMessageEvent.onMessage() {
        if (!isEnable(group.id)) {
            return
        }
        val msg = message.contentToString()
        val groupId = group.id
        if (isMember(msg)) {
            val xAxB = map[groupId]
            if (xAxB != null) {
                group.sendMessage(xAxB.run(msg.toInt()))
                if (xAxB.isClose) {
                    map.remove(groupId)
                }
            }
        }
        if (msg == "猜数字") {
            if (map[groupId] != null) {
                group.sendMessage("猜数字游戏已经开始了,发送数字即可")
                return
            }
            group.sendMessage("猜数字游戏开始了,发送数字即可")
            map[groupId] = XAxB(groupId, 100)
        }
    }

    fun isMember(port: String): Boolean {
        val pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")
        val isNum = pattern.matcher(port)
        return isNum.matches()
    }

    class XAxB(val id: Long, val max: Int) {

        val random = Random()

        var min = random.nextInt(max)

        var isClose = false

        fun run(num: Int): String {
            if (num > min) {
                return "猜大了噢"
            }
            if (num < min) {
                return "猜小了噢"
            }
            isClose = true
            return "恭喜你猜对了"
        }

    }

}