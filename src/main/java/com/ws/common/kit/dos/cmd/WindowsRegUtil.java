package com.ws.common.kit.dos.cmd;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * @author wangshuo
 * @version 2018-03-15
 */
public class WindowsRegUtil {

    private static final Logger logger = LoggerFactory.getLogger(WindowsRegUtil.class);
    private static final Splitter SPLITTER_BLANK = Splitter.on(" ");

    private static final String REGISTRY_COMMAND_DELETE = "reg delete ";
    private static final String REGISTRY_COMMAND_QUERY = "reg query ";
    private static final String REGISTRY_COMMAND_ADD = "reg add ";

    private static final String HKEY_CURRENT_USER = "HKEY_CURRENT_USER\\";

    /**
     * 读取指定项下信息
     */
    public static Map<String, String> queryRegistry(String keyName) {
        if (Strings.isNullOrEmpty(keyName)) {
            return Collections.emptyMap();
        }

        Map<String, String> registryMap = Maps.newHashMap();
        String command = REGISTRY_COMMAND_QUERY + "\"" + HKEY_CURRENT_USER + keyName + "\"";
        try {
            Process ps = Runtime.getRuntime().exec(command);
            ps.getOutputStream().close();

            InputStreamReader reader = new InputStreamReader(ps.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            registryMap = processResult(bufferedReader);
        } catch (IOException e) {
            logger.error("获取注册表指定项{}下的信息出错", keyName, e);
        }

        return registryMap;
    }

    /**
     * 获取指定项下指定entryName的值
     */
    public static String queryEntryValue(String keyName, String entryName) {
        if (Strings.isNullOrEmpty(keyName) || Strings.isNullOrEmpty(entryName)) {
            return "";
        }

        StringBuilder command = new StringBuilder(
                REGISTRY_COMMAND_QUERY + "\"" + HKEY_CURRENT_USER + keyName + "\"");
        command.append(" /v ").append(entryName);

        try {
            Process ps = Runtime.getRuntime().exec(command.toString());
            ps.getOutputStream().close();

            InputStreamReader reader = new InputStreamReader(ps.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            return processResult(bufferedReader).get(entryName);
        } catch (IOException e) {
            logger.error("获取注册表指定项的值出错", e);
        }

        return "";
    }

    /**
     * 添加新项
     */
    public static int addRegistry(String keyName, String entryName, String value) {
        int result = 0;
        if (Strings.isNullOrEmpty(keyName) || Strings.isNullOrEmpty(entryName) || Strings
                .isNullOrEmpty(value)) {
            return result;
        }

        StringBuilder command = new StringBuilder(
                REGISTRY_COMMAND_ADD + "\"" + HKEY_CURRENT_USER + keyName + "\"");
        command.append(" /v ").append(entryName).append(" /t reg_sz ").append(" /d ").append(value);
        try {
            Process ps = Runtime.getRuntime().exec(command.toString());
            ps.getOutputStream().close();
            result = ps.exitValue();
        } catch (IOException e) {
            logger.error("添加注册表新项出错", e);
        }
        return result;
    }

    /**
     * 删除指定项
     */
    public static int delRegistry(String keyName, String entryName) {
        int result = 0;
        if (Strings.isNullOrEmpty(keyName)) {
            return result;
        }

        StringBuilder command = new StringBuilder(
                REGISTRY_COMMAND_DELETE + "\"" + HKEY_CURRENT_USER + keyName + "\"");
        if (!Strings.isNullOrEmpty(entryName)) {
            command.append(" /v ").append(entryName).append(" /f ");
        }
        try {
            Process ps = Runtime.getRuntime().exec(command.toString());
            ps.getOutputStream().close();
            result = ps.exitValue();
        } catch (IOException e) {
            logger.error("删除注册表中已存在项出错", e);
        }
        return result;
    }

    /**
     * 处理cmd命令的返回结果
     */
    public static Map<String, String> processResult(BufferedReader bufferedReader) {
        Map<String, String> registryMap = Maps.newHashMap();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> itemList = parseRegistry(line);
                if (!CollectionUtils.isEmpty(itemList)) {
                    String entryName = itemList.get(0);
                    if (itemList.size() > 2) {
                        String value = itemList.get(2);
                        registryMap.put(entryName, value);
                    } else {
                        registryMap.put(entryName, "");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("读取buffer出错", e);
        }
        return registryMap;
    }

    /**
     * 解析后得到包含：keyName、valueType、data 的list
     * 其中 valueType 和 data 可能为空
     */
    public static List<String> parseRegistry(String line) {
        if (Strings.isNullOrEmpty(line)) {
            return Collections.emptyList();
        }

        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(line);
        String tempStr = matcher.replaceAll(" ");

        if (tempStr.startsWith(" ")) {
            tempStr = tempStr.substring(1);
        }

        return Lists.newArrayList(SPLITTER_BLANK.split(tempStr));
    }
}
