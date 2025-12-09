package com.lxp.common.infrastructure.retry;

import com.lxp.common.retry.RetryPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

public class DefaultRetryPolicy implements RetryPolicy {

    @Override
    public int getMaxAttempts() {
        return 3;
    }

    @Override
    public long getBackoffMillis(int attempt) {
        return 100L;
    }

    @Override
    public boolean shouldRetry(Exception e) {
        return e instanceof OptimisticLockingFailureException;
    }
}
