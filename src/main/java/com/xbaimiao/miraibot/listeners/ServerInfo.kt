package com.xbaimiao.miraibot.listeners

import java.util.regex.Pattern

class ServerInfo(string: String) {

    var ip: String
    var port: String

    fun isMember(): Boolean {
        val pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")
        val isNum = pattern.matcher(port)
        return isNum.matches()
    }

    init {
        if (string.contains(":")) {
            ip = string.split(":")[0]
            port = string.split(":")[1]
        } else {
            ip = string
            port = "25565"
        }
    }
}