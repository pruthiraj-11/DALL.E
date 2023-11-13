package com.app.dalle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.app.dalle.databinding.ActivityServiceBinding;

public class ServiceActivity extends AppCompatActivity {
    ActivityServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        binding.button.setOnClickListener(view -> {
            startActivity(new Intent(ServiceActivity.this, MainActivity.class));
            finish();
        });
        binding.button2.setOnClickListener(view -> {
            startActivity(new Intent(ServiceActivity.this, ChatGPTActivity.class));
            finish();
        });
    }
}