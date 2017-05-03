package by.raf.akunamatata.services;

import android.accounts.Account;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.CoverPhoto;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.User;

import static by.raf.akunamatata.model.DataProvider.AKUNA_MATATA_PREFERENCES;


public class UserIntentService extends IntentService {
    public static final String PARAM_USER = "user";
    private static final String NAME = "by.raf.akunamatata.UserIntentService";
    private final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private GoogleSignInAccount mAcct;
    public UserIntentService() {
        super(NAME);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        mAcct = (GoogleSignInAccount) intent.getExtras().get("acct");
        String id = intent.getExtras().getString("id", null);

        List<String> scope = new ArrayList<>();
        scope.add(Scopes.PROFILE);
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(this, scope);
        credential.setSelectedAccount(new Account(mAcct.getEmail(), "com.google"));
        People service = new People.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();
        Person meProfile = null;
        try {
            meProfile = service.people().get("people/me").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (meProfile != null) {

            List<Gender> genders = meProfile.getGenders();

            List<Birthday> birthdays = meProfile.getBirthdays();
            List<Photo> photos = meProfile.getPhotos();
            Integer gender;
            String path;
            Long birthday;
            SharedPreferences sp = getSharedPreferences(AKUNA_MATATA_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(User.PREF_ID, id);
            if (genders != null && genders.size() > 0) {
                gender = genders.get(0).getValue().equals("male") ? User.GENDER_MAN : User.GENDER_WOMAN;
            } else {
                gender = User.GENDER_HZ;
            }
            editor.putInt(User.PREF_GENDER, gender);
            if (birthdays != null && birthdays.size() > 0) {
                com.google.api.services.people.v1.model.Date jDate = birthdays.get(0).getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, jDate.getYear());
                calendar.set(Calendar.MONTH, jDate.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, jDate.getDay());
                birthday = calendar.getTimeInMillis();
                editor.putLong(User.PREF_BIRTHDAY, birthday);

            }
            if (photos != null && photos.size() > 0) {
                path = photos.get(0).getUrl();
                editor.putString(User.PREF_PHOTO, path);
            }
            editor.putString(User.PREF_NAME, mAcct.getGivenName());
            editor.putString(User.PREF_LAST_NAME, mAcct.getFamilyName());
            editor.putInt(User.PREF_REGALE, User.REGALE);
            editor.putInt(User.PREF_WANT, User.WANT);
            editor.putInt(User.PREF_FREE, User.FREE);
            editor.commit();

            DataProvider.getInstance(this).updateCurrentUser(this);

        }
    }
}
