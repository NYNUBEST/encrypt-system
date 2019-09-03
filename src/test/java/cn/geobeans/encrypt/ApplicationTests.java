package cn.geobeans.encrypt;

import io.xjar.boot.XBoot;
import io.xjar.jar.XJarRegexEntryFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Test
    public void contextLoads() throws Exception {
        String password = "123456";
        File plaintext = new File("E:\\tmp\\envs-qs-server-1.3.6.jar");
        File encrypted = new File("E:\\tmp\\envs-qs-server.jar");
        XBoot.encrypt(plaintext, encrypted, password, new XJarRegexEntryFilter("BOOT-INF/classes/.+?"));
    }

}
