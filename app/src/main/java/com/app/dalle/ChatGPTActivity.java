package com.app.dalle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.app.dalle.databinding.ActivityChatGptactivityBinding;

public class ChatGPTActivity extends AppCompatActivity {
    ActivityChatGptactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatGptactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}