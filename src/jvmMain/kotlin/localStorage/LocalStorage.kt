package localStorage

interface LocalStorage<T> {
    fun get(key: String): T
    fun save(key: String, value: T)
}