package com.example.common.download

import android.text.TextUtils
import com.example.common.download.callback.SingleTaskCallBack
import com.example.common.download.callback.TotalTaskCallBackImp
import com.example.common.download.executor.ExecutorManager
import com.example.common.download.task.TotalTask
import com.example.common.mvp.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import java.lang.IllegalArgumentException

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 单例模式下的线程池，用于管理总任务去下载
 *     PackageName: com.example.common.download
 */
@Suppress("SENSELESS_COMPARISON")
class DownloadFileManager private constructor() {

    private var TAG = "DownloadFileManager"

    //创建一个全局的线程池 管理文件的下载
    val mExecutor by lazy {
        ExecutorManager.createExecutor()
    }

    //记录下载的任务
    var mTaskMap = mutableMapOf<String, TotalTask>()

    companion object{

        private var downloadManager: DownloadFileManager? = null

        fun getInstance(): DownloadFileManager{
            if (downloadManager == null){
                return DownloadFileManager()
            }
            return downloadManager!!
        }
    }



}

fun switchToMainThread(mainFun: () -> Unit) {
    Observable.create(ObservableOnSubscribe<Any> { emitter ->
        emitter?.onNext("")
    }).compose(RxUtils.defaultTransformer())
        .subscribe {
            mainFun.invoke()
        }
}