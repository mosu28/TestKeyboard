package space.mosu.testkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

class IME : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var tapX = 0F
    private var tapY = 0F
    override fun onCreateInputView(): View {
        super.onCreateInputView()
        return (layoutInflater.inflate(R.layout.input, null) as KeyboardView).apply {
            keyboard = Keyboard(this@IME, R.xml.keyboard)
            setOnKeyboardActionListener(this@IME)
            setOnTouchListener { view, motionEvent ->
                val moveX = motionEvent.x
                val moveY = motionEvent.y
                return@setOnTouchListener when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        tapX = moveX
                        tapY = moveY
                        Log.i("action", "down")
                        false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        Log.i("action", "move")

                        Log.i("point", "(${moveX - tapX}, ${moveY - tapY})")
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        Log.i("action", "up")
                        false
                    }
                    else -> {
                        true
                    }
                }
            }
            isPreviewEnabled = false
        }
    }

    override fun onKey(primaryCode: Int, codes: IntArray?) {
        Log.i("debug", "onkey")
        val ic = currentInputConnection
        when (primaryCode) {
            KeyEvent.KEYCODE_1 -> ic.commitText("1", 1)
            KeyEvent.KEYCODE_2 -> ic.commitText("2", 1)
            KeyEvent.KEYCODE_3 -> ic.commitText("3", 1)
            KeyEvent.KEYCODE_4 -> ic.commitText("4", 1)
            KeyEvent.KEYCODE_5 -> ic.commitText("5", 1)
            KeyEvent.KEYCODE_6 -> ic.commitText("6", 1)
            KeyEvent.KEYCODE_7 -> ic.commitText("7", 1)
            KeyEvent.KEYCODE_8 -> ic.commitText("8", 1)
            KeyEvent.KEYCODE_9 -> ic.commitText("9", 1)
            KeyEvent.KEYCODE_0 -> ic.commitText("0", 1)
            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            KeyEvent.KEYCODE_ENTER -> ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        }
    }

    override fun onPress(primaryCode: Int) {
        Log.i("press", primaryCode.toString())
    }

    override fun onRelease(primaryCode: Int) {
    }

    override fun onText(text: CharSequence?) {
    }

    override fun swipeLeft() {
        Log.i("swipe", "left")
    }

    override fun swipeUp() {
        Log.i("swipe", "up")
    }

    override fun swipeRight() {
        Log.i("swipe", "right")
    }

    override fun swipeDown() {
        Log.i("swipe", "down")
    }
}
