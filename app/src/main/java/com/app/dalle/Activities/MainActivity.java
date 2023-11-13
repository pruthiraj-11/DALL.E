package com.app.dalle.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.dalle.R;
import com.app.dalle.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    String imageURL;
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

        binding.saveimagebtn.setOnClickListener(view -> {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                requestPermissions();
            } else {
                Picasso.get().load(imageURL).into(new Target() {
                                                      @Override
                                                      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                          try {
                                                              String root = Environment.getExternalStorageDirectory().toString();
                                                              File myDir = new File(root + "/DALL.E 2");
                                                              if (!myDir.exists()) {
                                                                  myDir.mkdirs();
                                                              }
                                                              String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis())+".jpg";
                                                              myDir = new File(myDir, name);
                                                              FileOutputStream fileOutputStream = new FileOutputStream(myDir);
                                                              bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                                                              fileOutputStream.flush();
                                                              fileOutputStream.close();
                                                          } catch(Exception e){
                                                              Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                                          }
                                                      }
                                                      @Override
                                                      public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                                          Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                                      }
                                                      @Override
                                                      public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                      }
                                                  }
                        );
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(MainActivity.this, ServiceActivity.class));
                finish();
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
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    JSONObject jsonObject=new JSONObject(Objects.requireNonNull(response.body()).toString());
                    imageURL=jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImage(imageURL);
                    binding.progressBar.setVisibility(View.GONE);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadImage(String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(url).placeholder(R.drawable.ic_cloud).into(binding.outputView);
                binding.saveimagebtn.setEnabled(true);
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

    private void requestPermissions() {
        Dexter.withContext(getApplicationContext())
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "All the permissions are granted", Toast.LENGTH_SHORT).show();
                        }
                        else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                            showSettingsDialog();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread().check();
    }
}