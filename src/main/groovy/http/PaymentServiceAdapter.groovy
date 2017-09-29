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


    def static getJsonPaymentFromTo(String pattern, DataTable table) {
        def list = []
        def listB = []
        def requestPattern = new BaseRequestPattern<PaymentFromTo>()
        def requestPattern2 = new BaseRequestPattern<PaymentDetails>()

        table.asMaps(String, String).eachWithIndex { it, Integer index ->
            switch (pattern) {
                case "null": list.add(["from": requestPattern.nullPattern(createPaymentFrom(it)),
                                       "to"  : requestPattern.nullPattern(createPaymentTo(it))].plus(requestPattern2.nullPattern(createPaymentDetails(it))));
                    break;
                case "empty": list.add(["from": requestPattern.emptyPattern(createPaymentFrom(it)),
                                        "to"  : requestPattern.emptyPattern(createPaymentTo(it))].plus(requestPattern2.nullPattern(createPaymentDetails(it))));

                    break;
                case "absent": list.add(["from": requestPattern.absentPattern(createPaymentFrom(it)),
                                         "to"  : requestPattern.absentPattern(createPaymentTo(it))].plus(requestPattern2.absentPattern(createPaymentDetails(it))));
                    break;
            }
        }


        new JsonBuilder(list).getContent()
    }

    def
    static PaymentDetails createPaymentDetails(Map<String, String> tableRow) {
        //  def fields = table.asMaps(String, String)[0]
        PaymentDetails.Builder builder = PaymentDetails.builder()
        builder.setdetails(tableRow.get("details"))
                .setexternalid(tableRow.get("externalId"))

        builder.build()
    }


    def static getJsonPaymentDetailsExtId(String pattern, DataTable table) {

    }

    def
    static PaymentFromTo createPaymentFrom(Map<String, String> tableRow) {
        PaymentFromTo.Builder builder = PaymentFromTo.builder()
        builder.setProvider(tableRow.get("fromProvider"))
                .setIdentity(tableRow.get("fromIdentity"))
                .setAmount(tableRow.get("fromAmount"))
                .setCurrency(tableRow.get("fromCurrency"))

        builder.build()
    }

    def
    static PaymentFromTo createPaymentTo(Map<String, String> tableRow) {
        PaymentFromTo.Builder builder = PaymentFromTo.builder()
        builder.setProvider(tableRow.get("toProvider"))
                .setIdentity(tableRow.get("toIdentity"))
                .setAmount(tableRow.get("toAmount"))
                .setCurrency(tableRow.get("toCurrency"))

        builder.build()
    }

}
