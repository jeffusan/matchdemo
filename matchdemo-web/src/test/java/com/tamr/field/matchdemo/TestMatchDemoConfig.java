package com.tamr.field.matchdemo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Created by jellin on 5/29/15.
 * exclude main boot context so we don't get
 * db connection to postgres.
 */

@SpringBootApplication
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchDemoApplication.class)},
        basePackages = "com.tamr.field"
)
public class TestMatchDemoConfig {
}
