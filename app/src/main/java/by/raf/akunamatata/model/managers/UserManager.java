package by.raf.akunamatata.model.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth. UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.activities.SignInActivity;

/**
 * Created by raf on 4/23/17.
 */

public class UserManager extends Observable {
    private FirebaseAuth mAuth;
    public GoogleApiClient mGoogleApiClient;
    private List<Observer> loginListeners;
    private static final int OBSERVABLE_CODE = 1;
    private GoogleSignInAccount mAccount;
    private boolean sendingAuth;

    public UserManager() {
        loginListeners = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        loginListeners.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        loginListeners.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer listener : loginListeners) {
            listener.update(this, OBSERVABLE_CODE);
        }
    }

    public void auth() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);
            }
        } else {
            if(sendingAuth)  {
                return;
            }
            sendingAuth = true;
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    sendingAuth = false;
                    if (result.isSuccess()) {
                        GoogleSignInAccount acct = result.getSignInAccount();
                        firebaseAuthWithGoogle(acct);
                    }
                }

            });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if(isAuth()) {
            notifyObservers();
            return;
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            notifyObservers();
                        }

                    }
                });
    }

    public void logout(final Context context) {
        if(!NetworkManager.connected) {
            return;
        }
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Intent intent = new Intent(context, SignInActivity.class);
                                intent.putExtra(SignInActivity.LOGOUT_ACTION, true);
                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);

                            }
                        }
                    });
                }
            }
            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }
    public boolean isAuth() {
        if (mAuth.getCurrentUser() != null) {
            for (UserInfo user : mAuth.getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("google.com")) {
                    return true;
                }
            }
        }
        return false;
    }


    public void setAccount(GoogleSignInAccount account) {
        mAccount = account;
    }
}
