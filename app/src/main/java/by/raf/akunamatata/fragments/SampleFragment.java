package by.raf.akunamatata.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;

import com.hookedonplay.decoviewlib.DecoView;

import by.raf.akunamatata.R;

abstract public class SampleFragment extends Fragment {
    protected final String TAG = getClass().getSimpleName();
    static final public int COLOR_BLUE = Color.parseColor("#1D76D2");
    static final public int COLOR_PINK = Color.parseColor("#FF4081");
    static final public int COLOR_YELLOW = Color.parseColor("#FFC107");
    static final public int COLOR_EDGE = Color.parseColor("#22000000");
    static final public int COLOR_BACK = Color.parseColor("#0166BB66");
    static final public float mSeriesMax = 100f;

    private boolean mInitialized;

    protected boolean createAnimation() {
        if (mInitialized) {
            createTracks();
            if (super.getUserVisibleHint()) {
                setupEvents();
            }
            return true;
        }
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                createAnimation();
            }
        } else {
            stopFragment();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null) {
            return;
        }

        mInitialized = true;
        createAnimation();
    }

    abstract protected void setupEvents();

    abstract protected void createTracks();

    protected void stopFragment() {
        final DecoView arcView = getDecoView();

        if (arcView == null || arcView.isEmpty()) {
            return;
        }
        arcView.executeReset();
        arcView.deleteAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        createAnimation();
    }

    protected float getDimension(float base) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base, getResources().getDisplayMetrics());
    }

    protected DecoView getDecoView() {
        if (getView() == null) {
            return null;
        }
        try {
            return (DecoView) getView().findViewById(R.id.dynamicArcView);
        } catch (NullPointerException npe) {
            Log.e(TAG, "Unable to resolve view " + npe.getMessage());
        }
        return null;
    }
}
