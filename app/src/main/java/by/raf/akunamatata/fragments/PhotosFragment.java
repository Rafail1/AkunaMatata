package by.raf.akunamatata.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.Photo;

public class PhotosFragment extends Fragment {
    public static final String ARG_EVENT_POS = "ARG_EVENT_POS";
    private RecyclerView mRecyclerView;
    private Event mEvent;
    private PhotoAdapter mPhotoAdapter;
    private ChildEventListener mPhotoListener;
    private ArrayList<Photo> mPhotos = new ArrayList<>();
    private DatabaseReference myRefPhotos;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public void unregisterPhotoListener() {
        myRefPhotos.removeEventListener(mPhotoListener);
    }

    public void registerPhotoListener() {

        myRefPhotos = DataProvider.getInstance(getContext()).getDatabase().getReference("akunamatata/photos");
        myRefPhotos.keepSynced(true);
        mPhotoListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Photo photo = dataSnapshot.getValue(Photo.class);
                for (int i = 0; i < mPhotos.size(); i++) {
                    if (mPhotos.get(i).getId().equals(photo.getId())) {
                        return;
                    }
                }
                mPhotos.add(photo);
                mPhotoAdapter.notifyItemInserted(mPhotos.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Photo photo = dataSnapshot.getValue(Photo.class);
                for (int i = 0; i < mPhotos.size(); i++) {
                    if (mPhotos.get(i).getId().equals(photo.getId())) {
                        mPhotos.remove(i);
                        mPhotoAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRefPhotos.orderByChild("eventId").equalTo(mEvent.getId()).addChildEventListener(mPhotoListener);
    }

    public static PhotosFragment newInstance(String pos) {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_POS, pos);
        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        registerPhotoListener();
    }

    @Override
    public void onStop() {
        unregisterPhotoListener();
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_photos, container, false);
        int eventPos = getArguments().getInt(ARG_EVENT_POS);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mEvent = DataProvider.getInstance(getContext()).getEventList().get(eventPos);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.events_photos_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mPhotoAdapter = new PhotoAdapter(mPhotos);
        mRecyclerView.setAdapter(mPhotoAdapter);
        myRefPhotos = DataProvider.getInstance(getContext()).getDatabase().getReference("akunamatata/photos");

        return v;
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Photo mPhoto;
        private ImageView mImageView;

        public PhotoHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.event_photo);
        }

        public void bindPhoto(final Photo photo) {
            mPhoto = photo;
            new Picasso.Builder(getContext().getApplicationContext())
                    .build()
                    .load(photo.getUri())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_stat_ic_notification)
                    .error(R.drawable.uploading)
                    .centerCrop().resize(100, 100)
                    .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mImageView.setOnClickListener(PhotoHolder.this);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext().getApplicationContext())
                                    .load(Uri.parse(photo.getUri()))
                                    .placeholder(R.drawable.ic_stat_ic_notification)
                                    .error(R.drawable.uploading)
                                    .into(mImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            mImageView.setOnClickListener(PhotoHolder.this);
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });

        }

        @Override
        public void onClick(View view) {

        }
    }

    public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<Photo> mPhotoList;

        public PhotoAdapter(List<Photo> photos) {
            mPhotoList = photos;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_photo, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            Photo photo = mPhotoList.get(position);
            holder.bindPhoto(photo);
        }

        @Override
        public int getItemCount() {
            return mPhotoList.size();
        }
    }
}
