package space.mosu.testkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

enum class JpCharacters(val chars: String) {
    A("あいうえお"),
    K("かきくけこ"),
    S("さしすせそ"),
    T("たちつてと"),
    N("なにぬねの"),
    H("はひふへほ"),
    M("まみむめも"),
    Y("や（ゆ）よ"),
    R("らりるれろ"),
    W("わをんー")
}

class IME : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    lateinit var keyboardView: KeyboardView
    private var tapX = 0F
    private var tapY = 0F
    private var moveX = 0F
    private var moveY = 0F
    private val preview = IMEPreview()

    override fun onCreateInputView(): View {
        super.onCreateInputView()
        keyboardView = (layoutInflater.inflate(R.layout.input, null) as KeyboardView).apply {
            keyboard = Keyboard(this@IME, R.xml.keyboard)
            setOnKeyboardActionListener(this@IME)
            setOnTouchListener { view, motionEvent ->
                val motionX = motionEvent.x
                val motionY = motionEvent.y
                return@setOnTouchListener when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        tapX = motionX
                        tapY = motionY
                        val key = keyboard.keys.find { it.isInside(tapX.toInt(), tapY.toInt()) }
                        Log.i("action", "down")
                        Log.i("key", key.toString())
                        preview.also {
                            it.anchorKey = key
                            it.show("Tap!!")
                        }
                        false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        Log.i("action", "move")
                        moveX = motionX - tapX
                        moveY = motionY - tapY
                        Log.i("point", "($moveX, $moveY)")
                        preview.show("Moving...")
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        preview.dismiss()
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
        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        setInputView(onCreateInputView())
        preview.setup(keyboardView)
    }

    override fun onKey(primaryCode: Int, codes: IntArray?) {
        Log.i("debug", "onkey")
        val ic = currentInputConnection
        when (primaryCode) {
            KeyEvent.KEYCODE_1 -> ic.commitText(setCharacter(JpCharacters.A.chars), 1)
            KeyEvent.KEYCODE_2 -> ic.commitText(setCharacter(JpCharacters.K.chars), 1)
            KeyEvent.KEYCODE_3 -> ic.commitText(setCharacter(JpCharacters.S.chars), 1)
            KeyEvent.KEYCODE_4 -> ic.commitText(setCharacter(JpCharacters.T.chars), 1)
            KeyEvent.KEYCODE_5 -> ic.commitText(setCharacter(JpCharacters.N.chars), 1)
            KeyEvent.KEYCODE_6 -> ic.commitText(setCharacter(JpCharacters.H.chars), 1)
            KeyEvent.KEYCODE_7 -> ic.commitText(setCharacter(JpCharacters.M.chars), 1)
            KeyEvent.KEYCODE_8 -> ic.commitText(setCharacter(JpCharacters.Y.chars), 1)
            KeyEvent.KEYCODE_9 -> ic.commitText(setCharacter(JpCharacters.R.chars), 1)
            KeyEvent.KEYCODE_0 -> ic.commitText(setCharacter(JpCharacters.W.chars), 1)
            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            KeyEvent.KEYCODE_ENTER -> ic.sendKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ENTER
                )
            )
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

    private fun setCharacter(chars: String): String {
        val distance = sqrt(moveX.pow(2) + moveY.pow(2))
        Log.i("distance", distance.toString())
        if (distance <= 50F) {
            return chars[0].toString()
        }
        val direction = when {
            moveX < 0 && abs(moveX) > abs(moveY) && chars.length > 1 -> 1
            moveY < 0 && abs(moveX) < abs(moveY) && chars.length > 2 -> 2
            moveX > 0 && abs(moveX) > abs(moveY) && chars.length > 3 -> 3
            moveY > 0 && abs(moveX) < abs(moveY) && chars.length > 4 -> 4
            else -> 0
        }
        Log.i("direction", direction.toString())
        return chars[direction].toString()
    }
}
