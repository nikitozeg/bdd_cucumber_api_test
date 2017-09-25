package helpers

import cucumber.api.DataTable

/**
 * Created by ivannik on 12/6/2016.
 */
class ResponseHelper {

    static def compareAttributes(DataTable table, Map<String, String> actual) {
        def expectedList = table.topCells().toList()
        expectedList.sort()
        def actualList = actual.keySet().toList().sort()
        assert expectedList == actualList: "Response Attributes are not identical"
    }
}
