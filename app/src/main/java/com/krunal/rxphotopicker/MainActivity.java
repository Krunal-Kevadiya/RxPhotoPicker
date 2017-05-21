package com.krunal.rxphotopicker;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kevadiyakrunalk.rxpermissions.DialogCallback;
import com.kevadiyakrunalk.rxpermissions.PermissionCallback;
import com.kevadiyakrunalk.rxpermissions.PermissionStatus;
import com.kevadiyakrunalk.rxpermissions.RxPermissions;
import com.kevadiyakrunalk.rxphotopicker.CropOption;
import com.kevadiyakrunalk.rxphotopicker.PhotoInterface;
import com.kevadiyakrunalk.rxphotopicker.RxPhotoPicker;
import com.kevadiyakrunalk.rxphotopicker.Sources;
import com.kevadiyakrunalk.rxphotopicker.Transformers;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
    }

    public void onGalleryUri(View view) {
        final CropOption.Builder builder = new CropOption.Builder();
        builder.setOutputHW(690, 690);
        builder.setAspectRatio(3, 2);
        builder.setScale(true);

        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_library), getString(R.string.msg_ration_library))
                .showAccessRemovedDialog(getString(R.string.title_permission_library), getString(R.string.msg_setting_library))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickSingleImage(Sources.GALLERY, Transformers.URI, true, builder, new PhotoInterface<Uri>() {
                                        @Override
                                        public void onPhotoResult(Uri uri) {
                                            if (uri != Uri.EMPTY) {
                                                Log.e("gallery", "Uri -> " + uri);
                                                imageView.setImageURI(uri);
                                            } else
                                                Log.e("gallery", "Uri -> EMPTY");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onGalleryBitmap(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_library), getString(R.string.msg_ration_library))
                .showAccessRemovedDialog(getString(R.string.title_permission_library), getString(R.string.msg_setting_library))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickSingleImage(Sources.GALLERY, Transformers.BITMAP, true, new PhotoInterface<Bitmap>() {
                                        @Override
                                        public void onPhotoResult(Bitmap bitmap) {
                                            if (bitmap != null) {
                                                Log.e("gallery", "Bitmap -> " + bitmap.toString());
                                                imageView.setImageBitmap(bitmap);
                                            } else
                                                Log.e("gallery", "Bitmap -> NULL");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onGalleryFile(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_library), getString(R.string.msg_ration_library))
                .showAccessRemovedDialog(getString(R.string.title_permission_library), getString(R.string.msg_setting_library))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickSingleImage(Sources.GALLERY, Transformers.FILE, true, new PhotoInterface<File>() {
                                        @Override
                                        public void onPhotoResult(File file) {
                                            if (file != null) {
                                                Log.e("gallery", "File -> " + file.getName() + " ,Size -> " + fileSize(file.length()));
                                                imageView.setImageURI(Uri.parse(file.getPath()));
                                            } else
                                                Log.e("gallery", "File -> NULL");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }



    public void onCameraUri(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_camera), getString(R.string.msg_ration_camera))
                .showAccessRemovedDialog(getString(R.string.title_permission_camera), getString(R.string.msg_setting_camera))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickSingleImage(Sources.CAMERA, Transformers.URI, true, new PhotoInterface<Uri>() {
                                        @Override
                                        public void onPhotoResult(Uri uri) {
                                            if (uri != Uri.EMPTY) {
                                                Log.e("camera", "Uri -> " + uri);
                                                imageView.setImageURI(uri);
                                            } else
                                                Log.e("camera", "Uri -> EMPTY");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onCameraBitmap(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_camera), getString(R.string.msg_ration_camera))
                .showAccessRemovedDialog(getString(R.string.title_permission_camera), getString(R.string.msg_setting_camera))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickSingleImage(Sources.CAMERA, Transformers.BITMAP, true, new PhotoInterface<Bitmap>() {
                                        @Override
                                        public void onPhotoResult(Bitmap bitmap) {
                                            if (bitmap != null) {
                                                Log.e("camera", "Bitmap -> " + bitmap.toString());
                                                imageView.setImageBitmap(bitmap);
                                            } else
                                                Log.e("camera", "Bitmap -> NULL");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onCameraFile(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_camera), getString(R.string.msg_ration_camera))
                .showAccessRemovedDialog(getString(R.string.title_permission_camera), getString(R.string.msg_setting_camera))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickSingleImage(Sources.CAMERA, Transformers.FILE, true, new PhotoInterface<File>() {
                                        @Override
                                        public void onPhotoResult(File file) {
                                            if (file != null) {
                                                Log.e("camera", "File -> " + file.getName() + " ,Size -> " + fileSize(file.length()));
                                                imageView.setImageURI(Uri.parse(file.getPath()));
                                            } else
                                                Log.e("camera", "File -> NULL");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }



    public void onGalleryMultipleUri(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_library), getString(R.string.msg_ration_library))
                .showAccessRemovedDialog(getString(R.string.title_permission_library), getString(R.string.msg_setting_library))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickMultipleImage(Transformers.URI, new PhotoInterface<List<Uri>>() {
                                        @Override
                                        public void onPhotoResult(List<Uri> uri) {
                                            for (Uri uri1 : uri) {
                                                if (uri1 != Uri.EMPTY) {
                                                    Log.e("gallery multiple", "Uri -> " + uri1);
                                                    imageView.setImageURI(uri1);
                                                } else
                                                    Log.e("gallery multiple", "Uri -> EMPTY");
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onGalleryMultipleBitmap(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_library), getString(R.string.msg_ration_library))
                .showAccessRemovedDialog(getString(R.string.title_permission_library), getString(R.string.msg_setting_library))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickMultipleImage(Transformers.BITMAP, new PhotoInterface<List<Bitmap>>() {
                                        @Override
                                        public void onPhotoResult(List<Bitmap> bitmap) {
                                            for (Bitmap bitmap1 : bitmap) {
                                                if (bitmap1 != null) {
                                                    Log.e("gallery multiple", "Bitmap -> " + bitmap1.toString());
                                                    imageView.setImageBitmap(bitmap1);
                                                } else
                                                    Log.e("gallery multiple", "Bitmap -> NULL");
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onGalleryMultipleFile(View view) {
        RxPermissions.getInstance()
                .with(this)
                .showRationalDialog(getString(R.string.title_permission_library), getString(R.string.msg_ration_library))
                .showAccessRemovedDialog(getString(R.string.title_permission_library), getString(R.string.msg_setting_library))
                .checkPermission(true, true, new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus permissionStatus, String... strings) {
                        if (permissionStatus == PermissionStatus.GRANTED) {
                            RxPhotoPicker.getInstance().with(context)
                                    .pickMultipleImage(Transformers.FILE, new PhotoInterface<List<File>>() {
                                        @Override
                                        public void onPhotoResult(List<File> file) {
                                            for (File file1 : file) {
                                                if (file1 != null) {
                                                    Log.e("gallery multiple", "File -> " + file1.getName() + " ,Size -> " + fileSize(file1.length()));
                                                    imageView.setImageURI(Uri.parse(file1.getPath()));
                                                } else
                                                    Log.e("gallery multiple", "File -> NULL");
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRational(DialogCallback dialogCallback, String... strings) {
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback dialogCallback, String... strings) {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public String fileSize(long size) {
        long kb = size / 1024;
        if (kb >= 1024)
            return (kb / 1024) + " Mb";
        else
            return kb + " Kb";
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView2);
        findViewById(R.id.btn_g_uri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryUri(v);
            }
        });
        findViewById(R.id.btn_g_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryBitmap(v);
            }
        });
        findViewById(R.id.btn_g_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryFile(v);
            }
        });
        findViewById(R.id.btn_c_uri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraUri(v);
            }
        });
        findViewById(R.id.btn_c_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraBitmap(v);
            }
        });
        findViewById(R.id.btn_c_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraFile(v);
            }
        });
        findViewById(R.id.btn_gm_uri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryMultipleUri(v);
            }
        });
        findViewById(R.id.btn_gm_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryMultipleBitmap(v);
            }
        });
        findViewById(R.id.btn_gm_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryMultipleFile(v);
            }
        });
    }
}
