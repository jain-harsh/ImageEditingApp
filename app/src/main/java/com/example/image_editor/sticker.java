package com.example.image_editor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class sticker extends AppCompatActivity {

    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        Bundle bundle= getIntent().getExtras();
        image=bundle.getParcelable("image");

//        stickerMaker maker=new stickerMaker(this,image);
//        RelativeLayout rootLayout = findViewById(R.id.sticker_layout);

        //Add custom view into root layout
//        rootLayout.addView(maker);
    }
}