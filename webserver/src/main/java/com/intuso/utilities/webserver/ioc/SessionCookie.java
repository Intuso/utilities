package com.intuso.utilities.webserver.ioc;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tomc on 12/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface SessionCookie {}
