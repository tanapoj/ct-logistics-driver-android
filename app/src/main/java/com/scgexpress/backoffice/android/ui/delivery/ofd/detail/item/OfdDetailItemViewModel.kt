package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class OfdDetailItemViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val _data = MutableLiveData<ArrayList<OfdDetailItemModel>>()
    val data: LiveData<ArrayList<OfdDetailItemModel>>
        get() = _data

    fun requestItem(id: String) {
        _data.value = arrayListOf(
            OfdDetailItemModel(
                "112645648",
                OfdDetailItemStatus.OFD,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            ),
            OfdDetailItemModel(
                "121648922",
                OfdDetailItemStatus.OFD,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            ),
            OfdDetailItemModel(
                "121648922",
                OfdDetailItemStatus.OFD_RETENTION,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            ),
            OfdDetailItemModel(
                "121648922",
                OfdDetailItemStatus.OFD_DELIVERED,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            ),
            OfdDetailItemModel(
                "121648922",
                OfdDetailItemStatus.PICKUP_PICKED,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            ),
            OfdDetailItemModel(
                "121648922",
                OfdDetailItemStatus.PICKUP,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            ),
            OfdDetailItemModel(
                "123546151",
                OfdDetailItemStatus.PICKUP_PICKED,
                "119/209, Sansiri Village,\nSaimai, Bangkok 10220",
                "2018-07-24 26:48",
                "0812345677"
            )
        )
    }

}
