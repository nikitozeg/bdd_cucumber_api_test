package http

import Criteria.PaymentDetails
import Criteria.PaymentFromTo
import configuration.Configuration
import cucumber.api.DataTable
import groovy.json.JsonBuilder
import requestPatterns.BaseRequestPattern

/**
 * Created by ivannik on 12/06/2016.
 */

@Singleton(strict = false)

class PaymentServiceAdapter extends HttpsServiceAdapter {

    private PaymentServiceAdapter() {
        super((String) Configuration.getConf().v2payments)
    }

    def List<Map<String, String>> getJsonForPaymentParameters2(String jsonContent) {
        return callJsonServiceAsPOST("", jsonContent, new ResponseParser())
    }


}
