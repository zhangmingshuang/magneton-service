package com.magneton.service.core.spring.defence;

import com.magneton.service.core.defence.RequestDefence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangmingshuang
 * @since 2019/6/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource(value = "classpath:application.properties")
@Import(RequestDefenceConfiguration.class)
public class RequestDefenceConfigurationTest {

    @Autowired
    private RequestDefence requestDefence;

    @Test
    public void test(){
        System.out.println(requestDefence);
        System.out.println(requestDefence.mode());
    }
}
