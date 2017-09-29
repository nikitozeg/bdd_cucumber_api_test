package com.db.icms2

import gherkin.formatter.model.DataTableRow
import helpers.ResponseHelper
import http.ConfirmationServiceAdapter
import http.FeesServiceAdapter
import http.PaymentServiceAdapter
import http.PaymentStateServiceAdapter
import http.PreflightServiceAdapter
import cucumber.api.DataTable
import groovy.json.JsonBuilder
import http.TokenServiceAdapter
import org.openqa.selenium.NotFoundException
import utils.DataTableUtils


this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)



def jsonBuffer = new JsonBuilder()
def token

PreflightServiceAdapter preflightCreate = PreflightServiceAdapter.instance
PaymentServiceAdapter paymentCreate = PaymentServiceAdapter.instance
FeesServiceAdapter feesCreate = FeesServiceAdapter.instance
ConfirmationServiceAdapter confirmationCreate = ConfirmationServiceAdapter.instance
PaymentStateServiceAdapter paymentState = PaymentStateServiceAdapter.instance
TokenServiceAdapter tokenObj = TokenServiceAdapter.instance


public class CustomWorld2 {
    def paymentIDs = []
    List<Map<String, String>> responseObject = null
    List<Map<String, String>> responseObjectPreflight = null
    List<Map<String, String>> responseObjectPayment = null
    List<Map<String, String>> responseObjectFees = null

    DataTable rootResponse
    def paymentsList = []
    def paymentsListTO = []
    def paymentsListFrom = []
    def externalId = []

    def confirmURL
    def confirmationDigits
    Boolean stateIsCompleted

    def getResult() {
        this.result
    }

    def setResult(String string) {
        this.result = string
    }
}

World {
    new CustomWorld2()
}

And(~'^teststep1$') { ->

    setResult("test1")
}


And(~'^Token service is connected$') { ->
    tokenObj.instance.connect()
}

And(~'^Token service is disconnected$') { ->
    tokenObj.instance.disconnect()
}

And(~'^Token recieved for user ([^"]*)$') { String userName ->
    def tokenResponse = TokenServiceAdapter.instance.getToken(userName, jsonBuffer.toString())
    token = tokenResponse.access_token[0]
    System.setProperty("token", token.toString());
    token
}

When(~'^Preflight is connected$') { ->
    preflightCreate.instance.connect()
}



And(~'^Preflight is disconnected$') { ->
    preflightCreate.instance.disconnect()
}

And(~'^Payment is disconnected$') { ->
    paymentCreate.instance.disconnect()
}

And(~'^Fees is disconnected$') { ->
    feesCreate.instance.disconnect()
}

And(~'^Fees is connected$') { ->
    feesCreate.instance.connect()
}

And(~'^Payment is connected$') { ->
    paymentCreate.instance.connect()
}

And(~'^Confirmation is disconnected$') { ->
    confirmationCreate.instance.disconnect()
}

And(~'^GET v2/payment/id state is disconnected$') { ->
    paymentState.instance.disconnect()
}

And(~'^GET v2/payment/id state is connected$') { ->
    paymentState.instance.connect()
}


And(~'^Confirmation is connected$') { ->
    confirmationCreate.instance.connect()
}


And(~'^Preflight Request is executed') { ->
    responseObjectPreflight = PreflightServiceAdapter.instance.getJsonPreflight(jsonBuffer.toString())
    println(responseObjectPreflight)
}

And(~'^Payment Request is executed') { ->
    responseObjectPayment = PaymentServiceAdapter.instance.getJsonForPaymentParameters2(jsonBuffer.toString())
    println(responseObjectPayment)
    confirmationDigits = responseObjectPayment[0].get("confirmationDigits")
    confirmURL = responseObjectPayment[0].get("confirm")

    def payments = responseObjectPayment[0].get("payments")

    payments.each { it ->
        paymentIDs << it.id
    }
}

And(~'^Confirmation Request is executed') { ->

    def a = "726210".toCharArray()

    def paymentPwd = []
    confirmationDigits.each { it ->
        paymentPwd << a.getAt(it - 1).toString()
    }
    paymentPwd

    responseObject = ConfirmationServiceAdapter.instance.getJsonForPaymentParameters2(confirmURL, new JsonBuilder(["confirmation": paymentPwd.join()]).toString())
    println(responseObject)
}

And(~'^Payment state is COMPLETED$') { ->

    while (!stateIsCompleted) {
        responseObject = PaymentStateServiceAdapter.instance.getJsonForPaymentState("/v2/payments/", confirmURL - "/confirmation" - "/v2/payments/")
        //  responseObject = PaymentStateServiceAdapter.instance.getJsonForPaymentState("/v2/payments/", "d33ff361-f01f-420b-8156-d5b46105813e")
        println(responseObject)
        if (responseObject[0].get("state").equals("Completed"))
            stateIsCompleted = true
        Thread.sleep(5000)
    }


}

Then(~'^Root object is:$') { DataTable table ->
    rootResponse = table
}

Then(~'^Payment object is:$') { DataTable table ->


    def newTable = DataTableUtils.replaceValueInColumn(table, "externalId", externalId)

    newTable.asMaps(String, String).eachWithIndex { it, int i ->
        paymentsList << DataTable.create([] << it)
    }
    paymentsList
}

Then(~'^From object is:$') { DataTable table ->
    table.asMaps(String, String).eachWithIndex { it, int i ->
        paymentsListFrom << DataTable.create([] << it)
    }
}

Then(~'^To object is:$') { DataTable table ->
    table.asMaps(String, String).eachWithIndex { it, int i ->
        paymentsListTO << DataTable.create([] << it)
    }
}



And(~'^Fees Request is executed') { ->
    responseObjectFees = FeesServiceAdapter.instance.getJsonFees(jsonBuffer.toString())
    println(responseObjectFees)
}

Then(~'^(Fees|Preflight) results checked$') { String opt ->
    def recievedObject

    switch (opt) {
        case "Fees": recievedObject = responseObjectFees; break;
        case "Preflight": recievedObject = responseObjectPreflight; break;
        default: throw new IllegalStateException("No any case-branch for " + opt)
    }

    recievedObject.each { it ->
        ResponseHelper.compareAttributes(rootResponse, it)
    }
    DataTableUtils.diff(rootResponse, recievedObject)


    recievedObject[0].get("payments").eachWithIndex { Map<String, String> it, Integer index ->
        DataTableUtils.diff(paymentsList[index], [] << recievedObject[0].get("payments")[index])
        ResponseHelper.compareAttributes(paymentsList[index], recievedObject[0].get("payments")[index])
    }

    recievedObject[0].get("payments").eachWithIndex { Map<String, String> it, Integer index ->
        DataTableUtils.diff(paymentsListFrom[index], [] << it.from)
        ResponseHelper.compareAttributes(paymentsListFrom[index], it.from)
    }

    recievedObject[0].get("payments").eachWithIndex { Map<String, String> it, Integer index ->
        DataTableUtils.diff(paymentsListTO[index], [] << it.to)
        ResponseHelper.compareAttributes(paymentsListTO[index], it.to)
    }

}

Then(~'^Payment results checked:$') { DataTable table ->
DataTableRow
  /*  def tableFiltered = DataTableUtils.replaceValueInColumn(table, "message", [])
    def tableFiltered2 = DataTableUtils.replaceValueInColumn(tableFiltered, "confirm", [])
    def tableFiltered3 = DataTableUtils.replaceValueInColumn(tableFiltered2, "confirmationDigits", [])

    def tableFilteredd = DataTableUtils.replaceValueInColumn((DataTable.create(responseObjectPayment)), "message", [])
    def tableFilteredd2 = DataTableUtils.replaceValueInColumn(tableFilteredd, "confirm", [])
    def tableFilteredd3 = DataTableUtils.replaceValueInColumn(tableFilteredd2, "confirmationDigits", [])
*/
   //  def tableFiltered = DataTableUtils.replaceValueInColumn(table, table.topCells() - "message" - "confirm" - "confirmationDigits")
     def tableFiltered2 = DataTableUtils.filterDataTable((DataTable.create(responseObjectPayment)), table.topCells() - "message" - "confirm" - "confirmationDigits")

//    def newrecTable = DataTableUtils.filterDataTable((DataTable.create(responseObjectPayment)), tableFiltered.topCells())
    //  (List<Map<String, String>>) newrecTable.asMaps(String, String)

    DataTableUtils.diff(tableFiltered3, tableFilteredd3.asMaps(String, String))
    responseObjectPayment.each { it ->
        ResponseHelper.compareAttributes(table, it)
    }

    tableFilteredd3[0].get("payments").eachWithIndex { Map<String, String> it, Integer index ->
        DataTableUtils.diff(paymentsList[index], [] << tableFilteredd3[0].get("payments")[index])
        ResponseHelper.compareAttributes(paymentsList[index], tableFilteredd3[0].get("payments")[index])
    }

    tableFilteredd3[0].get("payments").eachWithIndex { Map<String, String> it, Integer index ->
        DataTableUtils.diff(paymentsListFrom[index], [] << it.from)
        ResponseHelper.compareAttributes(paymentsListFrom[index], it.from)
    }

    tableFilteredd3[0].get("payments").eachWithIndex { Map<String, String> it, Integer index ->
        DataTableUtils.diff(paymentsListTO[index], [] << it.to)
        ResponseHelper.compareAttributes(paymentsListTO[index], it.to)
    }

}

And(~'^Add card identity to body request for payment service via "(null|empty|absent)" pattern by:$') { String pattern, DataTable table ->
    jsonBuffer.content.payments.eachWithIndex { it, int index ->
        if (it.to.provider.equalsIgnoreCase("card"))
            it.to.identity = preflightCreate.getJsonPaymentCard(pattern, table, index)
    }
}

And(~'^Add parameters to body request for payment service via "(null|empty|absent)" pattern by:$') { String pattern, DataTable table ->
    // def a = new JsonBuilder([]).getContent()
    jsonBuffer {}
   def a= table.transpose()    // jsonBuffer.content.asd= new JsonBuilder(paymentCreate.getJsonForPaymentParameters(pattern, paymentCreate.createSingleParametersCPPermission(table)))

    //jsonBuffer = new JsonBuilder(preflightCreate2.getJsondetails(pattern, preflightCreate2.createPaymentDetails(table)))

    table.asMaps(String, String).each { def map ->
        externalId << UUID.randomUUID().toString()
    }

    def newTable = DataTableUtils.replaceValueInColumn(table, "externalId", externalId)

    jsonBuffer.content.payments = paymentCreate.getJsonPaymentFromTo(pattern, newTable)

    /*jsonBuffer.content.payments.eachWithIndex { it, int index ->
         it.to.identity = preflightCreate.getJsonPaymentCard(pattern, table, index)
    }*/


    println(jsonBuffer)
}




And(~'^Payment #(\\d+):$') { Integer position, DataTable table ->
    //  boolean flag = false;

    //{ Map<String, String> it ->
    // if (details.equals(it.details.toString())) {
    //    flag = true

    DataTableUtils.diff(table, [] << responseObject[0].get("payments")[position - 1])
    ResponseHelper.compareAttributes(table, responseObject[0].get("payments")[position - 1])
    //   }
    //   }
    //    if (!flag) throw new NotFoundException("Expected Eplatform ID=$ep_id is not found in platform_authorizations")


}

And(~'^To for details = "([^"]*)":$') { String details, DataTable table ->
    boolean flag = false;
    paymentsListTO << table
    responseObject[0].get("payments").each { Map<String, String> it ->
        if (details.equals(it.details.toString())) {
            flag = true
            DataTableUtils.diff(table, [] << it.to)
            ResponseHelper.compareAttributes(table, it.to)
        }
    }
    if (!flag) throw new NotFoundException("Expected Eplatform ID=$ep_id is not found in platform_authorizations")
}

And(~'^From for details = "([^"]*)":$') { String details, DataTable table ->
    boolean flag = false;
    paymentsListFrom << table
    responseObject[0].get("payments").each { Map<String, String> it ->
        if (details.equals(it.details.toString())) {
            flag = true
            DataTableUtils.diff(table, [] << it.from)
            ResponseHelper.compareAttributes(table, it.from)
        }
    }
    if (!flag) throw new NotFoundException("Expected Eplatform ID=$ep_id is not found in platform_authorizations")
}








