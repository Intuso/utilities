package com.intuso.utilities.webserver.filter.security.ioc;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.intuso.utilities.webserver.filter.security.SecurityFilter;

import java.util.Map;

/**
 * Created by tomc on 21/01/17.
 */
public class SecurityModule extends ServletModule {

    private final Map<String, String> filterParams = Maps.newHashMap();

    public SecurityModule(String loginPage, String nextParam, Multimap<String, String> unsecuredEndpoints) {
        filterParams.put(SecurityFilter.LOGIN_PAGE, loginPage);
        filterParams.put(SecurityFilter.NEXT_PARAM, nextParam);
        int i = 0;
        for(Map.Entry<String, String> entry : unsecuredEndpoints.entries())
            filterParams.put(SecurityFilter.UNSECURED_PREFIX + i++, entry.getKey() + SecurityFilter.METHOD_PATH_SEPARATOR + entry.getValue());
    }

    @Override
    protected void configureServlets() {
        super.configureServlets();
        bind(SecurityFilter.class).in(Scopes.SINGLETON);
        filter("*").through(SecurityFilter.class, filterParams);
    }
}
