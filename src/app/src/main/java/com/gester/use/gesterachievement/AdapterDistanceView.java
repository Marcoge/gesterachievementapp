package com.gester.use.gesterachievement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USE on 10.06.2015.
 */
public class AdapterDistanceView extends ArrayAdapter<DistanceViewObject> {

    public AdapterDistanceView(Context context, ArrayList<DistanceViewObject> viewObjects) {
        super(context, 0, viewObjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DistanceViewObject viewObject = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }
        // Lookup view for data population
        ImageView picture = (ImageView)convertView.findViewById(R.id.imageViewListLayoutPic);
        TextView name = (TextView) convertView.findViewById(R.id.textViewListLayoutAchievementName);
        TextView distance = (TextView) convertView.findViewById(R.id.textViewListLayoutMiscText);
        // Populate the data into the template view using the data object

        picture.setImageDrawable(viewObject.getPicture());
        name.setText(viewObject.getName());
        distance.setText(viewObject.getDistance());
        // Return the completed view to render on screen
        return convertView;
    }
}