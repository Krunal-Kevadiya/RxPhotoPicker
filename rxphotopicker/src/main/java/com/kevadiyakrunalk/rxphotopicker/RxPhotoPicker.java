package com.kevadiyakrunalk.rxphotopicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * The type Rx image picker.
 */
public class RxPhotoPicker {
    private static RxPhotoPicker sSingleton;

    private Context context;
    private FileUtil fileUtil;
    private CropOption.Builder builder;
    private PublishSubject<Uri> publishSubject;
    private PublishSubject<List<Uri>> publishSubjectMultipleImages;

    private RxPhotoPicker() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RxPhotoPicker getInstance() {
        if (sSingleton == null) {
            synchronized (RxPhotoPicker.class) {
                if (sSingleton == null) {
                    sSingleton = new RxPhotoPicker();
                }
            }
        }
        return sSingleton;
    }

    public RxPhotoPicker with(Context ctx) {
        context = ctx;
        fileUtil = new FileUtil(context);
        return sSingleton;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Gets active subscription.
     *
     * @return the active subscription
     */
    public Observable<Uri> getActiveSubscription() {
        return publishSubject;
    }

    /**
     * Request image observable.
     *
     * @param imageSource the image source
     * @return the observable
     */
    public Observable<Uri> requestImage(Sources imageSource, boolean allowImageCrop) {
        publishSubject = PublishSubject.create();
        startImagePickPhotoActivity(imageSource.ordinal(), false, allowImageCrop);
        return publishSubject;
    }

    /**
     * Request multiple images observable.
     *
     * @return the observable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Observable<List<Uri>> requestMultipleImages() {
        publishSubjectMultipleImages = PublishSubject.create();
        startImagePickPhotoActivity(Sources.GALLERY.ordinal(), true, false);
        return publishSubjectMultipleImages;
    }

    public CropOption.Builder getBuilder() {
        return builder;
    }

    /**
     * On image picked.
     *
     * @param uri the uri
     */
    void onImagePicked(Uri uri) {
        if (publishSubject != null) {
            publishSubject.onNext(uri);
            publishSubject.onCompleted();
        }
    }

    /**
     * On images picked.
     *
     * @param uris the uris
     */
    void onImagesPicked(List<Uri> uris) {
        if (publishSubjectMultipleImages != null) {
            publishSubjectMultipleImages.onNext(uris);
            publishSubjectMultipleImages.onCompleted();
        }
    }

    private void startImagePickPhotoActivity(int imageSource, boolean allowMultipleImages, boolean allowImageCrop) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PhotoActivity.ALLOW_MULTIPLE_IMAGES, allowMultipleImages);
        intent.putExtra(PhotoActivity.ALLOW_IMAGE_CROP, allowImageCrop);
        intent.putExtra(PhotoActivity.IMAGE_SOURCE, imageSource);
        context.startActivity(intent);
    }

    /**
     * Pick single image.
     *
     * @param sources      the sources
     * @param transformers the transformers
     * @param result       the result
     */
    @SuppressWarnings("unchecked")
    public void pickSingleImage(Sources sources, Transformers transformers, PhotoInterface result) {
        if (transformers == Transformers.FILE) {
            requestImage(sources, false)
                    .flatMap(new Func1<Uri, Observable<File>>() {
                        @Override
                        public Observable<File> call(Uri uri) {
                            File filePath = null;
                            try {
                                filePath = fileUtil.createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(filePath != null)
                                return fileUtil.uriToFile(uri, filePath);
                            else
                                return null;
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else if (transformers == Transformers.BITMAP) {
            requestImage(sources, false)
                    .flatMap(new Func1<Uri, Observable<Bitmap>>() {
                        @Override
                        public Observable<Bitmap> call(Uri uri) {
                            return fileUtil.uriToBitmap(uri);
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else {
            requestImage(sources, false)
                    .subscribe(result::onPhotoResult);
        }
    }

    @SuppressWarnings("unchecked")
    public void pickSingleImage(Sources sources, Transformers transformers, boolean allowImageCrop, PhotoInterface result) {
        builder = new CropOption.Builder();
        if (transformers == Transformers.FILE) {
            requestImage(sources, allowImageCrop)
                    .flatMap(new Func1<Uri, Observable<File>>() {
                        @Override
                        public Observable<File> call(Uri uri) {
                            File filePath = null;
                            try {
                                filePath = fileUtil.createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(filePath != null)
                                return fileUtil.uriToFile(uri, filePath);
                            else
                                return null;
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else if (transformers == Transformers.BITMAP) {
            requestImage(sources, allowImageCrop)
                    .flatMap(new Func1<Uri, Observable<Bitmap>>() {
                        @Override
                        public Observable<Bitmap> call(Uri uri) {
                            return fileUtil.uriToBitmap(uri);
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else {
            requestImage(sources, allowImageCrop)
                    .subscribe(result::onPhotoResult);
        }
    }

    @SuppressWarnings("unchecked")
    public void pickSingleImage(Sources sources, Transformers transformers, boolean allowImageCrop, CropOption.Builder builder, PhotoInterface result) {
        this.builder = builder;
        if (transformers == Transformers.FILE) {
            requestImage(sources, allowImageCrop)
                    .flatMap(new Func1<Uri, Observable<File>>() {
                        @Override
                        public Observable<File> call(Uri uri) {
                            File filePath = null;
                            try {
                                filePath = fileUtil.createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(filePath != null)
                                return fileUtil.uriToFile(uri, filePath);
                            else
                                return null;
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else if (transformers == Transformers.BITMAP) {
            requestImage(sources, allowImageCrop)
                    .flatMap(new Func1<Uri, Observable<Bitmap>>() {
                        @Override
                        public Observable<Bitmap> call(Uri uri) {
                            return fileUtil.uriToBitmap(uri);
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else {
            requestImage(sources, allowImageCrop)
                    .subscribe(result::onPhotoResult);
        }
    }

    /**
     * Pick multiple image.
     *
     * @param transformers the transformers
     * @param result       the result
     */
    @SuppressWarnings("unchecked")
    public void pickMultipleImage(Transformers transformers, PhotoInterface result) {
        if (transformers == Transformers.FILE) {
            requestMultipleImages()
                    .flatMap(new Func1<List<Uri>, Observable<List<File>>>() {
                        @Override
                        public Observable<List<File>> call(List<Uri> uris) {
                            List<File> filePath = new ArrayList<>();
                            int size = uris.size();
                            try {
                                for(int i=0; i<size; i++)
                                    filePath.add(fileUtil.createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(filePath.size() > 0)
                                return fileUtil.uriToFile(uris, filePath);
                            else
                                return null;
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else if (transformers == Transformers.BITMAP) {
            requestMultipleImages()
                    .flatMap(new Func1<List<Uri>, Observable<List<Bitmap>>>() {
                        @Override
                        public Observable<List<Bitmap>> call(List<Uri> uris) {
                            return fileUtil.uriToBitmap(uris);
                        }
                    })
                    .subscribe(result::onPhotoResult);
        } else {
            requestMultipleImages()
                    .subscribe(result::onPhotoResult);
        }
    }
}
