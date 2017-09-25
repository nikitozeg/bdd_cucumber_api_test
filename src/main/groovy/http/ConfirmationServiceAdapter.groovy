package http

import configuration.Configuration

/**
 * Created by ivannik on 12/06/2016.
 */

@Singleton(strict = false)

class ConfirmationServiceAdapter extends HttpsServiceAdapter {

    private ConfirmationServiceAdapter() {
        super((String) Configuration.getConf().base)
    }

    def List<Map<String, String>> getJsonForPaymentParameters2(String servicePath, String jsonContent) {
        return callJsonServiceAsPUT(servicePath, jsonContent, new ResponseParser())
    }


}
