package de.uniaugsburg.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonParser {


    private static String getJsonString(Context context, String fileName, boolean fromAsset) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        InputStream inputStream;
        if(fromAsset) {
            AssetManager assetManager = context.getAssets();
            inputStream = assetManager.open(fileName);
        } else{

            File file = new File(context.getFilesDir(), fileName);

            inputStream = new FileInputStream(file);

        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((inputStream)));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            jsonString.append(line);
        }
        bufferedReader.close();
        return jsonString.toString();
    }

    public static Map<String, List<Integer>> parseJson(Context context) {
        // Parse the JSON string
        Map<String, List<Integer>> itemKcalMap = new HashMap<>();
        try {
            String jsonString = getJsonString(context, "list_values.json", false);
            JSONObject json = new JSONObject(jsonString);

            // Iterate over the JSON keys and add them to the map
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject item = json.getJSONObject(key);
                int kcal = item.getInt("kcal");

                if (!itemKcalMap.containsKey(key)) {
                    itemKcalMap.put(key, new ArrayList<>());
                    Objects.requireNonNull(itemKcalMap.get(key)).add(kcal);
                }

            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return itemKcalMap;
    }

    public static Map<String, List<Integer>> parseJsonFromAsset(Context context) {

        // Parse the JSON string
        Map<String, List<Integer>> itemKcalMap = null;
        try {
            String jsonString = getJsonString(context, "list_values.json", true);
            JSONObject json = new JSONObject(jsonString);
            itemKcalMap = new HashMap<>();

            // Iterate over the JSON keys and add them to the map
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject item = json.getJSONObject(key);
                int kcal = item.getInt("kcal");

                if (!itemKcalMap.containsKey(key)) {
                    itemKcalMap.put(key, new ArrayList<>());
                    Objects.requireNonNull(itemKcalMap.get(key)).add(kcal);
                }

            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return itemKcalMap;
    }

    public static Map<String, String> parseUserDataJsonFromAsset(Context context){
        Map<String, String> userData = null;
        try {
            String jsonString = getJsonString(context, "user.json", true);
            JSONObject json = new JSONObject(jsonString);
            userData = new HashMap<>();

            // Iterate over the JSON keys and add them to the map
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = json.getString(key);

                if (!userData.containsKey(key)) {
                    userData.put(key, value);
                }

            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return userData;
    }

    public static Map<String, String> parseUserDataJson(Context context){
        Map<String, String> userData = null;
        try {
            String jsonString = getJsonString(context, "user.json", false);
            JSONObject json = new JSONObject(jsonString);
            userData = new HashMap<>();

            // Iterate over the JSON keys and add them to the map
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = json.getString(key);

                if (!userData.containsKey(key)) {
                    userData.put(key, value);
                }

            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return userData;
    }

    public static void writeJsonCalories(Map<String, List<Integer>> itemKcalMap, Context context)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String key : itemKcalMap.keySet()) {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("kcal", Objects.requireNonNull(itemKcalMap.get(key)).get(0));
            jsonObject.put(key, jsonObject2);
        }

        String jsonString = jsonObject.toString();

        try {
            File file = new File(context.getFilesDir(), "list_values.json");
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeJsonUserData(Map<String, String> userData, Context context)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String key : userData.keySet()) {
            jsonObject.put(key, userData.get(key));
        }

        String jsonString = jsonObject.toString();

        try {
            File file = new File(context.getFilesDir(), "user.json");
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
