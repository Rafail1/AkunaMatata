package by.raf.akunamatata.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.activities.PhotosActivity;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.ServerListener;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;
import by.raf.akunamatata.myviews.IWill;
import by.raf.akunamatata.myviews.MyDecoView;

import static by.raf.akunamatata.model.Event.CHANGED;
import static by.raf.akunamatata.model.Event.REMOVED;

public class EventFragment extends Fragment implements Observer {
    public static final String ARG_EVENT_POSITION = "EVENT_POSITION";
    private ImageView mPicture;
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mAddress;
    private TextView mDetails;
    private IWill mIWill;
    private MyDecoView arcView;
    private Button mHowItIs;
    private Event mEvent;
    private int pos;

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
        pos = getArguments().getInt(ARG_EVENT_POSITION, 0);
        mEvent = DataProvider.getInstance(getContext()).mEventList.get(pos);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        NetworkManager.getInstance().addObserver(this);
        DataProvider.getInstance(getContext()).addObserver(this);
        NetworkManager.getInstance().registerReceiver(getContext());
    }


    @Override
    public void onStop() {
        NetworkManager.getInstance().deleteObserver(this);
        DataProvider.getInstance(getContext()).deleteObserver(this);
        NetworkManager.getInstance().unregisterReceiver(getContext());
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
        mIWill = (IWill) v.findViewById(R.id.detail_event_i_will);
        mHowItIs = (Button) v.findViewById(R.id.detail_event_request);

        Picasso.with(getContext()).load(mEvent.getPicture()).into(mPicture);
        mAuthor.setText(mEvent.getAuthor());
        mTitle.setText(mEvent.getTitle());
        mAddress.setText(mEvent.getAddress());
        mDetails.setText(mEvent.getDescription());
        mIWill.setText(getString(R.string.button_will_be));
        mHowItIs.setText(getString(R.string.button_watch_photos));
        mHowItIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PhotosActivity.newIntent(getContext(), mEvent.getId()));
            }
        });
        arcView = (MyDecoView) v.findViewById(R.id.dynamicArcView);

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
            mIWill.setClickListener(mEvent, myId, getContext());
        }
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        arcView.disableHardwareAccelerationForDecoView();
        float[] stats = mEvent.mygetStat();
            arcView.start(stats);
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

        if (getUserVisibleHint()) {
            float[] newStats = newEvent.mygetStat();
            arcView.renew(newStats);
        }
        mEvent = newEvent;
    }
}
