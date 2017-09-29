package com.db.icms.test

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith


@RunWith(Cucumber.class)
@CucumberOptions(
        glue = ["classpath:com/db/icms2"],
        features = ["classpath:com/db/icms/"],
        format = ["pretty", "html:target/site/cucumber-pretty", "json:target/cucumber-json/cucumber.json"]

)
class ePlatformSuite {
}
