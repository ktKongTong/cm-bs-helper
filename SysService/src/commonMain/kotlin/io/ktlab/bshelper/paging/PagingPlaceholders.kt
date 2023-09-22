package io.ktlab.bshelper.paging


internal data class PagingPlaceholderKey(private val index: Int)  {
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(index)
//    }

     fun describeContents(): Int {
        return 0
    }

    companion object {
    }
}

internal object PagingPlaceholderContentType