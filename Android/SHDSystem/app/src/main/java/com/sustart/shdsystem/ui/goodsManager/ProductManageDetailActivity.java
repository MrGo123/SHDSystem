package com.sustart.shdsystem.ui.goodsManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.sustart.shdsystem.MainActivity;
import com.sustart.shdsystem.R;
import com.sustart.shdsystem.SHDSystemApplication;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 用户发布的一件商品的管理页，可修改该商品的基本信息
 */
public class ProductManageDetailActivity extends AppCompatActivity {
    private static String TAG = "ProductManageDetailActivity.class";

    private Button selectImageBtn;
    private Button cameraImageBtn;
    private Button submitBtn;
    private ImageView imageView;
    private TextInputLayout manageItemProductNameIL;
    private TextInputLayout manageItemProductPriceIL;
    private TextInputLayout manageItemProductDescIL;
    private TextInputLayout manageItemProductTypeIL;

    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;

    private Uri imageUri;

    private Long currentTimestamp;

    private Product manageItemProduct;

    private EditText manageItemProductNameEditText;
    private EditText manageItemProductPriceEditText;
    private EditText manageItemProductDescEditText;
    private EditText manageItemProductTypeEditText;

    private File imageFile;
    private SHDSystemApplication application;

    private okhttp3.Callback callback1 = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.e(TAG, "更新成功，更新服务器接口返回数据：" + body);
            }
        }

        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            Log.e(TAG, "连接服务器失败! ");
            e.printStackTrace();
        }
    };
    private okhttp3.Callback callback2 = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.e(TAG, "图片上传成功" + body);
            }
        }

        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            Log.e(TAG, "图片上传失败 ");
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manage_detail);

        Intent intent = getIntent();
        manageItemProduct = (Product) intent.getSerializableExtra("manageItemProduct");

        application = (SHDSystemApplication) getApplication();

        selectImageBtn = findViewById(R.id.product_manage_detail_select_image_button);
        cameraImageBtn = findViewById(R.id.product_manage_detail_camera_button);
        imageView = findViewById(R.id.product_manage_detail_image_show);
        manageItemProductNameIL = findViewById(R.id.product_manage_detail_product_name);
        manageItemProductPriceIL = findViewById(R.id.product_manage_detail_product_price);
        manageItemProductDescIL = findViewById(R.id.product_manage_detail_product_desc);
        manageItemProductTypeIL = findViewById(R.id.product_manage_detail_product_type);

        manageItemProductNameEditText = manageItemProductNameIL.getEditText();
        manageItemProductPriceEditText = manageItemProductPriceIL.getEditText();
        manageItemProductDescEditText = manageItemProductDescIL.getEditText();
        manageItemProductTypeEditText = manageItemProductTypeIL.getEditText();

        submitBtn = findViewById(R.id.product_manage_detail_submit_button);

//        开启imageView的缓存，方便上传时重新获取用户选择的图片
        imageView.setDrawingCacheEnabled(true);
//        imageView = findViewById(R.id.image_show);

        cameraImageBtnBind();
        selectImageBtnBind();
        submitBtnBind();
    }


    private void submitBtnBind() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String productName = manageItemProductNameEditText.getText().toString();
                String productPrice = manageItemProductPriceEditText.getText().toString();
                String productDesc = manageItemProductDescEditText.getText().toString();
                String productType = manageItemProductTypeEditText.getText().toString();

                imageFile = saveMyBitmap(imageView.getDrawingCache());

                manageItemProduct.setName(productName);
                manageItemProduct.setPrice(Integer.valueOf(productPrice));
                manageItemProduct.setDescription(productDesc);
                manageItemProduct.setType(productType);

                updateByHttp();
                //        关闭imageView的缓存，
                imageView.setDrawingCacheEnabled(false);
            }
        });
    }

    private void updateByHttp() {
        String requestUrl1 = Constant.HOST_URL + "product/edit";
        RequestBody requestBody1 = new FormBody.Builder()
                .add("id", manageItemProduct.getId()+"")
                .add("name", manageItemProduct.getName())
                .add("price", String.valueOf(manageItemProduct.getPrice()))
                .add("imageUrl", manageItemProduct.getImageUrl())
                .add("type", manageItemProduct.getType())
                .add("description", manageItemProduct.getDescription())
                .add("sellerId", manageItemProduct.getSellerId())
                .build();
        Request request1 = new Request.Builder().url(requestUrl1).post(requestBody1).build();

//            上传商品图片
        String requestUrl2 = Constant.HOST_URL + "product/uploadAvatar";
//        配置文件类型
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
//        创建数据体
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("file", manageItemProduct.getImageUrl(), RequestBody.create(MEDIA_TYPE_PNG, imageFile));
        final MultipartBody requestBody = builder.build();
        //构建请求
        final Request request2 = new Request.Builder()
                .url(requestUrl2)//地址
                .post(requestBody)//添加请求体
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(request1).enqueue(callback1);
            client.newCall(request2).enqueue(callback2);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
            return;
        }

//        设定一定发布成功
        Toast.makeText(ProductManageDetailActivity.this, "商品发布成功", Toast.LENGTH_SHORT).show();
        Intent intentToMainActivity = new Intent(ProductManageDetailActivity.this, MainActivity.class);
        startActivity(intentToMainActivity);
    }

    private void selectImageBtnBind() {
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态申请获取访问 读写磁盘的权限
                if (ContextCompat.checkSelfPermission(ProductManageDetailActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProductManageDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                } else {
                    //打开相册
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_PHOTO); // 打开相册
                }
            }
        });
    }

    private void cameraImageBtnBind() {
        cameraImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 创建File对象，用于存储拍照后的图片
                //存放在手机SD卡的应用关联缓存目录下
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
               /* 从Android 6.0系统开始，读写SD卡被列为了危险权限，如果将图片存放在SD卡的任何其他目录，
                  都要进行运行时权限处理才行，而使用应用关联 目录则可以跳过这一步
                */
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                   7.0系统开始，直接使用本地真实路径的Uri被认为是不安全的，会抛 出一个FileUriExposedException异常。
                   而FileProvider则是一种特殊的内容提供器，它使用了和内 容提供器类似的机制来对数据进行保护，
                   可以选择性地将封装过的Uri共享给外部，从而提高了 应用的安全性
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //大于等于版本24（7.0）的场合
                    imageUri = FileProvider.getUriForFile(ProductManageDetailActivity.this, "com.feige.pickphoto.fileprovider", outputImage);
                } else {
                    //小于android 版本7.0（24）的场合
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式
     *
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 在UI中展示已上传图片
     *
     * @param imagePath
     */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将bitmap转化为png格式
     *
     * @param mBitmap
     * @return
     */
    public File saveMyBitmap(Bitmap mBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = null;
        //     先生成自定义的文件名其中一部分： 该发布用户的id + 当前的时间戳
        currentTimestamp = System.currentTimeMillis();
        String tempFileName = "" + application.loginUser.getId() + currentTimestamp;
        System.out.println("自定义的文件名部分" + tempFileName);
//        真实的文件名为自定义的部分拼接上一段莫名其妙的数字串
        try {
            file = File.createTempFile(
                    tempFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}