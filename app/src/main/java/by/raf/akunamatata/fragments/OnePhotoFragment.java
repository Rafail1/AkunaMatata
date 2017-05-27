package by.raf.akunamatata.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.GlobalVars;
import by.raf.akunamatata.model.Photo;


public class OnePhotoFragment extends Fragment {
    private static final String ARG_PHOTO_ID = "ARG_PHOTO_ID";
    private DatabaseReference mRef;
    private Photo mPhoto;
    @BindView(R.id.photo) ImageView mPicture;
    private ValueEventListener mListener;
    private Unbinder unbinder;

    public static OnePhotoFragment newInstance(String photoId) {

        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_ID, photoId);
        OnePhotoFragment fragment = new OnePhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_photo, container, false);
        unbinder = ButterKnife.bind(this, view);
        String pid = getArguments().getString(ARG_PHOTO_ID);
        mRef = DataProvider.getInstance().getDatabase().getReference("akunamatata/photos/"+ pid);
        loadImage();
        return view;
    }

    public void loadImage() {
        if(mPicture.getDrawable() != null) {
            return;
        }
        if(mPhoto == null) {
            mListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPhoto = dataSnapshot.getValue(Photo.class);
                    DisplayMetrics displayMetrics = new DisplayMetrics();


                    ((Activity) getContext()).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displayMetrics);
                    ((GlobalVars) getContext().getApplicationContext())
                            .loadImage(mPicture, mPhoto.getUri());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mRef.addListenerForSingleValueEvent(mListener);
        } else {
            ((GlobalVars) getContext().getApplicationContext())
                    .loadImage(mPicture, mPhoto.getUri());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRef.removeEventListener(mListener);
        if(mPicture != null) {
            mPicture.setImageDrawable(null);
        }
    }


}
