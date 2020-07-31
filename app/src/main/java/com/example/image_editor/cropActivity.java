package com.example.image_editor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

public class cropActivity extends AppCompatActivity {

    Uri imageUri;
    ImageView image;

//    CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("Uri");
        if (path != null) {
            imageUri = Uri.parse(path);
        }
        Toast.makeText(this, " " + imageUri.toString() + " ", Toast.LENGTH_SHORT).show();
        image=findViewById(R.id.cropped);
//        cropImageView=findViewById(R.id.cropImageView);
//        cropImageView.setImageUriAsync(imageUri);
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Bitmap bit = result.getBitmap();
//                image.setImageBitmap(bit);
                Uri resultUri = result.getUri();
//                image.setImageURI(resultUri);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("Uricrop", resultUri.toString());
//                intent.putExtra("image", bit);
                setResult(RESULT_OK, intent);
                cropActivity.this.finish();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
