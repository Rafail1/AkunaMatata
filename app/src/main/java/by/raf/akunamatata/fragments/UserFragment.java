package by.raf.akunamatata.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SizeReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import by.raf.akunamatata.R;
import by.raf.akunamatata.model.GlobalVars;
import by.raf.akunamatata.model.User;
import by.raf.akunamatata.model.managers.UserManager;

/**
 * Created by raf on 5/27/17.
 */

public class UserFragment extends Fragment {
    @BindView(R.id.user_photo)
    ImageView mUserPhoto;
    @BindView(R.id.user_name)
    TextView mUserName;
    private Unbinder unbinder;
    private User me;

    public static UserFragment newInstance() {
        Bundle args = new Bundle();
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);
        me = UserManager.getInstance().getCurrentUser(getContext());
        if(me.getPicture() != null) {
            ((GlobalVars) getActivity().getApplicationContext()).loadImage(mUserPhoto, me.getPicture());
        }


        mUserName.setText(me.getName()+" "+me.getLastName());
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
