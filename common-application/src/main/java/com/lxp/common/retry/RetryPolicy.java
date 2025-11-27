package com.lxp.common.retry;

public interface RetryPolicy {

    int getMaxAttempts();

    long getBackoffMillis(int attempt);

    boolean shouldRetry(Exception e);
}