package ro.pub.cs.sytems.eim.practicaltest02v9;



import android.util.Log;



import org.json.JSONArray;

import org.json.JSONObject;



import java.io.BufferedReader;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.URL;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;

import java.util.List;



public class AnagramWebService {



    private static final String TAG = "PT02V9_WEB";



    public static String fetchAndFilter(String word, int minLetters) {

        try {

            String safeWord = URLEncoder.encode(word, "UTF-8");

            String urlStr = "http://www.anagramica.com/all/:" + safeWord;

            Log.i(TAG, "Requesting: " + urlStr);



            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();

            conn.setRequestMethod("GET");

            conn.setConnectTimeout(8000);

            conn.setReadTimeout(8000);



            int code = conn.getResponseCode();

            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();



            String body = readAll(is);

            Log.i(TAG, "HTTP " + code + " body=" + body);



            JSONObject obj = new JSONObject(body);

            JSONArray arr = obj.getJSONArray("all");



            List<String> filtered = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {

                String an = arr.getString(i);

                if (an != null && an.length() >= minLetters) {

                    filtered.add(an);

                }

            }



            if (filtered.isEmpty()) return "(no anagrams)";

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < filtered.size(); i++) {

                if (i > 0) sb.append(" ");

                sb.append(filtered.get(i));

            }

            return sb.toString();



        } catch (Exception e) {

            Log.e(TAG, "fetchAndFilter error", e);

            return "ERROR " + e.getMessage();

        }

    }



    private static String readAll(InputStream is) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {

            sb.append(line);

        }

        return sb.toString();

    }

}