package com.scgexpress.backoffice.android.ui.menu

import androidx.annotation.DrawableRes

data class MenuModel(

        val id: Int,

        val title: String,

        val bgColor: Int,

        val textColor: Int,

        @DrawableRes
        val iconRes: Int
)