package com.example.roofmate_mvp;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PushyAPI {
    public static ObjectMapper mapper = new ObjectMapper();

    // Insert your Secret API Key here
    public static final String SECRET_API_KEY = "3840a0c216cf1abeb2fcca2a3a51216ce9b63ccd5632aee12742ccf5d8cb81f3";

    public static void sendPush(PushyPushRequest req) {
        new SendPushTask().execute(req);
    }

    private static class SendPushTask extends AsyncTask<PushyPushRequest, Void, Void> {
        @Override
        protected Void doInBackground(PushyPushRequest... requests) {
            PushyPushRequest req = requests[0];
            HttpURLConnection urlConnection = null;
            try {
                // Create URL
                URL url = new URL("https://api.pushy.me/push?api_key=" + SECRET_API_KEY);

                // Open connection
                urlConnection = (HttpURLConnection) url.openConnection();

                // Set method to POST
                urlConnection.setRequestMethod("POST");

                // Set content type to JSON
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Convert post data to JSON
                byte[] json = mapper.writeValueAsBytes(req);

                // Enable output
                urlConnection.setDoOutput(true);

                // Send post data
                OutputStream out = urlConnection.getOutputStream();
                out.write(json);
                out.close();

                // Get response code
                int responseCode = urlConnection.getResponseCode();

                // Get response JSON as string
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                String responseJSON = responseBuilder.toString();

                // Convert JSON response into HashMap
                Map<String, Object> map = mapper.readValue(responseJSON, Map.class);

                // Got an error?
                if (map.containsKey("error")) {
                    // Log error
                    Log.e(TAG, "Pushy API Error: " + map.get("error").toString());
                    // Throw it
                    throw new Exception(map.get("error").toString());
                }
            } catch (Exception e) {
                // Log the exception
                Log.e(TAG, "Exception in sendPush: ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

    public static class PushyPushRequest {
        public Object to;
        public Object data;
        public Object notification;

        public PushyPushRequest(Object data, Object to, Object notification) {
            this.to = to;
            this.data = data;
            this.notification = notification;
        }
    }
}
