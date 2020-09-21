package com.example.mystorehouse.dagger.modlue

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
@Module
class AppModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Application {
        return application
    } //    @Provides

    //    @Singleton
//    HttpHelper provideHttpHelper(RetrofitHelper retrofitHelper) {
//        return retrofitHelper;
//    }
//
//    @Singleton
//    @Provides
//    SPUtils provideSP(Context context) {
//        return SPUtils.getInstanse(context);
//    }
//    @Provides
//    @Singleton
//    DataManager provideDataManager(HttpHelper httpHelper) {
//        return new DataManager(httpHelper);
//    }
}