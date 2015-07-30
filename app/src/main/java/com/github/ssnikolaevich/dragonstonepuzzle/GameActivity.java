package com.github.ssnikolaevich.dragonstonepuzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.ssnikolaevich.slidingpuzzle.Direction;
import com.github.ssnikolaevich.slidingpuzzle.Game;
import com.github.ssnikolaevich.slidingpuzzle.GameEvent;
import com.github.ssnikolaevich.slidingpuzzle.GameListener;
import com.github.ssnikolaevich.slidingpuzzle.Tile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class GameActivity extends Activity {
    private Game game;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
    }

    public void onSolved(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void onCancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void init() {
        Intent intent = getIntent();
        String levelName = intent.getStringExtra(LevelSelectionActivity.LEVEL_NAME);
        initGame(levelName);

        gameView = (GameView)findViewById(R.id.gameView);
        if (game != null) {
            gameView.setGame(game);
            game.addListener(new GameListener() {
                @Override
                public void handle(GameEvent gameEvent) {
                    handleGameEvent(gameEvent);
                }
            });
        }
    }

    private void initGame(String levelName) {
        try {
            final String assetName = "levels/" + levelName + ".xml";
            InputStream inputStream = getResources().getAssets().open(assetName);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element element = document.getDocumentElement();
            element.normalize();
            game = new Game(element);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getName(), ex.getMessage());
        }
    }

    private void handleGameEvent(GameEvent event) {
        switch (event.getType()) {
            case MOVE: {
                onGameMove(event.getDirection(), event.getPushedTiles());
                break;
            }
            case GAME_OVER: {
                onGameOver();
                break;
            }
        }
    }

    private void onGameMove(Direction direction, Collection<Tile> tiles) {
        // TODO
    }

    private void onGameOver() {
        // TODO
    }
}
