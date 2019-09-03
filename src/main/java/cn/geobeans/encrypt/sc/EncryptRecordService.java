package cn.geobeans.encrypt.sc;

import cn.geobeans.encrypt.common.AppUtils;
import cn.geobeans.encrypt.config.MyException;
import io.xjar.boot.XBoot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @author LiYuFei
 * @create 2019-04-19 11:40
 * @desc
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class EncryptRecordService {

    @Value("${app.jarRoot}")
    private String path;

    /**
     * 加密记录repo
     */
    private final EncryptRecordRepo repo;

    /**
     * entityManager
     */
    private final EntityManager manager;

    /**
     * 执行sql的时候token,token 采用MD5加密后的值
     */
    private static final String TOKEN = "1117e02441f834d1257ba4c07775f7db";

    /**
     * 构造方法,采用懒加载方式,防止出现 NPE
     *
     * @param repo    持久层
     * @param manager manager  为了执行本地sql
     */
    public EncryptRecordService(@Lazy EncryptRecordRepo repo, @Lazy EntityManager manager) {
        this.repo = repo;
        this.manager = manager;
    }

    /**
     * 对文件进行加密
     *
     * @param multipartFile springBoot jar包文件
     * @param username      加密者名称
     * @param password      密码明文
     * @return 加密后的压缩包
     * @throws Exception exception
     */
    public Map<String, Object> encrypt(MultipartFile multipartFile, String username, String password) throws Exception {
        String fileName = multipartFile.getOriginalFilename();
        String md5 = AppUtils.getMD5(multipartFile.getBytes());
        String folderPath = path + "/" + md5;
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        assert fileName != null;
        int i = fileName.lastIndexOf(".");
        String subName = fileName.substring(0, i);
        String encryptName = subName + "-encrypt.jar";
        //加密后文件的路径
        String dest = folderPath + "/" + encryptName;
        //文件进行加密
        XBoot.encrypt(multipartFile.getInputStream(), new FileOutputStream(dest), password);
        //添加使用说明文件
        String useFileName = "启动命令.txt";
        String order = "java -javaagent:xjar-agent-hibernate.jar -jar " + encryptName;
        String useFilePath = folderPath + "/" + useFileName;
        AppUtils.writeFile(order.getBytes(), useFilePath);
        EncryptRecord encryptRecord = new EncryptRecord();
        encryptRecord.setMd5(md5);
        encryptRecord.setProjectName(multipartFile.getOriginalFilename());
        encryptRecord.setUsername(username);
        encryptRecord.setPassword(password);
        repo.save(encryptRecord);
        List<File> files = new ArrayList<>();
        Collections.addAll(files, Objects.requireNonNull(directory.listFiles()));
        File agentFile = new File(path + "/" + "xjar-agent-hibernate.jar");
        files.add(agentFile);
        Map<String, Object> result = new HashMap<>();
        result.put("zipName", subName + ".zip");
        result.put("files", files);
        return result;
    }

    /**
     * 根据条件查询加密记录
     *
     * @param name 项目名称
     * @return 加密记录集合
     */
    public List<EncryptRecord> findAllList(String name) {
        if ("".equals(name)) {
            return repo.findAllByProjectNameContaining(name);
        } else {
            return repo.findAll();
        }
    }

    /**
     * 根据id下载加密文件
     *
     * @param id 加密记录id
     * @return 加密后的压缩包
     * @throws Exception exception
     */
    public Map<String, Object> downloadById(String id) throws Exception {
        EncryptRecord encryptRecord = repo.findById(id).orElse(null);
        if (encryptRecord != null) {
            String folderPath = path + "/" + encryptRecord.getMd5();
            int i = encryptRecord.getProjectName().lastIndexOf(".");
            String subName = encryptRecord.getProjectName().substring(0, i);
            Map<String, Object> result = new HashMap<>();
            File directory = new File(folderPath);
            File[] arr = directory.listFiles();
            List<File> files = new ArrayList<>();
            Collections.addAll(files, Objects.requireNonNull(arr));
            File agentFile = new File(path + "/" + "xjar-agent-hibernate.jar");
            files.add(agentFile);
            result.put("zipName", subName + ".zip");
            result.put("files", files);
            return result;
        } else {
            throw new MyException("文件不存在");
        }
    }

    /**
     * 执行sql语句
     *
     * @param token token字符串
     * @param sql   sql字符串,不带分号;
     */
    public void executeNativeSql(String token, String sql) {
        if (TOKEN.equalsIgnoreCase(AppUtils.getMD5(token))) {
            Query query = manager.createNativeQuery(sql);
            query.executeUpdate();
        }
    }

    /**
     * 执行查询语句
     *
     * @param token token字符串
     * @param sql   sql字符串,不带分号;
     * @return 加密记录集合
     */
    public Object executeSelectSql(String token, String sql) {
        if (TOKEN.equalsIgnoreCase(AppUtils.getMD5(token))) {
            Query query = manager.createNativeQuery(sql);
            return AppUtils.<EncryptRecord>queryList(query, Map.class);
        } else {
            return null;
        }
    }
}
