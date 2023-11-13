import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.util.proto.ColorProto.Color
import com.sts.sontalksign.databinding.CustomNoRecordFormBinding

class CustomNoRecordForm(private val context: AppCompatActivity) {
    private lateinit var binding: CustomNoRecordFormBinding
    private val nDialog = Dialog(context)
    private var onClickListener: OnBtnDismissCancelClickedListener? = null // Listener type corrected, and set to nullable for safety

    fun show() {
        binding = CustomNoRecordFormBinding.inflate(context.layoutInflater)
        nDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        nDialog.setContentView(binding.root)
        nDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        nDialog.show()

        binding.btnDismiss.setOnClickListener {
            onClickListener?.onBtnDismissCancelClicked() // Call the listener's method
        }

        binding.btnCancel.setOnClickListener {
            onClickListener?.onBtnDismissCancelClicked() // Call the listener's method
        }

        // TODO: Handle other events if necessary
    }

    fun setOnBtnDismissCancelClickedListener(listener: OnBtnDismissCancelClickedListener) {
        onClickListener = listener
    }

    interface OnBtnDismissCancelClickedListener {
        fun onBtnDismissCancelClicked() // Corrected method name and removed unnecessary parameters
    }
}
