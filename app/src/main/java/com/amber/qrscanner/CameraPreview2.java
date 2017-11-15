package com.amber.qrscanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by amber_sleepeanuty on 2017/10/26.
 */

public class CameraPreview2 extends SurfaceView implements SurfaceHolder.Callback {
    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPreview2(Context context) {
        super(context);
        mContext = context;
        try {
            initCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initCamera() throws CameraAccessException {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        String mCameraId = null;
        try {
            String[] id = manager.getCameraIdList();
            ArrayList<String> mIdList = (ArrayList<String>) Arrays.asList(id);
            if (mIdList.contains("" + CameraCharacteristics.LENS_FACING_BACK)) {
                mCameraId = "" + CameraCharacteristics.LENS_FACING_BACK;
            } else if (mIdList.contains("" + CameraCharacteristics.LENS_FACING_FRONT)) {
                mCameraId = "" + CameraCharacteristics.LENS_FACING_FRONT;
            }
            if (mCameraId == null ||ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.openCamera(mCameraId, cb1, handler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    CameraDevice.StateCallback cb1 = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            boolean isReadyForCameraSession = true;
            boolean isReadyForCameraRequest = true;
            onBuildRequest(camera);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            //todo throw or notice the disconnection
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            //todo handle error msg
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void onBuildRequest(CameraDevice camera) {
        try {
            // 用于预览的builder 并与该surfaceview进行绑定
            final Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(getHolder().getSurface());
            camera.createCaptureSession(Arrays.asList(getHolder().getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    builder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    CaptureRequest cr = builder.build();
                    try {
                        session.setRepeatingRequest(cr,null,handler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            },handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler();

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //initCamera();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
