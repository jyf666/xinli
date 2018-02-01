package utils;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by XIAODA on 2015/10/20.
 */
public class FileUtils {

    public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_GB = ONE_KB * ONE_MB;

    public static String byteCountToDisplaySize(long size) {
        String displaySize;
        if (size / ONE_GB > 0) {
            displaySize = String.valueOf(size / ONE_GB) + " GB";
        } else if (size / ONE_MB > 0) {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
        } else if (size / ONE_KB > 0) {
            displaySize = String.valueOf(size / ONE_KB) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    /**
     * <p>将文件转成base64 字符串</p>
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeBase64String(buffer);
    }
    /**
     * <p>将base64字符解码保存文件</p>
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code,String targetPath) throws Exception {
        byte[] buffer = Base64.decodeBase64(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }
    /**
     * <p>将base64字符保存文本文件</p>
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void toFile(String base64Code,String targetPath) throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    public static File getPublicFile(String path, String filename) throws MalformedURLException {
        File file = null;
        if (path==null){
            String tDir = System.getProperty("java.io.tmpdir");
            String outputFilePath = tDir + "/" + filename;
            file = new File(outputFilePath);
            return file;
        }
        String urlString = SystemConstant.PUBLIC_DIE_PREFIX + path + filename;
        URL url = new URL(urlString);
        String tDir = System.getProperty("java.io.tmpdir");
        String outputFilePath = tDir + "/" + filename;
        file = new File(outputFilePath);
        file.deleteOnExit();
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(url, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
