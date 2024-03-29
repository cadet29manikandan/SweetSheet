package com.krm.sweetsheet.sweetpick;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DimEffect implements Effect {

    private float Value;

    public DimEffect(float value) {
        Value = value;
    }

    public float getValue() {
        return Value;
    }

    @Override
    public void effect(ViewGroup vp, ImageView view) {
        view.setBackgroundColor(Color.argb((int) (150 * Value), 150, 150, 150));
    }
}
