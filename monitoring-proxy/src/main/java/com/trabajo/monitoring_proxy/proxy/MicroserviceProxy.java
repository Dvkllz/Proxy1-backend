package com.trabajo.monitoring_proxy.proxy;

public interface MicroserviceProxy<T> {
    T execute(String operation, Object... params);
}