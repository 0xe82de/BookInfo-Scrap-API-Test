package com.e82de.bookinfoscrap.nl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static java.net.URLEncoder.*;
import static java.nio.charset.StandardCharsets.*;

public class NlScrapper {

    static final String BASE_URL = "https://seoji.nl.go.kr/landingPage/SearchApi.do";
    static final String KEY = "";
    static final String EQUAL = "=";
    static final String RETURN_TYPE = "json";

    public static void main(String[] args) throws IOException, ParseException {
        String pageNo = "1";
        String pageSize = "1";

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(BASE_URL);
        urlBuilder.append("?" + encode("cert_key", UTF_8) + EQUAL + KEY);
        urlBuilder.append("&" + encode("result_style", UTF_8) + EQUAL + RETURN_TYPE);
        urlBuilder.append("&" + encode("page_no", UTF_8) + EQUAL + pageNo);
        urlBuilder.append("&" + encode("page_size", UTF_8) + EQUAL + pageSize);

        URL url = new URL(urlBuilder.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        int responseCode = conn.getResponseCode();
        System.out.println("Response code: " + responseCode);

        BufferedReader br;
        if (200 <= responseCode && responseCode <= 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        conn.disconnect();
        br.close();

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(sb.toString());
        String getPageNo = (String) obj.get("PAGE_NO");
        int getTotalCount = Integer.parseInt((String) obj.get("TOTAL_COUNT"));
        JSONArray dataArray = (JSONArray) obj.get("docs");

        System.out.println("getPageNo = " + getPageNo);
        System.out.println("getTotalCount = " + getTotalCount);
        System.out.println("dataArray = " + dataArray);
    }

}