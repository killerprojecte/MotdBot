package com.xbaimiao.miraibot.module

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

open class XConfig(val file: File) : YamlConfiguration() {

    constructor(fileName: String) : this(File(fileName))

    init {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        this.load(file)
    }

    override fun saveToFile() {
        save(file)
    }

    fun getStringColor(path: String): String? {
        return super.getString(path)?.replace("&", "§")
    }

    fun getStringColor(path: String, def: String?): String? {
        return super.getString(path, def)?.replace("&", "§")
    }

    fun isEmpty(): Boolean {
        return getKeys(true).size == 0
    }

    companion object {

        @JvmStatic
        fun create(file: File): XConfig {
            return XConfig(file)
        }
    }

}