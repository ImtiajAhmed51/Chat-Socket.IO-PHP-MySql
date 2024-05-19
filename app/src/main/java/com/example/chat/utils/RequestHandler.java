package com.example.chat.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        StringBuilder sb = new StringBuilder(4096);
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(requestURL).openConnection();
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            OutputStream outputStream = httpsURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(getPostDataString(postDataParams));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            if (httpsURLConnection.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                sb = new StringBuilder();
                while (true) {
                    String readLine = br.readLine();
                    String response = readLine;
                    if (readLine == null) {
                        break;
                    }
                    sb.append(response);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Server Connection Failed";
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}