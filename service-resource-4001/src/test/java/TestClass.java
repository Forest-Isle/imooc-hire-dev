import com.senyu.resource.ResourceApplication;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ResourceApplication.class)
public class TestClass {

    private static final Logger log = LoggerFactory.getLogger(TestClass.class);

    @Test
    public void test() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("fdsaafjdsoladsjfois");
        encryptor.setConfig(config);

        String pwd = encryptor.encrypt("guest");
        log.info("encoded pwd: {}", pwd);
        String decrypt = encryptor.decrypt(pwd);
        log.info("decoded pwd: {}", decrypt);

    }
}
