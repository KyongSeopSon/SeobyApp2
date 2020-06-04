package com.lpointdsc.aos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lpointdsc.aos.FirebaseInterface;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseInterface ieFirebase;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // DeepLink 처리 (Manual)
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            Log.i("seobyapp", "Deep link clicked " + uri);
        }

        // 동적링크 처리 (Firebase Dynamic Link)
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            String viewName = deepLink.getQueryParameter("view");

                            if (viewName.equals("Detail")) {

                                // 앱 클릭 이벤트 트래킹
                                ieFirebase.SendFirebaseEvent("EVENT", "APP_메인", "클릭", "Detail뷰호출");
                                Toast.makeText(getApplicationContext(), "앱 Detail뷰호출 클릭 이벤트를 전송하였습니다", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
        ShowInformation();

    }

    public boolean CheckUserID() {
        EditText txtUserID = (EditText)findViewById(R.id.txtUserID);

        boolean result = ((txtUserID != null) && (!txtUserID.getText().toString().equals("")));

        if (result) {
            ieFirebase = new FirebaseInterface(this, txtUserID.getText().toString());
        }

        return result;
    }

    public void btnScreenView_Click(View v) {
        String user_id = "14900164";
        String sso_id = "abb5b57a9e87a55e6756d4f13089b28d3bae8835d6ffc8cc116fd50da506d80e1f539cbf1fde903daf5652d63a22b4b5365a359813726086745bffbabdecc06b";


        if (CheckUserID()) {
            // 화면뷰 트래킹
            ieFirebase.SendFirebaseEvent("PAGEVIEW", "APP_메인", "MainActivity", null);
            Toast.makeText(getApplicationContext(), "앱 화면뷰 페이지뷰를 전송하였습니다", Toast.LENGTH_LONG).show();
        }

    }

    public void btnEvent_Click(View v) {
        if (CheckUserID()) {
            // 앱 클릭 이벤트 트래킹
            ieFirebase.SendFirebaseEvent("EVENT", "APP_메인", "클릭", "앱이벤트");
            Toast.makeText(getApplicationContext(), "앱 이벤트 클릭 이벤트를 전송하였습니다", Toast.LENGTH_LONG).show();
        }
    }

    public void btnWebView_Click(View v) {
        if (CheckUserID()) {
            // 앱 클릭 이벤트 트래킹
            ieFirebase.SendFirebaseEvent("EVENT", "APP_메인", "클릭", "웹뷰호출");
            Toast.makeText(getApplicationContext(), "앱 웹뷰호출 클릭 이벤트를 전송하였습니다", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
            startActivity(intent);
        }
    }

    public void btnDetail_Click(View v) {
        if (CheckUserID()) {
            // 앱 클릭 이벤트 트래킹
            ieFirebase.SendFirebaseEvent("EVENT", "APP_메인", "클릭", "Detail뷰호출");
            Toast.makeText(getApplicationContext(), "앱 Detail뷰호출 클릭 이벤트를 전송하였습니다", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ShowInformation() {

        Context mContext = getApplicationContext();

        String androidID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serialNumber = Build.SERIAL;
        String model = Build.MODEL;
        String brand = Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        String device = Build.DEVICE;
        String product = Build.PRODUCT;

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);


        }
        String imei = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getImei();
        String linenumber = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        String simOperator = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();

        String uniqueID = UUID.randomUUID().toString();

        TextView tvInformation = (TextView) findViewById(R.id.tvInformation);

        String result = "Android ID : " + androidID +
                "\nSerial Number : " + serialNumber +
                "\nModel : " + model +
                "\nBrand : " + brand +
                "\nManufacturer : " + manufacturer +
                "\nDevice : " + device +
                "\nProduct : " + product +
                "\nUUID : " + uniqueID;

        tvInformation.setText(result);

        GoogleAppIdTask task = new GoogleAppIdTask(getApplicationContext(), tvInformation);
        task.execute();


    }
}
