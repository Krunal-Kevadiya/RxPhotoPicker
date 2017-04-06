package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;

/**
 * The type Compressor.
 */
public class Compressor {
    private static Compressor sSingleton;

    private Context context;
    private float maxWidth = 612.0f;
    private float maxHeight = 816.0f;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int quality = 80;
    private String destinationDirectoryPath;

    private Compressor(Context ctx, String filePathDir) {
        context = ctx;
        destinationDirectoryPath = filePathDir;
    }

    /**
     * Gets default.
     *
     * @param ctx         the context
     * @param filePathDir the file path dir
     * @return the default
     */
    public static Compressor getInstance(Context ctx, String filePathDir) {
        if (sSingleton == null) {
            synchronized (Compressor.class) {
                sSingleton = new Compressor(ctx, filePathDir);
            }
        }
        return sSingleton;
    }

    /**
     * Compress to file file.
     *
     * @param file the file
     * @return the file
     */
    public File compressToFile(File file) {
        return compressImage(Uri.fromFile(file), maxWidth, maxHeight, compressFormat, quality, destinationDirectoryPath);
    }

    /**
     * Compress image file.
     *
     * @param imageUri       the image uri
     * @param maxWidth       the max width
     * @param maxHeight      the max height
     * @param compressFormat the compress format
     * @param quality        the quality
     * @param parentPath     the parent path
     * @return the file
     */
    public File compressImage(Uri imageUri, float maxWidth, float maxHeight, Bitmap.CompressFormat compressFormat, int quality, String parentPath) {
        FileOutputStream out = null;
        String filename = generateFilePath(context, parentPath, imageUri, compressFormat.name().toLowerCase());
        try {
            out = new FileOutputStream(filename);

            //write the compressed bitmap at the destination specified by filename.
            ImageUtil.getInstance(context).getScaledBitmap(imageUri, maxWidth, maxHeight).compress(compressFormat, quality, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }

        return new File(filename);
    }

    private String generateFilePath(Context context, String parentPath, Uri uri, String extension) {
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator + FileUtil.getInstance(context).splitFileName(FileUtil.getInstance(context).getFileName(uri))[0] + "." + extension;
    }

    /**
     * Compress to bitmap bitmap.
     *
     * @param file the file
     * @return the bitmap
     */
    public Bitmap compressToBitmap(File file) {
        return ImageUtil.getInstance(context).getScaledBitmap(Uri.fromFile(file), maxWidth, maxHeight);
    }

    /**
     * Compress to file as observable observable.
     *
     * @param file the file
     * @return the observable
     */
    public Observable<File> compressToFileAsObservable(final File file) {
        return Observable.defer(() -> Observable.just(compressToFile(file)));
    }

    /**
     * Compress to bitmap as observable observable.
     *
     * @param file the file
     * @return the observable
     */
    public Observable<Bitmap> compressToBitmapAsObservable(final File file) {
        return Observable.defer(() -> Observable.just(compressToBitmap(file)));
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private Compressor compressor;

        /**
         * Instantiates a new Builder.
         *
         * @param context     the context
         * @param filePathDir the file path dir
         */
        public Builder(Context context, String filePathDir) {
            compressor = new Compressor(context, filePathDir);
        }

        /**
         * Sets max width.
         *
         * @param maxWidth the max width
         * @return the max width
         */
        public Builder setMaxWidth(float maxWidth) {
            compressor.maxWidth = maxWidth;
            return this;
        }

        /**
         * Sets max height.
         *
         * @param maxHeight the max height
         * @return the max height
         */
        public Builder setMaxHeight(float maxHeight) {
            compressor.maxHeight = maxHeight;
            return this;
        }

        /**
         * Sets compress format.
         *
         * @param compressFormat the compress format
         * @return the compress format
         */
        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            compressor.compressFormat = compressFormat;
            return this;
        }

        /**
         * Sets quality.
         *
         * @param quality the quality
         * @return the quality
         */
        public Builder setQuality(int quality) {
            compressor.quality = quality;
            return this;
        }

        /**
         * Sets destination directory path.
         *
         * @param destinationDirectoryPath the destination directory path
         * @return the destination directory path
         */
        public Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
            compressor.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        /**
         * Build compressor.
         *
         * @return the compressor
         */
        public Compressor build() {
            return compressor;
        }
    }
}