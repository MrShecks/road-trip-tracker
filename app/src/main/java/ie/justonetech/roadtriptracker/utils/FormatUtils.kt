package ie.justonetech.roadtriptracker.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////////
// FormatUtils
// Singleton class to provide formatting utilities and cached formatters for different raw types
// (dates, durations etc) used throughout the application
///////////////////////////////////////////////////////////////////////////////////////////////////

class FormatUtils private constructor() {

    enum class DateFormat {
        FORMAT_SHORT_TEXT,
        FORMAT_DATABASE,
        FORMAT_LONG_SHORT_TIME
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val dateFormats = mapOf(

        DateFormat.FORMAT_SHORT_TEXT to SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault()),
        DateFormat.FORMAT_DATABASE to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
        DateFormat.FORMAT_LONG_SHORT_TIME to SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
    )

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    fun formatDate(date: Date?, defaultValue: String = "", format: DateFormat = DateFormat.FORMAT_SHORT_TEXT): String {
        synchronized(LOCK) {
            return date?.let {
                dateFormats[format]?.format(date) ?: defaultValue
            } ?: defaultValue
        }
    }

    fun parseDate(str: String, defaultValue: Date? = null, format: DateFormat = DateFormat.FORMAT_SHORT_TEXT): Date? {
        synchronized(LOCK) {
            return try {
                dateFormats[format]?.parse(str)
            } catch (e: ParseException) {
                defaultValue
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        @Volatile
        private var instance: FormatUtils? = null
        private val LOCK = Any()

        @JvmStatic
        operator fun invoke(): FormatUtils {
            return instance?: synchronized(LOCK) {
                instance?: FormatUtils().also {
                    instance = it
                }
            }
        }
    }
}