package de.uniaugsburg.app.ui.camera;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mTextCamera;

    private String resultId = "";
    private String resultName = "";

    private String resultCalories = "";

    private String type;
    private OkHttpClient client;
    private String infoUrl = "";
    //private final String apiKey = "6cbbb8f2f6184dbb95ae5641d1dce7e4";
    private final String apiKey = "d5a4c2042b5b4d9b8371479b3f81a435";
    private String appendInfo = "";


    public CameraViewModel() {
        mText = new MutableLiveData<>();
        mTextCamera = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMTextCamera(){
        return mTextCamera;
    }

    public LiveData<String> getTextCamera(){
        return mTextCamera;
    }


    public void changeValue(String foodType, String foodName) {
        client = new OkHttpClient();
        type = foodType;
        String searchUrl = "";

        if (foodType.equals("Recipe")) {
            Log.d("info", "Recipe selected");
            searchUrl = "https://api.spoonacular.com/recipes/complexSearch";
            infoUrl = "https://api.spoonacular.com/recipes";
            appendInfo = "nutritionWidget.json";
        } else {
            Log.d("info", "Ingredient selected");
            searchUrl = "https://api.spoonacular.com/food/ingredients/search";
            infoUrl = "https://api.spoonacular.com/food/ingredients";
            appendInfo = "information";
        }

        String number = "1";
        Request request = new Request.Builder()
                .url(searchUrl + "?apiKey=" + apiKey + "&number=" + number + "&query=" + foodName)
                .build();

        Log.d("request", request.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.body() == null) { return; }
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    JSONArray jArray = jsonObject.getJSONArray("results");
                    if(jArray.length() == 0) {
                        mText.postValue("No matching items found");
                        return;
                    }

                    JSONObject firstEntry = new JSONObject(jArray.get(0).toString());
                    String name;

                    if (foodType.equals("Recipe")) {
                        name = firstEntry.getString("title");
                    } else {
                        name = firstEntry.getString("name");
                    }
                    resultId = firstEntry.getString("id");
                    resultName = name;
                    Log.d("results", resultId + resultName);
                    writeInfo(resultId, resultName);
                } catch (JSONException e) {
                    mText.postValue("Call limit exceeded");
                    e.printStackTrace();
                }
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void writeInfo(String resultId, String resultName) {
        Log.d("second query", resultId + resultName);
        if (!resultId.equals("") && !resultName.equals("")) {
            Request infoRequest;
            if(type.equals("Recipe")) {
                infoRequest = new Request.Builder()
                        .url(infoUrl + "/" + resultId + "/" + appendInfo + "?apiKey=" + apiKey)
                        .build();
            } else {
                infoRequest = new Request.Builder()
                        .url(infoUrl + "/" + resultId + "/" + appendInfo + "?apiKey=" + apiKey + "&unit=g&amount=100")
                        .build();
            }

            Log.d("request", infoRequest.toString());
            client.newCall(infoRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.body() == null) { return; }
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        String calories = "0";
                        if(type.equals("Recipe")) {
                            calories = jsonObject.getString("calories");
                            resultCalories = calories.replace("k", "");
                        } else {
                            JSONObject nutrition = jsonObject.getJSONObject("nutrition");
                            JSONArray nutrients = nutrition.getJSONArray("nutrients");
                            JSONObject nutritionValue;
                            for(int i = 0; i < nutrients.length(); i++) {
                                nutritionValue = new JSONObject(nutrients.getString(i));
                                if(nutritionValue.getString("name").equals("Calories")) {
                                    resultCalories = nutritionValue.getString("amount");
                                    break;
                                }

                            }

                        }
                        String append = " kcal";
                        if(type.equals("Ingredient")) {
                            append += "/100g";
                        }
                        mText.postValue(resultName + "\n" + resultCalories + append);
                    } catch (JSONException e) {
                        mText.postValue("Service error");
                    }
                }
            });
        }
    }
}