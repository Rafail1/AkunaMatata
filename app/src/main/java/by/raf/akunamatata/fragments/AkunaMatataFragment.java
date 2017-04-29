package by.raf.akunamatata.fragments;

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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

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
    private UserManager mUserManager;
    private NetworkManager mNetworkManager;
    private RecyclerView mRecyclerView;
    private DataProvider mDataProvider;
    private RecyclerView.Adapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView.LayoutManager mLayoutManager;
    public static AkunaMatataFragment newInstance() {
        return new AkunaMatataFragment();
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof ServerListener) {
            Class entityClass = (Class) o;
            if(entityClass == Event.class) {
                mAdapter.notifyDataSetChanged();
            }
        } else if(observable instanceof NetworkManager) {
            if(NetworkManager.getInstance().isNetworkConnected(getActivity())) {
                if(!mUserManager.isAuth()) {
                    mUserManager.auth(mGoogleApiClient);
                }
            } else {
                getActivity().invalidateOptionsMenu();
            }
        } else if(observable instanceof UserManager) {
            getActivity().invalidateOptionsMenu();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserManager = UserManager.getInstance();
        mNetworkManager = NetworkManager.getInstance();
        mDataProvider = DataProvider.getInstance(getContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mUserManager.auth(mGoogleApiClient);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserManager.addObserver(this);
        mNetworkManager.addObserver(this);
        mDataProvider.addObserver(this);
        mNetworkManager.registerReceiver(getContext());
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mUserManager.deleteObserver(this);
        mNetworkManager.deleteObserver(this);
        mDataProvider.deleteObserver(this);
        mNetworkManager.unregisterReceiver(getContext());
        mGoogleApiClient.disconnect();
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

        mAdapter = new EventAdapter(getActivity(), mDataProvider.getEventList());
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem exit = menu.findItem(R.id.menu_sign_out);
        if(!mUserManager.isAuth() || !NetworkManager.getInstance().isNetworkConnected(getActivity())) {
            exit.setTitle(R.string.menu_offline);
        } else {
            exit.setTitle(R.string.menu_sign_out);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                if(mUserManager.isAuth()) {
                    mUserManager.logout(getContext(), mGoogleApiClient);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
