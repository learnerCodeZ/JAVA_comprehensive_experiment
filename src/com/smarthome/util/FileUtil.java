package com.smarthome.util;

import java.io.*;

/**
 * 文件操作工具类
 */
public class FileUtil {

    public static String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            return "";
        }
        return content.toString();
    }

    public static void writeFile(String path, String content) {
        ensureDirectory(new File(path).getParent());
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ensureDirectory(String dirPath) {
        if (dirPath != null && !dirPath.isEmpty()) {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }
}
