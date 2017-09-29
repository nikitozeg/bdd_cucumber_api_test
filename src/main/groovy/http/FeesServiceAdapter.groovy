package http

import Criteria.CardDetails
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

class FeesServiceAdapter extends HttpsServiceAdapter {

    private FeesServiceAdapter() {
        super((String) Configuration.getConf().v2payments)
    }

    def List<Map<String, String>> getJsonFees(String jsonContent) {
        return callJsonServiceAsPOST("/fees", jsonContent, new ResponseParser())
    }

}
