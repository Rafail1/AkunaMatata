package by.raf.akunamatata.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.activities.PhotosActivity;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.GlobalVars;
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
    private static final String PARAM_PHOTO_FILE = "file_url";
    private static final int SELECT_PIC = 2;
    private static final int PROFILE_PIC_COUNT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private ImageView mPicture;
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mAddress;
    private TextView mDate;
    private TextView mDetails;
    private IWill mIWill;
    private MyDecoView arcView;
    private Button mHowItIs;
    private Event mEvent;
    private int pos;
    private Button mAddPhoto;
    private long currentImageTime;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Uri imageUri;
    private static final int SELECT_FILE = 2;
    private String mUploading;
    private String mUploadingInProgress;
    private String mUploadingError;
    private String mUploadingInSuccess;

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
        mEvent = DataProvider.getInstance().mEventList.get(pos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        NetworkManager.getInstance().addObserver(this);
        DataProvider.getInstance().addObserver(this);
        NetworkManager.getInstance().registerReceiver(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPicture != null) {
            mPicture.setImageDrawable(null);
        }
    }

    private int orientationPhoto(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
    }

    public void uploadPhoto(Uri uri) {
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        int orientation = 1;
        try {
            orientation = orientationPhoto(uri.getPath(), bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.error_camera), Toast.LENGTH_LONG).show();
            return;
        }
        if (!NetworkManager.getInstance().isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final Photo photo = new Photo();
        photo.setPath("images/" + mEvent.getId() + "/" + uri.getLastPathSegment());
        photo.setDateCreate(currentImageTime);
        photo.setEventId(mEvent.getId());
        photo.setOrientation(orientation);
        photo.setFromUser(UserManager.getInstance().getUID());
        StorageReference riversRef = storageRef.child("images/" + mEvent.getId() + "/" + uri.getLastPathSegment());
        final UploadTask task = riversRef.putFile(uri);


        final int id = 1;
        mNotifyManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setContentTitle(mUploading)
                .setContentText(mUploadingInProgress)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.uploading);
        task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") long bytesTransferred = taskSnapshot.getBytesTransferred();
                @SuppressWarnings("VisibleForTests") long bytesCount = taskSnapshot.getTotalByteCount();
                double progress = (100.0 * bytesTransferred) / bytesCount;

                mBuilder.setProgress(100, (int) progress, false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mBuilder.setContentText(mUploadingInSuccess)
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                DatabaseReference myRefPhotos = DataProvider.getInstance().getDatabase().getReference("akunamatata/photos");
                String id = myRefPhotos.push().getKey();
                photo.setId(id);

                @SuppressWarnings("VisibleForTests") Uri d = taskSnapshot.getDownloadUrl();
                photo.setUri(d.toString());
                myRefPhotos.child(id).setValue(photo);
                Log.d("d ", d.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mBuilder.setContentText(mUploadingError)
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStop() {
        NetworkManager.getInstance().deleteObserver(this);
        DataProvider.getInstance().deleteObserver(this);
        NetworkManager.getInstance().unregisterReceiver(getContext());
        super.onStop();
    }

    private static void requestPermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.permission_storage))
                    .setPositiveButton(R.string.tamam, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private File createImageFile() throws IOException {
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(getContext());
            return null;
        }
        long timeStamp = new Date().getTime();
        String myId = UserManager.getInstance().getUID();
        String imageFileName = "JPEG_" + timeStamp + "_" + myId;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }
        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        currentImageTime = timeStamp;
        return image;
    }

    private void getDialog() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = null;
        try {
            photo = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photo != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PARAM_PHOTO_FILE, imageUri);
        super.onSaveInstanceState(outState);

    }

    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return "file://" + cursor.getString(column_index);

        }
        return null;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK && imageUri != null) {
                    uploadPhoto(imageUri);
                }
                break;
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    uploadPhoto(Uri.parse(getPath(uri)));
                }
        }
    }


    private void fillImageView(final String url) {
        ((GlobalVars) getContext().getApplicationContext()).loadImage(mPicture, url);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        mPicture = (ImageView) v.findViewById(R.id.detail_event_image);
        mAuthor = (TextView) v.findViewById(R.id.detail_event_author);
        mDate = (TextView) v.findViewById(R.id.detail_event_date);
        mTitle = (TextView) v.findViewById(R.id.detail_event_title);
        mAddress = (TextView) v.findViewById(R.id.detail_event_address);
        mDetails = (TextView) v.findViewById(R.id.detail_event_description);
        mIWill = (IWill) v.findViewById(R.id.detail_event_i_will);
        mHowItIs = (Button) v.findViewById(R.id.detail_event_request);
        mAddPhoto = (Button) v.findViewById(R.id.detail_evrnt_add_photp);
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog();
            }
        });
        mUploading = getContext().getString(R.string.uploading);
        mUploadingInProgress = getContext().getString(R.string.progress_uploading);
        mUploadingError = getContext().getString(R.string.error_uploading);
        mUploadingInSuccess = getContext().getString(R.string.success_uploading);


        mAuthor.setText(mEvent.getAuthor());
        mTitle.setText(mEvent.getTitle());
        mAddress.setText(mEvent.getAddress());
        mDetails.setText(mEvent.getDescription());
        mIWill.setText(getString(R.string.button_will_be));

        Locale ru = new Locale("ru", "RU", "RU");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, ru);
        mDate.setText(df.format(mEvent.getDateStart()));

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
        fillImageView(mEvent.getPicture());
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
                        Event newEvent = DataProvider.getInstance().mEventList.get(args[2]);
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
            fillImageView(newEvent.getPicture());
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
