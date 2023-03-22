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
    const val PARAMS_DELIVERY_TASK_IN_PROGRESS = "IN_PROGRESS"
    const val PARAMS_DELIVERY_TASK_COMPLETED = "DELIVERED"
    const val PARAMS_DELIVERY_TASK_RETENTION = "RETENTION"
    const val PARAMS_DELIVERY_REVERSE = "HAS_ITEM_TO_PICKUP"
    const val PARAMS_DELIVERY_REVERSE_NONE = "NONE"
    const val PARAMS_DELIVERY_REVERSE_RECEIVED = "RECEIVED"
    const val PARAMS_DELIVERY_ITEM_LIST = "deliveryItemList"
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
    const val PARAMS_MANIFEST_SENT_LIST = "manifestSentList"
    const val PARAMS_MANIFEST_TAB_TYPE = "tabType"
    const val PARAMS_PICKUP_NEW_BOOKING = "pickupNewBooking"
    const val PARAMS_PICKUP_IN_PROGRESS = "pickupInProgress"
    const val PARAMS_PICKUP_ITEM_LIST = "pickupItemList"
    const val PARAMS_PICKUP_COMPLETED = "pickupCompleted"
    const val PARAMS_PICKUP_CONTINUE_NEXT_TASK = "continue_next_task"
    const val PARAMS_PICKUP_TASK = "pickupTask"
    const val PARAMS_PICKUP_TASK_TOTAL_COUNT = "pickupTaskTotalCount"
    const val PARAMS_PICKUP_TASK_ID = "pickupTaskID"
    const val PARAMS_PICKUP_TASK_ID_LIST = "pickupTaskIDList"
    const val PARAMS_PICKUP_TASK_NOT_EXIST = "pickupTaskNotExist"
    const val PARAMS_PICKUP_SEARCH_SHIPPER_CODE = "search_shipper_code"
    const val FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK = "continue_next_task"
    const val PARAMS_PICKUP_TASK_SELECT_TAB = "select_tab"
    const val PARAMS_PICKUP_TASK_ACCEPTED: String = "ACCEPT"
    const val PARAMS_PICKUP_TASK_REJECTED: String = "REJECT"
    const val PARAMS_PICKUP_TASK_NEW_BOOKING = "NEW_BOOKING"
    const val PARAMS_PICKUP_TASK_IN_PROGRESS = "IN_PROGRESS"
    const val PARAMS_PICKUP_TASK_COMPLETED = "COMPLETED"
    const val PARAMS_PICKUP_TASK_ACTION_RESEND = "resend"
    const val PARAMS_PICKUP_CUSTOMER = "pickupCustomer"
    const val PARAMS_PICKUP_CUSTOMER_BUSINESS_TYPE = "pickupCustomerBusinessType"
    const val PARAMS_PICKUP_CUSTOMER_CODE = "pickupCustomerCode"
    const val PARAMS_PICKUP_PAYMENT = "pickupPayment"
    const val PARAMS_PICKUP_SIZE = "pickupSize"
    const val PARAMS_TRACKING_ID = "trackingID"
    const val PARAMS_TRACKING_STATUS_ID = "trackingStatusID"
    const val PARAMS_TOPIC_ID = "topicId"
    const val PARAMS_TOPIC_USER_ID = "topicUserId"
    const val PARAMS_TOPIC_TITLE = "topicTitle"
    const val PARAMS_TOPIC_BODY = "topicBody"
    const val PARAMS_BOOKING_ID = "bookingID"
    const val PARAMS_ORGANIZATION = "organization"
    const val PARAMS_GROUP_ID = "groupID"
    const val PARAMS_REASON_NORMAL = "normal"
    const val PARAMS_REASON_CHANGE_SD = "changeSD"
    const val PARAMS_REASON_CHANGE_DATE = "changeDate"
    const val PARAMS_DELIVERY_TASK = "deliveryTask"
    const val PARAMS_ALL_TASK = "allTask"
    const val PARAMS_FILTER_DISTANCE_NEAR = "near"
    const val PARAMS_FILTER_DISTANCE_FAR = "far"
    const val PARAMS_FILTER_DISTANCE_DEFAULT = "default"
    const val PARAMS_FILTER_DISTANCE_CUSTOM = "custom"



    const val PARAMS_TAG_DIALOG_PHOTO_SELECT = "PHOTO_SELECT_DIALOG"

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

    /* request refresh pickup task Intent */
    const val REQUEST_PICKUP_TASK_REFRESH = 1002

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

    const val DATETIME_FORMAT_Ymd = "yyyy-MM-dd"
    const val DATETIME_FORMAT_His = "HH:mm:ss"
    const val DATETIME_FORMAT_YmdHis = "$DATETIME_FORMAT_Ymd $DATETIME_FORMAT_His"
    const val DATETIME_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss"

    const val BUTTON_CLICKED_DELAY: Long = 2000

    class None
    val NONE: None = None()
}