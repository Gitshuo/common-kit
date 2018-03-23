package com.ws.common.kit.excel;

import com.ws.common.kit.PoiDataVo;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangshuo
 * @version 2018-03-02
 */
public class TestExcelParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestExcelParser.class);

    @Test
    public void readPoiData() {
        String dirName = "/Users/wangshuo/Documents/work/POI数据V2/";
        String fileName = "5F_POI_INFO.xlsx";
        try {

            FileInputStream fis = new FileInputStream(new File(dirName + fileName));
            List<PoiDataVo> poiDataVos = ReadExcelUtil.readExcel(fileName, fis, PoiDataVo.class, 2);
            for (PoiDataVo vo : poiDataVos) {
                LOGGER.info("POI数据信息:{}", vo);
            }
        } catch (Exception e) {
            LOGGER.error("excel file parse error!");
        }
    }
}
