package de.uniaugsburg.app.ui.add;

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
                .url(url + "?apiKey=6cbbb8f2f6184dbb95ae5641d1dce7e4&query=" + foodName)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                mText.postValue("failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mText.postValue("correct");
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}