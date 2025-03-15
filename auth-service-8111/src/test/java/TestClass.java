import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senyu.auth.AuthApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;

@SpringBootTest(classes = AuthApplication.class)
public class TestClass {

    //密钥
    public static final String user_key = "myverysecuresecretkeywithmorethan32chars";

    @Test
    public void createJWT() throws JsonProcessingException {
        // 1. 对密钥进行base64编码
        String base64 = new BASE64Encoder().encode(user_key.getBytes());
        // 2. 对base64生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        // 3. 通过jwt去生成一个token字符串
        stu stu = new stu("tom", 18);
        ObjectMapper objectMapper = new ObjectMapper();
        String stuJson = objectMapper.writeValueAsString(stu);
        String myJWT = Jwts.builder()
                .setSubject(stuJson) // 设置用户自定义数据
                .signWith(secretKey) // 使用哪个密钥对象进行jwt的生成
                .compact(); // 压缩并且生成jwt
        System.out.println(myJWT);
    }

    @Test
    public void checkJWT() throws JsonProcessingException {
        String jwt = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ7XCJuYW1lXCI6XCJ0b21cIixcImFnZVwiOjE4fSJ9.Bwgm0XwXPaaG4Qy7QSPN2zveyGAjIbYT0zs0kdbJ1Dm2FA93m5VLWfTmCHioB-KG";
        String base64 = new BASE64Encoder().encode(user_key.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build(); // 构造解析器
        // 解析成功 可以获得Claims 从而去获取相关数据 如果跑异常 说明解析不通过
        Jws<Claims> claims = jwtParser.parseClaimsJws(jwt); // 解析jwt
        String stuJson = claims.getBody().getSubject();
        ObjectMapper objectMapper = new ObjectMapper();
        stu stu = objectMapper.readValue(stuJson, stu.class);
        System.out.println(stu);
    }
}
