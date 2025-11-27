package com.lxp.common.domain.policy;

import com.lxp.common.domain.exception.ErrorCode;

public interface BusinessRule {
    boolean isBroken();
    String getMessage();
    ErrorCode getErrorCode();
}
