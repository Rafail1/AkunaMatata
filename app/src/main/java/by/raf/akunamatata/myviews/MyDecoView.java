package by.raf.akunamatata.myviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;

/**
 * Created by raf on 5/3/17.
 */

public class MyDecoView extends DecoView {
    static final public int COLOR_BLUE = Color.parseColor("#1D76D2");
    static final public int COLOR_PINK = Color.parseColor("#FF4081");
    static final public int COLOR_YELLOW = Color.parseColor("#FFC107");
    static final public int COLOR_BACK = Color.parseColor("#FF777777");
    private int mBack1Index;
    private int mSeries1Index;
    private int mSeries2Index;
    private int mSeries3Index;
    private int mDuration;
    private float full = 100;
    SeriesItem.Builder b0;
    SeriesItem.Builder b1;
    SeriesItem.Builder b2;
    SeriesItem.Builder b3;

    public MyDecoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected float getDimension(float base) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base, getResources().getDisplayMetrics());
    }

    public void setupEvents(float[] stats) {
        executeReset();
        renew(stats);
    }

    public void createTracks() {
        executeReset();
        deleteAll();
        prepare(getDimension(46));
        mBack1Index = addSeries(b0.build());
        mSeries1Index = addSeries(b1.setSeriesLabel(new SeriesLabel.Builder("Парни").build()).build());
        mSeries2Index = addSeries(b2.setSeriesLabel(new SeriesLabel.Builder("Девушки").build()).build());
        mSeries3Index = addSeries(b3.setSeriesLabel(new SeriesLabel.Builder("Остальные)))").build()).build());
    }

    public void renew(float[] stats) {
        if (stats[2] > 0) {
            float mans = stats[0];
            float girls = stats[1];

            addEvent(new DecoEvent.Builder(full).setIndex(mSeries1Index).setDuration(mDuration).build());
            addEvent(new DecoEvent.Builder(full - mans).setIndex(mSeries2Index).setDuration(mDuration).build());
            addEvent(new DecoEvent.Builder(full - mans - girls).setIndex(mSeries3Index).setDuration(mDuration).build());
        } else {
            addEvent(new DecoEvent.Builder(0).setIndex(mSeries1Index).setDuration(mDuration).build());
            addEvent(new DecoEvent.Builder(0).setIndex(mSeries2Index).setDuration(mDuration).build());
            addEvent(new DecoEvent.Builder(0).setIndex(mSeries3Index).setDuration(mDuration).build());
            addEvent(new DecoEvent.Builder(100).setIndex(mBack1Index).setDuration(mDuration).build());
        }
    }

    public void start(float[] stats) {
        createTracks();
        setDuration(500);
        setupEvents(stats);
    }

    private void prepare(float lineWidth) {
        b0 = new SeriesItem.Builder(COLOR_BACK)
                .setRange(0, full, full)
                .setLineWidth(lineWidth)
                .setInitialVisibility(false);
        b1 = new SeriesItem.Builder(COLOR_BLUE)
                .setRange(0, full, 0)
                .setInitialVisibility(false)
                .setShowPointWhenEmpty(false)
                .setLineWidth(lineWidth)
                .setCapRounded(false);

        b2 = new SeriesItem.Builder(COLOR_PINK)
                .setRange(0, full, 0)
                .setInitialVisibility(false)
                .setShowPointWhenEmpty(false)
                .setLineWidth(lineWidth)
                .setCapRounded(false);
        b3 = new SeriesItem.Builder(COLOR_YELLOW)
                .setRange(0, full, 0)
                .setInitialVisibility(false)
                .setShowPointWhenEmpty(false)
                .setLineWidth(lineWidth)
                .setCapRounded(false);
    }

    public void setupMiniEvents(float[] stats) {
        prepare(15f);
        setDuration(100);
        addSeries(b0.build());
        mSeries1Index = addSeries(b1.build());
        mSeries2Index = addSeries(b2.build());
        mSeries3Index = addSeries(b3.build());
        setupEvents(stats);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }
}
