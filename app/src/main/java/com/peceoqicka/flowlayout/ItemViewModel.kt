package com.peceoqicka.flowlayout

import android.databinding.BaseObservable
import android.databinding.Bindable

/**
 * <pre>
 *      author  :   Acer
 *      e-mail  :   xx@xxx
 *      time    :   2018/7/9
 *      desc    :
 *      version :   1.0
 * </pre>
 */
class ItemViewModel : BaseObservable() {
    var label: String = ""
        set(value) {
            field = value;notifyPropertyChanged(BR.label)
        }
        @Bindable
        get
}