package http


import configuration.Configuration

/**
 * Created by ivannik on 12/06/2016.
 */

@Singleton(strict = false)

class TokenServiceAdapter extends HttpsServiceAdapter {

    private TokenServiceAdapter() {
        super((String) Configuration.getConf().http4)
    }

    def List<Map<String, String>> getToken(String userName, String jsonContent) {
        return callServiceAsPOSTBasic(userName, "", jsonContent, new ResponseParser())
    }

}
