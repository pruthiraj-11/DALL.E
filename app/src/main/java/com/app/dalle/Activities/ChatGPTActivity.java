package com.app.dalle.Activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dalle.Adapters.QueryAdapter;
import com.app.dalle.Models.QueryModel;
import com.app.dalle.databinding.ActivityChatGptactivityBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTActivity extends AppCompatActivity {
    ActivityChatGptactivityBinding binding;
    ArrayList<QueryModel> list;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    QueryAdapter queryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatGptactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(binding.editQuery.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        list=new ArrayList<>();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ChatGPTActivity.this, ServiceActivity.class));
                finish();
            }
        });

        queryAdapter=new QueryAdapter(list);
        binding.chatRecyclerView.setAdapter(queryAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        binding.editQuery.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int[] textLocation = new int[2];
                binding.editQuery.getLocationOnScreen(textLocation);
                if (event.getRawX()<=textLocation[0]+binding.editQuery.getTotalPaddingLeft()) {
                    // Left drawable was tapped
                    binding.editQuery.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager!=null){
                        inputMethodManager.toggleSoftInputFromWindow(binding.rootLayout.getApplicationWindowToken(),
                                InputMethodManager.SHOW_FORCED, 0);
                    }
                    return true;
                }
                if (event.getRawX()>=textLocation[0]+binding.editQuery.getWidth()-binding.editQuery.getTotalPaddingRight()){
                    String query=binding.editQuery.getText().toString().trim();
                    if (!query.isEmpty()) {
                        addToRv(query,"user");
                        binding.editQuery.setText("");
                        generateResponse(query);
                    }
                    return true;
                }
            }
            return true;
        });

    }

    private void addToRv(String message,String sendBy){
        runOnUiThread(() -> {
            list.add(new QueryModel(message,sendBy));
            queryAdapter.notifyDataSetChanged();
            binding.chatRecyclerView.smoothScrollToPosition(queryAdapter.getItemCount());
        });
    }

    private void addResponse(String response){
        list.remove(list.size()-1);
        addToRv(response,"chatgpt");
    }

    private void generateResponse(String message){
        list.add(new QueryModel("Generating...","chatgpt"));
        JSONObject jsonBody=new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo-instruct");
            jsonBody.put("prompt",message);
            jsonBody.put("max_tokens",4000);
            jsonBody.put("temperature",0);
        } catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        RequestBody requestBody=RequestBody.create(jsonBody.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getLocalizedMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()){
                    JSONObject jsonObject=null;
                    try {
                        jsonObject=new JSONObject(Objects.requireNonNull(response.body()).string());
                        String botResponse=jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
                        addResponse(botResponse.trim());
                    } catch (Exception e){
                        Toast.makeText(ChatGPTActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    addResponse("Failed to load response due to "+response.body().toString());
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}