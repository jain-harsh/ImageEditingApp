package com.example.image_editor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class loadImage extends AppCompatActivity {

    Toolbar toolbar;
    Button gallery,camera;
    int Image_capture=1 , Image_select=0;
    Intent intent;
    Bitmap Camera,Gallery;
    Uri CameraUri;
    FileOutputStream outStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_image);

        toolbar=findViewById(R.id.image_toolbar);
        gallery=findViewById(R.id.gallery);
        camera=findViewById(R.id.camera);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tasveer");

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storage=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(storage, Image_select);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picture, Image_capture);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data!=null) {
            switch (requestCode) {
                case 0:
                    Uri GalleryUri = data.getData();
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("Gallery",true);
                    intent.putExtra("Uri", GalleryUri.toString());
                    startActivity(intent);
                    break;

                case 1:
                    Camera = (Bitmap) data.getExtras().get("data");
                    saveToMemory(Camera);
                    try {
                        Camera=rotateImageIfRequired(Camera,CameraUri);
                    } catch (IOException e) {
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("Camera",true);
                    intent.putExtra("Image", Camera);
                    intent.putExtra("Uri",CameraUri.toString());
                    startActivity(intent);
                    break;
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"plz select",Toast.LENGTH_SHORT).show();
        }
    }

    public void saveToMemory(Bitmap bitmap){
        try {
            File storage = Environment.getExternalStorageDirectory();
            File dir = new File(storage.getAbsolutePath() + "/tasveer");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = String.format("%d.jpeg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            CameraUri= Uri.parse(outFile.getAbsolutePath());
//            Toast.makeText(this,"stored to - "+outFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPictureTaken - wrote to " + outFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//        Toast.makeText(this,"Image rotated by"+orientation,Toast.LENGTH_LONG).show();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Toast.makeText(this,"Image rotated by"+degree,Toast.LENGTH_LONG).show();
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}