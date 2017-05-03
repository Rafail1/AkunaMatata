package by.raf.akunamatata.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.ServerListener;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;
import by.raf.akunamatata.recyclerdata.EventAdapter;



public class AkunaMatataFragment extends Fragment implements Observer {
    public static final int CHANGED = 0;
    public static final int ADDED = 1;
    public static final int REMOVED = 2;
    private NetworkManager mNetworkManager;
    private RecyclerView mRecyclerView;
    private DataProvider mDataProvider;
    private RecyclerView.Adapter mAdapter;
    private Callbacks mCallbacks;
    private RecyclerView.LayoutManager mLayoutManager;
    public static AkunaMatataFragment newInstance() {
        return new AkunaMatataFragment();
    }
    public interface Callbacks {
        void onEventSelected(int eventPosition);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkManager = NetworkManager.getInstance();
        mDataProvider = DataProvider.getInstance(getContext());

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
        mNetworkManager.addObserver(this);
        mDataProvider.addObserver(this);
        mNetworkManager.registerReceiver(getContext());
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new EventAdapter(getActivity(), mCallbacks, mDataProvider.getEventList());
            mRecyclerView.setAdapter(mAdapter);
        }
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
        View v = inflater.inflate(R.layout.fragment_akuna_matata, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.events_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem exit = menu.findItem(R.id.menu_sign_out);
        if(NetworkManager.getInstance().isNetworkConnected(getActivity())) {
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
                if(NetworkManager.getInstance().isNetworkConnected(getActivity())) {
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
        if(observable instanceof ServerListener) {
            int[] args = (int[]) o;
            if(args[0] == Event.OBSERVER_ID) {
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
        } else if(observable instanceof NetworkManager) {
            if(NetworkManager.getInstance().isNetworkConnected(getActivity())) {
                getActivity().invalidateOptionsMenu();
            } else {
                getActivity().invalidateOptionsMenu();
                mAdapter.notifyDataSetChanged();
            }
        }
    }


}
