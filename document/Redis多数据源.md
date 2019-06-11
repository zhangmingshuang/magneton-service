# Redis多数据源
- 源码 
> service-core-spring
>
> com.magneton.service.core.spring.redis

- 配置
```yaml
spring:
  redis:
    multi:
      db1: # 数据源RedisTemplate的名称
        host: 192.168.1.244
        database: 12
        luttuce:
          pool:
            maxActive: 1000
      db2: # 数据源RedisTemplate的名称
        host: 192.168.1.244
        database: 13
        luttuce:
          pool:
            maxActive: 2000
```

- 用法
```java
@Autowired
private RedisTemplate db1;
@Autowired
private RedisTemplate db2;

@Test
public void test() {
    Object val = this.db1.opsForValue().get("db1");
    Assert.assertTrue("db1".equals(val));
    Boolean del = this.db1.delete("db1");
    Assert.assertTrue(del);
    Assert.assertTrue(db2.delete("db2"));
}
```

- 用法2
```java
@Autowired
private MultiRedisTemplateFactory multiRedisTemplateFactory;

@Test
public void testFactory() {
    RedisTemplate db = multiRedisTemplateFactory.getRedisTemplate("db1");
    Assert.assertNotNull(db);
    db.opsForValue().set("test", "test", 10, TimeUnit.SECONDS);
    System.out.println(db.opsForValue().get("test"));
    Assert.assertTrue("test".equals(db.opsForValue().get("test")));
}
```

- 并发测试报告
```txt
Db1线程池1000 并发1000，持续10S
avgRt:4, minRt:1
passQps:1167
maxSuccessQps:1504, successQps:1167
totalException:0, exceptionQps:0

Db2线程池2000 并发1000，持续10S
avgRt:21, minRt:1
passQps:1978
maxSuccessQps:2574, successQps:1978
totalException:0, exceptionQps:0
```