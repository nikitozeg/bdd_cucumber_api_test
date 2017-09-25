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

class PreflightServiceAdapter extends HttpsServiceAdapter {

    private PreflightServiceAdapter() {
        super((String) Configuration.getConf().http2)
    }

    def List<Map<String, String>> getJsonPreflight(String jsonContent) {
        return callJsonServiceAsPOST("", jsonContent, new ResponseParser())
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

    def
    static PaymentDetails createPaymentDetails(DataTable table) {
        def fields = table.asMaps(String, String)[0]
        PaymentDetails.Builder builder = PaymentDetails.builder()
        builder.setdetails(fields.get("details"))
                .setexternalid(fields.get("externalid"))

        builder.build()
    }

    def
    static CardDetails createPaymentCard(Map<String, String> tableRow) {
        CardDetails.Builder builder = CardDetails.builder()
        builder.setfullPAN(tableRow.get("fullPAN"))
                .setcardHolder(tableRow.get("cardHolder"))
        builder.build()
    }

    def static getJsonPaymentFromTo(String pattern, DataTable table) {
        def list = []
        def requestPattern = new BaseRequestPattern<PaymentFromTo>()
        def requestPattern2 = new BaseRequestPattern<PaymentDetails>()
        table.asMaps(String, String).each { it ->
            switch (pattern) {
                case "null": list.add(["from": requestPattern.nullPattern(createPaymentFrom(it)),
                                       "to"  : requestPattern.nullPattern(createPaymentTo(it))]); break;
                case "empty": list.add(["from": requestPattern.emptyPattern(createPaymentFrom(it)),
                                        "to"  : requestPattern.emptyPattern(createPaymentTo(it))]); break;
                case "absent": list.add(["from": requestPattern.absentPattern(createPaymentFrom(it)),
                                         "to"  : requestPattern.absentPattern(createPaymentTo(it)),
                ]);
                    break;
            }
        }
        new JsonBuilder(list).getContent()
    }

    def static getJsonPaymentCard(String pattern, DataTable table, int index) {
        def requestPattern = new BaseRequestPattern<CardDetails>()
        switch (pattern) {
            case "null": requestPattern.nullPattern(createPaymentCard(table.asMaps(String, String)[index])); break;
            case "empty": requestPattern.emptyPattern(createPaymentCard(table.asMaps(String, String)[index])); break;
            case "absent": requestPattern.absentPattern(createPaymentCard(table.asMaps(String, String)[index])); break;
        }

    }

}
