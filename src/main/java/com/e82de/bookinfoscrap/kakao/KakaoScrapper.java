package com.e82de.bookinfoscrap.kakao;

import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class KakaoScrapper {

    static final String BASE_URL = "https://dapi.kakao.com/v3/search/book";
    static final String KEY = "";
    static final String EQUAL = "=";

    public static void main(String[] args) throws IOException {
        String query = "스프링 부트";

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);
        urlBuilder.append("?" + encode("target", UTF_8) + EQUAL + "title");
        urlBuilder.append("&" + encode("query", UTF_8) + EQUAL + encode(query, UTF_8));

        URL url = new URL(urlBuilder.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Authorization", "KakaoAK " + KEY);

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

        System.out.println("url : " + urlBuilder.toString());
        System.out.println(sb.toString());
    }

}