package by.raf.akunamatata.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.ServerListener;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;
import by.raf.akunamatata.myviews.IWill;
import by.raf.akunamatata.myviews.MyDecoView;

import static by.raf.akunamatata.model.Event.ADDED;
import static by.raf.akunamatata.model.Event.CHANGED;
import static by.raf.akunamatata.model.Event.REMOVED;

public class AkunaMatataFragment extends Fragment implements Observer {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Callbacks mCallbacks;

    public static AkunaMatataFragment newInstance() {
        return new AkunaMatataFragment();
    }

    public interface Callbacks {
        void onEventSelected(int eventPosition);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        NetworkManager.getInstance().addObserver(this);
        DataProvider.getInstance(getContext()).addObserver(this);
        NetworkManager.getInstance().registerReceiver(getContext());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new EventAdapter(getActivity(), mCallbacks, DataProvider.getInstance(getContext()).getEventList());
            mRecyclerView.setAdapter(mAdapter);
        }
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
        View v = inflater.inflate(R.layout.fragment_akuna_matata, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.events_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem exit = menu.findItem(R.id.menu_sign_out);
        if (NetworkManager.getInstance().isNetworkConnected(getActivity())) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                exit.setTitle(R.string.menu_sign_in);
            } else {
                exit.setTitle(R.string.menu_sign_out);
            }
        } else {
            exit.setTitle(R.string.menu_offline);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                if (NetworkManager.getInstance().isNetworkConnected(getActivity())) {
                    FirebaseAuth.getInstance().signOut();
                    UserManager.getInstance().logout(getContext());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof ServerListener) {
            int[] args = (int[]) o;
            if (args[0] == Event.OBSERVER_ID) {
                switch (args[1]) {
                    case CHANGED:
                        mAdapter.notifyItemChanged(args[2]);
                        break;
                    case REMOVED:
                        mAdapter.notifyItemRemoved(args[2]);
                        break;
                    case ADDED:
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                }

            }
        } else if (observable instanceof NetworkManager) {
            getActivity().invalidateOptionsMenu();
            if (!NetworkManager.getInstance().isNetworkConnected(getActivity())) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        private List<Event> mDataset;
        private Context mContext;
        private Event mEvent;

        EventAdapter(Context context, AkunaMatataFragment.Callbacks callbacks, ArrayList<Event> myDataset) {
            mContext = context;
            mDataset = myDataset;
            mCallbacks = callbacks;
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
            return new EventHolder(v);
        }

        private void reloadEventPhoto(final Event newEvent, final EventHolder holder) {
            if (holder.mPicture.getDrawable() == null || !newEvent.getPicture().equals(mEvent.getPicture())) {
                new Picasso.Builder(mContext)
                        .build()
                        .load(newEvent.getPicture())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .centerCrop()
                        .resize(100, 100)
                        .error(R.drawable.uploading)
                        .into(holder.mPicture, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext)
                                        .load(newEvent.getPicture())
                                        .centerCrop()
                                        .resize(100, 100)
                                        .placeholder(R.drawable.ic_stat_ic_notification)
                                        .error(R.drawable.uploading)
                                        .into(holder.mPicture);
                            }
                        });

            }
            mEvent = newEvent;
        }

        @Override
        public void onBindViewHolder(final EventHolder holder, int position) {
            reloadEventPhoto(mDataset.get(position), holder);
            mEvent = mDataset.get(position);
            holder.bindEvent(mEvent);
            holder.mTitle.setText(mEvent.getTitle());
            holder.mAuthor.setText(mEvent.getAuthor());
            holder.mDetails.setText(mEvent.getDescription());
            MyDecoView decoView = holder.arcView;
            float stats[] = mEvent.mygetStat();

            if (decoView.isEmpty()) {
                decoView.setupMiniEvents(stats);
            } else {
                decoView.renew(stats);

            }
            Locale ru = new Locale("ru", "RU", "RU");
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, ru);
            holder.mDate.setText(df.format(mEvent.getDateStart()));
            final String myId = UserManager.getInstance().getUID();
            if (myId == null) {
                holder.mAction.setVisibility(View.INVISIBLE);
            } else {
                holder.mAction.setVisibility(View.VISIBLE);
                if (mEvent.getUsers().containsKey(myId)) {
                    holder.mAction.setText(mContext.getString(R.string.button_will_not_be));
                } else {
                    holder.mAction.setText(mContext.getString(R.string.button_will_be));
                }
                holder.mAction.setClickListener(mEvent, myId, mContext);
            }

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPicture;
        private TextView mAuthor;
        private TextView mTitle;
        private TextView mDetails;
        private MyDecoView arcView;
        private TextView mDate;
        private IWill mAction;
        private Event mEvent;

        EventHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mDetails = (TextView) v.findViewById(R.id.event_detail_text);
            mPicture = (ImageView) v.findViewById(R.id.event_image);
            mAuthor = (TextView) v.findViewById(R.id.event_author);
            mTitle = (TextView) v.findViewById(R.id.event_title);
            arcView = (MyDecoView) v.findViewById(R.id.circle_view);
            mDate = (TextView) v.findViewById(R.id.event_date);
            mAction = (IWill) v.findViewById(R.id.button_will_be);

        }

        @Override
        public void onClick(View view) {
            mCallbacks.onEventSelected(getAdapterPosition());
        }

        public void bindEvent(Event currentEvent) {
            mEvent = currentEvent;
        }
    }
}
