import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
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
                binding.tvHistoryConversationText.gravity = Gravity.START
            } else {
                binding.tvHistoryConversationText.gravity = Gravity.END
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
