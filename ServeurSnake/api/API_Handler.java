package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

public class API_Handler {

    String sessionToken;
    String username;

    public API_Handler(String email, String password) {
        try {
            URL url = new URL("http://localhost:8080/WebSnake/API/InitSession");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            } catch (ConnectException e) {
                System.out.println("WebSnake server is not running");
            }
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = in.readLine();
                in.close();

                JSONObject json = new JSONObject(response);
                this.sessionToken = json.getString("sessionToken");
                this.username = json.getString("username");
            } else {
                System.out.println("Failed to initiate session");
            }
        } catch (Exception e) {
            System.out.println("Failed to initiate session");
        }
    }

    public void killSession() {
        try {
            URL url = new URL("http://localhost:8080/WebSnake/API/KillSession");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"sessionToken\":\"" + this.sessionToken + "\",\"username\":\"" + this.username
                    + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Failed to kill session");
            }
        } catch (Exception e) {
            System.out.println("Failed to kill session");
            e.printStackTrace();
        }

    }

    public Map<String, Integer> getScores() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("best", -1);
        map.put("total", -1);
        map.put("PlayedGames", -1);

        try {
            URL url = new URL("http://localhost:8080/WebSnake/API/GetScore");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"sessionToken\":\"" + this.sessionToken + "\",\"username\":\"" + this.username
                    + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = in.readLine();
                in.close();
                JSONObject json = new JSONObject(response);
                JSONArray scores = json.getJSONArray("scores");
                map.put("best", scores.getJSONObject(0).getInt("Best"));
                map.put("total", scores.getJSONObject(1).getInt("Total"));
                map.put("PlayedGames", scores.getJSONObject(2).getInt("PlayedGames"));
            } else {
                System.out.println("Failed to get scores");
            }
        } catch (Exception e) {
            System.out.println("Failed to get scores");
            e.printStackTrace();
        }

        return map;
    }

    public void updateScore(int score) {
        try {
            URL url = new URL("http://localhost:8080/WebSnake/API/UpdateScore");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"sessionToken\":\"" + this.sessionToken + "\",\"username\":\"" + this.username
                    + "\",\"score\":" + score + "}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Failed to update score");
            } else {
                System.out.println("Score updated");
            }
        } catch (Exception e) {
            System.out.println("Failed to update score");
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return this.username;
    }
}
