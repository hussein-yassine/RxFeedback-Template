package com.codefather.vanapp.Utils

/**
 * Created by Jreij on 11/3/2017.
 */
object Constants {


    object Application {
        const val VERSION = "1.0"
        const val PACKAGE_NAME = "com.cme.daycarechannelparent"
        const val WEBSITE_PATH = "http://www.thedaycarechannel.com"
        const val FACEBOOK_PATH = "https://www.facebook.com/DaycareChannel"
        const val TWITTER_PATH = "https://twitter.com/DaycareChannel"
        const val LINKED_IN_PATH = "https://www.linkedin.com/company/daycarechannel"
        const val YOUTUBE_PATH = ""
    }

    object FileConstants {
        const val BASE_PATH = "DayCareChannel"
        const val IMAGES_FOLDER_SUBDIRECTORY = "Images"
        const val DATABASE_FOLDER_SUBDIRECTORY = "Database"
        const val TEMP_FOLDER_SUBDIRECTORY = "Temp"
        const val SYNC_FOLDER_SUBDIRECTORY = "Sync"
        const val DATABASE_RESTORE_SUBDIRECTORY = "Database"
        const val DATABASE_NAME = "dayCareParent.db"
        const val LOG_FOLDER_SUBDIRECTORY = "Logs"
        val ALL_DIRECTORIES = listOf(IMAGES_FOLDER_SUBDIRECTORY, DATABASE_FOLDER_SUBDIRECTORY, SYNC_FOLDER_SUBDIRECTORY, TEMP_FOLDER_SUBDIRECTORY, LOG_FOLDER_SUBDIRECTORY)
    }

    object DateFormats {
        const val DATE_FORMAT_PRIMARY = "yyyy-MM-dd"
        const val DATE_TIME_FORMAT_TERNARY = "yyyy-MM-dd'T'HH:mm:ss"
        const val DATE_TIME_FORMAT_SECONDARY = "yyyy-MM-dd'T'HH:mm:ssZ"
        const val DATE_TIME_FORMAT_PRIMARY = "yyyy-MM-dd'T'HH:mm:ssZ"
        const val TIME_FORMAT_PRIMARY = "HH:mm:ss"
        const val TIME_MIN_FORMAT_PRIMARY = "HH:mm"
        const val FULL_DATE_DAY_FORMAT = "EEE, MMM, dd, yyyy"
        const val TIME_FORMAT_DISPLAY = "hh:mm a"
        const val MONTH_DAY_FORMAT_DISPLAY = "EEE, MMM d"
        const val MONTH_DAY_YEAR_FORMAT_DISPLAY = "MMM d yyyy"
        const val DATE_TIME_FORMAT_DISPLAY = "MMM d yyyy - HH:mm"
        const val DATE_MONTH_YEAR = "MMM yyyy"
        const val MINUTES_DAY = "mm"
        const val HOUR_DAY = "HH"
        const val WEEK_DAY = "EEE"
        const val DATE_DAY = "dd"
        const val WEEK_DATE_DAY = "EEE dd"
        const val DATE_MONTH = "dd MMM"
        const val DATE_DAY_MONTH_YEAR = "dd MMM yyyy"
    }
}