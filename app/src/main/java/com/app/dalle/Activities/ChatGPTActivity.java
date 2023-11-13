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

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dalle.Adapters.QueryAdapter;
import com.app.dalle.Models.QueryModel;
import com.app.dalle.databinding.ActivityChatGptactivityBinding;

import java.util.ArrayList;

public class ChatGPTActivity extends AppCompatActivity {
    ActivityChatGptactivityBinding binding;
    ArrayList<QueryModel> list;
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
                        inputMethodManager.toggleSoftInputFromWindow(
                                binding.rootLayout.getApplicationWindowToken(),
                                InputMethodManager.SHOW_FORCED, 0);
                    }
                    return true;
                }
                if (event.getRawX()>=textLocation[0]+binding.editQuery.getWidth()-binding.editQuery.getTotalPaddingRight()){
                    String query=binding.editQuery.getText().toString().trim();
                    if (!query.isEmpty()) {
                        addToRv(query,"user");
                        binding.editQuery.setText("");
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