package com.github.ssnikolaevich.dragonstonepuzzle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        try {
            ArrayList<String> levels = loadLevelsList();
            LevelStateManager manager = new LevelStateManager(levels.size());
            LevelsAdapter adapter = new LevelsAdapter(this, manager);
            GridView levelsGrid = (GridView)findViewById(R.id.levelsGrid);
            levelsGrid.setAdapter(adapter);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Log.e(this.getClass().getName(), ex.getMessage());
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
}
