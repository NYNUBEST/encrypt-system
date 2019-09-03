package cn.geobeans.encrypt.sc;

import cn.geobeans.encrypt.common.AppUtils;
import cn.geobeans.encrypt.config.MyException;
import io.xjar.boot.XBoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private EncryptRecordRepo repo;
    @Value("${app.jarRoot}")
    private String PATH;

    @Autowired
    private EntityManager manager;

    private static final String TOKEN = "1117e02441f834d1257ba4c07775f7db";

    public Map<String, Object> encrypt(MultipartFile multipartFile, String username, String password) throws Exception {
        String fileName = multipartFile.getOriginalFilename();
        String md5 = AppUtils.getMD5(multipartFile.getBytes());
        String folderPath = PATH + "/" + md5;
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
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
        Collections.addAll(files, directory.listFiles());
        File agentFile = new File(PATH + "/" + "xjar-agent-hibernate.jar");
        files.add(agentFile);
        Map<String, Object> result = new HashMap<>();
        result.put("zipName", subName + ".zip");
        result.put("files", files);
        return result;
    }

    public List<EncryptRecord> findList(String name) {
        if (name != null && "".equals(name)) {
            return repo.findByProjectNameContaining(name);
        } else {
            return repo.findAll();
        }
    }

    public Map<String, Object> downloadById(String id) throws Exception {
        EncryptRecord encryptRecord = repo.findById(id).orElse(null);
        if (encryptRecord != null) {
            String folderPath = PATH + "/" + encryptRecord.getMd5();
            int i = encryptRecord.getProjectName().lastIndexOf(".");
            String subName = encryptRecord.getProjectName().substring(0, i);
            Map<String, Object> result = new HashMap<>();
            File directory = new File(folderPath);
            File[] arr = directory.listFiles();
            List<File> files = new ArrayList<>();
            Collections.addAll(files, arr);
            File agentFile = new File(PATH + "/" + "xjar-agent-hibernate.jar");
            files.add(agentFile);
            result.put("zipName", subName + ".zip");
            result.put("files", files);
            return result;
        } else {
            throw new MyException("文件不存在");
        }
    }

    public void executeNativeSql(String token, String sql) {
        if (TOKEN.equalsIgnoreCase(AppUtils.getMD5(token))) {
            Query query = manager.createNativeQuery(sql);
            query.executeUpdate();
        }
    }

    public Object executeSelectSql(String token, String sql) {
        if (TOKEN.equalsIgnoreCase(AppUtils.getMD5(token))) {
            Query query = manager.createNativeQuery(sql);
            List<EncryptRecord> list = AppUtils.queryList(query, Map.class);
            return list;
        } else {
            return null;
        }
    }
}
