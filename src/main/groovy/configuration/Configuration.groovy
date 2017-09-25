package configuration

import groovy.util.logging.Slf4j

@Singleton(strict = false)
@Slf4j
final class Configuration {

    public static String env = "iqoption"
    private final ConfigObject config

    private Configuration() {
        config = loadDefaultConfiguration()
    }

    private static ConfigObject loadDefaultConfiguration() {
        def defaultConfigUrl =  new URL(
                null,
                "classpath:environments.conf.groovy",
                new ClassPathHandler(Thread.currentThread().getContextClassLoader())
        )
        new ConfigSlurper(env).parse(defaultConfigUrl)
    }


    static ConfigObject getConf() {
        Configuration.instance.config
    }

}
