package com.xbaimiao.miraibot

import com.xbaimiao.miraibot.module.IO.toFile
import com.xbaimiao.miraibot.module.XConfig
import net.mamoe.mirai.utils.BotConfiguration
import java.io.File

object Start {

    lateinit var config: XConfig
        private set

    @JvmStatic
    fun main(args: Array<String>) {
        saveResource("setting.yml", "mirai${File.separator}setting.yml")
        saveResource("config.yml", "mirai${File.separator}config.yml")
        config = XConfig("mirai/setting.yml")
        val botConfiguration = object : BotConfiguration() {
            init {
                protocol = when (config.getString("protocol")) {
                    "手表" -> MiraiProtocol.ANDROID_WATCH
                    "手机" -> MiraiProtocol.ANDROID_PHONE
                    else -> MiraiProtocol.ANDROID_PAD
                }
                if (config["log"] == false) {
                    noBotLog()
                }
                if (config["netLog"] == false) {
                    noNetworkLog()
                }
                fileBasedDeviceInfo("deviceInfo.json")
            }
        }
        Bot.run(config.getLong("qq"), config.getString("password")!!, botConfiguration)

//        val botConfiguration = object : BotConfiguration() {
//            init {
//                protocol = MiraiProtocol.ANDROID_PAD
//                fileBasedDeviceInfo("deviceInfo.json")
//            }
//        }
//        Bot.run(2157207381, "17784439313g@", botConfiguration)
    }

    fun saveResource(name: String, path: String) {
        val file = File(path)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
            this::class.java.classLoader.getResourceAsStream(name)?.toFile(file)
        }
    }

}
