package com.example.image_editor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agilie.RotatableAutofitEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    Toolbar image_toolbar;
    Button addText, sticker;
    ImageView edit_image, undo, rotate, crop, brush;
    Uri uri, savedImagePath;
    Bitmap Image;
    int cropActivity = 00;
    FileOutputStream outStream = null;
    int screenWidth, screenHeight;
    private RelativeLayout container;
    private ArrayList<RotatableAutofitEditText> editTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_image = findViewById(R.id.edit_image);
        undo = findViewById(R.id.undo);
        rotate = findViewById(R.id.rotate);
        crop = findViewById(R.id.crop);
        brush = findViewById(R.id.brush);
        image_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        addText = findViewById(R.id.addText);
        sticker = findViewById(R.id.sticker);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("Camera")) {
            String path = bundle.getString("Uri");
//            Toast.makeText(this," "+path+"hiiiii",Toast.LENGTH_SHORT).show();
            if (path != null) {
                uri = Uri.parse(path);
//                Toast.makeText(this," "+uri.toString()+" ",Toast.LENGTH_SHORT).show();
            }
            Image = bundle.getParcelable("Image");
            try {
                Image = rotateImageIfRequired(Image, uri);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (bundle.getBoolean("Gallery")) {
            String path = bundle.getString("Uri");
            if (path != null) {
                uri = Uri.parse(path);
//                Toast.makeText(this," "+uri.toString()+" ",Toast.LENGTH_SHORT).show();
            }
            Image = UriToBitmap(uri);
        }




//        edit_image.setImageURI(uri);
//        if(uri!=null) {
//            Image=UriToBitmap(uri);
//            image=Gallery;
//            Gallery=reduceSize(Gallery);
//            edit_image.setImageBitmap(image);
//        }else{
//            image=Camera;
//            Camera=reduceSize(Camera);
        edit_image.setImageBitmap(Image);
//        }

        setSupportActionBar(image_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextButton();
            }
        });

        sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), sticker.class);
                intent.putExtra("image", Image);
//                Toast.makeText(getApplicationContext(),"opening...",Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "undo", Toast.LENGTH_SHORT).show();
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image = rotateImage(Image, 90);
                edit_image.setImageBitmap(Image);
                Toast.makeText(getApplicationContext(), "rotate", Toast.LENGTH_SHORT).show();
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(uri)
                        .setAspectRatio(1, 1)
                        .start(MainActivity.this);
                Toast.makeText(getApplicationContext(), "crop", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, cropActivity.class);
//                intent.putExtra("Uri", uri.toString());
//                startActivityForResult(intent, cropActivity);

            }
        });

        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "brush", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveToMemory(Image);
//                Toast.makeText(getApplicationContext(),"saving",Toast.LENGTH_SHORT).show();
                break;

            case R.id.share:
                share(Image);
                Toast.makeText(getApplicationContext(), "share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.effects:
                Toast.makeText(getApplicationContext(), "effects", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Mirror:
                Toast.makeText(getApplicationContext(), "Mirror", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getScreensize() {
        DisplayMetrics displaymetrics;
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
    }

    private void addTextButton() {
        RotatableAutofitEditText newEditText = (RotatableAutofitEditText) LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, container, false);
        container.addView(newEditText);
        newEditText.requestLayout();
        editTexts.add(newEditText);
        showSoftKeyboard(newEditText);
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Image = result.getBitmap();
                edit_image.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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
        Toast.makeText(this, "Image rotated by" + degree, Toast.LENGTH_LONG).show();
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void share(Bitmap image) {
        saveToMemory(image);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        shareIntent.putExtra(Intent.EXTRA_STREAM, savedImagePath);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
    }

    public Bitmap UriToBitmap(Uri image_path) {
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(image_path, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    private Bitmap reduceSize(Bitmap bit, int maxSize) {
        int height = edit_image.getHeight();
        int width = edit_image.getWidth();
        int bitwidth = (int) (bit.getWidth() * 0.8);
        int bitheight = bit.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bit, width, height, true);
    }

    public void saveToMemory(Bitmap bitmap) {
        try {
            File storage = Environment.getExternalStorageDirectory();
            File dir = new File(storage.getAbsolutePath() + "/tasveer");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = String.format("%d.jpeg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            savedImagePath = Uri.parse(outFile.getAbsolutePath());
            Toast.makeText(this, "stored to - " + outFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPictureTaken - wrote to " + outFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}