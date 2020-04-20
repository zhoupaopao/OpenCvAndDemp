package com.example.opencvanddemp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class DrawImageView extends androidx.appcompat.widget.AppCompatImageView {

    public DrawImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    Paint paint = new Paint();
    {
        paint.setAntiAlias(true);//用于防止边缘的锯齿
        paint.setColor(Color.GREEN);//设置颜色
        paint.setStyle(Paint.Style.STROKE);//设置样式为空心矩形
        paint.setStrokeWidth(2.5f);//设置空心矩形边框的宽度
        paint.setAlpha(1000);//设置透明度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(new Rect(getWidth()/4,getHeight()/4,getWidth()*3/4,getHeight()*3/4),paint);//绘制矩形，并设置矩形框显示的位置
    }
}
