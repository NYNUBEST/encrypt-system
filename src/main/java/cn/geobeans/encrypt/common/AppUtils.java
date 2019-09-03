package cn.geobeans.encrypt.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import javax.persistence.Query;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author LiYuFei
 * @create 2019-04-19 16:42
 * @desc
 **/
public class AppUtils {

    public static String getMD5(byte[] bytes) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(bytes);
        String hashString = new BigInteger(1, digest).toString(16);
        return hashString;
    }

    public static void writeFile(byte[] bytes, String fullPath) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        File file = createFile(fullPath, true);
        FileOutputStream fileOutputStream = new FileOutputStream(fullPath);
        byte[] buff = new byte[1024];
        int len;
        while ((len = byteArrayInputStream.read(buff)) != -1) {
            fileOutputStream.write(buff, 0, len);
        }
    }

    public static File createFile(String path, boolean covered) throws Exception {
        File file = new File(path);
        if (file.exists()) {
            if (covered) {
                file.delete();
            } else {
                return file;
            }
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.createNewFile();
        return file;
    }

    /**
     * 文件转byte数组
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static byte[] file2Byte(File file) throws Exception {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            byte[] buffer = null;
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
            return buffer;
        } catch (Exception e) {
            throw e;
        } finally {
            closeQuietly(bos, fis);
        }

    }

    public static void closeQuietly(OutputStream out, InputStream is) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {

            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

    public static String getMD5(String source) {
        String s = null;
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source.getBytes());
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>>
                // 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 处理manager查询出的结构
     *
     * @param query
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> queryList(Query query, Class t, Object... params) {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                Object obj = params[i];
                if (obj != null) {
                    query.setParameter(i + 1, params[i]);
                }
            }
        }
        List<T> list = ((NativeQueryImpl) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (t != null) {
            return JSONArray.parseArray(JSON.toJSONString(list), t);
        } else {
            return list;
        }
    }
}
