package com.jcedar.visibook.lautech.helper;

/**
 * Created by Afolayan on 13/10/2015.
 */
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ServiceHandler {

    private static final String TAG = ServiceHandler.class.getSimpleName();
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public static String makeServiceCall(String url, int method) {
        return makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public static String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            HttpGet httpget = new HttpGet("https://host/");

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

    public String userSignIn(String email ) throws Exception {
        String jsonResult = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(AppSettings.SERVER_URL + "check_email.php?email="+email);
        List<BasicNameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("email", email));


        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response;

        //String json = new Gson().toJson(user);
        Log.d(TAG, AppSettings.SERVER_URL + "check_email.php");
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, "UTF-8");
            request.setEntity(entity);
            response = httpClient.execute(request);
            Log.d("SplashersAuth", "response " + response);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200){
                jsonResult = EntityUtils.toString(response.getEntity());
                Log.d("PowerAuth", "Authentication Result " + jsonResult);
            }else if(statusLine.getStatusCode() == 503){
                jsonResult = "503Message:Service Unavailable";
                Log.d("PowerAuth", "Authentication Result " + jsonResult);
            }
            else{
                String error = "Unfavorable response during authentication. ";
                String code =  "Code "+statusLine.getStatusCode();
                String message = "Message "+statusLine.getReasonPhrase();

                Log.d("SplashAuth response", error);
                Log.d("SplashAuth code", code);
                Log.d("SplashAuth message", message);
                Log.d("SplashAuth json result", jsonResult);
                throw new Exception(error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResult;

    }

    public String getSomething(){
        String jsonResult = "";
        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpPost httpPost = new HttpPost(AppSettings.SERVER_URL + "check_email.php");

        // Building post parameters
        // key and value pair
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("email", "afolayanseyi@gmail.com"));

        // Url Encoding the POST parameters
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
            e.printStackTrace();
        }

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost);
            jsonResult = EntityUtils.toString(response.getEntity());
            // writing response to log
            Log.d("Http Response:", response.toString());
        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }
        return jsonResult;
    }
}
