package com.github.ssnikolaevich.dragonstonepuzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.ssnikolaevich.slidingpuzzle.Direction;
import com.github.ssnikolaevich.slidingpuzzle.Game;
import com.github.ssnikolaevich.slidingpuzzle.GameEvent;
import com.github.ssnikolaevich.slidingpuzzle.GameListener;
import com.github.ssnikolaevich.slidingpuzzle.Position;
import com.github.ssnikolaevich.slidingpuzzle.Tile;


public class GameView extends View implements GestureDetector.OnGestureListener, GameListener {
    private static final int SWIPE_DISTANCE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private Bitmap gliph;
    private Game game;
    private Paint paint;
    private Rect sourceRect;
    private Rect destinationRect;
    private GestureDetectorCompat gestureDetector;

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
        gestureDetector = new GestureDetectorCompat(getContext(), this);
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

    private Position screenToGame(float x, float y) {
        Position position = new Position();
        if (game != null) {
            position.setColumn((int)(x * game.getColumns() / getWidth()));
            position.setRow((int)(y * game.getRows() / getHeight()));
        }
        return position;
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
        if (this.game != null) {
            this.game.removeListener(this);
        }
        this.game = game;
        gliph = null;
        if (this.game != null) {
            this.game.addListener(this);
            updateGliph();
        }
    }

    private void updateGliph() {
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

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(this.getClass().getName(), "onDown: " + e.toString());
        return true;

    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(this.getClass().getName(), "onShowPress: " + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(this.getClass().getName(), "onSingleTapUp: " + e.toString());
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(this.getClass().getName(), "onScroll: " + e1.toString() + e2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(this.getClass().getName(), "onLongPress: " + e.toString());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(this.getClass().getName(), "onFling: " + e1.toString() + e2.toString());
        final float x = e1.getX();
        final float y = e1.getY();
        float distanceX = e2.getX() - x;
        float distanceY = e2.getY() - y;
        if (Math.abs(distanceX) > Math.abs(distanceY)) {
            if ((Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD)
                    && (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)) {
                onSwipe(x, y, (distanceX > 0)? Direction.RIGHT : Direction.LEFT);
                return true;
            }
        } else {
            if ((Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD)
                    && (Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)) {
                onSwipe(x, y, (distanceY > 0)? Direction.DOWN : Direction.UP);
                return true;
            }
        }
        return false;
    }

    private void onSwipe(float x, float y, Direction direction) {
        if ((game == null) || game.isOver())
            return;
        Position position = screenToGame(x, y);
        game.makeMove(position.getColumn(), position.getRow(), direction);
    }

    @Override
    public void handle(GameEvent gameEvent) {
        postInvalidate();
    }
}
