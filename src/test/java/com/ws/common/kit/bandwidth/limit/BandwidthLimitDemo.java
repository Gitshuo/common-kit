package com.ws.common.kit.bandwidth.limit;

import com.ws.common.kit.intenet.BandwidthLimiter;
import com.ws.common.kit.intenet.DownloadLimiter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangshuo
 * @version 2018-03-15
 */
public class BandwidthLimitDemo {

    private static final Logger logger = LoggerFactory.getLogger(BandwidthLimitDemo.class);
    private static final int CACHE_SIZE = 10 * 1024;

    /**
     * 下载文件 限制最大下载速度
     */
    public static boolean downloadFileByHttps(String url, String savePath, String fileName,
            String desc, int maxRate) {
        InputStream in = null;
        FileOutputStream fileOut = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpget);

            if (response.getStatusLine().getStatusCode() >= 400) {
                logger.error("url:{} 响应失败, statusCode:{}", url,
                        response.getStatusLine().getStatusCode());
                return false;
            }
            HttpEntity entity = response.getEntity();
            DownloadLimiter downloadLimiter = new DownloadLimiter(entity.getContent(),
                    new BandwidthLimiter(maxRate));
            in = new DataInputStream(downloadLimiter);
            File dir = new File(savePath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    logger.error("保存文件，创建目录dir{}失败", savePath);
                    return false;
                }
            }
            File file = new File(savePath, fileName);
            fileOut = new FileOutputStream(file);
            //根据实际运行效果 设置缓冲区大小
            byte[] buffer = new byte[CACHE_SIZE];
            int readByte;
            while ((readByte = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, readByte);
            }
        } catch (IOException e) {
            logger.error("文件{}下载失败，文件描述信息：{}，异常：{}", fileName, desc, e);
            return false;
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException e) {
                    logger.error("关闭输出流失败", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭输入流失败", e);
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("关闭httpClient失败", e);
            }
        }
        return true;
    }

}
