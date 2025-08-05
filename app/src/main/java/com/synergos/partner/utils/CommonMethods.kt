package com.synergos.partner.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.synergos.partner.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import android.view.Gravity
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

object CommonMethods {
    private const val PREF_NAME = "location_prefs"
    private const val KEY_IS_SERVICE_RUNNING = "is_service_running"
    private const val REQUEST_CODE_OVERLAY = 101

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun isTracking(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean("is_tracking", false)
    }

    fun setTracking(context: Context, value: Boolean) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean("is_tracking", value).apply()
    }

    fun markAutoStartRequested(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean("auto_start_done", true).apply()
    }

    fun hasRequestedAutoStart(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean("auto_start_done", false)
    }

    fun setServiceRunning(context: Context, isRunning: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_SERVICE_RUNNING, isRunning)
            .apply()
    }


    fun openAutoStartSettings(context: Context) {
        try {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val component = when {
                Build.MANUFACTURER.equals("xiaomi", ignoreCase = true) ->
                    ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")

                Build.MANUFACTURER.equals("oppo", ignoreCase = true) ->
                    ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")

                Build.MANUFACTURER.equals("vivo", ignoreCase = true) ->
                    ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")

                Build.MANUFACTURER.equals("realme", ignoreCase = true) ->
                    ComponentName("com.realme.securitycenter", "com.realme.securitycenter.StartupAppListActivity")

                else -> null
            }

            if (component != null) {
                intent.component = component
                if (canResolveIntent(context, intent)) {
                    context.startActivity(intent)
                    return
                }
            }

            // fallback
            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(fallbackIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Please enable AutoStart manually from settings", Toast.LENGTH_LONG).show()
        }
    }


    private fun canResolveIntent(context: Context, intent: Intent): Boolean {
        val packageManager = context.packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

   /* fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == serviceClass.name
        }
    } */

    fun isServiceRunning(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_SERVICE_RUNNING, false)
    }

    fun checkAndRequestOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY)
        } else {
            Log.d("OverlayPermission", "Already granted")
        }
    }

    fun handleOverlayPermissionResult(activity: Activity, requestCode: Int) {
        if (requestCode == REQUEST_CODE_OVERLAY) {
            if (Settings.canDrawOverlays(activity)) {
                Log.d("OverlayPermission", "Granted")
                Toast.makeText(activity, "Overlay permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("OverlayPermission", "Denied")
                Toast.makeText(activity, "Overlay permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun setStatusBarColor(activity: Activity, colorResId: Int, lightStatusBar: Boolean = true) {
        val window = activity.window

        window.statusBarColor = ContextCompat.getColor(activity, colorResId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                if (lightStatusBar) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (lightStatusBar) {
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }



    fun vibratePhone(context: Context, duration: Long = 200L, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(duration, amplitude)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }



    @SuppressLint("MissingPermission")
    fun getFallbackLine1Number(context: Context, slotIndex: Int): String? {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                telephonyManager.createForSubscriptionId(slotIndex)?.line1Number
            } else {
                telephonyManager.line1Number
            }
        } catch (e: Exception) {
            null
        }
    }


    fun getGreetingMessage(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "☀\uFE0F Good Morning"
            in 12..16 -> "\uD83C\uDF24 Good Afternoon"
            else -> "\uD83C\uDF19 Good Evening"
        }
    }


    fun String.toFormattedDate(): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = parser.parse(this)
            formatter.format(date ?: Date())
        } catch (e: Exception) {
            this
        }
    }

    fun String.toFormattedDate_ddmmmyyyy(): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // correct pattern
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = parser.parse(this)
            formatter.format(date ?: Date())
        } catch (e: Exception) {
            this
        }
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    fun isCheckNetwork(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    fun getToast(activity: Activity, strTxtToast: String) {
        Toast.makeText(activity, strTxtToast, Toast.LENGTH_SHORT).show()
    }



    fun showConfirmationDialog(
        context: Context,
        title : String,
        message: String,
        isCancelable : Boolean,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(isCancelable)
            .show()
    }


    fun getCurrentDateFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.now().format(formatter)
    }

    fun getCurrentDateTimeWithT(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy 'T' HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }


    fun convertToIndiaTime(dateTimeStr: String): String {
        return try {
            // 1. Formatter for input string (without timezone)
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // 2. Parse as LocalDateTime (since no timezone in input)
            val localDateTime = LocalDateTime.parse(dateTimeStr, inputFormatter)

            // 3. Assume input is UTC, convert to ZonedDateTime
            val utcDateTime = localDateTime.atZone(ZoneId.of("UTC"))

            // 4. Convert to India Time
            val indiaTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))

            // 5. Format back to same format
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            indiaTime.format(outputFormatter)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun showErrorFullMsg(activity: Activity, message: String) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams

        // Handle top positioning safely
        when (params) {
            is androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams -> {
                params.gravity = Gravity.TOP
                snackbarView.layoutParams = params
            }
            is FrameLayout.LayoutParams -> {
                params.gravity = Gravity.TOP
                snackbarView.layoutParams = params
            }
        }

        snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.red_logout))

        snackbar.setTextColor(Color.WHITE)

        snackbarView.backgroundTintList = null

        snackbar.show()
    }


    fun showSuccessFullMsg(activity: Activity, message: String) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams

        // Handle top positioning safely
        when (params) {
            is androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams -> {
                params.gravity = Gravity.TOP
                snackbarView.layoutParams = params
            }
            is FrameLayout.LayoutParams -> {
                params.gravity = Gravity.TOP
                snackbarView.layoutParams = params
            }
        }

        snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.parrotgreen))

        snackbar.setTextColor(Color.WHITE)

        snackbarView.backgroundTintList = null
        snackbar.show()
    }



    fun getMorningRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 12)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }

    fun getAfternoonRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 17)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }

    fun getEveningRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 17)
        calendar.set(Calendar.MINUTE, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 0)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }




}