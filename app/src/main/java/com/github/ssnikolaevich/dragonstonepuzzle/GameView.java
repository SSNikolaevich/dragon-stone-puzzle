package com.github.ssnikolaevich.dragonstonepuzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.ssnikolaevich.slidingpuzzle.Game;
import com.github.ssnikolaevich.slidingpuzzle.Position;
import com.github.ssnikolaevich.slidingpuzzle.Tile;


public class GameView extends View {
    private Bitmap gliph;
    private Game game;
    private Paint paint;
    private Rect sourceRect;
    private Rect destinationRect;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        gliph = null;
        game = null;
        sourceRect = new Rect();
        destinationRect = new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((game == null) || (gliph == null))
            return;
        for (Tile tile : game.getTiles()) {
            drawTile(canvas, tile);
        }
    }

    private void drawTile(Canvas canvas, Tile tile) {
        final int columns = game.getColumns();
        final int rows = game.getRows();
        final int tileColumns = tile.getColumns();
        final int tileRows = tile.getRows();
        final int gliphWidth = gliph.getWidth() / columns;
        final int gliphHeight = gliph.getHeight() / rows;
        final int tileWidth = getWidth() / columns;
        final int tileHeight = getHeight() / rows;

        final Position origin = tile.getOrigin();
        sourceRect.set(
            origin.getColumn() * gliphWidth,
            origin.getRow() * gliphHeight,
            (origin.getColumn() + tileColumns) * gliphWidth,
            (origin.getRow() + tileRows) * gliphHeight
        );

        final Position position = tile.getPosition();
        destinationRect.set(
            position.getColumn() * tileWidth,
            position.getRow() * tileHeight,
            (position.getColumn() + tileColumns) * tileWidth,
            (position.getRow() + tileRows) * tileHeight
        );

        canvas.drawBitmap(gliph, sourceRect, destinationRect, paint);
    }

    public Bitmap getGliph() {
        return gliph;
    }

    public void setGame(Game game) {
        this.game = game;
        updateGliph();
    }

    private void updateGliph() {
        gliph = null;
        if (game == null)
            return;

        Resources resources = getResources();
        final String gliphName = game.getGliph();
        final String packageName = getContext().getPackageName();
        final int resourceId = resources.getIdentifier(gliphName, "drawable", packageName);
        if (resourceId == 0) {
            String message = "Drawable not found: " + gliphName;
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getName(), message);
        } else {
            gliph = BitmapFactory.decodeResource(resources, resourceId);
        }
    }
}
