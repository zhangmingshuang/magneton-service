package com.magneton.service.core.spring.redis;

import com.magneton.service.core.spring.ConcurrentTest;
import com.magneton.service.core.spring.YamlPropertySourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;


/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@PropertySource(value = "classpath:redis-multi.yml", factory = YamlPropertySourceFactory.class)
@Import(MultiRedisAutoConfiguration.class)
public class MultiRedisDatasourceTest {

    @Value("${test.a:empty}")
    private String a;
    @Value("${spring.redis.host:emptyb}")
    private String b;
    @Autowired
    private MultiRedisTemplateFactory multiRedisTemplateFactory;
    @Autowired
    private RedisTemplate db1;
    @Autowired
    private RedisTemplate db2;

    @Before
    public void before() {
        System.out.println(a);
        System.out.println(b);
        System.out.println(db1);
        System.out.println(db2);
        System.out.println(multiRedisTemplateFactory);
        db1.opsForValue().set("db1", "db1");
        db2.opsForValue().set("db2", "db2");
    }

    @Test
    public void testFactory() {
        RedisTemplate db = multiRedisTemplateFactory.getRedisTemplate("db1");
        Assert.assertNotNull(db);
        db.opsForValue().set("test", "test", 10, TimeUnit.SECONDS);
        System.out.println(db.opsForValue().get("test"));
        Assert.assertTrue("test".equals(db.opsForValue().get("test")));
    }

    @Test
    public void test() {
        Object val = this.db1.opsForValue().get("db1");
        Assert.assertTrue("db1".equals(val));
        Boolean del = this.db1.delete("db1");
        Assert.assertTrue(del);
        Assert.assertTrue(db2.delete("db2"));
    }

    @Test
    public void testDb1Concurrent() {
//        avgRt:4, minRt:1
//        passQps:1167
//        maxSuccessQps:1504, successQps:1167
//        totalException:0, exceptionQps:0
        ConcurrentTest.start(1_000, 10, () -> {
            db1.opsForValue().getAndSet("db1", "test");
        });
    }

    @Test
    public void testDb2Concurrent() {
//        avgRt:21, minRt:1
//        passQps:1978
//        maxSuccessQps:2574, successQps:1978
//        totalException:0, exceptionQps:0
        ConcurrentTest.start(1_000, 10, () -> {
            db2.opsForValue().getAndSet("db2", "test");
        });
    }
}
