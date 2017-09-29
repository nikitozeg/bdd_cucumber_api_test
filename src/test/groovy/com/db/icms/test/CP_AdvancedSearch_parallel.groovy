package com.db.icms.test;

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber.class)
@CucumberOptions(
        features = ["classpath:com/db/icms"],
        format = ["pretty", "html:target/site/cucumber-pretty", "json:target/cucumber-json/cucumber.json"],
        glue = ["classpath:com/db/icms2"]

)
public class CP_AdvancedSearch_parallel {
    public CP_AdvancedSearch_parallel() {
    }
}
