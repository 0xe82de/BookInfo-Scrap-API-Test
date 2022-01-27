package com.e82de.bookinfoscrap.naver;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class NaverScrapper {

    static final String BASE_URL = "https://openapi.naver.com/v1/search/book_adv.xml";
    static final String CLIENT_ID = "";
    static final String CLIENT_SECRET = "";
    static final String EQUAL = "=";

    public static void main(String[] args) throws IOException {
        String display = "100";
        String start = "1";
        String publisher = "위즈덤하우스";

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);
        urlBuilder.append("?" + encode("display", UTF_8) + EQUAL + encode(display, UTF_8));
        urlBuilder.append("&" + encode("start", UTF_8) + EQUAL + encode(start, UTF_8));
        urlBuilder.append("&" + encode("d_publ", UTF_8) + EQUAL + encode(publisher, UTF_8));

        URL url = new URL(urlBuilder.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
        conn.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

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
