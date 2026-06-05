package com.assist.playassistv1

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class AutoClickService : AccessibilityService() {

    // هذه الدالة تعمل بمجرد تفعيل المستخدم للخدمة من إعدادات الهاتف
    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    // هذه الدالة للاستماع لتغيرات الشاشة (لن نحتاجها بكثرة الآن)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    // هذه الدالة تعمل عند إيقاف الخدمة أو حدوث مقاطعة
    override fun onInterrupt() {
    }

    // دالة سحرية: هذه هي الأداة التي ستقوم بالنقر الفعلي على أي إحداثيات (X, Y) نعطيها لها
    fun performClick(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        val builder = GestureDescription.Builder()
        // إنشاء حركة لمس تستغرق 100 ملي ثانية
        val gesture = builder.addStroke(GestureDescription.StrokeDescription(path, 0, 100)).build()
        dispatchGesture(gesture, null, null)
    }
}
