package com.sts.sontalksign.feature.common

interface UpdateRvListener {
    fun onUpdateSelectedTagRv(item: CommonTagItem);
    fun onUpdateUnselectedTagRv(item: CommonTagItem);
}