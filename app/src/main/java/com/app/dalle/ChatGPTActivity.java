package com.app.dalle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.app.dalle.databinding.ActivityChatGptactivityBinding;

public class ChatGPTActivity extends AppCompatActivity {
    ActivityChatGptactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatGptactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ChatGPTActivity.this, ServiceActivity.class));
                finish();
            }
        });
    }
}