package com.scgexpress.backoffice.android.base

import android.app.Activity
import android.content.Intent
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.ui.menu.MenuActivity


fun handleDrawerNavigation(activity: Activity, itemId: Int, onClickLogout: () -> Unit = {}): Boolean {

    when (itemId) {
        R.id.nav_menu_dashboard -> {
            val intent = Intent(activity, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            activity.startActivity(intent)
            activity.finish()
            return true
        }
        R.id.nav_menu_pickup -> {
            //val intent = Intent(activity, PickupMainActivity::class.java)
            //activity.startActivity(intent)
            return true
        }
        R.id.nav_menu_delivery -> {
            //val intent = Intent(activity, DeliveryMainActivity::class.java)
            //activity.startActivity(intent)
            return true
        }
        R.id.nav_menu_line_haul -> {
            // todo : add something
            return true
        }
        R.id.nav_menu_sdreport -> {
            //val intent = Intent(activity, SDReportMainActivity::class.java)
            //activity.startActivity(intent)
            return true
        }
        R.id.nav_menu_line -> {
            val intent = Utils.getLineAppIntent(activity)
            activity.startActivity(intent)
            return true
        }
        R.id.nav_menu_logout -> {
            onClickLogout()
            return true
        }
    }

    return false
}
