package de.uniaugsburg.app.ui.add;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    public AddViewModel() {
        mText = new MutableLiveData<>();
    }

    public void changeValue(String foodType, String foodName) {
        OkHttpClient client = new OkHttpClient();

        String url = "";
        if(foodType == "recipe")  {
            url = "https://api.spoonacular.com/recipes/complexSearch";
        } else {
            url = "https://api.spoonacular.com/food/ingredients/search";
        }

        Request request = new Request.Builder()
                .url("https://api.spoonacular.com/food/ingredients/search?apiKey=6cbbb8f2f6184dbb95ae5641d1dce7e4&query=apple")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mText.postValue("failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.body() != null) {
                    Log.d("url", request.toString());
                    Log.d("call", call.toString());
                    Log.d("response", response.body().string());
                    mText.postValue(response.body().string());
                }
                mText.postValue("empty");
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}