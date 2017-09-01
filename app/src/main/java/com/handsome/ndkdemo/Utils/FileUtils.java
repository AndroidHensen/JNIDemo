package com.handsome.ndkdemo.Utils;

/**
 * =====作者=====
 * 许英俊
 * =====时间=====
 * 2017/8/28.
 */

public class FileUtils {

    /**
     * 拆分
     *
     * @param path
     * @param path_pattern
     * @param count
     */
    public native static void diff(String path, String path_pattern, int count);

    /**
     * 合并
     *
     * @param path_pattern
     * @param count
     * @param merge_path
     */
    public native static void patch(String path_pattern, int count, String merge_path);

    /**
     * 加密
     *
     * @param normal_path
     * @param crypt_path
     */
    public native static void crypt(String normal_path, String crypt_path);

    /**
     * 解密
     *
     * @param crypt_path
     * @param decrypt_path
     */
    public native static void decrypt(String crypt_path, String decrypt_path);
}
