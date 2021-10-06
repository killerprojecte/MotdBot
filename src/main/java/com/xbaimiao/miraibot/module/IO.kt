package com.xbaimiao.miraibot.module

import java.io.*
import java.net.URL

object IO {

    fun readFully(inputStream: InputStream): String {
        val stream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            stream.write(buf, 0, len)
        }
        return stream.toString("UTF-8")
    }

    fun readFromURL(`in`: String?, def: String): String {
        try {
            URL(`in`).openStream().use { inputStream ->
                BufferedInputStream(inputStream).use { bufferedInputStream ->
                    return readFully(
                        bufferedInputStream
                    )
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            return def
        }
    }

    fun downloadFile(`in`: String, file: File): Boolean {
        try {
            URL(`in`).openStream().use { inputStream ->
                BufferedInputStream(inputStream).use { bufferedInputStream ->
                    bufferedInputStream.toFile(file)
                    return true
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            return false
        }
    }

    fun InputStream.toFile(file: File): File {
        FileOutputStream(file).use { fos ->
            BufferedOutputStream(fos).use { bos ->
                val buf = ByteArray(1024)
                var len: Int
                while (this.read(buf).also { len = it } > 0) {
                    bos.write(buf, 0, len)
                }
                bos.flush()
            }
        }
        return file
    }

    fun copy(file1: File, file2: File) {
        FileInputStream(file1).use { fileIn ->
            FileOutputStream(file2).use { fileOut ->
                fileIn.channel.use { channelIn ->
                    fileOut.channel.use { channelOut ->
                        channelIn.transferTo(
                            0L,
                            channelIn.size(),
                            channelOut
                        )
                    }
                }
            }
        }
    }

}
