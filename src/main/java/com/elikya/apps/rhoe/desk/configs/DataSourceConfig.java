/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.configs;

import com.elikya.apps.rhoe.desk.encoding.CriticalDataEncoder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:file:./pr/rhoe_db=");
        dataSourceBuilder.username("rhoe-desk");
        dataSourceBuilder.password(CriticalDataEncoder.encodeHomeDirectory());
        return dataSourceBuilder.build();
    }

}
