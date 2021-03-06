
import com.example.common.mvp.base.BaseFragment
import com.example.common.mvp.customview.CustomProgressDialog
import com.example.common.mvp.presenter.RxPresenter
import com.example.common.mvp.utils.ConnectivityUtils
import com.example.common.mvp.utils.ToastUtil
import com.example.mystorehouse.R
import com.example.mystorehouse.dagger.component.AppComponent
import com.example.mystorehouse.dagger.component.DaggerAppComponent
import com.example.mystorehouse.dagger.component.DaggerFragmentComponent
import com.example.mystorehouse.dagger.component.FragmentComponent
import com.example.mystorehouse.dagger.modlue.AppModule
import com.example.mystorehouse.dagger.modlue.FragmentModule
import com.example.mystorehouse.dagger.modlue.HttpModule
import com.trello.rxlifecycle4.components.support.RxDialogFragment
import com.trello.rxlifecycle4.components.support.RxFragment

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/01
 *     Desc   :
 *     PackageName: com.genlot.hnapp.mvp.base
 */

inline fun <P :  RxPresenter<*,*>> BaseFragment<P>.checkNetwork(){
    if(!ConnectivityUtils.isNetworkAvailable()) {
        ToastUtil.showShort(getResources().getString(R.string.network_not_available))
        return
    }
}

/**
 * 拓展加载弹窗属性 属性是静态的需要一个单例
 */
val <P :  RxPresenter<*,*>> BaseFragment<P>.mProgressDialog: CustomProgressDialog?
    get() {
        return ProgressDialogUtils.instance.getProgressDialog(activity)
    }

fun <P :  RxPresenter<*,*>> BaseFragment<P>.showProgress(msg: String){
    try {
        mProgressDialog?.run {
            if (!this.isShowing) {
                this.setMessage(msg)
                this.setCancelable(true)
                this.setCanceledOnTouchOutside(false)
                this.show()
            } else {
                this.setMessage(msg)
                this.show()
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <P :  RxPresenter<*,*>> BaseFragment<P>.hideProgress(){
    try {
        mProgressDialog?.run {
            if (this.isShowing) {
                this.dismiss()
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

/**
 * 拓展依赖注入
 */
var <P :  RxPresenter<*,*>> BaseFragment<P>.mFragmentComponent: FragmentComponent
    get() = DaggerFragmentComponent.builder()
        .fragmentModule(mFragmentModule)
        .appComponent(mAppComponent)
        .build()
    set(value) {
        //供第三方自己实现
    }

var <P :  RxPresenter<*,*>> BaseFragment<P>.mAppComponent: AppComponent
    get() = DaggerAppComponent.builder()
        .appModule(AppModule(activity.application))
        .httpModule(HttpModule())
        .build()
    set(value) {
        //供第三方自己实现
    }

var <P :  RxPresenter<*,*>> BaseFragment<P>.mFragmentModule: FragmentModule
    get() = FragmentModule(this)
    set(value) {
        //供第三方自己实现
    }

//var <P :  RxPresenter<*,*>> BaseFragment<P>.mFragmentModule: FragmentModule<RxDialogFragment>
//    get() = FragmentModule(this)
//    set(value) {
//        //供第三方自己实现
//    }
////
//fun getFragmentComponent(): FragmentComponent? {
//    return DaggerFragmentComponent.builder()
//        .appComponent(getAppComponent())
//        .fragmentModule(getFragmentModule())
//        .build()
//}
//
//fun getFragmentModule(): FragmentModule? {
//    return FragmentModule(this)
//}
//
//fun getAppComponent(): AppComponent? {
//    return DaggerAppComponent.builder()
//        .appModule(AppModule(getActivity().getApplication()))
//        .httpModule(HttpModule())
//        .build()
//}

