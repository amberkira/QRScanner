package com.amber.qrscanner;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amber_sleepeanuty on 2017/10/23.
 */

public class ImgProcessTask extends AsyncTask<Void, Void, String> {
    private byte[] mData;
    private Camera mCamera;

    public ImgProcessTask(byte[] data, Camera camera) {
        this.mCamera = camera;
        this.mData = data;

    }

    @Override
    protected String doInBackground(Void... params) {
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;

        byte[] rotatedData = new byte[mData.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = mData[x + y * width];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        String result = processData(rotatedData,width,height);
        return result;
    }

    private String processData(byte[] mData, int width, int height) {
        Log.e("process","enter");
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);
            List<BarcodeFormat> allFormats = new ArrayList<>();
            allFormats.add(BarcodeFormat.AZTEC);
            allFormats.add(BarcodeFormat.CODABAR);
            allFormats.add(BarcodeFormat.CODE_39);
            allFormats.add(BarcodeFormat.CODE_93);
            allFormats.add(BarcodeFormat.CODE_128);
            allFormats.add(BarcodeFormat.DATA_MATRIX);
            allFormats.add(BarcodeFormat.EAN_8);
            allFormats.add(BarcodeFormat.EAN_13);
            allFormats.add(BarcodeFormat.ITF);
            allFormats.add(BarcodeFormat.MAXICODE);
            allFormats.add(BarcodeFormat.PDF_417);
            allFormats.add(BarcodeFormat.QR_CODE);
            allFormats.add(BarcodeFormat.RSS_14);
            allFormats.add(BarcodeFormat.RSS_EXPANDED);
            allFormats.add(BarcodeFormat.UPC_A);
            allFormats.add(BarcodeFormat.UPC_E);
            allFormats.add(BarcodeFormat.UPC_EAN_EXTENSION);

            HINTS.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);
            HINTS.put(DecodeHintType.POSSIBLE_FORMATS, allFormats);
            //HINTS.put(DecodeHintType.CHARACTER_SET, "utf-8");

        multiFormatReader.setHints(HINTS);
        //multiFormatReader.setHints();//do zxing decoder
        PlanarYUVLuminanceSource source = null;
        Result raw = null;
        String result =null;
        Rect rect = new Rect(0,0,width,height);
        if (rect!=null){
            source = new PlanarYUVLuminanceSource(mData, width, height, 0, 0, rect.width(), rect.height(), false);
        }
        try {
            raw = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }finally {
            multiFormatReader.reset();
        }

        if (raw!= null){
            result = raw.getText();
        }else {
            //result = "";
        }
        Log.e("process","got data");
        return result;
    }

}
