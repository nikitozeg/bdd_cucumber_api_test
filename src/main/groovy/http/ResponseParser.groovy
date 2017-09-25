package http

import groovy.json.JsonSlurper

class ResponseParser {

    def static List<Map<String, String>> parseResponseGET(String response) {
        JsonSlurper jsonSlurper = new JsonSlurper();
        def list=new ArrayList();
        def jsonObj = jsonSlurper.parseText(response);
        list.add(jsonObj);
        return list
    }
    def static List<Map<String, String>> parseResponse(String response) {
        JsonSlurper jsonSlurper = new JsonSlurper();
        def jsonObj = jsonSlurper.parseText(response);
//        total_count = jsonObj.system_info.total_count
       // if ((jsonObj.payload.job_id)&&(jsonObj.payload.size()==1))
      //      return (jsonObj.payload).collect()
        def list = new ArrayList();
        list.add(jsonObj)
        return list

    }
}
