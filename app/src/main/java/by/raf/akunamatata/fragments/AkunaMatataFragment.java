package by.raf.akunamatata.fragments;
import android.content.Context;
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
}
