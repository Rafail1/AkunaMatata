package by.raf.akunamatata.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import by.raf.akunamatata.R;
import by.raf.akunamatata.activities.PhotosPageViewActivity;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.GlobalVars;
import by.raf.akunamatata.model.Photo;

public class PhotosFragment extends Fragment {
    public static final String ARG_EVENT_POS = "ARG_EVENT_POS";
    @BindView(R.id.events_photos_recycler_view) RecyclerView mRecyclerView;
    private Event mEvent;
    private PhotoAdapter mPhotoAdapter;
    private ChildEventListener mPhotoListener;
    private ArrayList<Photo> mPhotos = new ArrayList<>();
    private DatabaseReference myRefPhotos;
    private Unbinder unbinder;

    public void unregisterPhotoListener() {
        myRefPhotos.removeEventListener(mPhotoListener);
    }

    public void registerPhotoListener() {

        myRefPhotos = DataProvider.getInstance().getDatabase().getReference("akunamatata/photos");
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

    public static PhotosFragment newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_POS, pos);
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
        unbinder = ButterKnife.bind(this, v);
        int eventPos = getArguments().getInt(ARG_EVENT_POS);
        mEvent = DataProvider.getInstance().getEventList().get(eventPos);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mPhotoAdapter = new PhotoAdapter(mPhotos);
        mRecyclerView.setAdapter(mPhotoAdapter);
        myRefPhotos = DataProvider.getInstance().getDatabase().getReference("akunamatata/photos");

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Photo mPhoto;
        @BindView(R.id.event_photo) ImageView mImageView;

        public PhotoHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bindPhoto(final Photo photo) {
            mPhoto = photo;
            ((GlobalVars)getContext().getApplicationContext())
                    .loadImage(mImageView, mPhoto.getUri());
            mImageView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Intent intent = PhotosPageViewActivity.newIntent(getContext(), mPhoto.getId(), mPhotos);
            startActivity(intent);
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
