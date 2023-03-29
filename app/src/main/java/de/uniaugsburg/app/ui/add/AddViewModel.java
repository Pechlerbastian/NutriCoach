package de.uniaugsburg.app.ui.add;

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

public class AddViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private String resultId = "";
    private String resultName = "";

    private String resultCalories = "";

    private String type;
    private OkHttpClient client;
    private String infoUrl = "";
    private final String apiKey = "6cbbb8f2f6184dbb95ae5641d1dce7e4";
    private String appendInfo = "";


    public AddViewModel() {
        mText = new MutableLiveData<>();
    }

    public String[] changeValue(String foodType, String foodName) {
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
                    throw new RuntimeException(e);
                }
            }
        });
        return new String[] {resultName, resultCalories };
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void writeInfo(String resultId, String resultName) {
        Log.d("second query", resultId + resultName);
        if (!resultId.equals("") && !resultName.equals("")) {
            Request infoRequest = new Request.Builder()
                    .url(infoUrl + "/" + resultId + "/" + appendInfo + "?apiKey=" + apiKey + "&amount=1")
                    .build();

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
                        mText.postValue(resultName + ":\n" + "Calories: " + resultCalories + " kcal");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}