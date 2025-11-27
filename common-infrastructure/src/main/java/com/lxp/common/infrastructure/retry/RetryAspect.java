package com.lxp.common.infrastructure.retry;

import com.lxp.common.annotaion.Retryable;
import com.lxp.common.retry.RetryPolicy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryAspect {

    private final RetryPolicy retryPolicy;

    public RetryAspect(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    @Around("@annotation(retryable)")
    public Object retry(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = resolveMaxAttempts(retryable);
        int attempt = 0;

        while (true) {
            try {
                attempt++;
                return joinPoint.proceed();
            } catch (Exception e) {
                if (!retryPolicy.shouldRetry(e) || attempt >= maxAttempts) {
                    throw e;
                }

                long backoff = resolveBackoff(retryable, attempt);
                Thread.sleep(backoff);
            }
        }
    }

    private int resolveMaxAttempts(Retryable retryable) {
        return retryable.maxAttempts() > 0
                ? retryable.maxAttempts()
                : retryPolicy.getMaxAttempts();
    }

    private long resolveBackoff(Retryable retryable, int attempt) {
        return retryable.backoffMillis() > 0
                ? retryable.backoffMillis()
                : retryPolicy.getBackoffMillis(attempt);
    }
}
