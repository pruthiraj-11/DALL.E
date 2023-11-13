package com.app.dalle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.dalle.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        binding.generatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=binding.inputview.getText().toString().trim();
                if(input.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please describe something", Toast.LENGTH_SHORT).show();
                } else {
                        generateImage(input);
//                    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    api-key
                }
            }
        });
    }

    private void generateImage(String input)  {
        binding.progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonBody=new JSONObject();
        try {
            jsonBody.put("prompt",input);
            jsonBody.put("size","256x256");
        } catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        RequestBody requestBody=RequestBody.create(jsonBody.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().toString());
                    String imageURL=jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImage(imageURL);
                    binding.progressBar.setVisibility(View.GONE);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadImage(String imageURL) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(imageURL).placeholder(R.drawable.ic_cloud).into(binding.outputView);
            }
        });
    }
}