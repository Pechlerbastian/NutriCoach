package de.uniaugsburg.app.ui.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CameraViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is camera fragment");
    }

    public void changeValue() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/todos/1")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mText.postValue("failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mText.postValue("beans");
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}