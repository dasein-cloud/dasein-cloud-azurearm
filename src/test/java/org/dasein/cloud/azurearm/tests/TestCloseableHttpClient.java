package org.dasein.cloud.azurearm.tests;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by vmunthiu on 8/14/2015.
 */
public class TestCloseableHttpClient<T> extends CloseableHttpClient {

    private boolean executeCalled = false;
    private HttpUriRequest actualHttpUriRequest;
    private T returnValue;

    public TestCloseableHttpClient(T returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public HttpParams getParams() {
        return null;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }

    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        executeCalled = true;
        actualHttpUriRequest = request;
        return (T) returnValue;
    }

    public HttpUriRequest getActualHttpUriRequest() {
        return actualHttpUriRequest;
    }

    public boolean isExecuteCalled() {
        return executeCalled;
    }
}

