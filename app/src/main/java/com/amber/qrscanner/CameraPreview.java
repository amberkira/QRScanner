package com.amber.qrscanner;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created by amber_sleepeanuty on 2017/10/23.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback  {

    private Camera mCamera;
    private boolean isAutoFocus = true;
    private boolean isPreviewing;
    private boolean isCreated;

    public CameraPreview(Context context,Camera camera) {
        super(context);
        this.mCamera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
            isCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        stopPreview();

        post(new Runnable() {
            @Override
            public void run() {
                startPreview();
            }
        });


    }

    private void stopPreview(){
        removeCallbacks(AutoRunnerable);
        mCamera.cancelAutoFocus();
        isPreviewing= false;
        //isCreated = false;

        try {
            mCamera.setPreviewDisplay(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.stopPreview();
        mCamera.setOneShotPreviewCallback(null);
        //mCamera.release();
    }
    public void startPreview() {
        if (mCamera == null)
            throw new NullPointerException("camera is null");
        getHolder().addCallback(this);
        try {
            mCamera.setPreviewDisplay(getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPreviewing = true;
        mCamera.startPreview();
        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                isPreviewing = true;
                doAutoLoop();
                //mCamera.autoFocus(mAutoFocusCb);
            }
        });
    }

    private void doAutoLoop() {
        mCamera.autoFocus(mAutoFocusCb);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        mCamera.release();
        mCamera = null;

    }

    Runnable AutoRunnerable = new Runnable() {
        @Override
        public void run() {
            if (isCreated && isPreviewing && isAutoFocus && mCamera != null) {
                mCamera.autoFocus(mAutoFocusCb);
            }
        }
    };

    Camera.AutoFocusCallback mAutoFocusCb = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success){
                postDelayed(AutoRunnerable,1500);
            }else {
                postDelayed(AutoRunnerable,500);
            }
        }
    };
}
