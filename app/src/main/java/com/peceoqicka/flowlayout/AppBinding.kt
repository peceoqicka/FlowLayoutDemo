package com.peceoqicka.flowlayout

import android.databinding.BindingAdapter
import android.widget.BaseAdapter

@BindingAdapter("bind:fl_adapter")
fun setFlowLayoutAdapter(flowLayout: FlowLayout, adapter: BaseAdapter?) {
    adapter?.let {
        flowLayout.setAdapter(it)
    }
}