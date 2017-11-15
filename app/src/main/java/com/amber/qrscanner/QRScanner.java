package com.amber.qrscanner;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.RelativeLayout;



/**
 * Created by amber_sleepeanuty on 2017/10/23.
 */

public class QRScanner extends RelativeLayout implements Camera.PreviewCallback{
    CameraPreview mPreview;
    MaskView mMask;
    Handler mHandler;
    Camera mCamera;
    Context mContext;
    onQRResultListener mListener;


    public QRScanner(Context context) {
        this(context,null);
    }

    public QRScanner(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QRScanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initCamera();
        mHandler = new Handler();
        mPreview = new CameraPreview(context,mCamera);
        mMask = new MaskView(context,attrs,defStyleAttr);
        mPreview.setId(R.id.preview);
        addView(mPreview);
        //RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        //params.addRule(ALIGN_TOP,mPreview.getId());
        //params.addRule(ALIGN_BOTTOM,mPreview.getId());
        //setLayoutParams(params);
        addView(mMask);
        mPreview.startPreview();

    }

    private void initCamera() {
        Log.e("previewframe","en");
        if (mCamera == null){
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            Camera.Parameters p = mCamera.getParameters();
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
           // p.setPreviewSize(600,800);
            mCamera.setDisplayOrientation(getDisplayOrientation());
            mCamera.setParameters(p);
            //mCamera.setParameters(p);
            //mCamera.setOneShotPreviewCallback(this);

        }
    }

    public int getDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
            Log.e("process","onPreviewFrame");
            cancelCurTask();
            ImgProcessTask imgProcessTask = (ImgProcessTask) new ImgProcessTask(data,camera){
                @Override
                protected void onPostExecute(String s) {
                    //Log.e("process","result: "+s);

                    if (s != null&&s.length()!=0) {
                        mListener.onSuccess(s);
                    }else {
                        mListener.onError();
                       // mCamera.setOneShotPreviewCallback(QRScanner.this);
                    }
                }
            }.execute();


    }

    private void cancelCurTask() {
    }

    public void startSpot(){
        mHandler.removeCallbacks(mScanRunnber);
        mHandler.postDelayed(mScanRunnber,1500);
    }


    Runnable mScanRunnber = new Runnable() {
        @Override
        public void run() {
            mCamera.setOneShotPreviewCallback(QRScanner.this);
        }
    };



    public void setQRResultListener(onQRResultListener listener){
        mListener = listener;
    }
    interface onQRResultListener{
        void onSuccess(String s);
        void onError();
    }



}
