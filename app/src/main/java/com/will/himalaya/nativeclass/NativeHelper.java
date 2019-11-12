package com.will.himalaya.nativeclass;

import android.graphics.Bitmap;

public class NativeHelper {
    static {
        System.loadLibrary("blur");
    }

    //参数r为对bitmap虚化对程度范围
    static native void blurBitmap(Object bitmap,int r);


    public static Bitmap blurNatively(Bitmap original, int radius, boolean canReuseInBitmap) {
        if (radius < 1) {
            return null;
        }

        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);

        // Return this none blur
        if (radius == 1) {
            return bitmap;
        }

        //Jni BitMap Blur
        blurBitmap(bitmap, radius);

        return (bitmap);
    }


    private static Bitmap buildBitmap(Bitmap original, boolean canReuseInBitmap) {
        // First we should check the original
        if (original == null)
            throw new NullPointerException("Blur bitmap original isn't null");

        Bitmap.Config config = original.getConfig();
        if (config != Bitmap.Config.ARGB_8888 && config != Bitmap.Config.RGB_565) {
            throw new RuntimeException("Blur bitmap only supported Bitmap.Config.ARGB_8888 and Bitmap.Config.RGB_565.");
        }

        // If can reuse in bitmap return this or copy
        Bitmap rBitmap;
        if (canReuseInBitmap) {
            rBitmap = original;
        } else {
            rBitmap = original.copy(config, true);
        }
        return (rBitmap);
    }


}
