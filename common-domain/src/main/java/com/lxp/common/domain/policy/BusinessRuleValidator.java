package com.lxp.common.domain.policy;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.common.domain.exception.ErrorCode;

public final class BusinessRuleValidator {
    public static void validate(BusinessRule rule) {
        if (rule.isBroken()) {
            throw new DomainException(rule.getErrorCode(), rule.getMessage()) {
            };
        }
    }

    public static void validateAll(BusinessRule... rules) {
        for (BusinessRule rule : rules) {
            validate(rule);
        }
    }
}
