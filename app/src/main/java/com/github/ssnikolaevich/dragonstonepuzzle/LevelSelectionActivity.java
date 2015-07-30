package com.github.ssnikolaevich.dragonstonepuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
    public final static int UNLOCKED_LEVELS_LIMIT = 4;

    public final static String LEVEL_SELECTION_ACTIVITY_PREFERENCES = "level_selection_activity_preferences";
    public final static String LEVELS_STATE="levels_state";

    private ArrayList<String> levels;
    private LevelStateManager levelStateManager;
    private GridView levelsGrid;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        settings = getSharedPreferences(LEVEL_SELECTION_ACTIVITY_PREFERENCES, Context.MODE_PRIVATE);

        try {
            levels = loadLevelsList();
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getName(), ex.getMessage());
        }

        levelStateManager = new LevelStateManager(levels.size());
        levelStateManager.setUnlockedLevelsLimit(UNLOCKED_LEVELS_LIMIT);
        loadLevelsState();

        LevelsAdapter adapter = new LevelsAdapter(this, levelStateManager);
        levelsGrid = (GridView)findViewById(R.id.levelsGrid);
        levelsGrid.setAdapter(adapter);
        levelsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickLevelTile(position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final boolean wasAlreadySolved = levelStateManager.isSolved(requestCode);
        final boolean isSolved = wasAlreadySolved || (resultCode == RESULT_OK);
        levelStateManager.setSolved(requestCode, isSolved);
        if (isSolved && (!wasAlreadySolved)) {
            saveLevelsState();
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

    private void onClickLevelTile(int levelNumber) {
        Log.d(this.getClass().getName(), "Select game level \""
                + levels.get(levelNumber) + "\" (" + levelNumber + ")");
        if (!levelStateManager.isLocked(levelNumber)) {
            startGame(levelNumber);
        }
    }

    private void startGame(int levelNumber) {
        Log.d(this.getClass().getName(), "Start game level \""
                + levels.get(levelNumber) + "\" (" + levelNumber + ")");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(LEVEL_NAME, levels.get(levelNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, levelNumber);
    }

    private void loadLevelsState() {
        if (settings.contains(LEVELS_STATE)) {
            String value = settings.getString(LEVELS_STATE, "");
            setLevelsStateFromString(value);
        }
    }

    private void saveLevelsState() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LEVELS_STATE, getLevelsStateAsString());
        editor.apply();
    }

    private String getLevelsStateAsString() {
        String s = "";
        for (int i = 0; i < levelStateManager.getLevelsCount(); ++i) {
            s += levelStateManager.isSolved(i)? '1' : '0';
        }
        return s;
    }

    private void setLevelsStateFromString(String value) {
        final int limit = Math.min(levelStateManager.getLevelsCount(), value.length());
        for (int i = 0; i < limit; ++i) {
            levelStateManager.setSolved(i, value.charAt(i) == '1');
        }
    }
}
