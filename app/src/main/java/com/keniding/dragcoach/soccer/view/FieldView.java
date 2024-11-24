package com.keniding.dragcoach.soccer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.keniding.dragcoach.soccer.model.PlayerPosition;

import java.util.ArrayList;
import java.util.List;

public class FieldView extends View {
    private List<PlayerView> players;
    private Bitmap fieldBackground;
    private Paint paint;
    private float fieldWidth, fieldHeight;
    private PlayerView selectedPlayer;
    private boolean isDragging;

    public FieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        players = new ArrayList<>();
        isDragging = false;
        setupFieldBackground();
        setupTouchListener();
    }

    private void setupFieldBackground() {
        fieldBackground = Bitmap.createBitmap(800, 1200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fieldBackground);
        float width = fieldBackground.getWidth();
        float height = fieldBackground.getHeight();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#2E7D32")); // Verde oscuro para el césped
        canvas.drawRect(0, 0, width, height, paint);

        // Patrón de césped
        paint.setColor(Color.parseColor("#388E3C")); // Verde un poco más claro
        for (int i = 0; i < height; i += 30) {
            canvas.drawLine(0, i, width, i, paint);
        }

        // Líneas del campo
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);

        // Borde del campo
        canvas.drawRect(10, 10, width - 10, height - 10, paint);

        // Línea central
        canvas.drawLine(10, height/2, width - 10, height/2, paint);

        // Círculo central
        canvas.drawCircle(width/2, height/2, 100, paint);
        canvas.drawPoint(width/2, height/2, paint);

        // Área grande superior
        float penaltyAreaWidth = width * 0.6f;
        float penaltyAreaHeight = height * 0.2f;
        float penaltyAreaX = (width - penaltyAreaWidth) / 2;
        canvas.drawRect(penaltyAreaX, 10, width - penaltyAreaX, penaltyAreaHeight, paint);

        // Área pequeña superior
        float goalAreaWidth = width * 0.3f;
        float goalAreaHeight = height * 0.1f;
        float goalAreaX = (width - goalAreaWidth) / 2;
        canvas.drawRect(goalAreaX, 10, width - goalAreaX, goalAreaHeight, paint);

        // Área grande inferior
        canvas.drawRect(penaltyAreaX, height - penaltyAreaHeight,
                width - penaltyAreaX, height - 10, paint);

        // Área pequeña inferior
        canvas.drawRect(goalAreaX, height - goalAreaHeight,
                width - goalAreaX, height - 10, paint);

        // Punto de penalti superior
        canvas.drawCircle(width/2, penaltyAreaHeight - 50, 5, paint);

        // Punto de penalti inferior
        canvas.drawCircle(width/2, height - penaltyAreaHeight + 50, 5, paint);

        // Semicírculo área superior
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(width/2 - 100, penaltyAreaHeight - 100,
                width/2 + 100, penaltyAreaHeight + 100,
                180, 180, false, paint);

        // Semicírculo área inferior
        canvas.drawArc(width/2 - 100, height - penaltyAreaHeight - 100,
                width/2 + 100, height - penaltyAreaHeight + 100,
                0, 180, false, paint);

        // Esquinas
        float cornerRadius = 20;
        paint.setStyle(Paint.Style.STROKE);
        // Superior izquierda
        canvas.drawArc(10, 10, 10 + cornerRadius, 10 + cornerRadius, 180, 90, false, paint);
        // Superior derecha
        canvas.drawArc(width - 10 - cornerRadius, 10, width - 10, 10 + cornerRadius, 270, 90, false, paint);
        // Inferior izquierda
        canvas.drawArc(10, height - 10 - cornerRadius, 10 + cornerRadius, height - 10, 90, 90, false, paint);
        // Inferior derecha
        canvas.drawArc(width - 10 - cornerRadius, height - 10 - cornerRadius, width - 10, height - 10, 0, 90, false, paint);
    }

    private void setupTouchListener() {
        setOnTouchListener((v, event) -> {
            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    selectedPlayer = findPlayerAtPosition(touchX, touchY);
                    if (selectedPlayer != null) {
                        selectedPlayer.setSelected(true);
                        isDragging = true;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (isDragging && selectedPlayer != null) {
                        selectedPlayer.setPosition(touchX, touchY);
                        invalidate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (selectedPlayer != null) {
                        selectedPlayer.setSelected(false);
                        selectedPlayer = null;
                        isDragging = false;
                        v.performClick();
                    }
                    break;
            }
            return true;
        });

        setOnClickListener(v -> {});
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private PlayerView findPlayerAtPosition(float x, float y) {
        for (PlayerView player : players) {
            float dx = x - player.getX();
            float dy = y - player.getY();
            if (Math.sqrt(dx * dx + dy * dy) < player.getRadius()) {
                return player;
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(fieldBackground, 0, 0, null);
        for (PlayerView player : players) {
            player.draw(canvas);
        }
    }

    public void addPlayer(PlayerView player) {
        players.add(player);
        invalidate();
    }

    public void removePlayer(PlayerView player) {
        players.remove(player);
        invalidate();
    }

    public void clearPlayers() {
        players.clear();
        invalidate();
    }

    public List<PlayerPosition> saveFormation() {
        List<PlayerPosition> formation = new ArrayList<>();
        for (PlayerView player : players) {
            formation.add(new PlayerPosition(
                    player.getNumber(),
                    player.getX(),
                    player.getY(),
                    player.getPosition()
            ));
        }
        return formation;
    }

    public void loadFormation(List<PlayerPosition> formation) {
        clearPlayers();
        for (PlayerPosition pos : formation) {
            PlayerView player = new PlayerView(getContext());
            player.setNumber(pos.getNumber());
            player.setPosition(pos.getX(), pos.getY());
            addPlayer(player);
        }
    }
}
