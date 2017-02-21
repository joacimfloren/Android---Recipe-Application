package MurvaTools;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Helpers.ApiHelper;

/**
 * Created by rikardolsson on 2016-11-30.
 */

public class Connector {
    private static ArrayList<Integer> successCodes = new ArrayList<Integer>() {{
        add(200);
        add(201);
        add(202);
        add(203);
        add(204);
        add(205);
    }};

    public static HttpURLConnection getDeleteConnector(URL url) throws  IOException {
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);

        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

        return connection;
    }

    public static HttpURLConnection getPostConnector(URL url, String json) throws IOException {
        HttpURLConnection connection = null;

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);

        outWriter.write(json);
        outWriter.flush();
        outWriter.close();
        outputStream.close();

        return connection;
    }

    public static HttpURLConnection getPatchConnector(URL url, String json) throws IOException {
        HttpURLConnection connection = null;

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);

        outWriter.write(json);
        outWriter.flush();
        outWriter.close();
        outputStream.close();

        return connection;
    }

    public static HttpURLConnection getPutImageConnector(URL url, InputStream imgStream) throws IOException {
        HttpURLConnection connection = null;

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
        connection.setRequestProperty("Content-Type", " multipart/form-data;boundary=SOME_BOUNDARY123JKJKJHHJJH");

        String bodyTop = "--SOME_BOUNDARY123JKJKJHHJJH\r\n" +
                "Content-Disposition: form-data; name=\"image\"; filename=\"image\"\r\n" +
                "Content-Type: image/jpeg\r\n" +
                "\r\n";

        String bodyBot = "\r\n" +
                "--SOME_BOUNDARY123JKJKJHHJJH--";

        OutputStream outputStream = connection.getOutputStream();

        outputStream.write(bodyTop.getBytes());
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = imgStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.write(bodyBot.getBytes());

        outputStream.flush();
        outputStream.close();

        return connection;
    }

    public static String getError(HttpURLConnection connection) throws IOException {
        int statusCode = connection.getResponseCode();

        StringBuilder sb = new StringBuilder();
        String line;
        String error = null;

        if (!Connector.successCodes.contains(Integer.valueOf(statusCode))) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();

            String s = sb.toString();

            if (s.equals("")) {
                error = ApiHelper.getError(Integer.toString(statusCode));
            } else {
                error = ApiHelper.getFirstErrorcode(s);
            }
        }

        return error;
    }
}
