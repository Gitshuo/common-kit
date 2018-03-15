package com.ws.common.kit.dos.cmd;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangshuo
 * @version 2018-03-15
 */
public class CmdUtil {

    private static final Logger logger = LoggerFactory.getLogger(CmdUtil.class);
    private static final Joiner blankJoiner = Joiner.on(" ");
    /**
     * 执行cmd命令，可带参数
     */
    public static boolean executeCmdStr(String commandStr, List<String> parameters) {
        if (Strings.isNullOrEmpty(commandStr)) {
            return false;
        }
        String cmdParams = blankJoiner.join(parameters);
        logger.info("实际执行的cmd命令，commandString:{}", commandStr + cmdParams);
        try {
            Process process = Runtime.getRuntime().exec(commandStr + cmdParams);
            process.waitFor();

            if (process.exitValue() == 0) {
                process.destroy();
                return true;
            }else {
                process.destroy();
                return false;
            }
        } catch (IOException e) {
            logger.error("执行cmd命令出错，cmdStr：{}", commandStr, e);
        } catch (InterruptedException e) {
            logger.error("执行cmd命令时，进程被打断，cmdStr：{}", commandStr, e);
        }
        return false;
    }

    /**
     * 执行批处理文件
     */
    public static boolean executeBatFile(String cmdDir, String fileName, List<String> parameters) {
        File file = new File(cmdDir, fileName);
        if (!file.exists()) {
            logger.error("批处理文件{}不存在", fileName);
            return false;
        }

        String command = "cmd.exe /C start /b " + cmdDir + fileName;
        return executeCmdStr(command, parameters);
    }

    /***
     * 将cmd命令保存到文件，执行bat文件
     */
    public static boolean saveAndExecute(String cmdDir, String fileName, String commandStr,
            List<String> parameters) {
        if (Strings.isNullOrEmpty(fileName) || Strings.isNullOrEmpty(commandStr)) {
            return false;
        }

        File dir = new File(cmdDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }

        File file = new File(cmdDir, fileName);
        PrintStream printStream;
        try {
            printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("文件:{}不存在", fileName, e);
            return false;
        }
        printStream.print(commandStr);
        return executeBatFile(cmdDir, fileName, parameters);
    }
}
