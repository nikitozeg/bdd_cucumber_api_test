package com.db.icms2

import http.TokenServiceAdapter



this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

Boolean firstExecute = true

//Run new ThreadForQuit which observe shutdown of jvm runtime
/*Before()
        {
            //AfterClass
            if (firstExecute) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        System.out.println(" CLOSE-RUN")
                        DriverManager.quitWebDriver();
                        if (System.getProperty('CI')) {
                            System.println("*************Killing Toolbar*************")
                            String batFilePath = System.getProperty("user.dir") + "\\etc\\stop_toolbar.bat"
                            Runtime.getRuntime().exec("cmd /c $batFilePath")
                        }
                    }
                })
                firstExecute = false
            }

        }*/

Before() { TokenServiceAdapter.instance.disconnect()
    TokenServiceAdapter.instance.connect()
    def tokenResponse = TokenServiceAdapter.instance.getToken("bizSanction@qa.swiftcom.uk")
    def token = tokenResponse.access_token[0]
    System.setProperty("token", token.toString());
    token
}
