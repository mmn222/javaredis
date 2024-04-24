import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@RestController
public class RedisApplication {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setPassword(redisPassword);
        return new JedisConnectionFactory(config);
    }

    @Bean
    RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @GetMapping("/{key}")
    public String getValue(@PathVariable String key) {
        return redisTemplate().opsForValue().get(key);
    }

    @GetMapping("/dbsize")
    public Long getDbSize() {
        return redisTemplate().getConnectionFactory().getConnection().dbSize();
    }

    @DeleteMapping("/flushdb")
    public String flushDb() {
        redisTemplate().getConnectionFactory().getConnection().flushDb();
        return "PartyUID DB flushed";
    }

    @GetMapping("/crossref/{key}")
    public String getValueCrossRef(@PathVariable String key) {
        return redisTemplate().opsForValue().get(key);
    }

    @DeleteMapping("/crossref/delete/{key}")
    public String deleteValueCrossRef(@PathVariable String key) {
        String value = redisTemplate().opsForValue().get(key);
        redisTemplate().delete(key);
        return "Value " + value + " with key " + key + " removed from crossref DB";
    }

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }
