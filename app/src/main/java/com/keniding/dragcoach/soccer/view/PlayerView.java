package com.keniding.dragcoach.soccer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.keniding.dragcoach.R;

import lombok.Getter;
import lombok.Setter;

public class PlayerView extends View {
    private Paint paint;

    private Paint numberPaint;  // Nuevo paint para números
    private Paint textPaint;    // Nuevo paint para texto

    @Getter @Setter
    private String playerName;

    @Getter @Setter
    private String position;

    @Getter @Setter
    private int number;

    @Getter
    private float x;

    @Getter
    private float y;

    @Getter @Setter
    private int playerColor;

    @Getter
    private float radius;

    @Getter
    private boolean isSelected;

    @Getter @Setter
    private boolean isHomeTeam; // Nuevo: para distinguir equipos

    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        numberPaint = new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_regular));

        radius = 50f;
        isSelected = false;
        isHomeTeam = true;
        setTeamColor(true);
    }

    public void setTeamColor(boolean isHome) {
        isHomeTeam = isHome;
        playerColor = isHome ? Color.rgb(255, 0, 0) : Color.rgb(0, 0, 255); // Rojo para local, Azul para visitante
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(playerColor);
        canvas.drawCircle(x, y, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4f);
        canvas.drawCircle(x, y, radius, paint);

        numberPaint.setColor(Color.WHITE);
        numberPaint.setTextSize(40f);
        Paint.FontMetrics fm = numberPaint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;
        float textOffset = (textHeight / 2) - fm.bottom;
        canvas.drawText(String.valueOf(number), x, y + textOffset, numberPaint);

        if (position != null && !position.isEmpty()) {
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(30f);
            canvas.drawText(position, x, y + radius + 30f, textPaint);
        }

        if (isSelected) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(5f);
            canvas.drawCircle(x, y, radius + 8, paint);
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        invalidate();
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        invalidate();
    }
}