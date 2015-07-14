package com.github.ssnikolaevich.dragonstonepuzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.ssnikolaevich.slidingpuzzle.LevelListLoader;
import com.github.ssnikolaevich.slidingpuzzle.LevelStateManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class LevelSelectionActivity extends Activity {
    public final static String LEVEL_NAME = "com.github.ssnikolaevich.dragonstonepuzzle.LEVEL";

    private ArrayList<String> levels;
    private LevelStateManager levelStateManager;
    private GridView levelsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        try {
            levels = loadLevelsList();
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Log.e(this.getClass().getName(), ex.getMessage());
        }
        levelStateManager = new LevelStateManager(levels.size());
        LevelsAdapter adapter = new LevelsAdapter(this, levelStateManager);
        levelsGrid = (GridView)findViewById(R.id.levelsGrid);
        levelsGrid.setAdapter(adapter);
        levelsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startGame(position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final boolean wasAlreadySolved = levelStateManager.isSolved(requestCode);
        final boolean isSolved = wasAlreadySolved || (resultCode == RESULT_OK);
        levelStateManager.setSolved(requestCode, isSolved);
        if (isSolved && (!wasAlreadySolved)) {
            levelsGrid.invalidateViews();
        }
    }

    private ArrayList<String> loadLevelsList() throws
            ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = getResources().getAssets().open("levels.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        Element element = document.getDocumentElement();
        element.normalize();
        return LevelListLoader.load(element);
    }

    private void startGame(int levelNumber) {
        Log.d(this.getClass().getName(), "Start game level \""
                + levels.get(levelNumber) + "\" (" + levelNumber + ")");
        if (!levelStateManager.isLocked(levelNumber)) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(LEVEL_NAME, levels.get(levelNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, levelNumber);
        }
    }
}
