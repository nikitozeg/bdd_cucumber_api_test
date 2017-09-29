package http

import Criteria.CardDetails
import configuration.Configuration
import cucumber.api.DataTable
import requestPatterns.BaseRequestPattern

/**
 * Created by ivannik on 12/06/2016.
 */

@Singleton(strict = false)

class PreflightServiceAdapter extends HttpsServiceAdapter {

    private PreflightServiceAdapter() {
        super((String) Configuration.getConf().v2payments)
    }

    def List<Map<String, String>> getJsonPreflight(String jsonContent) {
        return callJsonServiceAsPOST("/preflight", jsonContent, new ResponseParser())
    }



    def
    static CardDetails createPaymentCard(Map<String, String> tableRow) {
        CardDetails.Builder builder = CardDetails.builder()
        builder.setfullPAN(tableRow.get("fullPAN"))
                .setcardHolder(tableRow.get("cardHolder"))
        builder.build()
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
