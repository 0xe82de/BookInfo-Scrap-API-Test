package com.e82de.bookinfoscrap.interpark;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class InterparkScrapper {

    static final String BASE_URL = "https://book.interpark.com/api/bestSeller.api";
    static final String KEY = "";
    static final String EQUAL = "=";
    static final String OUTPUT = "json";

    /**
     * 인터파크 카테고리 아이디
     */
    static final int[] categoryIds = new int[] {
            101, 102, 103, 104, 105, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 122,
            123, 124, 125, 126, 128, 129
    };

    static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static void main(String[] args) throws IOException, ParseException {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);
        urlBuilder.append("?" + encode("key", UTF_8) + EQUAL + KEY);
        urlBuilder.append("&" + encode("output", UTF_8) + EQUAL + OUTPUT);
        urlBuilder.append("&" + encode("categoryId", UTF_8) + EQUAL);

        for (int categoryId : categoryIds) {
            URL url = new URL(urlBuilder.toString() + categoryId);

            HttpsURLConnection conn = getHttpURLConnection(url);
            int responseCode = conn.getResponseCode();

            boolean isSuccess = 200 <= responseCode && responseCode <= 300;
            String response = getResponse(conn, isSuccess);

            if (isSuccess) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject totalInfoJson = (JSONObject) parser.parse(response);
                    JSONArray bookInfoJsons = (JSONArray) totalInfoJson.get("item");

                    for (Object bookInfoJson : bookInfoJsons) {
                        InterparkBookInfo interparkBookInfo = getInterparkBookInfo((JSONObject) bookInfoJson);
                        // System.out.println(interparkBookInfo);
                    }

                    System.out.println("url : " + urlBuilder.toString() + categoryId + " -> SUCCESS!!");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("url : " + urlBuilder.toString() + categoryId + " -> FAIL!!");
                }
            } else {
                System.out.println("url : " + urlBuilder.toString() + categoryId + " -> FAIL!!");
            }
        }
    }

    private static InterparkBookInfo getInterparkBookInfo(JSONObject bookInfoJson) {
        return InterparkBookInfo.builder()
                .isbn((String) bookInfoJson.get("isbn"))
                .title((String) bookInfoJson.get("title"))
                .author((String) bookInfoJson.get("author"))
                .description((String) bookInfoJson.get("description"))
                .price((int) (long) bookInfoJson.get("priceStandard"))
                .smallImgUrl((String) bookInfoJson.get("coverSmallUrl"))
                .largeImgUrl((String) bookInfoJson.get("coverLargeUrl"))
                .categoryId(Integer.parseInt((String) bookInfoJson.get("categoryId")))
                .categoryName(((String) bookInfoJson.get("categoryName")).split(">")[1])
                .publisher((String) bookInfoJson.get("publisher"))
                .publishDate(LocalDateTime.parse((String) bookInfoJson.get("pubDate") + "111111", dateFormatter))
                .build();
    }

    private static String getResponse(HttpsURLConnection conn, boolean isSuccess) throws IOException {
        BufferedReader br;
        if (isSuccess) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        // 저장된 데이터를 라인 별로 읽어 StringBuilder 객체에 저장
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        conn.disconnect();
        br.close();

        return sb.toString();
    }

    private static HttpsURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); // 커넥션 객체 생성
        conn.setRequestMethod("GET"); // HTTP 메서드 설정
        conn.setRequestProperty("Content-type", "application/json"); // Content Type 설정
        return conn;
    }

}