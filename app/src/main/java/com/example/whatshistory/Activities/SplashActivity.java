package com.example.whatshistory.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.whatshistory.R;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                PermissionX.init(SplashActivity.this).permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_SMS)
                        .onExplainRequestReason(new ExplainReasonCallback() {
                            @Override
                            public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel");
                            }
                        }).onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            Intent i = new Intent(SplashActivity.this, DashboardActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SplashActivity.this, "We cant proceed without permissions!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }, 1000);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(SplashActivity.this, DashboardActivity.class);
//                startActivity(i);
//                finish();
//            }
//        }, 4000);
    }
}