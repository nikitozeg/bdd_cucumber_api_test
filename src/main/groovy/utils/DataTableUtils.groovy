package utils

import cucumber.api.DataTable
import cucumber.runtime.table.TableDiffException

/**
 * Created by ivannik on 12/6/2016.
 */
class DataTableUtils {

    static def diff(DataTable table, List<Map<String, String>> receivedObjects) throws TableDiffException {
        try {
            table.unorderedDiff(prepareDataForDiff(table, receivedObjects))
        } catch (TableDiffException e) {
            assert false: e
        }
    }


    private static List<?> prepareDataForDiff(DataTable table, List<Map<String, String>> receivedObjects) {
        def headers = table.raw().first()
        [headers] + receivedObjects.collect { toDataTableRow(headers, it) }
    }

    static def toDataTableRow(headers, Map<String, String> object) {
        headers.collect {
            def value = object[it]
            switch (it) {
                case "payments": return value.details[0].toString(); break;
                case "to": return value.provider.toString(); break;
                case "from": return value.provider.toString(); break;
                default: return value == null ? "null" : value.toString()
            }
        }
    }

    static def checkDetails(DataTable table, Map<String, String> receivedData) {
        def headers = table.raw().first()
        assert headers.size().equals(receivedData.size()): "Received data should contain: " + headers + ", actual: " + receivedData.keySet()
        headers.each { String header ->
            switch (header) {
                case "graph": assert !(receivedData.get(header).isEmpty()); break;
                case "percent": break;
                default: assert table.asMaps(String, String)[0].get(header).equals(receivedData.get(header).toString()): header + " is incorrect, actual value: " + receivedData.get(header) + ", should be " + table.asMaps(String, String)[0].get(header)
            }
        }
    }

    static def checkPercent(Map<String, String> receivedData) {
        def ints = 0..100
        assert (ints.contains(receivedData.get("percent")));
    }

    /**
     * Filters DataTable by given fields and rows count
     *
     * Example: DataTable
     * | field1 | field 2 | field3 |
     * | 1      | 2       | 3      |
     * | 11     | 22      | 33     |
     * | 111    | 222     | 333    |
     *
     * Can be filtered by filterDataTable(table, ['field1','field3'], 2)
     * | field1 | field3 |
     * | 1      | 3      |
     * | 11     | 33     |
     * @param source table
     * @param fields to filter by
     * @param rowCount rows in filtered table
     * @return filtered table
     */
    static DataTable filterDataTable(DataTable table, Collection<String> fields, int rowCount = -1) {

        List lists = table.raw()
        if (rowCount > 0) {
            lists = lists[0..<rowCount + 1]
        }
        def newRawList = lists.collect { [] }

        List firstRaw = table.topCells()
        fields.each { String field ->
            int indexColumn = firstRaw.indexOf(field)
            if (indexColumn >= 0) {
                newRawList.eachWithIndex { List raw, int indexRaw ->
                    raw << lists[indexRaw][indexColumn]
                }
            }
        }

        return newRawList.first().size() > 0 ? DataTable.create(newRawList) : DataTable.create([firstRaw])

    }

    static DataTable replaceValueInColumn(DataTable table, String columnName, List newValue) {
        def List<Map<String, String>> list = []
        def map = [:]
        table.asMaps(String, String).eachWithIndex { it, int indexRow ->
            it.each { it2 ->
                if (it2.key.toString().equals(columnName)) {
                    map.put(it2.key, newValue[indexRow].toString())
                } else {
                    map.put(it2.key, it2.value)
                }
            }
            list.add(map)
            map=[:]
        }
        def newtable = DataTable.create(list)
        newtable
    }
}
