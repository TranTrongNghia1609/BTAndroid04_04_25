package vn.iotstar.bt04_04_25.UploadImage;

import android.Manifest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.iotstar.bt04_04_25.R;


public class UploadImgaeActivity extends AppCompatActivity {


    Button btn_uploadFile, btn_UploadImage;
    ImageView imageViewChoose;
    EditText edt_username;
    private Uri mUri;
    private ProgressDialog mProgressDialog;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = UploadImgaeActivity.class.getName();
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null){
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            imageViewChoose.setImageBitmap(bitmap);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    public static String[] storge_permission ={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permission_33 ={
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES
    };
    public static String[] permission() {
        String[] p;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            p = storge_permission_33;
        }else{
            p = storge_permission;
        }
        return p;
    }

    private void CheckPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }
        else {
            requestPermissions(permission(), MY_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permission, @NotNull int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permission,grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }
    private void openGallery()
    {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Selected Picture"));
    }

    private void anhxa()
    {
        btn_uploadFile = findViewById(R.id.btnLoadFile);
        btn_UploadImage = findViewById(R.id.btnUploadImage);
        imageViewChoose = findViewById(R.id.imageViewChoose);
        edt_username = findViewById(R.id.edt_username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_imgae);
        anhxa();
        mProgressDialog = new ProgressDialog(UploadImgaeActivity.this);
        mProgressDialog.setMessage("Please waid upload");
        btn_uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission();
            }
        });
        btn_UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUri != null){
                    UploadImage1();
                }
            }
        });
    }
    public void UploadImage1()
    {

        mProgressDialog.show();
        String username = edt_username.getText().toString().trim();
        RequestBody requestUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);


        String IMAGE_PATH = RealPathUtil.getRealPath(this, mUri);
        Log.e("ffff", IMAGE_PATH);
        File file = new File(IMAGE_PATH);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part partbodyavatar=
                MultipartBody.Part.createFormData(Const.MY_IMAGES, file.getName(), requestFile);
        //Goi retrofit
        ServiceAPI.serviceapi.upload(requestUsername,partbodyavatar).enqueue(new Callback<List<ImageUpload>>() {
            @Override
            public void onResponse(Call<List<ImageUpload>> call, Response<List<ImageUpload>> response) {
                mProgressDialog.dismiss();
                List<ImageUpload> imageUploads = response.body();
                if(imageUploads.size()>0){
                    for(int i= 0; i<imageUploads.size(); i++){
                        Glide.with(UploadImgaeActivity.this)
                                .load(imageUploads.get(i).getAvatar())
                                .into(imageViewChoose);
                        Toast.makeText(UploadImgaeActivity.this, "SuccessFull", Toast.LENGTH_SHORT);
                    }

                }else {
                    Toast.makeText(UploadImgaeActivity.this, "That Bai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ImageUpload>> call, Throwable throwable) {
                mProgressDialog.dismiss();
                Log.e("TAG", throwable.toString());
                Toast.makeText(UploadImgaeActivity.this, "Goi API That Bai", Toast.LENGTH_SHORT).show();
            }
        });
    }
}