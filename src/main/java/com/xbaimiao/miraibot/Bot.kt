package com.xbaimiao.miraibot

import com.xbaimiao.miraibot.listeners.MiniGame
import com.xbaimiao.miraibot.listeners.PingMCServer
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.BotConfiguration

object Bot {

    lateinit var miraiBot: Bot

    fun run(qqId: Long, password: String, botConfiguration: BotConfiguration) {
        runBlocking {
            miraiBot = BotFactory.newBot(qqId, password, botConfiguration)
            miraiBot.login()
            miraiBot.eventChannel.registerListenerHost(PingMCServer())
            miraiBot.eventChannel.registerListenerHost(MiniGame())
            miraiBot.join()
        }
    }

}