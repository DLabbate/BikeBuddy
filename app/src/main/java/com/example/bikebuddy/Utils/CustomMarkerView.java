package com.example.bikebuddy.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.example.bikebuddy.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText("( " + durationToTime(e.getX())+" , "+ String.format("%.2f",e.getY())+ " )"); // set the entry-value as the display text
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2),-getHeight());
    }




    private String durationToTime(double seconds){

        int P1 = (int) seconds % 60;
        int P2 = (int) seconds / 60;
        int P3 = (int) P2 % 60;
        P2 = P2 / 60;

        String time = P2 + ":" + P3 + ":" + P1;
        return time;
    }
}
