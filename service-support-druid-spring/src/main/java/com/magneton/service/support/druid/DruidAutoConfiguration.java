package com.magneton.service.support.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@Configuration
@Import({
        DruidProperties.class
})
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class DruidAutoConfiguration implements PriorityOrdered {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(DruidAutoConfiguration.class);

    public DruidAutoConfiguration() {
    }

    @Autowired
    private DruidProperties config;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", config.getStatName());
        reg.addInitParameter("loginPassword", config.getStatPassword());
        reg.addInitParameter("logSlowSql", config.getLogSlowSql());
        LOGGER.info("druid>ServletRegistrationBean. user:{}, pwd:{}",
                config.getStatName(), config.getStatPassword());
        return reg;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions",
                "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(config.getUrl());
        datasource.setUsername(config.getUsername());
        datasource.setPassword(config.getPassword());
        datasource.setDriverClassName(config.getDriverClassName());
        datasource.setInitialSize(config.getInitialSize());
        datasource.setMinIdle(config.getMinIdle());
        datasource.setMaxActive(config.getMaxActive());
        datasource.setMaxWait(config.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(config.getValidationQuery());
        datasource.setTestWhileIdle(config.isTestWhileIdle());
        datasource.setTestOnBorrow(config.isTestOnBorrow());
        datasource.setTestOnReturn(config.isTestOnReturn());
        try {
            datasource.setFilters(config.getFilters());
        } catch (Exception e) {
            throw new RuntimeException("filters error");
        }
        LOGGER.info("druid>DataSource. init:{}, maxActive:{}", config.getInitialSize(), config.getMaxActive());
        return datasource;
    }

}
