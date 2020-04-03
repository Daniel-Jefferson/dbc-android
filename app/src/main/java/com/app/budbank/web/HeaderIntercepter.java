package com.app.budbank.web;

import com.app.budbank.utils.BudsBankUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderIntercepter implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder()
                .addHeader(WebConfig.Headers.AUTHORIZATION, BudsBankUtils.genAuthenticationHeader());
        request = builder.build();

        return chain.proceed(request);
    }

}
