package io.ktlab.bshelper.utils

import okio.Path.Companion.toPath
import org.junit.Test

class FileUtilKtTests {




    @Test
    fun asValidFilename() {
        val res = BSMapUtils.mapDigest("/Users/sisyphus/Documents/playlist/1e6ff (Somewhere Out There - Swifter, Mawntee, Reddek)".toPath())
        println(res.first)
        assert(res.first == "448d219117992026eb23c98dc920ce73a912f289")
        // generate test
        val test = listOf(
            "a" to "a",
            "a/b" to "a_b",
            "a\\b" to "a_b",
            "a:b" to "a_b",
            "a*b" to "a_b",
            "a?b" to "a_b",
            "a\"b" to "a_b",
            "a<b" to "a_b",
            "a>b" to "a_b",
            "a|b" to "a_b",
            "a/b:c*d?e\"f<g>h|i" to "a_b_c_d_e_f_g_h_i",
        )
        test.forEach {
            assert(it.first.asValidFilename() == it.second)
        }
    }

    @Test
    fun encodeImageToBase64() {
    }

    @Test
    fun renameDirIfDirExist() {

    }
}