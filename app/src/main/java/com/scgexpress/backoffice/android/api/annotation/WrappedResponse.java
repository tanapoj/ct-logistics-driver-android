package com.scgexpress.backoffice.android.api.annotation;

import com.scgexpress.backoffice.android.api.parser.ApiResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

// kotlin do not support: `annotation class Unwrap(val value: KClass<out SimpleApiResult<*>>)`
@Target(ElementType.METHOD)
public @interface WrappedResponse {
    Class<? extends ApiResult> value();
}