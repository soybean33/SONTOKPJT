package com.sts.sontalksign.feature.conversation

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryConversationBinding

class ConversationCameraAdapter (private val ConversationCameraList: ArrayList<ConversationCameraModel>) :
        RecyclerView.Adapter<ConversationCameraAdapter.ConversationViewHolder>() {



            inner class ConversationViewHolder(private val binding: HistoryConversationBinding) :
                    RecyclerView.ViewHolder(binding.root) {
                        fun bind(ConversationCamera : ConversationCameraModel) {
                            binding.tvHistoryConversationText.text = ConversationCamera.ConversationText

                            val isLeftMessage = ConversationCamera.isLeft
                            if (isLeftMessage) {
                                binding.llhHistoryConversationText.gravity = Gravity.START

                            } else {
                                binding.llhHistoryConversationText.gravity = Gravity.END
                            }

                        }
                    }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
                val binding = HistoryConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ConversationViewHolder(binding)
            }

            override fun getItemCount(): Int {
                return ConversationCameraList.size
            }


            override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
                holder.bind(ConversationCameraList[position])
            }
        }
