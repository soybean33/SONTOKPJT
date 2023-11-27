import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.sts.sontalksign.databinding.CustomNoRecordFormBinding

class CustomNoRecordForm(private val context: AppCompatActivity) {
    private lateinit var binding: CustomNoRecordFormBinding
    private val nDialog = Dialog(context)
    private lateinit var onClickListener: OnBtnClickedListener

    fun show() {
        binding = CustomNoRecordFormBinding.inflate(context.layoutInflater)
        nDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        nDialog.setContentView(binding.root)

        /** LayoutParams 설정 및 원하는 위치로 gravity 설정 */
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(nDialog.window?.attributes)
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = 400 // y 좌표
        nDialog.window?.attributes = layoutParams

        nDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        nDialog.show()

        binding.btnDismiss.setOnClickListener {
            onClickListener?.onBtnDismissClicked()
            nDialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            onClickListener?.onBtnCancelClicked()
            nDialog.dismiss()
        }
    }

    fun setOnBtnClickedListener(listener: OnBtnClickedListener) {
        onClickListener = listener
    }

    interface OnBtnClickedListener {
        fun onBtnDismissClicked() //"종료" 버튼
        fun onBtnCancelClicked() //"취소" 버튼
    }
}
