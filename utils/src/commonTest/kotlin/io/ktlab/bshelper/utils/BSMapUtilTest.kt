package io.ktlab.bshelper.utils
import java.io.File
import kotlin.test.Test


class BSMapUtilsTest{
    companion object {
        val sampleData = File("/Users/sisyphus/Downloads/1973b (Hands Up to the Sky - Joshabi & Alice)")
    }

    @Test
    fun shouldFindMatches() {
        val res = BSMapUtils.extractMapInfoFromDir("abc",sampleData,"")
        println(res.mapId)
//        assertEquals(2, results.size)
//        for (result in results) {
////            assertContains(result, "abc")
//        }
    }
}