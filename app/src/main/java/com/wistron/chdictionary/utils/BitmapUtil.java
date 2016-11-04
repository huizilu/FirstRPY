package com.wistron.chdictionary.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by zhangheng on 2015/12/2.
 */
public final class BitmapUtil {
    private static BitmapFactory.Options options;

    /**
     * 不将图片读到内存中，先获取图片宽高
     *
     * @param path
     * @return
     */
    public static int[] getWidthHeightFromPath(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        BitmapFactory.Options options = getBitmapFactoryOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outHeight != 0 && options.outWidth != 0) {
            int[] wh = new int[2];
            wh[0] = options.outWidth;
            wh[1] = options.outHeight;
            return wh;
        }
        return null;
    }

    /**
     * 不将图片读到内存中，先获取图片宽高
     *
     * @param resourceId
     * @return
     */
    public static int[] getWidthHeightFromResourceId(Resources resources, int resourceId) {
        if (resourceId < 0)
            return null;
        BitmapFactory.Options options = getBitmapFactoryOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resourceId, options);
        if (options.outHeight != 0 && options.outWidth != 0) {
            int[] wh = new int[2];
            wh[0] = options.outWidth;
            wh[1] = options.outHeight;
            return wh;
        }
        return null;
    }

    /**
     * 根据传递的宽高信息,对该路径的图片进行合理压缩,对于旋转的图片进行校正
     * 从文件中加载图片并压缩成指定大小
     * 先通过BitmapFactory.decodeStream方法，创建出一个bitmap，
     * 再调用上述方法将其设为ImageView的 source。decodeStream最大的秘密在
     * 于其直接调用JNI>>nativeDecodeAsset()来完成decode，无需再使用java层的createBitmap，
     * 从而节省了java层的空间
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromPath(String path, int width, int height) {
        int[] wh = getWidthHeightFromPath(path);
        if (wh == null)
            return null;
        BitmapFactory.Options options = getBitmapFactoryOptions();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = computeSampleSize(wh[0], wh[1], Math.min(width, height), width * height);
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int degress = getDegress(path);
        return rotateFromDegree(bitmap, degress);

    }

    public static Bitmap getBitmapFromResourceId(Resources resources, int resourcesId, int width, int height) {
        int[] wh = getWidthHeightFromResourceId(resources, resourcesId);
        if (wh == null)
            return null;
        BitmapFactory.Options options = getBitmapFactoryOptions();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = computeSampleSize(wh[0], wh[1], Math.min(width, height), width * height);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourcesId, options);
        return bitmap;
    }

    /**
     * Google推荐的计算方式
     *
     * @param width
     * @param height
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(int width, int height, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(width, height, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(int width, int height, int minSideLength, int maxNumOfPixels) {
        double w = width;
        double h = height;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int getDegress(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateFromDegree(Bitmap bitmap, int degree) {
        if (bitmap == null || degree == 0)
            return bitmap;
        Matrix matrix = new Matrix();
        matrix.preRotate(degree);
        Bitmap mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return mBitmap;
    }

    private static BitmapFactory.Options getBitmapFactoryOptions() {
        if (options == null)
            options = new BitmapFactory.Options();
        else
            clear();
        return options;
    }

    /**
     * 清空信息,以方便之后的图片压缩
     */
    public static void clear() {
        if (options == null)
            return;
        options.inSampleSize = 0;
        options.inJustDecodeBounds = false;
    }

    /**
     * 置为null,以方便垃圾回收器回收
     */
    public static void destroy() {
        options = null;
    }

}
