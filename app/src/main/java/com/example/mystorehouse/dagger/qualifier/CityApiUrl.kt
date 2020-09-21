package com.example.mystorehouse.dagger.qualifier

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Qualifier

/**
 * Created by codeest on 2017/2/26.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
annotation class CityApiUrl