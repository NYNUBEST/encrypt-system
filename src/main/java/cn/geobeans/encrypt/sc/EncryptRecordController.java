package cn.geobeans.encrypt.sc;

import cn.geobeans.encrypt.common.JsonResponse;
import cn.geobeans.encrypt.common.ZipUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @author LiYuFei
 * @create 2019-04-19 10:52
 * @desc 加密controller
 **/
@RestController
@RequestMapping("/encrypt")
public class EncryptRecordController {
    private static final String POST = "";
    private static final String GET_LIST = "/list";
    private static final String DOWNLOAD = "/{id}";
    private static final String EXECUTE_SQL = "/execute/sql";
    private static final String SELECT_SQL = "/select/sql";

    @Autowired
    private EncryptRecordService service;

    @ApiOperation("springBoot.jar进行加密")
    @PostMapping(POST)
    public JsonResponse encrypt1(MultipartFile file, @ApiParam("加密者姓名(中遥内部记录)") @RequestParam(value = "username") String username,
                                 @ApiParam("密码") @RequestParam(value = "password") String password,
                                 HttpServletResponse response) throws Exception {
        Map<String, Object> map = service.encrypt(file, username, password);
        String zipName = map.get("zipName").toString();
        List<File> files = (List<File>) map.get("files");
        response.setContentType("application/x-zip-compressed");
        response.setHeader("Content-Disposition", "attachment;filename=\""
                + new String(zipName.getBytes(), "ISO8859-1") + "\"");
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        try {
            for (int i = 0; i < files.size(); i++) {
                File file1 = files.get(i);
                ZipUtils.doCompress(file1.getAbsolutePath(), out);
                response.flushBuffer();
            }
            return new JsonResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResponse(false, e.getMessage());
        } finally {
            out.close();
        }
    }

    @ApiOperation("根据名称查询所有")
    @GetMapping(GET_LIST)
    public JsonResponse getList(@ApiParam("jar包名称") @RequestParam(value = "name", required = false) String name) {
        return new JsonResponse(service.findList(name));
    }

    @ApiOperation("根据id进行下载")
    @GetMapping(DOWNLOAD)
    public JsonResponse download(@PathVariable String id, HttpServletResponse response) throws Exception {
        Map<String, Object> map = service.downloadById(id);
        String zipName = map.get("zipName").toString();
        List<File> files = (List<File>) map.get("files");
        response.setContentType("application/x-zip-compressed");
        response.setHeader("Content-Disposition", "attachment;filename=\""
                + new String(zipName.getBytes(), "ISO8859-1") + "\"");
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        try {
            for (int i = 0; i < files.size(); i++) {
                File file1 = files.get(i);
                ZipUtils.doCompress(file1.getAbsolutePath(), out);
                response.flushBuffer();
            }
            return new JsonResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResponse(false, e.getMessage());
        } finally {
            out.close();
        }
    }

    @ApiOperation("执行本地查询sql")
    @GetMapping(SELECT_SQL)
    public JsonResponse executeSelectSql(@ApiParam("口令") @RequestParam(value = "token") String token, @ApiParam("sql") @RequestParam(value = "sql") String sql) {
        return new JsonResponse(service.executeSelectSql(token, sql));
    }

    @ApiOperation("执行本地sql")
    @GetMapping(EXECUTE_SQL)
    public JsonResponse executeNativeSql(@ApiParam("口令") @RequestParam(value = "token") String token, @ApiParam("sql") @RequestParam(value = "sql") String sql) {
        service.executeNativeSql(token, sql);
        return new JsonResponse(true);
    }


}
