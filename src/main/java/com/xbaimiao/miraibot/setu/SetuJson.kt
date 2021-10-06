package com.xbaimiao.miraibot.setu

import com.xbaimiao.miraibot.downloadImage
import com.xbaimiao.miraibot.toByteArray
import java.awt.image.BufferedImage
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.Proxy

data class SetuJson(
    val `data`: List<DataX>,
    val error: String
) {
    /**
     * 下载色图片到文件
     */
    fun download(file: File, proxy: Proxy? = null) {
        if (Data.list.contains(`data`[0].urls.original)) {
            println("文件重复 跳过下载")
            return
        }
        Data.list.add(`data`[0].urls.original)
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.mkdirs()
            }
            file.createNewFile()
        }
        BufferedOutputStream(FileOutputStream(file)).use {
            it.write(image(proxy).toByteArray())
        }
    }

    fun image(proxy: Proxy? = null): BufferedImage {
        return downloadImage(`data`[0].urls.original.replace("\\/", "/"), proxy)
    }
}