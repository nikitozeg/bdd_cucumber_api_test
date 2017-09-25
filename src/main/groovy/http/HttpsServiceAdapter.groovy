package http

import configuration.Configuration
import groovy.util.logging.Slf4j
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils


@Slf4j
public class HttpsServiceAdapter {

    private final String url;
    private CloseableHttpClient httpclient
    private BasicCookieStore cookieStore
    String fullTime;

    public HttpsServiceAdapter(String url) {
        this.url = url;
    }


    public void connect() {
        log.info("Connecting to url: " + url);

        if (httpclient != null) {
            throw new Exception("Client is already connected. Connection may not be closed correctly or the instance is shared for different users.")
        }

        cookieStore = new BasicCookieStore();
        httpclient = new HttpClients().createDefault();
    }

    public void disconnect() {
        if (httpclient != null) {
            httpclient.close();
            httpclient = null;
        }
        if (cookieStore != null) {
            cookieStore.clear();
        }
    }


    public List<Map<String, String>> callJsonServiceAsGET(String servicePath, String parameter,
                                                          final ResponseParser responseParser) {

        HttpGet httpGet = new HttpGet(url + servicePath + parameter);
        httpGet.addHeader("Content-Type", "application/json;charset=UTF-8")
        httpGet.addHeader("Authorization", "Bearer " + System.getProperty("token"))

        // Create a custom response handler
        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            def handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    def responseString = EntityUtils.toString(entity)
                    return entity != null ? responseParser.parseResponseGET(responseString) : null;
                } else {
                    HttpEntity entity = response.getEntity();
                    EntityUtils.consume(entity);
                    throw new ClientProtocolException("Unexpected response status: " + response.getStatusLine());
                }
            }

        };

        try {
            return ((List<Map<String, String>>) ((httpclient).execute(httpGet, responseHandler)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Map<String, String>> callJsonServiceAsPOST(String servicePath, String requestBody,
                                                           final ResponseParser responseParser) {

        String processingTime
        String request

        HttpPost httpPost = new HttpPost(url + servicePath);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8")
        httpPost.setHeader("Authorization", "Bearer " + System.getProperty("token"))
        httpPost.setEntity(new StringEntity(requestBody))
        log.info("URL: " + url + servicePath);
        log.info("Executing request " + requestBody);
        // log.info("Executing request " + httpPost.getRequestLine());

        // Create a custom response handler
        ResponseHandler responseHandler = new ResponseHandler() {

            @Override
            def handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || 422) {
                    HttpEntity entity = response.getEntity();
                    def responseString = EntityUtils.toString(entity)
                    log.debug("Json result: " + responseString);
                    if (entity != null) {
                        def obj = responseParser.parseResponse(responseString);
                        return obj
                    } else {
                        processingTime = "N/A" + "|"
                        null;
                    }
                } else {
                    HttpEntity entity = response.getEntity();
                    EntityUtils.consume(entity);
                    processingTime = "N/A" + "|"
                    fullTime = "N/A" + "|"
                    request = requestBody + "\r\n"
                    throw new ClientProtocolException("Unexpected response status: " + response.getStatusLine());
                }
            }

        };

        long start = System.currentTimeMillis();
        def execute = httpclient.execute(httpPost, responseHandler)
        fullTime = System.currentTimeMillis() - start;

        request = requestBody + "\r\n"
        return execute;
    }

    public List<Map<String, String>> callJsonServiceAsPUT(String servicePath, String requestBody,
                                                          final ResponseParser responseParser) {

        String processingTime
        String request

        HttpPut httpPut = new HttpPut(url + servicePath);
        httpPut.setHeader("Content-Type", "application/json;charset=UTF-8")
        httpPut.setHeader("Authorization", "Bearer " + System.getProperty("token"))
        httpPut.setEntity(new StringEntity(requestBody))
        log.info("URL: " + url + servicePath);
        log.info("Executing request " + requestBody);
        // log.info("Executing request " + httpPost.getRequestLine());

        // Create a custom response handler
        ResponseHandler responseHandler = new ResponseHandler() {

            @Override
            def handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || 422) {
                    HttpEntity entity = response.getEntity();
                    def responseString = EntityUtils.toString(entity)
                    log.debug("Json result: " + responseString);
                    if (entity != null) {
                        def obj = responseParser.parseResponse(responseString);
                        return obj
                    } else {
                        processingTime = "N/A" + "|"
                        null;
                    }
                } else {
                    HttpEntity entity = response.getEntity();
                    EntityUtils.consume(entity);
                    processingTime = "N/A" + "|"
                    fullTime = "N/A" + "|"
                    request = requestBody + "\r\n"
                    throw new ClientProtocolException("Unexpected response status: " + response.getStatusLine());
                }
            }

        };

        long start = System.currentTimeMillis();
        def execute = httpclient.execute(httpPut, responseHandler)
        fullTime = System.currentTimeMillis() - start;

        request = requestBody + "\r\n"
        return execute;
    }

    public List<Map<String, String>> callServiceAsPOSTBasic(String userName, String servicePath, String requestBody,
                                                            final ResponseParser responseParser) {
        String request

        HttpPost httpPost = new HttpPost(url + servicePath);
        httpPost.setHeader("Authorization", Configuration.getConf().auth)

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", userName));
        params.add(new BasicNameValuePair("password", Configuration.getConf().pwd));
        params.add(new BasicNameValuePair("grant_type", "password_otp"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        log.info("URL: " + url + servicePath);
        log.info("Executing request " + requestBody);
        // log.info("Executing request " + httpPost.getRequestLine());
        // Create a custom response handler
        ResponseHandler responseHandler = new ResponseHandler() {

            @Override
            def handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    def responseString = EntityUtils.toString(entity)
                    log.debug("Json result: " + responseString);
                    if (entity != null) {
                        def obj = responseParser.parseResponse(responseString);
                        return obj
                    } else {
                        processingTime = "N/A" + "|"
                        null;
                    }
                } else {
                    HttpEntity entity = response.getEntity();
                    EntityUtils.consume(entity);
                    request = requestBody + "\r\n"
                    throw new ClientProtocolException("Unexpected response status: " + response.getStatusLine());
                }
            }

        };

        def execute = httpclient.execute(httpPost, responseHandler)
        request = requestBody + "\r\n"
        return execute;
    }


}
