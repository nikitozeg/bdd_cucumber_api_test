package http

import configuration.Configuration

/**
 * Created by ivannik on 12/06/2016.
 */

@Singleton(strict = false)

class PaymentStateServiceAdapter extends HttpsServiceAdapter {

    private PaymentStateServiceAdapter() {
        super((String) Configuration.getConf().base)
    }

    def List<Map<String, String>> getJsonForPaymentState(String servicePath, String parameter) {
        return callJsonServiceAsGET(servicePath, parameter, new ResponseParser())
    }

}
