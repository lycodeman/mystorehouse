
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.common.mvp.base.BaseActivity
import com.example.common.mvp.customview.CustomProgressDialog
import com.example.common.mvp.presenter.RxPresenter
import com.example.mystorehouse.dagger.modlue.ActivityModule
import com.example.mystorehouse.dagger.modlue.AppModule
import com.example.mystorehouse.dagger.modlue.HttpModule
import com.example.common.mvp.utils.ConnectivityUtils
import com.example.common.mvp.utils.ToastUtil
import com.example.mystorehouse.R
import com.example.mystorehouse.dagger.component.ActivityComponent
import com.example.mystorehouse.dagger.component.AppComponent
import com.example.mystorehouse.dagger.component.DaggerActivityComponent
import com.example.mystorehouse.dagger.component.DaggerAppComponent
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/01
 *     Desc   :
 *     PackageName: com.genlot.hnapp.mvp.base
 */
inline fun RxAppCompatActivity.checkNetwork(){
    if(!ConnectivityUtils.isNetworkAvailable()) {
        ToastUtil.showShort(resources.getString(R.string.network_not_available))
        return
    }
}

inline fun AppCompatActivity.checkNetwork(){
    if(!ConnectivityUtils.isNetworkAvailable()) {
        ToastUtil.showShort(resources.getString(R.string.network_not_available))
        return
    }
}

inline fun Context.checkNetwork(){
    if(!ConnectivityUtils.isNetworkAvailable()) {
        ToastUtil.showShort(getResources().getString(R.string.network_not_available))
        return
    }
}



/**
 * 拓展加载弹窗属性 属性是静态的需要一个单例
 */
class ProgressDialogUtils private constructor(){
    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ProgressDialogUtils()
        }
    }
    private var mProgressDialog: CustomProgressDialog? = null
    fun getProgressDialog(context: Context) : CustomProgressDialog{
        if (mProgressDialog == null){
            mProgressDialog = CustomProgressDialog(context)
        }
        return mProgressDialog!!
    }
}
val <P :  RxPresenter<*,*>> BaseActivity<P>.mProgressDialog: CustomProgressDialog?
    get() {
        return ProgressDialogUtils.instance.getProgressDialog(this)
    }

fun <P :  RxPresenter<*,*>> BaseActivity<P>.showProgress(msg: String){
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

fun <P :  RxPresenter<*,*>> BaseActivity<P>.hideProgress(){
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
var <P :  RxPresenter<*,*>> BaseActivity<P>.mActivityComponent: ActivityComponent
    get() = DaggerActivityComponent.builder()
        .activityModule(mActivityModule)
        .appComponent(mAppComponent)
        .build()
    set(value) {
        //供第三方自己实现
    }

var <P :  RxPresenter<*,*>> BaseActivity<P>.mAppComponent: AppComponent
    get() = DaggerAppComponent.builder()
        .appModule(AppModule(application))
        .httpModule(HttpModule())
        .build()
    set(value) {
        //供第三方自己实现
    }

var <P :  RxPresenter<*,*>> BaseActivity<P>.mActivityModule: ActivityModule
    get() = ActivityModule(this)
    set(value) {
        //供第三方自己实现
    }

