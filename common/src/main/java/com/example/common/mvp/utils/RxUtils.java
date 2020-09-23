package com.example.common.mvp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle4.components.support.RxDialogFragment;
import com.trello.rxlifecycle4.components.support.RxFragment;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */

public class RxUtils {

    public static <T> ObservableTransformer<T, T> dialogOTransformer(final Activity activity, final String msg, final boolean cancelable) {
        return new ObservableTransformer<T, T>() {
            private ProgressDialog progressDialog;

            @Override
            public ObservableSource<T> apply(final Observable<T> upstream) {

                return upstream.doOnSubscribe(new Consumer<Disposable>() {//订阅前调用
                    @Override
                    public void accept(@NonNull final Disposable disposable) throws Exception {
                        progressDialog = ProgressDialog.show(activity, null, msg, true, cancelable);
                        if (cancelable) {
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    disposable.dispose();
                                }
                            });
                        }
                    }
                }).doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {//终止时调用
                        if (progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                    }
                });
            }
        };
    }

    public static <T> FlowableTransformer<T, T> dialogFTransformer(final Activity activity, final String msg, final boolean cancelable) {
        return new FlowableTransformer<T, T>() {
            private ProgressDialog progressDialog;
            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream.doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull final Subscription subscription) throws Exception {
                        //订阅前调用
                        progressDialog = ProgressDialog.show(activity, null, msg, true, cancelable);
                        if (cancelable) {
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    subscription.cancel();
                                }
                            });
                        }
                    }
                }).doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {//终止时调用
                        if (progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                    }
                });
            }
        };
    }

    /*默认线程转换*/
    public static <T> ObservableTransformer<T, T> io() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /*默认线程转换*/
    public static <T> ObservableTransformer<T, T> defaultTransformer() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*默认线程转换*/
    public static <T> ObservableTransformer<T, T> defaultTransformer(RxAppCompatActivity appCompatActivity) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.compose(appCompatActivity.<T>bindToLifecycle()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /*默认线程转换*/
    public static <T> ObservableTransformer<T, T> defaultTransformer(RxFragment rxFragment) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.compose(rxFragment.<T>bindToLifecycle()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /*默认线程转换*/
    public static <T> ObservableTransformer<T, T> defaultTransformer(RxDialogFragment rxDialogFragment) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.compose(rxDialogFragment.<T>bindToLifecycle()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new FlowableTransformer<T, T>() {
            @Override
            public Flowable<T> apply(Flowable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 统一返回结果处理
     * @param <T>
     * @return
     */
//    public static <T> FlowableTransformer<GankHttpResponse<T>, T> handleResult() {   //compose判断结果
//        return new FlowableTransformer<GankHttpResponse<T>, T>() {
//            @Override
//            public Flowable<T> apply(Flowable<GankHttpResponse<T>> httpResponseFlowable) {
//                return httpResponseFlowable.flatMap(new Function<GankHttpResponse<T>, Flowable<T>>() {
//                    @Override
//                    public Flowable<T> apply(GankHttpResponse<T> tGankHttpResponse) {
//                        if(!tGankHttpResponse.getError()) {
//                            return createData(tGankHttpResponse.getResults());
//                        } else {
//                            return Flowable.error(new ApiException("服务器返回error"));
//                        }
//                    }
//                });
//            }
//        };
//    }

//    public static <T> ObservableTransformer<HttpResponseResult<T>, T> transformer() {
//        return new ObservableTransformer<HttpResponseResult<T>, T>() {
//            @Override
//            public ObservableSource<T> apply(Observable<HttpResponseResult<T>> upstream) {
//                return upstream
//                        .flatMap(ResultTransformer.<T>flatMap())
//                        .compose(SchedulerTransformer.<T>transformer());
//            }
//        };
//    }
//
//    private static <T> Function<HttpResponseResult<T>, ObservableSource<T>> flatMap() {
//        return new Function<HttpResponseResult<T>, ObservableSource<T>>() {
//            @Override
//            public ObservableSource<T> apply(@NonNull final HttpResponseResult<T> tHttpResponseResult) throws Exception {
//                return new Observable<T>() {
//                    @Override
//                    protected void subscribeActual(Observer<? super T> observer) {
//                        if (tHttpResponseResult.isSuccess()) {
//                            observer.onNext(tHttpResponseResult.getResult());
//                            observer.onComplete();
//                        } else {
//                            observer.onError(new HttpResponseException(tHttpResponseResult.getMsg(), tHttpResponseResult.getState()));
//                        }
//                    }
//                };
//            }
//        };
//    }

    /**
     * 生成Flowable
     *
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createObservable(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * 生成Flowable
     *
     * @param <T>
     * @return
     */
    public static <T> Flowable<T> createFlowable(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }
}
