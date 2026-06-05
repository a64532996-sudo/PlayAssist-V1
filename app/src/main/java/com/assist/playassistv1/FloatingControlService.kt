package com.assist.playassistv1

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button

class FloatingControlService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: Button

    override fun onBind(intent: Intent?): IBinder? {
        return null // لا نحتاج للربط المباشر هنا
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // تصميم زر التشغيل العائم
        floatingButton = Button(this).apply {
            text = "▶ تشغيل"
            setBackgroundColor(Color.parseColor("#4CAF50")) // لون أخضر
            setTextColor(Color.WHITE)
        }

        // تحديد نوع النافذة بناءً على إصدار الأندرويد
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // إعدادات حجم ومكان اللوحة العائمة
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 200

        // إضافة ميزة تحريك الزر بالسحب، وتغيير حالته عند النقر
        floatingButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isMoved = false

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isMoved = false
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val diffX = (event.rawX - initialTouchX).toInt()
                        val diffY = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(diffX) > 10 || Math.abs(diffY) > 10) {
                            isMoved = true
                            params.x = initialX + diffX
                            params.y = initialY + diffY
                            windowManager.updateViewLayout(floatingButton, params)
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isMoved) {
                            // عند النقر على الزر يتم التبديل بين التشغيل والإيقاف
                            if (floatingButton.text == "▶ تشغيل") {
                                floatingButton.text = "⏸ إيقاف"
                                floatingButton.setBackgroundColor(Color.RED)
                                // سيتم ربط النقر التلقائي هنا لاحقاً
                            } else {
                                floatingButton.text = "▶ تشغيل"
                                floatingButton.setBackgroundColor(Color.parseColor("#4CAF50"))
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(floatingButton, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }
    }
}
