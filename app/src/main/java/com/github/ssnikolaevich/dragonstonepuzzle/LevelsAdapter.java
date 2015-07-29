package com.github.ssnikolaevich.dragonstonepuzzle;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ssnikolaevich.slidingpuzzle.LevelStateManager;

public class LevelsAdapter extends BaseAdapter {
    private Context context;
    private LevelStateManager levels;
    private Drawable lockedTileBackground;
    private Drawable unlockedTileBackground;

    public LevelsAdapter(Context context, LevelStateManager levels) {
        this.context = context;
        this.levels = levels;
        lockedTileBackground = context.getResources().getDrawable(R.mipmap.level_locked_tile_bg);
        unlockedTileBackground = context.getResources().getDrawable(R.mipmap.level_unlocked_tile_bg);
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

        final boolean levelIsLocked = levels.isLocked(position);

        RelativeLayout tileLayout = (RelativeLayout)view.findViewById(R.id.levelTileLayout);
        Drawable bg = levelIsLocked? lockedTileBackground : unlockedTileBackground;
        tileLayout.setBackground(bg);

        ImageView levelStateView = (ImageView) view.findViewById(R.id.levelTileStateImage);
        final int levelStateImageId = levelIsLocked?
                R.mipmap.lock : (levels.isSolved(position)? R.mipmap.level_state_solved : 0);
        levelStateView.setImageResource(levelStateImageId);

        TextView levelNumber = (TextView)view.findViewById(R.id.levelNumber);
        levelNumber.setText("" + (position + 1));
        return view;
    }
}
