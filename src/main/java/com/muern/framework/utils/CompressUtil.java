package com.muern.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 压缩工具类：
 *  Deflater压缩/Inflater解压缩
 *  Gzip压缩/UnGzip解压缩
 */
public final class CompressUtil {

    private static final Logger logger = LoggerFactory.getLogger(CompressUtil.class);

    /**
     * Deflater压缩字符串 压缩后字节数组以Base64编码输出
     * @param str 待压缩的字符串
     * @return 压缩后的Base64字符串
     */
    public static String deflater(String str) {
        return deflater(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Deflater压缩字节数组 压缩后字节数组以Base64编码输出
     * @param bytes 待压缩的字节数组
     * @return 压缩后的Base64字符串
     */
    public static String deflater(byte[] bytes) {
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStream os = new DeflaterOutputStream(bos);
        ) {
            os.write(bytes);
            //必须要先关闭 否则下面读取的数据不完整
            os.close();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Inflater解压缩字符串 解压缩后的字节数组以Base64编码输出
     * @param base64Str 需要解压缩的字符串
     * @return 解压缩后的Base64字符串
     */
    public static String inflater2Str(String base64Str) {
        return new String(inflater(base64Str), StandardCharsets.UTF_8);
    }
    /**
     * Inflater解压缩字符串 解压缩后以字节数组输出
     * @param base64Str 需要解压缩的字符串
     * @return 解压缩后的字节数组
     */
    public static byte[] inflater(String base64Str) {
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStream os = new InflaterOutputStream(bos);
        ) {
            os.write(Base64.getDecoder().decode(base64Str));
            //必须要先关闭 否则下面读取的数据不完整
            os.close();
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * gzip压缩字符串 压缩后字节数组以Base64编码输出
     * @param str 待压缩的字符串
     * @return 压缩后的Base64字符串
     */
    public static String gzip(String str) {
        return gzip(str.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * gzip压缩字节数组 压缩后字节数组以Base64编码输出
     * @param bytes 待压缩的字节数组
     * @return 压缩后的Base64字符串
     */
    public static String gzip(byte[] bytes) {
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStream os = new GZIPOutputStream(bos);
        ) {
            os.write(bytes);
            os.close();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * gzip解压缩字符串 解压缩后的字节数组以Base64编码输出
     * @param base64Str 需要解压缩的字符串
     * @return 解压缩后的Base64字符串
     */
    public static String ungzip2Str(String base64Str) {
        return new String(ungzip(base64Str), StandardCharsets.UTF_8);
    }

    /**
     * gzip解压缩字符串 解压缩后以字节数组输出
     * @param base64Str 需要解压缩的字符串
     * @return 解压缩后的字节数组
     */
    public static byte[] ungzip(String base64Str) {
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(base64Str));
            InputStream is = new GZIPInputStream(bis);
        ) {
            bos.write(is.readAllBytes());
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static void main1(String[] args) {
        String path1 = "/data/code-server/code-server-3.8.1-amd64.rpm";
        String path2 = "/data/code-server/log.log";
        String path3 = "/data/workspace/bmw-card/bmw-card-h5/src/assets/banner.png";
        String path4 = "/data/workspace/bmw-card/bmw-card-h5/src/assets/money.png";
        String path5 = "/data/workspace/bmw-card/bmw-card-h5/src/assets/yinlian.png";
        byte[] srcBytes = FileUtil.file2Bytes(new File(path1));
        int srcLength = FileUtil.file2Base64(new File(path1)).length();
        System.out.println("文件原大小" + srcLength);
        String str1 = deflater(srcBytes);
        int defalterLength = str1.length();
        System.out.println("str压缩后[defalter]长度：" + defalterLength + "(" + defalterLength*100/(double)srcLength + "%)");
        String str2 = gzip(srcBytes);
        int gzipLength = str2.length();
        System.out.println("str压缩后[gzip]长度：" + gzipLength + "(" + gzipLength*100/(double)srcLength + "%)");
        // str1 = new String(inflater64(str1));
        // System.out.println("str解压缩后[infalter]长度：" + str1.length());
        // str2 = new String(ungzip(str2));
        // System.out.println("str解压缩后[gzip]长度：" + str2.length());
    }
}