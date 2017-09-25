package iqoption


import helpers.DatabaseHelper
import helpers.ResponseHelper
import http.ConfirmationServiceAdapter
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

List<Map<String, String>> responseObject = null

def jsonBuffer = new JsonBuilder()
def token
def confirmationDigits
String confirmationCode
def confirmURL
def stateIsCompleted

PreflightServiceAdapter preflightCreate = PreflightServiceAdapter.instance
PaymentServiceAdapter paymentCreate = PaymentServiceAdapter.instance
ConfirmationServiceAdapter confirmationCreate = ConfirmationServiceAdapter.instance
PaymentStateServiceAdapter paymentState = PaymentStateServiceAdapter.instance
TokenServiceAdapter tokenObj = TokenServiceAdapter.instance


public class CustomWorld2 {
    def paymentIDs = []
    def paymentsResponseList = []

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
    paymentCreate.instance.disconnect()
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

And(~'^Confirmation code recieved$') { ->
    confirmationCode = DatabaseHelper.getConfirmationCode()

}

And(~'^Preflight Request is executed') { ->
    responseObject = PreflightServiceAdapter.instance.getJsonPreflight(jsonBuffer.toString())
    println(responseObject)
}

And(~'^Payment Request is executed') { ->
    responseObject = PaymentServiceAdapter.instance.getJsonForPaymentParameters2(jsonBuffer.toString())
    println(responseObject)
    confirmationDigits = responseObject[0].get("confirmationDigits")
    confirmURL = responseObject[0].get("confirm")

    def payments = responseObject[0].get("payments")

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


Then(~'^(Preflight|Payment) results are:$') { String opt, DataTable table ->
    DataTableUtils.diff(table, responseObject)
    responseObject.each { it ->
        ResponseHelper.compareAttributes(table, it)
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
    // jsonBuffer.content.asd= new JsonBuilder(paymentCreate.getJsonForPaymentParameters(pattern, paymentCreate.createSingleParametersCPPermission(table)))

    //jsonBuffer = new JsonBuilder(preflightCreate2.getJsondetails(pattern, preflightCreate2.createPaymentDetails(table)))

    jsonBuffer.content.payments = preflightCreate.getJsonPaymentFromTo(pattern, table)

    /*jsonBuffer.content.payments.eachWithIndex { it, int index ->
         it.to.identity = preflightCreate.getJsonPaymentCard(pattern, table, index)
    }*/

    jsonBuffer.content.payments[0].put("details", "1")
    jsonBuffer.content.payments[1].put("details", "2")

    println(jsonBuffer)
}




And(~'^Payment #(\\d+):$') { Integer position, DataTable table ->
    //  boolean flag = false;

    //{ Map<String, String> it ->
    // if (details.equals(it.details.toString())) {
    //    flag = true
    paymentsResponseList << table
    DataTableUtils.diff(table, [] << responseObject[0].get("payments")[position - 1])
    ResponseHelper.compareAttributes(table, responseObject[0].get("payments")[position - 1])
    //   }
    //   }
    //    if (!flag) throw new NotFoundException("Expected Eplatform ID=$ep_id is not found in platform_authorizations")


}

And(~'^To for details = "([^"]*)":$') { String details, DataTable table ->
    boolean flag = false;

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

    responseObject[0].get("payments").each { Map<String, String> it ->
        if (details.equals(it.details.toString())) {
            flag = true
            DataTableUtils.diff(table, [] << it.from)
            ResponseHelper.compareAttributes(table, it.from)
        }
    }
    if (!flag) throw new NotFoundException("Expected Eplatform ID=$ep_id is not found in platform_authorizations")
}








