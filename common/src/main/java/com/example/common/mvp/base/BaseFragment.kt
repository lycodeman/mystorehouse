package com.example.common.mvp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.common.mvp.presenter.RxPresenter
import com.example.common.mvp.utils.ToastUtil.Companion.showShort
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
abstract class BaseFragment<P : RxPresenter<*, *>> : RxFragment() {
    /*懒加载处理 默认是懒加载*/
    private var isViewVisible = true //是否可见
    private var isPrepared = false//是否初始化完成 = false
    private var isFirstLoad = true //是否第一次加载
    protected var rootView: View? = null

    /*rx事件回收*/
    private var compositeDisposable: CompositeDisposable? = null

    lateinit var activity: RxAppCompatActivity

    @Inject
    lateinit var mPresenter: P
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as RxAppCompatActivity //解决getActivity（）报空指针的情况,在fragment中使用这个
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBefore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isFirstLoad = true
        rootView = inflater.inflate(initContentId(), container, false)
        isPrepared = true
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPresenter.attachView(initView())
        initListener()
        initData()
        onVisible()
    }

    override fun onDestroy() {
        super.onDestroy()
        onUnsubscribe()
    }

    abstract fun initBefore()

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 获取布局id
     * @return
     */
    abstract fun initContentId(): Int

    abstract fun initView(): RxFragment

    /**
     * 初始化监听器
     */
    open fun initListener() {}

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

    protected fun toast(msg: String?) {
        showShort(msg!!)
    }
    /*-------------------------------懒加载操作-----------------------------------*/
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) { //可见时进入
            isViewVisible = true
            onVisible()
        } else { //不可见时进入
            isViewVisible = false
            onInvisible()
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     * visible.
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            isViewVisible = true
            onVisible()
        } else {
            isViewVisible = false
            onInvisible()
        }
    }

    protected fun onVisible() {
        if (!isPrepared || !isViewVisible || !isFirstLoad) { //全部满足才会加载
            return
        }
        isFirstLoad = false
        lazyLoad()
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected fun lazyLoad() {}

    /**
     * 这里使用了viewepager暂时用不到onInvisible()
     * 不可见时的操作
     */
    protected fun onInvisible() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        mPresenter?.run {
            this.detachView()
        }
        super.onDestroyView()
    }
}