package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The type Image util.
 */
public class ImageUtil {
    private static ImageUtil sSingleton;

    private Context context;

    private ImageUtil(Context ctx) {
        context = ctx;
    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static ImageUtil getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (ImageUtil.class) {
                sSingleton = new ImageUtil(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Gets scaled bitmap.
     *
     * @param imageUri  the image uri
     * @param maxWidth  the max width
     * @param maxHeight the max height
     * @return the scaled bitmap
     */
    public Bitmap getScaledBitmap(Uri imageUri, float maxWidth, float maxHeight) {
        String filePath = FileUtil.getInstance(context).getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        if (bmp == null) {

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        //noinspection deprecation
        options.inPurgeable = true;
        //noinspection deprecation
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {

                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                    matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public Observable<File> uriToFile(final Uri uri, final File file) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    copyInputStreamToFile(inputStream, file);
                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Logs.getInstance(context).error("Image Util", "Error converting uri" + e.toString());
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<File>> uriToFile(final List<Uri> uri, final List<File> file) {
        return Observable.create(new Observable.OnSubscribe<List<File>>() {
            @Override
            public void call(Subscriber<? super List<File>> subscriber) {
                int size = uri.size();
                for (int i = 0; uri != null && i < size; i++) {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(uri.get(i));
                        copyInputStreamToFile(inputStream, file.get(i));
                    } catch (Exception e) {
                        Logs.getInstance(context).error("Image Util", "Error converting uri" + e.toString());
                        subscriber.onError(e);
                    }
                }
                subscriber.onNext(file);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Bitmap> uriToBitmap(final Uri uri) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Logs.getInstance(context).error("Image Util", "Error converting uri" + e.toString());
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Bitmap>> uriToBitmap(final List<Uri> uri) {
        return Observable.create(new Observable.OnSubscribe<List<Bitmap>>() {
            @Override
            public void call(Subscriber<? super List<Bitmap>> subscriber) {
                List<Bitmap> bitmaps = new ArrayList<>();
                for (Uri uri1 : uri) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri1);
                        bitmaps.add(bitmap);
                    } catch (Exception e) {
                        Logs.getInstance(context).error("Image Util", "Error converting uri" + e.toString());
                        subscriber.onError(e);
                    }
                }
                subscriber.onNext(bitmaps);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void copyInputStreamToFile(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[10 * 1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }
}