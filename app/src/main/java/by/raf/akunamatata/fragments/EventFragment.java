package by.raf.akunamatata.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.activities.PhotosActivity;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.Photo;
import by.raf.akunamatata.model.ServerListener;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;
import by.raf.akunamatata.myviews.IWill;
import by.raf.akunamatata.myviews.MyDecoView;

import static by.raf.akunamatata.model.Event.CHANGED;
import static by.raf.akunamatata.model.Event.REMOVED;

public class EventFragment extends Fragment implements Observer {
    public static final String ARG_EVENT_POSITION = "EVENT_POSITION";
    private static final int TAKE_PICTURE = 1;
    private static final String TAG = "EVENT_FRAGMENT";
    private static final String PARAM_PHOTO_FILE = "file_url";
    private ImageView mPicture;
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mAddress;
    private TextView mDetails;
    private IWill mIWill;
    private MyDecoView arcView;
    private Button mHowItIs;
    private Event mEvent;
    private int pos;
    private Button mAddPhoto;
    private String currentImage;
    private long currentImageTime;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Uri imageUri;

    public static EventFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_POSITION, position);
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable(PARAM_PHOTO_FILE);
        }
        pos = getArguments().getInt(ARG_EVENT_POSITION, 0);
        mEvent = DataProvider.getInstance(getContext()).mEventList.get(pos);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        NetworkManager.getInstance().addObserver(this);
        DataProvider.getInstance(getContext()).addObserver(this);
        NetworkManager.getInstance().registerReceiver(getContext());
    }

    public void uploadPhoto(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final Photo photo = new Photo();
        photo.setPath("images/" + mEvent.getId() + "/" + uri.getLastPathSegment());
        photo.setDateCreate(currentImageTime);
        photo.setEventId(mEvent.getId());
        photo.setFromUser(UserManager.getInstance().getUID());
        StorageReference riversRef = storageRef.child("images/" + mEvent.getId() + "/" + uri.getLastPathSegment());
        final UploadTask task = riversRef.putFile(uri);
        final int id = 1;
        mNotifyManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setContentTitle("Picture Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.uploading);
        task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") long bytesTransferred = taskSnapshot.getBytesTransferred();
                @SuppressWarnings("VisibleForTests") long bytesCount = taskSnapshot.getTotalByteCount();
                double progress = (100.0 *  bytesTransferred ) / bytesCount;

                mBuilder.setProgress(100, (int) progress, false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mBuilder.setContentText(getContext().getString(R.string.download_completed))
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                DatabaseReference myRefPhotos = DataProvider.getInstance(getContext()).getDatabase().getReference("akunamatata/photos");
                String id = myRefPhotos.push().getKey();
                photo.setId(id);

                @SuppressWarnings("VisibleForTests") Uri d = taskSnapshot.getDownloadUrl();
                photo.setUri(d.toString());
                myRefPhotos.child(id).setValue(photo);
                Log.d("d ", d.toString());
            }
        });
    }

    @Override
    public void onStop() {
        NetworkManager.getInstance().deleteObserver(this);
        DataProvider.getInstance(getContext()).deleteObserver(this);
        NetworkManager.getInstance().unregisterReceiver(getContext());
        super.onStop();
    }

    private File createImageFile() throws IOException {
        long timeStamp = new Date().getTime();
        String myId = UserManager.getInstance().getUID();
        String imageFileName = "JPEG_" + timeStamp + "_" + myId;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentImageTime = timeStamp;
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = null;
        try {
            photo = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PARAM_PHOTO_FILE, imageUri);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK && imageUri != null) {
                    uploadPhoto(imageUri);
                }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        mPicture = (ImageView) v.findViewById(R.id.detail_event_image);
        mAuthor = (TextView) v.findViewById(R.id.detail_event_author);
        mTitle = (TextView) v.findViewById(R.id.detail_event_title);
        mAddress = (TextView) v.findViewById(R.id.detail_event_address);
        mDetails = (TextView) v.findViewById(R.id.detail_event_description);
        mIWill = (IWill) v.findViewById(R.id.detail_event_i_will);
        mHowItIs = (Button) v.findViewById(R.id.detail_event_request);
        mAddPhoto = (Button) v.findViewById(R.id.detail_evrnt_add_photp);
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        Picasso.with(getContext()).load(mEvent.getPicture()).centerCrop().resize(100, 100).into(mPicture);
        mAuthor.setText(mEvent.getAuthor());
        mTitle.setText(mEvent.getTitle());
        mAddress.setText(mEvent.getAddress());
        mDetails.setText(mEvent.getDescription());
        mIWill.setText(getString(R.string.button_will_be));
        mHowItIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PhotosActivity.newIntent(getContext(), pos));
            }
        });
        arcView = (MyDecoView) v.findViewById(R.id.dynamicArcView);

        final String myId = UserManager.getInstance().getUID();
        if (myId == null) {
            mIWill.setVisibility(View.INVISIBLE);
        } else {
            mIWill.setVisibility(View.VISIBLE);
            if (mEvent.getUsers().containsKey(myId)) {
                mIWill.setText(getString(R.string.button_will_not_be));
            } else {
                mIWill.setText(getString(R.string.button_will_be));
            }
            mIWill.setClickListener(mEvent, myId, getContext());
        }
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        arcView.disableHardwareAccelerationForDecoView();
        float[] stats = mEvent.mygetStat();
        arcView.start(stats);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof ServerListener) {
            int[] args = (int[]) o;
            if (args[2] != pos) {
                return;
            }
            if (args[0] == Event.OBSERVER_ID) {
                switch (args[1]) {
                    case CHANGED:
                        Event newEvent = DataProvider.getInstance(getContext()).mEventList.get(args[2]);
                        reloadEvent(newEvent);
                        break;
                    case REMOVED:
                        Log.d("Removed", "Event");
                        break;
                }

            }

        }
    }

    private void reloadEvent(Event newEvent) {

        if (!newEvent.getPicture().equals(mEvent.getPicture())) {
            Picasso.with(getContext()).load(mEvent.getPicture()).into(mPicture);
        }
        if (!newEvent.getAuthor().equals(mEvent.getAuthor())) {
            mAuthor.setText(newEvent.getAuthor());
        }
        if (!newEvent.getTitle().equals(mEvent.getTitle())) {
            mTitle.setText(newEvent.getTitle());
        }
        if (!newEvent.getAddress().equals(mEvent.getAddress())) {
            mAddress.setText(newEvent.getAddress());
        }
        if (!newEvent.getDescription().equals(mEvent.getDescription())) {
            mDetails.setText(newEvent.getDescription());
        }

        if (getUserVisibleHint()) {
            float[] newStats = newEvent.mygetStat();
            arcView.renew(newStats);
        }
        mEvent = newEvent;
    }
}
