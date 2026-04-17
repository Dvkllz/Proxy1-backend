package com.trabajo.monitoring_proxy.proxy;

import com.trabajo.monitoring_proxy.model.LogEntity;
import com.trabajo.monitoring_proxy.repository.LogRepository;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingProxy<T> implements MicroserviceProxy<T> {

    private final T targetService;
    private final String serviceId;
    private final LogRepository logRepository;

    public LoggingProxy(T targetService, String serviceId, LogRepository logRepository) {
        this.targetService = targetService;
        this.serviceId = serviceId;
        this.logRepository = logRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T execute(String operation, Object... params) {
        LogEntity log = new LogEntity();
        log.setServiceId(serviceId);
        log.setOperation(operation);
        log.setRequestParams(params != null ? Arrays.toString(params) : "[]");

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            Method targetMethod = findMatchingMethod(operation, params);
            result = targetMethod.invoke(targetService, params);

            log.setStatus("SUCCESS");
            log.setResponseData(result != null ? result.toString() : "Success");
        } catch (Exception e) {
            log.setStatus("ERROR");
            log.setErrorMessage(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            throw new RuntimeException("Proxy error: " + log.getErrorMessage());
        } finally {
            log.setDurationMs(System.currentTimeMillis() - startTime);
            logRepository.save(log);
        }

        return (T) result;
    }

    private Method findMatchingMethod(String name, Object[] params) throws NoSuchMethodException {
        int paramCount = params == null ? 0 : params.length;
        for (Method method : targetService.getClass().getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == paramCount) {
                return method;
            }
        }
        throw new NoSuchMethodException("Method " + name + " not found in " + serviceId);
    }
}