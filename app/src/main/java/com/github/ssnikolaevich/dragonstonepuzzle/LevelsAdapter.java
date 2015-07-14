package com.github.ssnikolaevich.dragonstonepuzzle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.ssnikolaevich.slidingpuzzle.LevelStateManager;

import java.util.zip.Inflater;

public class LevelsAdapter extends BaseAdapter {
    private Context context;
    private LevelStateManager levels;

    public LevelsAdapter(Context context, LevelStateManager levels) {
        this.context = context;
        this.levels = levels;
    }

    @Override
    public int getCount() {
        return levels.getLevelsCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.levels_grid_item, null);
        } else {
            view = convertView;
        }
        TextView levelNumber = (TextView)view.findViewById(R.id.levelNumber);
        levelNumber.setText("" + position);
        return view;
    }
}
