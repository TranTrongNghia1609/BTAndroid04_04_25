package vn.iotstar.bt04_04_25.UploadImage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import vn.iotstar.bt04_04_25.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ImageView img_avatar = findViewById(R.id.imgAvatar);
        img_avatar.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, UploadImgaeActivity.class);
            startActivity(intent);
        });
    }
}