package com.example.quash_flutter_sdk.recorder

class QuashCircularBuffer<T>(private val capacity: Int) {
    private val buffer = arrayOfNulls<Any>(capacity)
    private var size = 0
    private var start = 0

    @Synchronized
    fun add(item: T) {
        if (size == capacity) {
            start = (start + 1) % capacity
        } else {
            size++
        }
        buffer[(start + size - 1) % capacity] = item
    }


    @Synchronized
    fun getAll(): List<T> {
        val list = mutableListOf<T>()
        for (i in 0 until size) {
            list.add(buffer[(start + i) % capacity] as T)
        }
        return list
    }

    @Synchronized
    fun clear() {
        for (i in buffer.indices) {
            buffer[i] = null
        }
        size = 0
        start = 0
    }
}