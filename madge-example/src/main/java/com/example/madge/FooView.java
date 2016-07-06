package com.example.madge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class FooView extends View {
    Bitmap bitmap;
    Paint paint;

    public FooView(Context context) {
        this(context, null);
    }

    public FooView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Drawable drawable = getResources().getDrawable(R.drawable.app_icon);
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x = (getWidth() - bitmap.getWidth()) / 2;
        float y = (getHeight() - bitmap.getHeight()) / 2;
        canvas.drawBitmap(bitmap, x, y, paint);
    }

}
