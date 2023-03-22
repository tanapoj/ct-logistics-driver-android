package com.scgexpress.backoffice.android.common

object Const {
    /* database */
    const val ROOM_DATABASE_NAME = "scgexpress.db"
    const val ROOM_DATABASE_TABLE_TOPIC = "topic"

    /* flavor */
    const val FLAVOR_API = "api"
    const val FLAVOR_MOCK = "mock"
    const val FLAVOR_PRODUCTION = "production"

    /* network */
    const val API_HOST_RETROFIT = "https://scgyamatodev.flare.works/Apinative/"

    /* param intent */
    const val PARAMS_MANIFEST = "manifest"
    const val PARAMS_MANIFEST_BOOKING_INFO = "bookingInfo"
    const val PARAMS_MANIFEST_BOOKING = "booking"
    const val PARAMS_MANIFEST_COMPLETED = "completed"
    const val PARAMS_MANIFEST_FILTER_ALL = "filterAll"
    const val PARAMS_MANIFEST_FILTER_BOOKING = "filterBooking"
    const val PARAMS_MANIFEST_FILTER_TRACKING = "filterTracking"
    const val PARAMS_MANIFEST_ID = "manifestID"
    const val PARAMS_MANIFEST_IN_PROGRESS = "inProgress"
    const val PARAMS_MANIFEST_ITEM_LIST = "manifestItemList"
    const val PARAMS_MANIFEST_TAB_TYPE = "tabType"
    const val PARAMS_TRACKING_ID = "trackingID"
    const val PARAMS_TRACKING_STATUS_ID = "trackingStatusID"
    const val PARAMS_MANIFEST_SENT_LIST = "manifestSentList"
    const val PARAMS_TOPIC_ID = "topicId"
    const val PARAMS_TOPIC_USER_ID = "topicUserId"
    const val PARAMS_TOPIC_TITLE = "topicTitle"
    const val PARAMS_TOPIC_BODY = "topicBody"
    const val PARAMS_BOOKING_ID = "bookingID"

    /* track Choosing Image Intent */
    const val PARAMS_REQUEST_IMAGE_CHOOSING = 1000
    const val PARAMS_REQUEST_IMAGE_TAKE_PHOTO = 1001

    const val PARAMS_DIRECTORY_ROOT = "/mockup"
    const val PARAMS_DIRECTORY_ROOT_IMAGE = "/images"

    /* request permission */
    const val RPARAMS_EQUEST_READ_EXTERNAL_STORAGE = 1

    /* track Choosing Image Intent */
    const val REQUEST_CHOOSING_IMAGE = 1000
    const val REQUEST_TAKE_PHOTO = 1001

    const val DIRECTORY_ROOT = "/scgexpress"
    const val DIRECTORY_IMAGE_ROOT = "/images"
    const val DIRECTORY_PICKUP = "pickup"
    const val DIRECTORY_OFD_DETAIL = "ofd"
    const val DIRECTORY_OFD_DETAIL_TRACKING = "ofd_tracking"

    const val PACKAGE_NAME = "com.scgexpress.backoffice.android"

    /* param dialog */
    const val PARAMS_DIALOG_RE_STATUS = "dialogReStatus"
    const val PARAMS_DIALOG_DATE_RANGE_PICKER = "dialogDateRangePicker"

    /* ofd status */
    const val OFD_SENT_STATUS_CODE = "34"

    /* aws */
    const val AWS_ACCESS_KEY = "AKIAJWEDPVMLXYO7FBVA"
    const val AWS_SECRET_KEY = "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"
    const val AWS_POOL_ID = "ap-southeast-1:925683e5-b7f8-4878-911d-5a48599e5bff"
    const val AWS_BUCKET = "scgexpress-native-app-uploads"
    const val AWS_BUCKET_DATA = "scgexpress-native-app-data"

    const val MASTERDATA_S3_LOCATION = "masterdata/mdata1.gz"
    const val MASTERDATA_TEMP_DIR = "masterdata"
    const val MASTERDATA_TEMP_FILENAME = "mdata.json"

    /* External Apps */
    const val LINE_PACKAGE_NAME = "jp.naver.line.android"
    const val LINE_ACTIVITY_NAME = "jp.naver.line.android.activity.selectchat.SelectChatActivity"

    /* Firebase Notification */
    const val NOTIFICATION_TYPE_SYNC_MASTERDATA = "SYNC"

    /* Google map API KEY */
    const val GOOGLE_MAP_API_KEY = "AIzaSyCrJ2tto0R037eooho6fPtXhDUn63OlvGg"

    /* request permission */
    const val REQUEST_READ_EXTERNAL_STORAGE = 1

    const val REQUEST_CODE_PICKUP = 1002
}