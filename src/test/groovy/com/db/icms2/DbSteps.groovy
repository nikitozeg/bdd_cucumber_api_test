package com.db.icms2

import cucumber.api.DataTable

import helpers.DatabaseHelper
import helpers.ResponseHelper
import utils.DataTableUtils


this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)


And(~'^DB record in (InvoicePositions|TPurseTransactions|LimitRecords|TExternalTransactions) table for the record #(\\d+)') { String SQLTableName, Integer index, DataTable table ->
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