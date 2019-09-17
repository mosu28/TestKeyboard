package space.mosu.testkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import kotlin.math.*

enum class JpCharacters(val chars: List<String>) {
    A(listOf("あいうえお", "ぁぃぅぇぉ")),
    K(listOf("かきくけこ", "がぎぐげご")),
    S(listOf("さしすせそ", "ざじずぜぞ")),
    T(listOf("たちつてと", "だぢづでど")),
    N(listOf("なにぬねの")),
    H(listOf("はひふへほ", "ばびぶべぼ", "ぱぴぷぺぽ")),
    M(listOf("まみむめも")),
    Y(listOf("や（ゆ）よ", "ゃ ゅ ょ")),
    R(listOf("らりるれろ")),
    W(listOf("わをんー"))
}

class IME : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    lateinit var keyboardView: CustomKeyboardView
    private var tapX = 0F
    private var tapY = 0F
    private var moveX = 0F
    private var moveY = 0F
    private var currentCharacters: String = ""
    private var isPrediction = true
    private val preview = IMEPreview()


    override fun onCreateInputView(): View {
        super.onCreateInputView()
        keyboardView = (layoutInflater.inflate(R.layout.input, null) as CustomKeyboardView).apply {
            keyboard = Keyboard(this@IME, R.xml.keyboard)
            setOnKeyboardActionListener(this@IME)
            setOnTouchListener { view, motionEvent ->
                val motionX = motionEvent.x
                val motionY = motionEvent.y
                return@setOnTouchListener when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        tapX = motionX
                        tapY = motionY
                        moveX = 0F
                        moveY = 0F
                        val key = keyboard.keys.find { it.isInside(tapX.toInt(), tapY.toInt()) }
                        val primaryCode = key!!.codes[0]
                        Log.i("key", primaryCode.toString())
                        if (isCharacter(primaryCode)) {
                            preview.also {
                                it.anchorKey = key
                                it.show(getCharacter(primaryCode))
                            }
                        }
                        false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        moveX = motionX - tapX
                        moveY = motionY - tapY
                        val key = keyboard.keys.find { it.isInside(tapX.toInt(), tapY.toInt()) }
                        val primaryCode = key!!.codes[0]
                        if (isCharacter(primaryCode)) {
                            preview.show(getCharacter(primaryCode))
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        preview.dismiss()
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
        val ic = currentInputConnection
        if (isCharacter(primaryCode)) {
            if (!isPrediction) {
                isPrediction = true
                currentCharacters = ""
            }
            currentCharacters += getCharacter(primaryCode)
            ic.setComposingText(currentCharacters, 1)
//            ic.finishComposingText()
//            ic.commitText(getCharacter(primaryCode), 1)
        } else {
            when (primaryCode) {
                Keyboard.KEYCODE_DELETE -> {
//                    currentCharacters =
//                        currentCharacters.slice(0 until currentCursor - 1) +
//                                currentCharacters.slice(currentCursor until currentCharacters.length)
                    if (isPrediction) {
                        currentCharacters = currentCharacters.slice(0 until currentCharacters.length - 1)
                        ic.setComposingText(currentCharacters, 1)
                    } else {
                        ic.deleteSurroundingText(1, 0)
                    }
                }
                KeyEvent.KEYCODE_ENTER -> {
                    if (isPrediction) {
                        ic.finishComposingText()
                        isPrediction = false
                    } else {
                        ic.sendKeyEvent(
                            KeyEvent(
                                KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_ENTER
                            )
                        )
                    }
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    ic.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_LEFT
                        )
                    )
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    ic.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_RIGHT
                        )
                    )
                }
                KeyEvent.KEYCODE_11 -> {
                    if (isPrediction) {
                        if (currentCharacters.isNotBlank()) {
                            val last = getLastCharacters(currentCharacters.last())
                            if (last == ' ') return
                            currentCharacters = currentCharacters.slice(0 until currentCharacters.length - 1)
                            currentCharacters += last
                            ic.setComposingText(currentCharacters, 1)
                        }
                    } else {

                    }
                }
                else -> {
                }
            }
        }
//        getCharacter(primaryCode).run {
//            ic.commitText(this, 1)
//        } ?: run {
//            when (primaryCode) {
//                Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
//                KeyEvent.KEYCODE_ENTER -> ic.sendKeyEvent(
//                    KeyEvent(
//                        KeyEvent.ACTION_DOWN,
//                        KeyEvent.KEYCODE_ENTER
//                    )
//                )
//                else -> {}
//            }
//        }
//        when (primaryCode) {
//            KeyEvent.KEYCODE_1 -> ic.commitText(setCharacter(JpCharacters.A.chars), 1)
//            KeyEvent.KEYCODE_2 -> ic.commitText(setCharacter(JpCharacters.K.chars), 1)
//            KeyEvent.KEYCODE_3 -> ic.commitText(setCharacter(JpCharacters.S.chars), 1)
//            KeyEvent.KEYCODE_4 -> ic.commitText(setCharacter(JpCharacters.T.chars), 1)
//            KeyEvent.KEYCODE_5 -> ic.commitText(setCharacter(JpCharacters.N.chars), 1)
//            KeyEvent.KEYCODE_6 -> ic.commitText(setCharacter(JpCharacters.H.chars), 1)
//            KeyEvent.KEYCODE_7 -> ic.commitText(setCharacter(JpCharacters.M.chars), 1)
//            KeyEvent.KEYCODE_8 -> ic.commitText(setCharacter(JpCharacters.Y.chars), 1)
//            KeyEvent.KEYCODE_9 -> ic.commitText(setCharacter(JpCharacters.R.chars), 1)
//            KeyEvent.KEYCODE_0 -> ic.commitText(setCharacter(JpCharacters.W.chars), 1)
//
//        }
    }

    private fun getLastCharacters(char: Char): Char = when {
        char in JpCharacters.A.chars.joinToString("") -> nextCharacter(char, JpCharacters.A.chars)
        char in JpCharacters.K.chars.joinToString("") -> nextCharacter(char, JpCharacters.K.chars)
        char in JpCharacters.S.chars.joinToString("") -> nextCharacter(char, JpCharacters.S.chars)
        char in JpCharacters.T.chars.joinToString("") -> nextCharacter(char, JpCharacters.T.chars)
        char in JpCharacters.N.chars.joinToString("") -> nextCharacter(char, JpCharacters.N.chars)
        char in JpCharacters.H.chars.joinToString("") -> nextCharacter(char, JpCharacters.H.chars)
        char in JpCharacters.M.chars.joinToString("") -> nextCharacter(char, JpCharacters.M.chars)
        char in JpCharacters.Y.chars.joinToString("") -> nextCharacter(char, JpCharacters.Y.chars)
        char in JpCharacters.R.chars.joinToString("") -> nextCharacter(char, JpCharacters.R.chars)
        char in JpCharacters.W.chars.joinToString("") -> nextCharacter(char, JpCharacters.W.chars)
        else -> ' '
    }

    private fun nextCharacter(char: Char, strs: List<String>): Char {
        for (i in strs.indices) {
            if (char in strs[i]) {
                val next = if (i + 1 >= strs.size) 0 else i + 1
                for (j in strs[i].indices) {
                    if (char == strs[i][j])
                    return strs[next][j]
                }
            }
        }
        return ' '
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {

        return super.onKeyUp(keyCode, event)
    }

    private fun getCharacter(primaryCode: Int): String = when (primaryCode) {
            KeyEvent.KEYCODE_1 -> setCharacter(JpCharacters.A.chars[0])
            KeyEvent.KEYCODE_2 -> setCharacter(JpCharacters.K.chars[0])
            KeyEvent.KEYCODE_3 -> setCharacter(JpCharacters.S.chars[0])
            KeyEvent.KEYCODE_4 -> setCharacter(JpCharacters.T.chars[0])
            KeyEvent.KEYCODE_5 -> setCharacter(JpCharacters.N.chars[0])
            KeyEvent.KEYCODE_6 -> setCharacter(JpCharacters.H.chars[0])
            KeyEvent.KEYCODE_7 -> setCharacter(JpCharacters.M.chars[0])
            KeyEvent.KEYCODE_8 -> setCharacter(JpCharacters.Y.chars[0])
            KeyEvent.KEYCODE_9 -> setCharacter(JpCharacters.R.chars[0])
            KeyEvent.KEYCODE_0 -> setCharacter(JpCharacters.W.chars[0])
            else -> ""
        }

    private fun isCharacter(primaryCode: Int): Boolean =
        KeyEvent.KEYCODE_0 <= primaryCode && primaryCode <= KeyEvent.KEYCODE_9

    override fun onPress(primaryCode: Int) {
    }

    override fun onRelease(primaryCode: Int) {
    }

    override fun onText(text: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeUp() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    private fun setCharacter(chars: String): String {
        val distance = sqrt(moveX.pow(2) + moveY.pow(2))
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
        return chars[direction].toString()
    }
}
