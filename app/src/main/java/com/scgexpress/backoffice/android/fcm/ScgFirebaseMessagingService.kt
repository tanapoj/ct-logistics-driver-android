package com.scgexpress.backoffice.android.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.MetaBooking
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import com.scgexpress.backoffice.android.ui.notification.NotificationModel
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject


class ScgFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val notificationId = 1
        private const val channelId = "channel01"
        private const val channelName = "FCMNotification"
        private const val contentTitle = "SCG Express"
    }

    @Inject
    lateinit var notificationRepository: NotificationLocalRepository

    @Inject
    lateinit var loginPreference: LoginPreference

    @Inject
    lateinit var masterDataPreference: MasterDataPreference

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Timber.d("From: %s", remoteMessage!!.from)

        // Check if message contains a data payload.
        // Ex. Message data payload: {action={""}, type=0, category=PICKUP BOOKINGS, timestamp=1537164162, message=You have new booking!}
        try {
            if (remoteMessage.data.isNotEmpty()) {
                Timber.d("Message data payload: %s", remoteMessage.data)

                checkNotificationType(remoteMessage)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    private fun checkNotificationType(remoteMessage: RemoteMessage) {

        Timber.i("checkNotificationType type=${getNotificationType(remoteMessage)}")

        when (getNotificationType(remoteMessage)) {
            Const.NOTIFICATION_TYPE_SYNC_MASTERDATA -> {
                forceUpdateMasterData()
            }
            else -> {
                if (getNotificationType(remoteMessage) == "CANCEL") {
                    notificationRepository.deleteNotification(getMetaId(remoteMessage))
                }

                notificationRepository.insertNotification(
                    parseDataPayloadToNotificationModel(remoteMessage)
                )

                if (remoteMessage.data["message"] != null)
                    showNotification(remoteMessage.data["message"]!!)
                else showNotification(remoteMessage.data["category"]!!)
            }
        }

    }

    private fun getMetaId(remoteMessage: RemoteMessage): String {
        val metaBooking = remoteMessage.data["meta"]?.let { Utils.convertStringToMetaBooking(it) }
            ?: MetaBooking()
        var metaID = ""
        try {
            if (remoteMessage.data["category"] == "PICKUP_BOOKINGS" || remoteMessage.data["category"] == "PICKUP BOOKINGS") {
                metaID = metaBooking.bookingID
            }
        } catch (e: Exception) {
        }
        return metaID
    }

    private fun getNotificationType(remoteMessage: RemoteMessage): String {
        return remoteMessage.data["type"] ?: ""
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        Timber.i("deviceToken : %s", s)
        FirebaseMessaging.getInstance().subscribeToTopic("broadcast")
    }

    private fun forceUpdateMasterData() {
        masterDataPreference.expired()
    }

    private fun showNotification(messageBody: String) {
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val intent = Intent(this, NotificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(
            this,
            channelId
        ).setContentTitle(contentTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            //.setContentInfo(notification.getTitle())
            .setLargeIcon(icon)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSmallIcon(R.drawable.ic_notifications_white_24dp)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun parseDataPayloadToNotificationModel(remoteMessage: RemoteMessage): NotificationModel {
        val metaID = getMetaId(remoteMessage)
        return NotificationModel(
            type = remoteMessage.data["type"] ?: "",
            userID = user.id,
            metaID = metaID,
            category = remoteMessage.data["category"] ?: "",
            message = remoteMessage.data["message"] ?: "",
            timestamp = if (remoteMessage.data["timestamp"] == null) Utils.getCurrentTimestamp() else remoteMessage.data["timestamp"]!!.toLong(),
            meta = remoteMessage.data["meta"] ?: ""
        )
    }
}
