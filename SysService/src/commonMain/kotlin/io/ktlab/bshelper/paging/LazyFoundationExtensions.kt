package io.ktlab.bshelper.paging

//@Suppress("PrimitiveInLambda")
//fun <T : Any> LazyPagingItems<T>.itemKey(
//    key: ((item: @JvmSuppressWildcards T) -> Any)? = null
//): (index: Int) -> Any {
//    return { index ->
//        if (key == null) {
//            PagingPlaceholderKey(index)
//        } else {
//            val item = peek(index)
//            if (item == null) PagingPlaceholderKey(index) else key(item)
//        }
//    }
//}
//
//@Suppress("PrimitiveInLambda")
//fun <T : Any> LazyPagingItems<T>.itemContentType(
//    contentType: ((item: @JvmSuppressWildcards T) -> Any?)? = null
//): (index: Int) -> Any? {
//    return { index ->
//        if (contentType == null) {
//            null
//        } else {
//            val item = peek(index)
//            if (item == null) PagingPlaceholderContentType else contentType(item)
//        }
//    }
//}
