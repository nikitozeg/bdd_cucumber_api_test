package iqoption

import cucumber.api.DataTable

import helpers.DatabaseHelper
import helpers.ResponseHelper
import utils.DataTableUtils


this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)


And(~'^DB record in (InvoicePositions|TPurseTransactions|LimitRecords|TExternalTransactions) table for the record #(\\d+)') { String SQLTableName, Integer index, DataTable table ->
    //paymentIDs = ["6044e8fd-71bc-4c8a-80e5-074b1b2d2a5c", "f89f2376-1f18-4bce-9f18-e2debbca9a98"]
    def receivedObjects
    switch (SQLTableName) {
        case "InvoicePositions":
            receivedObjects = DatabaseHelper.getConfirmationCode(paymentIDs[index - 1])
            break;
        case "TPurseTransactions":
            receivedObjects = DatabaseHelper.getTPurseTransactions(paymentIDs[index - 1])
            break;
        case "LimitRecords":
            receivedObjects = DatabaseHelper.getLimitRecord(paymentIDs[index - 1])
            break;
        case "TExternalTransactions":
            receivedObjects = DatabaseHelper.getExternalTransactions(paymentIDs[index - 1])
            break;
        default: throw new IllegalStateException("No any case-branch for " + SQLTableName)

    }
    DataTableUtils.diff(table, receivedObjects)
    ResponseHelper.compareAttributes(table, receivedObjects[0])


}