import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryConversationBinding
import com.sts.sontalksign.feature.conversation.ConversationCameraModel

class ConversationCameraAdapter(private val conversationCameraList: ArrayList<ConversationCameraModel>) :
    RecyclerView.Adapter<ConversationCameraAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(private val binding: HistoryConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversationCamera: ConversationCameraModel) {
            binding.tvHistoryConversationText.text = conversationCamera.ConversationText

            val isLeftMessage = conversationCamera.isLeft
            if (isLeftMessage) {
                binding.llhHistoryConversationText.gravity = Gravity.END
                binding.tvHistoryConversationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                binding.llhHistoryConversationText.alpha = 0.8f // 투명도를 0.8로 설정
                binding.tvHistoryConversationText.gravity = Gravity.RIGHT //오른쪽 정렬
//                binding.tvHistoryConversationText.setPadding(0, 0, 0, 10) // 10dp padding at the bottom
            } else {
                binding.llhHistoryConversationText.gravity = Gravity.START
                binding.tvHistoryConversationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                binding.llhHistoryConversationText.alpha = 0.8f // 투명도를 0.8로 설정
                binding.tvHistoryConversationText.gravity = Gravity.LEFT //왼쪽 정렬
//                binding.tvHistoryConversationText.setPadding(0, 0, 0, 10) // 10dp padding at the bottom

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = HistoryConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return conversationCameraList.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversationCameraList[position])
    }

    // 아이템 추가 및 스크롤
    fun addItemAndScroll(conversationCameraModel: ConversationCameraModel, recyclerView: RecyclerView) {
        conversationCameraList.add(conversationCameraModel)
        notifyDataSetChanged()
        recyclerView.scrollToPosition(conversationCameraList.size - 1)
    }
}
