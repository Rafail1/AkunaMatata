package by.raf.akunamatata.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Photo;


public class OnePhotoFragment extends Fragment {
    private static final String ARG_PHOTO_ID = "ARG_PHOTO_ID";
    private DatabaseReference mRef;
    private String mPid;
    private Photo mPhoto;
    private ImageView mImageView;
    private ValueEventListener mListener;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPid = getArguments().getString(ARG_PHOTO_ID);

        mRef = DataProvider.getInstance(getContext()).getDatabase().getReference("akunamatata/photos/"+mPid);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_photo, container, false);
        mImageView = (ImageView) view.findViewById(R.id.photo);
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPhoto = dataSnapshot.getValue(Photo.class);
                mImageView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                mImageView.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                                new Picasso.Builder(getContext())
                                        .build()
                                        .load(mPhoto.getUri())
                                        .rotate(90)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .resize(mImageView.getHeight(), mImageView.getWidth())
                                        .placeholder(R.drawable.ic_stat_ic_notification)
                                        .error(R.drawable.uploading)
                                        .into(mImageView, new Callback() {
                                            @Override
                                            public void onSuccess(){}
                                            @Override
                                            public void onError() {
                                                Picasso.with(getContext().getApplicationContext())
                                                        .load(Uri.parse(mPhoto.getUri()))
                                                        .rotate(90)
                                                        .resize(mImageView.getHeight(), mImageView.getWidth())
                                                        .placeholder(R.drawable.ic_stat_ic_notification)
                                                        .error(R.drawable.uploading)
                                                        .into(mImageView);
                                            }
                                        });
                            }
                        });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRef.addListenerForSingleValueEvent(mListener);
    }
    @Override
    public void onPause() {
        mRef.removeEventListener(mListener);
        super.onPause();
    }
}
