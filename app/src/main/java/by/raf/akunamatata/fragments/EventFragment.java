package by.raf.akunamatata.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.ServerListener;
import by.raf.akunamatata.model.User;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;

import static by.raf.akunamatata.fragments.AkunaMatataFragment.CHANGED;
import static by.raf.akunamatata.fragments.AkunaMatataFragment.REMOVED;


public class EventFragment extends SampleFragment implements Observer {
    public static final String ARG_EVENT_POSITION = "EVENT_POSITION";
    private ImageView mPicture;
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mAddress;
    private TextView mDetails;
    private Button mIWill;
    private DecoView arcView;
    private Button mHowItIs;
    private Event mEvent;
    private NetworkManager mNetworkManager;
    private DataProvider mDataProvider;

    private int mSeries1Index;
    private int mSeries2Index;
    private int mSeries3Index;
    private int mBack1Index;
    private int pos;
    float mans;
    float girls;
    int full = 100;
    private int mDuration;

    public static EventFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_POSITION, position);
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkManager = NetworkManager.getInstance();
        mDataProvider = DataProvider.getInstance(getContext());
        pos = getArguments().getInt(ARG_EVENT_POSITION, 0);
        mEvent = DataProvider.getInstance(getContext()).mEventList.get(pos);
        mDuration = 500;
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        mNetworkManager.addObserver(this);
        mDataProvider.addObserver(this);
        mNetworkManager.registerReceiver(getContext());
    }


    @Override
    public void onStop() {
        mNetworkManager.deleteObserver(this);
        mDataProvider.deleteObserver(this);
        mNetworkManager.unregisterReceiver(getContext());
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        mPicture = (ImageView) v.findViewById(R.id.detail_event_image);
        mAuthor = (TextView) v.findViewById(R.id.detail_event_author);
        mTitle = (TextView) v.findViewById(R.id.detail_event_title);
        mAddress = (TextView) v.findViewById(R.id.detail_event_address);
        mDetails = (TextView) v.findViewById(R.id.detail_event_description);
        mIWill = (Button) v.findViewById(R.id.detail_event_i_will);
        mHowItIs = (Button) v.findViewById(R.id.detail_event_request);

        Picasso.with(getContext()).load(mEvent.getPicture()).into(mPicture);
        mAuthor.setText(mEvent.getAuthor());
        mTitle.setText(mEvent.getTitle());
        mAddress.setText(mEvent.getAddress());
        mDetails.setText(mEvent.getDescription());
        mIWill.setText(getString(R.string.button_will_be));
        mHowItIs.setText(getString(R.string.button_request));

        arcView = (DecoView) v.findViewById(R.id.dynamicArcView);
        final String myId = UserManager.getInstance().getUID();
        if (myId == null) {
            mIWill.setVisibility(View.INVISIBLE);
        } else {
            mIWill.setVisibility(View.VISIBLE);
            if (mEvent.getUsers().containsKey(myId)) {
                mIWill.setText(getString(R.string.button_will_not_be));
            } else {
                mIWill.setText(getString(R.string.button_will_be));
            }
            mIWill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int stringId;

                    if (mEvent.getUsers().containsKey(myId)) {
                        stringId = R.string.button_will_be;
                        mEvent.getUsers().remove(myId);
                    } else {
                        mEvent.getUsers().put(myId, UserManager.getInstance().getCurrentUserMask(getContext()));
                        stringId = R.string.button_will_not_be;
                    }
                    mIWill.setText(getString(stringId));
                    DataProvider.getInstance(getContext()).updateEntity(mEvent);


                }
            });
        }
        return v;
    }

    @Override
    protected void setupEvents() {
        if (full == 0) {
            return;
        } else {
            final DecoView arcView = getDecoView();
            final View view = getView();
            if (arcView == null || arcView.isEmpty() || view == null) {
                return;
            }
            arcView.executeReset();
            arcView.addEvent(new DecoEvent.Builder(full)
                    .setIndex(mSeries1Index)
                    .setDuration(mDuration)
                    .build());

            arcView.addEvent(new DecoEvent.Builder(full - mans) // 50
                    .setIndex(mSeries2Index)
                    .setDuration(mDuration)
                    .build());
            arcView.addEvent(new DecoEvent.Builder(full - mans - girls) // 64
                    .setIndex(mSeries3Index)
                    .setDuration(mDuration)
                    .build());

            arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_COLOR_CHANGE, COLOR_BACK)
                    .setIndex(mBack1Index)
                    .setDelay(0)
                    .setDuration(mDuration * 3)
                    .build());
        }

    }

    @Override
    protected void createTracks() {
        final DecoView decoView = getDecoView();
        final View view = getView();
        if (decoView == null || view == null) {
            return;
        }

        float fullRaw = 0;
        float mansRaw = 0;
        float girlsRaw = 0;
        decoView.executeReset();
        decoView.deleteAll();


        HashMap<String, Integer> users = mEvent.getUsers();
        Iterator it = users.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int mask = (int) pair.getValue();
            if ((mask & User.GENDER_MAN) > 0) {
                mansRaw++;
            } else if ((mask & User.GENDER_WOMAN) > 0) {
                girlsRaw++;
            }
            fullRaw++;
        }

        if(fullRaw == 0) {
            girls = 0;
            mans = 0;

        } else {
            girls = girlsRaw / fullRaw * 100;
            mans = mansRaw / fullRaw * 100;
        }
        float circleInset = getDimension(23) - (getDimension(46) * 0.3f);
        SeriesItem seriesBack1Item = new SeriesItem.Builder(COLOR_BACK)
                .setRange(0, full, full)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_PIE)
                .setInset(new PointF(circleInset, circleInset))
                .build();

        mBack1Index = decoView.addSeries(seriesBack1Item);

        SeriesItem series1Item = new SeriesItem.Builder(COLOR_BLUE)
                .setRange(0, full, 0)
                .setInitialVisibility(false)
                .setLineWidth(getDimension(46))
                .setSeriesLabel(new SeriesLabel.Builder("Парни").build())
                .setCapRounded(false)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_INNER, COLOR_EDGE, 0.3f))
                .setShowPointWhenEmpty(false)
                .build();

        mSeries1Index = decoView.addSeries(series1Item);

        SeriesItem series2Item = new SeriesItem.Builder(COLOR_PINK)
                .setRange(0, full, 0)
                .setInitialVisibility(false)
                .setLineWidth(getDimension(46))
                .setSeriesLabel(new SeriesLabel.Builder("Девушки").build())
                .setCapRounded(false)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_INNER, COLOR_EDGE, 0.3f))
                .setShowPointWhenEmpty(false)
                .build();

        mSeries2Index = decoView.addSeries(series2Item);

        SeriesItem series3Item = new SeriesItem.Builder(COLOR_YELLOW)
                .setRange(0, full, 0)
                .setInitialVisibility(false)
                .setLineWidth(getDimension(46))
                .setSeriesLabel(new SeriesLabel.Builder("Остальные)))").build())
                .setCapRounded(false)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_INNER, COLOR_EDGE, 0.3f))
                .setShowPointWhenEmpty(false)
                .build();

        mSeries3Index = decoView.addSeries(series3Item);
    }

    @Override
    public void onResume() {
        super.onResume();
        arcView.disableHardwareAccelerationForDecoView();

    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof ServerListener) {

            int[] args = (int[]) o;
            if (args[2] != pos) {
                return;
            }
            if (args[0] == Event.OBSERVER_ID) {
                switch (args[1]) {
                    case CHANGED:
                        Event newEvent = DataProvider.getInstance(getContext()).mEventList.get(args[2]);
                        reloadEvent(newEvent);
                        break;
                    case REMOVED:
                        Log.d("Removed", "Event");
                        break;

                }

            }

        }
    }
    private void reloadEvent(Event newEvent) {

        if (!newEvent.getPicture().equals(mEvent.getPicture())) {
            Picasso.with(getContext()).load(mEvent.getPicture()).into(mPicture);
        }
        if (!newEvent.getAuthor().equals(mEvent.getAuthor())) {
            mAuthor.setText(newEvent.getAuthor());
        }
        if (!newEvent.getTitle().equals(mEvent.getTitle())) {
            mTitle.setText(newEvent.getTitle());
        }
        if (!newEvent.getAddress().equals(mEvent.getAddress())) {
            mAddress.setText(newEvent.getAddress());
        }
        if (!newEvent.getDescription().equals(mEvent.getDescription())) {
            mDetails.setText(newEvent.getDescription());
        }

        if(getUserVisibleHint()) {
            float newFull = 0;
            float newMans = 0;
            float newGirls = 0;


            HashMap<String, Integer> users = newEvent.getUsers();
            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                int mask = (int) pair.getValue();
                if ((mask & User.GENDER_MAN) > 0) {
                    newMans++;
                } else if ((mask & User.GENDER_WOMAN) > 0) {
                    newGirls++;
                }
                newFull++;
            }
            final DecoView decoView = getDecoView();
            if (newFull > 0) {
                girls = newGirls / newFull * 100;
                mans = newMans / newFull * 100;

                decoView.addEvent(new DecoEvent.Builder(full).setIndex(mSeries1Index).setDuration(mDuration).build());
                decoView.addEvent(new DecoEvent.Builder(full - mans).setIndex(mSeries2Index).setDuration(mDuration).build());
                decoView.addEvent(new DecoEvent.Builder(full - mans - girls).setIndex(mSeries3Index).setDuration(mDuration).build());
            } else {
                decoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries1Index).setDuration(mDuration).build());
                decoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries2Index).setDuration(mDuration).build());
                decoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries3Index).setDuration(mDuration).build());
            }
        }
        mEvent = newEvent;
    }
}
