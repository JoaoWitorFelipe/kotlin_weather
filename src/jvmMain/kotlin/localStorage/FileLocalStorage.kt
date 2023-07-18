package localStorage

import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class FileLocalStorage : LocalStorage<String> {
    override fun get(key: String): String {
        val properties = Properties()

        FileInputStream("config.properties").use { fileInputStream ->
            properties.load(fileInputStream)
            return properties.getProperty(key, "") ?: ""
        }
    }

    override fun save(key: String, value: String) {
        val properties = Properties()

        FileOutputStream("config.properties").use { fileOutputStream ->
            properties.setProperty(key, value)
            properties.store(fileOutputStream, null)
        }
    }
}