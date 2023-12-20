package io.ktlab.bshelper.utils

import okio.Path.Companion.toPath
import org.junit.jupiter.api.Test

class BSMapUtilsTest {
    @Test
    fun testMapDigest() {

        val path = "/Users/sisyphus/Documents/playlist/1e6ff (Somewhere Out There - Swifter, Mawntee, Reddek)".toPath()
        val res = BSMapUtils.mapDigest(path)
        println(res.first)
        assert(res.first == "448d219117992026eb23c98dc920ce73a912f289")
    }
}