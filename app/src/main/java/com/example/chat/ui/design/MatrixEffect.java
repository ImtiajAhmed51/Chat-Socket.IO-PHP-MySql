package com.example.chat.ui.design;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.chat.R;

import java.util.Random;

public class MatrixEffect extends View {
    private static final Random RANDOM = new Random();
    private Canvas canvas;
    private Bitmap canvasBmp;
    private char[] cars = "51LIANIKlianik3152013".toCharArray();
    private int columnSize;
    private int fontSize = 36;
    private int height;
    private Paint paintBg;
    private Paint paintBgBmp;
    private Paint paintInitBg;
    private Paint paintTxt;
    private int[] txtPosByColumn;
    private int width;

    public MatrixEffect(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintTxt = new Paint();
        paintTxt.setStyle(Paint.Style.FILL);
        paintTxt.setColor(getResources().getColor(R.color.purple_500));
        paintTxt.setTextSize(fontSize);

        paintBg = new Paint();
        paintBg.setColor(getResources().getColor(R.color.allBackgroundColor2));
        paintBg.setAlpha(5);
        paintBg.setStyle(Paint.Style.FILL);

        paintBgBmp = new Paint();
        paintBgBmp.setColor(getResources().getColor(R.color.allBackgroundColor2));

        paintInitBg = new Paint();
        paintInitBg.setColor(getResources().getColor(R.color.allBackgroundColor2));
        paintInitBg.setAlpha(255);
        paintInitBg.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        canvasBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBmp);
        canvas.drawRect(0, 0, width, height, paintInitBg);
        columnSize = width / fontSize;

        txtPosByColumn = new int[columnSize + 1];

        for (int x = 0; x < columnSize; x++) {
            txtPosByColumn[x] = RANDOM.nextInt(height / 2) + 1;
        }
    }

    private void drawText() {
        for (int i = 0; i < txtPosByColumn.length; i++) {
            canvas.drawText("" + cars[RANDOM.nextInt(cars.length)], i * fontSize, txtPosByColumn[i] * fontSize, paintTxt);

            if (txtPosByColumn[i] * fontSize > height && Math.random() > 0.975) {
                txtPosByColumn[i] = 0;
            }

            txtPosByColumn[i]++;
        }
    }

    private void drawCanvas() {
        canvas.drawRect(0, 0, width, height, paintBg);
        drawText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBmp, 0, 0, paintBgBmp);
        drawCanvas();
        invalidate();
    }
}
