package com.example.common.mvp.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import com.example.common.R
import com.example.common.mvp.presenter.BasePresenter
import com.trello.rxlifecycle4.components.support.RxDialogFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
abstract class BaseDialogFragment<T : BasePresenter> :
    RxDialogFragment() {
    @Inject
    protected lateinit var mPresenter: T
    protected var rootView: View? = null

    /*rx事件回收*/
    private var compositeDisposable: CompositeDisposable? = null
    var activity: Activity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity //解决getActivity（）报空指针的情况,在fragment中使用这个
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initBefore()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View(activity)
        val dialog =
            Dialog(getActivity()!!, R.style.style_dialog)
        dialog.setContentView(view)
        dialog.show()
        val window = dialog.window
        window!!.setGravity(Gravity.BOTTOM) //可设置dialog的位置
        window.decorView.setPadding(0, 0, 0, 0) //消除边距
        //        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        StatusBarUtil.setColor(activity, activity.getResources().getColor(R.color.colorPrimary));
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        return dialog
    }

    open fun initBefore() {}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(initContentId(), container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPresenter.attachView(initView())
        initListener()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        onUnsubscribe()
    }

    abstract fun initContentId(): Int

    abstract fun initView(): RxDialogFragment

    /**
     * 初始化监听器
     */
    open fun initListener() {}

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 所有rx订阅后，需要调用此方法，用于在detachView时取消订阅
     */
    protected fun addDisposable(disposable: Disposable?) {
        if (compositeDisposable == null) compositeDisposable = CompositeDisposable()
        compositeDisposable!!.add(disposable)
    }

    /**
     * 取消本页面所有订阅
     */
    protected fun onUnsubscribe() {
        if (compositeDisposable != null && compositeDisposable!!.isDisposed) {
            compositeDisposable!!.dispose()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        mPresenter?.run {
            this!!.detachView()
        }
        super.onDestroyView()
    }

    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }
}