package space.mosu.testkeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent

class CustomKeyboardView(context: Context, attrs: AttributeSet) : KeyboardView(context, attrs) {
    private val specialKeys = mutableListOf<Keyboard.Key>()

    override fun setKeyboard(keyboard: Keyboard) {
        super.setKeyboard(keyboard)
        for (key in keyboard.keys) {
            val primaryCode = key.codes[0]
            if (KeyEvent.KEYCODE_0 > primaryCode || primaryCode > KeyEvent.KEYCODE_9) {
                specialKeys.add(key)
            }
        }
    }

//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        for (key in specialKeys) {
//            val x = key.x + x.toInt()
//            val y = key.y + y.toInt()
//            val w = key.width
//            val h = key.height
//            val color: Int = Color.WHITE
//            val sd = ShapeDrawable()
//            sd.apply {
//                setBounds(x, y, x + w, y + h)
//                paint.color = color
//                draw(canvas)
//            }
//        }
//    }
}