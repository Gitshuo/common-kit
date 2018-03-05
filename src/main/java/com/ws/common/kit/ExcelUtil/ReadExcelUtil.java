package com.ws.common.kit.ExcelUtil;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ws.common.kit.BizException;
import com.ws.common.kit.ClassReflectUtil;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangshuo
 * @version 2018-03-01
 */
public class ReadExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReadExcelUtil.class);

    /**
     * 读取Excel文件内容，装配成指定对象
     *
     * @param fileName    文件名称
     * @param is          输入流
     * @param clazz       对象类型
     * @param <E>         list中元素类型
     * @return            装配成的对象list
     * @throws Exception  如果文件读取失败
     */
    public static <E> List<E> readExcel(String fileName, InputStream is, Class<E> clazz)
            throws Exception {
        return readExcel(fileName, is, clazz, 1);
    }

    /**
     * 读取Excel文件内容，装配成指定对象
     *
     * @param fileName    文件名称
     * @param is          输入流
     * @param clazz       对象类型
     * @param beginRowNum 开始读的行数
     * @param <E>         list中元素类型
     * @return            装配成的对象list
     * @throws Exception  如果文件读取失败
     */
    public static <E> List<E> readExcel(String fileName, InputStream is, Class<E> clazz,
            int beginRowNum) throws Exception {
        return new ExcelParser<>(clazz, beginRowNum).readExcel(fileName, is);
    }


    private static class ExcelParser<E> {
        private static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
        private static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";
        private static final String EMPTY = "";
        private static final String POINT = ".";

        private int beginRowNum = 2;
        private Class<E> clazz;

        ExcelParser(Class<E> clazz, int beginRowNum) {
            this.clazz = clazz;
            this.beginRowNum = beginRowNum;
        }

        List<E> readExcel(String fileName, InputStream is) throws Exception {
            if (Strings.isNullOrEmpty(fileName)) {
                return Collections.emptyList();
            }

            String postfix = getPostfix(fileName);
            if (Strings.isNullOrEmpty(postfix)) {
                return Collections.emptyList();
            }

            if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                return readXls(is);
            } else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                return readXlsx(is);
            }

            throw new BizException(MessageFormat.format("非法的文件, 扩展名:{0}", postfix));
        }

        private List<E> readXlsx(InputStream is) throws Exception {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            Iterator<XSSFSheet> sheetIterator = xssfWorkbook.iterator();
            List<E> list = Lists.newArrayList();
            while (sheetIterator.hasNext()) {
                list.addAll(processSheet(sheetIterator.next()));
            }
            return list;
        }

        private List<E> processSheet(XSSFSheet sheet) throws Exception {
            List<E> list = Lists.newArrayList();
            for (int rowNum = this.beginRowNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
                if (isEmptyOrNullRow(sheet.getRow(rowNum))) continue; //空行跳过
                list.add(processRow(sheet.getRow(rowNum)));
            }
            return list;
        }

        private List<E> readXls(InputStream is) throws Exception {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            List<E> list = Lists.newArrayList();
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) continue;
                list.addAll(processSheetXls(hssfSheet));
            }
            return list;
        }

        private List<E> processSheetXls(HSSFSheet sheet) throws Exception {
            List<E> list = Lists.newArrayList();
            for (int rowNum = beginRowNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
                if (isEmptyOrNullRow(sheet.getRow(rowNum))) continue; //空行跳过
                list.add(processRow(sheet.getRow(rowNum)));
            }
            return list;
        }

        private E processRow(Row row) throws Exception {
            E object = clazz.newInstance(); //构造一个实例对象
            List<Field> fields = ClassReflectUtil.getClassFields(clazz);
            for (Field field : fields) {
                processField(field, object, row::getCell);
            }

            return object;
        }

        private void processField(Field field, Object object, Function<Integer, Cell> row)
                throws Exception {
            if (field.isAnnotationPresent(PropParse.class)) {
                PropParse proParse = field.getAnnotation(PropParse.class);
                if (proParse == null) return;
                String proValue;
                proValue = getValue(row.apply(proParse.index()));
                if (Strings.isNullOrEmpty(proValue) && !proParse.nullable()) {
                    logger.error("解析资源文件报错，{}字段信息为空", field.getName());
                    throw new BizException(
                            MessageFormat.format("解析资源文件报错，{0}字段不能为空", field.getName()));
                }

                ClassReflectUtil.setValue(field, object, proValue);
            }
        }

        private String getValue(Cell cell) {
            if (Objects.isNull(cell)) {
                return "";
            }
            if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                DecimalFormat df = new DecimalFormat("0");
                return String.valueOf(df.format(cell.getNumericCellValue()));
            } else {
                return String.valueOf(cell.getStringCellValue());
            }
        }

        private String getPostfix(String fileName) {
            if (Strings.isNullOrEmpty(fileName)) {
                return EMPTY;
            }
            if (fileName.contains(POINT)) {
                return fileName.substring(fileName.lastIndexOf(POINT) + 1, fileName.length());
            }
            return EMPTY;
        }

        private boolean isEmptyOrNullRow(Row row) {
            if (Objects.isNull(row)) {
                return true;
            }

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                if (!Strings.isNullOrEmpty(getValue(cellIterator.next()))) {
                    return false;
                }
            }

            return true;
        }
    }
}
