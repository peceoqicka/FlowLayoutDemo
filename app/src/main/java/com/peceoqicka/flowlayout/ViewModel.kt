package com.peceoqicka.flowlayout

import android.databinding.BaseObservable
import android.databinding.Bindable

class ViewModel : BaseObservable() {
    lateinit var bindActivity: MainActivity

    var adapter: ItemAdapter? = null
        set(value) {
            field = value;notifyPropertyChanged(BR.adapter)
        }
        @Bindable
        get
}