package com.example.image_editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class stickerMaker extends View {
    Bitmap image;
    Bitmap background;
    Canvas canva=new Canvas();
    Paint paint=new Paint();
    Path path=new Path();
    public stickerMaker(Context context, Bitmap image) {
        super(context);
        this.image=image;
        background=Bitmap.createBitmap(image.getWidth(),image.getHeight(),Bitmap.Config.ARGB_8888);
        canva.setBitmap(background);
        canva.drawBitmap(image,0,0,null);

        paint.setAlpha(0);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(50);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canva.drawPath(path,paint);
        canva.drawBitmap(background,0,0,null);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;

            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                break;

            default: return false;
        }
        invalidate();
        return true;
    }
}
