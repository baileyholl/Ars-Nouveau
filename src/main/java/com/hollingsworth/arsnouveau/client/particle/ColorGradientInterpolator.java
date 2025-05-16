package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.gui.Color;

import java.util.List;
import java.util.function.Supplier;

public class ColorGradientInterpolator {

    Supplier<Double> getProgress;
    List<ColorPoint> timeline;

    public ColorGradientInterpolator(Supplier<Double> getProgress, List<ColorPoint> timeline) {
        this.timeline = timeline;
        this.getProgress = getProgress;
        if(timeline.size() < 2) {
            throw new IllegalArgumentException("Color gradient must have at least 2 colors");
        }
    }

    public Color getCurrentColor() {
        double t1 = 0;
        double t2 = 0;
        Color c1 = null;
        Color c2 = null;
        double currentProgress = getProgress.get();
        for(var entry : timeline) {
            if(entry.time() <= currentProgress) {
                t1 = entry.time();
                c1 = entry.color();
            } else {
                t2 = entry.time();
                c2 = entry.color();
                break;
            }
        }
        if(c1 == null){
            return c2;
        }
        if(c2 == null){
            return c1;
        }
        double progress = (currentProgress - t1) / (t2 - t1);
        return Color.interpolate(c1, c2, progress);
    }
}
