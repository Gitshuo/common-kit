package com.ws.common.kit.intenet;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author wangshuo
 * @version 2018-03-15
 */
public class UploadLimiter extends OutputStream {

    private OutputStream os = null;
    private BandwidthLimiter bandwidthLimiter = null;

    public UploadLimiter(OutputStream os, BandwidthLimiter bandwidthLimiter) {
        this.os = os;
        this.bandwidthLimiter = bandwidthLimiter;
    }

    @Override
    public void write(int b) throws IOException {
        if (bandwidthLimiter != null) {
            bandwidthLimiter.limitNextBytes();
        }
        this.os.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (bandwidthLimiter != null) {
            bandwidthLimiter.limitNextBytes(len);
        }
        this.os.write(b, off, len);
    }
}
