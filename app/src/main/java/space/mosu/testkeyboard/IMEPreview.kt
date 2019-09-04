package space.mosu.testkeyboard

import android.content.Context
import android.graphics.Color
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import org.jetbrains.anko.*

class IMEPreview {
    private var popupSize = 0
    private var standBy = false
    private var keyboardX = 0
    private var keyboardY = 0
    private var keyX = 0
    private var keyY = 0
    private var keyWidth = 0
    private var keyHeight = 0

    var anchorView: View? = null
    var anchorKey: Keyboard.Key? = null
        set(value) {
            field = value
            if (value is Keyboard.Key) {
                keyX = value.x
                keyY = value.x
                keyWidth = value.width
                keyHeight = value.height
            }
        }

    private val popup = PopupWindow().apply {
        inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        isClippingEnabled = true
        isFocusable = false
        isOutsideTouchable = false
    }

    private val ui = object : AnkoComponent<Context> {
        lateinit var textView: TextView
            private set

        override fun createView(ui: AnkoContext<Context>): View = with(ui) {
            frameLayout {
                textView = textView {
                    textColor = Color.BLACK
                    textSize = 18F
                    gravity = Gravity.CENTER
                }.lparams {
                    backgroundColor = Color.WHITE
                    popupSize = dip(96)
                    width = wrapContent
                    height = popupSize
                }
            }
        }
    }

    fun setup(view: KeyboardView) {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        anchorView = view
        keyboardX = location[0]
        keyboardY = location[1]
        popup.apply {
            contentView = ui.createView(view.context.UI {}).also {
                val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                it.measure(spec, spec)
            }
            width = contentView.measuredWidth
            height = contentView.measuredHeight
        }
        standBy = true
    }

    fun show(text: String) {
        if (!standBy) {
            return
        }

        val pointX = keyboardX + (keyX + keyWidth / 2) - ui.textView.measuredWidth / 2
        val pointY = keyboardY + (keyY + keyHeight / 2) - (1.25 * popupSize).toInt()

        ui.textView.text = text
        popup.showAtLocation(anchorView, Gravity.NO_GRAVITY, pointX, pointY)
    }

    fun dismiss() {
        if (popup.isShowing) popup.dismiss()
    }
}