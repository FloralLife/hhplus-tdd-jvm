package io.hhplus.tdd

import kotlin.random.Random

object TestUtils {
    fun randomId(): Long = Random.nextLong(1, 1_000_000)
}
