package com.example.isyarat;

import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class BndBox {
    private List<?> results;
    private int previewH;
    private int previewW;
    private double screenH;
    private double screenW;
    private String model;

    public BndBox(List<?> results, int previewH, int previewW, double screenH, double screenW, String model) {
        this.results = results;
        this.previewH = previewH;
        this.previewW = previewW;
        this.screenH = screenH;
        this.screenW = screenW;
        this.model = model;
    }

    public void renderBoxes(FrameLayout frameLayout) {
        for (Object re : results) {
            double x0 = ((Object) re).getRect().left;
            double w0 = ((Object) re).getRect().width();
            double y0 = ((Object) re).getRect().top;
            double h0 = ((Object) re).getRect().height();
            double scaleW, scaleH, x, y, w, h;

            if (screenH / screenW > previewH / previewW) {
                scaleW = screenH / previewH * previewW;
                scaleH = screenH;
                double difW = (scaleW - screenW) / scaleW;
                x = (x0 - difW / 2) * scaleW;
                w = w0 * scaleW;
                if (x0 < difW / 2) {
                    w -= (difW / 2 - x0) * scaleW;
                }
                y = y0 * scaleH;
                h = h0 * scaleH;
            } else {
                scaleH = screenW / previewW * previewH;
                scaleW = screenW;
                double difH = (scaleH - screenH) / scaleH;
                x = x0 * scaleW;
                w = w0 * scaleW;
                y = (y0 - difH / 2) * scaleH;
                h = h0 * scaleH;
                if (y0 < difH / 2) {
                    h -= (difH / 2 - y0) * scaleH;
                }
            }

            View boxView = new View(frameLayout.getContext());
            boxView.setLayoutParams(new FrameLayout.LayoutParams((int) w, (int) h));
            boxView.setX((float) Math.max(0, x));
            boxView.setY((float) Math.max(0, y));
            boxView.setBackground(ContextCompat.getDrawable(frameLayout.getContext(), R.drawable.box_background));

            TextView textView = new TextView(frameLayout.getContext());
            textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            textView.setPadding(10, 5, 0, 0);
            textView.setText(((Object) re).getDetectedClass() + " " + String.format("%.0f", ((Object) re).getConfidenceInClass() * 100) + "%");
            textView.setTextColor(ContextCompat.getColor(frameLayout.getContext(), R.color.box_text));
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.BOLD);
            boxView.addView(textView);

            frameLayout.addView(boxView);
        }
    }

    public void renderStrings(FrameLayout frameLayout) {
        double offset = -10;
        for (Object re : results) {
            offset += 14;
            TextView textView = new TextView(frameLayout.getContext());
            textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            textView.setX(10);
            textView.setY((float) offset);
            textView.setText(((Object) re).getLabel() + " " + String.format("%.0f", ((Object) re).getConfidence() * 100) + "%");
            textView.setTextColor(ContextCompat.getColor(frameLayout.getContext(), R.color.strings_text));
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.BOLD);

            frameLayout.addView(textView);
        }
    }

    public void renderKeypoints(FrameLayout frameLayout) {
        for (Object re : results) {
            for (Object k : ((Object) re).getKeypoints().values()) {
                double x0 = ((Object) k).getX();
                double y0 = ((Object) k).getY();
                double scaleW, scaleH, x, y;

                if (screenH / screenW > previewH / previewW) {
                    scaleW = screenH / previewH * previewW;
                    scaleH = screenH;
                    double difW = (scaleW - screenW) / scaleW;
                    x = (x0 - difW / 2) * scaleW;
                    y = y0 * scaleH;
                } else {
                    scaleH = screenW / previewW * previewH;
                    scaleW = screenW;
                    double difH = (scaleH - screenH) / scaleH;
                    x = x0 * scaleW;
                    y = (y0 - difH / 2) * scaleH;
                }

                TextView textView = new TextView(frameLayout.getContext());
                textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                textView.setX((float)
