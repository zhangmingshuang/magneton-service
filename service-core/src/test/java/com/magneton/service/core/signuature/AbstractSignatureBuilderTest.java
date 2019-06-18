package com.magneton.service.core.signuature;

import com.magneton.service.core.signature.AbstractSignatureBuilder;
import com.magneton.service.core.signature.Signature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/6/14
 */
public class AbstractSignatureBuilderTest {

    public AbstractSignatureBuilder builder;

    public static class AbstractSignature implements Signature {
        public AbstractSignatureBuilder builder;

        public AbstractSignature(AbstractSignatureBuilder builder) {
            this.builder = builder;
        }

        @Override
        public boolean validate(ServletRequest servletRequest) {
            return false;
        }

        @Override
        public String sign(Map<String, String[]> parameterMap) {
            return null;
        }

        @Override
        public String signContent(Map<String, String[]> parameterMap) {
            return null;
        }
    }

    @Before
    public void before() {
        builder = new AbstractSignatureBuilder() {
            @Override
            public Signature build() {
                return new AbstractSignature(this);
            }
        };
    }

    @Test
    public void test() {
        AbstractSignature build = (AbstractSignature) this.builder.time("t")
                .sign("s")
                .required("r")
                .exclude("e")
                .timeOut(1)
                .log(true)
                .securityKey("s")
                .build();
        Assert.assertEquals(build.builder.getSign(), "s");
        Assert.assertEquals(build.builder.getTime(), "t");
        Assert.assertArrayEquals(build.builder.getRequired(), new String[]{"r"});
        Assert.assertArrayEquals(build.builder.getExclude(), new String[]{"e"});
        Assert.assertEquals(build.builder.getSecurityKey(), "s");
        Assert.assertTrue(build.builder.getTimeOut() == 1);
        Assert.assertTrue(build.builder.isLog());
    }
}
