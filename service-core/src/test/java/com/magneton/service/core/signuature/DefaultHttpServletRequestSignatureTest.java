package com.magneton.service.core.signuature;

import com.magneton.service.core.signature.DefaultHttpServletRequestSignature;
import com.magneton.service.core.signature.Signature;
import org.apache.catalina.connector.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/6/14
 */
public class DefaultHttpServletRequestSignatureTest {

    public Signature signature;

    @Before
    public void before() {
        signature = new DefaultHttpServletRequestSignature.Builder()
                .required("sign", "t")
                .time("t")
                .timeOut(3_000)
                .timeOutExclude(10_000)
                .timeOutExcludePath("/abc")
                .log(true)
                .securityKey("test")
                .build();

    }

    @Test
    public void testValidate() {
        //非可以过timeOut的请求
        HttpServletRequest request = this.createRequest(2599);
        boolean validate = signature.validate(request);
        Assert.assertTrue(validate);
        request = this.createRequest(3100);
        Assert.assertFalse(signature.validate(request));
        request = this.createRequest(3100, "/abc");
        Assert.assertTrue(signature.validate(request));
    }

    private HttpServletRequest createRequest(final long timePast) {
        return this.createRequest(timePast, "/test");
    }

    private HttpServletRequest createRequest(final long timePast, final String path) {
        return new Request(null) {
            @Override
            public String getRequestURI() {
                return path;
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                Map map = new HashMap<>();
                long t = System.currentTimeMillis() - timePast;
                map.put("t", new String[]{t + ""});
                String sign = signature.sign(map);
                map.put("sign", new String[]{sign});
                return map;
            }
        };
    }
}
