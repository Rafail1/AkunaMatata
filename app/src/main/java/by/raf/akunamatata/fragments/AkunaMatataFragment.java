package by.raf.akunamatata.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import by.raf.akunamatata.R;
import by.raf.akunamatata.activities.AkunaMatataActivity;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.GlobalVars;
import by.raf.akunamatata.model.ServerListener;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;
import by.raf.akunamatata.myviews.IWill;

import static by.raf.akunamatata.activities.AkunaMatataActivity.RELOADED;
import static by.raf.akunamatata.model.Event.ADDED;
import static by.raf.akunamatata.model.Event.CHANGED;
import static by.raf.akunamatata.model.Event.REMOVED;

public class AkunaMatataFragment extends Fragment implements Observer {
    private Unbinder unbinder;
    @BindView(R.id.events_recycler_view) RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Callbacks mCallbacks;
    private String searchText;

    public static AkunaMatataFragment newInstance() {
        return new AkunaMatataFragment();
    }

    public interface Callbacks {
        void onEventSelected(int eventPosition);
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
        DataProvider.getInstance().addObserver(this);
        NetworkManager.getInstance().registerReceiver(getContext());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new EventAdapter(mCallbacks, DataProvider.getInstance()
                    .getEventList());
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onStop() {
        NetworkManager.getInstance().deleteObserver(this);
        DataProvider.getInstance().deleteObserver(this);
        NetworkManager.getInstance().unregisterReceiver(getContext());
        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_akuna_matata, container, false);
        unbinder = ButterKnife.bind(this, v);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        ((AkunaMatataActivity)getActivity()).loadEvents();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                        break;
                    case RELOADED:
                        mAdapter.notifyDataSetChanged();
                        break;
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
        private Event mEvent;

        EventAdapter(AkunaMatataFragment.Callbacks callbacks, ArrayList<Event> myDataset) {
            mDataset = myDataset;
            mCallbacks = callbacks;
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
            return new EventHolder(v);
        }



        @Override
        public void onBindViewHolder(final EventHolder holder, int position) {
            mEvent = mDataset.get(position);
            holder.bindEvent(mEvent);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.event_image) ImageView mPicture;
        @BindView(R.id.event_author) TextView mAuthor;
        @BindView(R.id.event_title) TextView mTitle;
        @BindView(R.id.event_detail_text) TextView mDetails;
        @BindView(R.id.event_date) TextView mDate;
        @BindView(R.id.text_count) TextView mCount;
        @BindView(R.id.button_will_be) IWill mAction;

        private Event mEvent;

        void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;

                itemView.setVisibility(View.VISIBLE);
            }else{
                itemView.setVisibility(View.GONE);

                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        EventHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
        @OnClick(R.id.cardView)
        @Override
        public void onClick(View view) {
            mCallbacks.onEventSelected(getAdapterPosition());
        }

        private void reloadEventPhoto(final Event newEvent) {
            if (mPicture.getDrawable() == null || !mEvent.getPicture().equals(newEvent.getPicture())) {
                ((GlobalVars)getContext().getApplicationContext()).
                        loadImage(mPicture, newEvent.getPicture());
            }
            mEvent = newEvent;
        }
        void bindEvent(Event currentEvent) {
            reloadEventPhoto(currentEvent);

            mTitle.setText(mEvent.getTitle());
            mAuthor.setText(mEvent.getAuthor());
            mDetails.setText(mEvent.getDescription());
            mCount.setText(getString(R.string.text_will_be, String.valueOf(mEvent.getCount())));


            mDate.setText(android.text.format.DateFormat.format("dd MMM HH:mm",mEvent.getDateStart()));
            final String myId = UserManager.getInstance().getUID();
            if (myId == null) {
                mAction.setVisibility(View.INVISIBLE);
            } else {
                mAction.setVisibility(View.VISIBLE);
                if (mEvent.getUsers().containsKey(myId)) {
                    mAction.setText(getContext().getString(R.string.button_will_not_be));
                } else {
                    mAction.setText(getContext().getString(R.string.button_will_be));
                }
                mAction.setClickListener(mEvent, myId, getContext());
            }
            if(searchText != null) {
                if (!mEvent.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    setVisibility(false);
                } else {
                    setVisibility(true);

                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.top_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText);
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                clearFilterEvents();
                return true;
            }
        });


    }

    private void clearFilterEvents() {
        searchText = null;

    }

    private void filterEvents(String newText) {
        searchText = newText;
        mAdapter.notifyDataSetChanged();
    }
}
