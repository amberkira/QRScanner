package com.amber.qrscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.PlanarYUVLuminanceSource;

public class MainActivity extends Activity implements QRScanner.onQRResultListener{
    //权限请求页面
    private static final int REQUEST_CODE = 0X001;
    //app内部需要的敏感权限
    protected final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };

    public QRScanner qrScanner;

    private PermissionCheck mPermissionChecker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPermissionChecker = new PermissionCheck(this);
        if(mPermissionChecker.lacksPermissions(PERMISSIONS)){
            startPermissionsActivity();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrScanner = (QRScanner) findViewById(R.id.qr);
        qrScanner.setQRResultListener(this);
        qrScanner.startSpot();
        //new CameraPreview2(MainActivity.this);
        //((QRScanner)findViewById(R.id.qr)).startSpot();
    }
    @Override
    protected void onResume() {
        super.onResume();

       
    }

    private void startPermissionsActivity() {

        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }

    }


    @Override
    public void onSuccess(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
        qrScanner.startSpot();
    }

    @Override
    public void onError() {
        //Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        qrScanner.startSpot();
    }
}
