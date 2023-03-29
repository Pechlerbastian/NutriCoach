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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonParser {


    public static Map<String, List<Integer>> parseJson(Context context) {
        StringBuilder jsonString = new StringBuilder();
        try {
            File file = new File(context.getFilesDir(), "items.json");
            FileInputStream inputStream = new FileInputStream(file);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((inputStream)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        // Parse the JSON string
        Map<String, List<Integer>> itemKcalMap = null;
        try {
            JSONObject json = new JSONObject(jsonString.toString());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemKcalMap;
    }

    public static Map<String, List<Integer>> parseJsonFromAsset(Context context) {
        StringBuilder jsonString = new StringBuilder();
        try {
            // TODO remove and use the internal storage! Assets are read only
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("list_values.json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((inputStream)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        // Parse the JSON string
        Map<String, List<Integer>> itemKcalMap = null;
        try {
            JSONObject json = new JSONObject(jsonString.toString());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemKcalMap;
    }

    public static void writeJson(Map<String, List<Integer>> itemKcalMap, Context context)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String key : itemKcalMap.keySet()) {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("kcal", Objects.requireNonNull(itemKcalMap.get(key)).get(0));
            jsonObject.put(key, jsonObject2);
        }

        String jsonString = jsonObject.toString();

        try {
            File file = new File(context.getFilesDir(), "items.json");
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
