package com.ws.common.kit;

/**
 * @author wangshuo
 * @version 2017-12-06
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -504955880063536901L;

    public BizException() {
    }

    public BizException(String errorMsg) {
        super(errorMsg);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(Throwable cause, String errorMsg) {
        super(errorMsg, cause);
    }
}
