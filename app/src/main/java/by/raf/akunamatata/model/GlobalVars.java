package by.raf.akunamatata.model;

import android.app.Application;
import android.widget.ImageView;

    import com.bumptech.glide.Glide;


public class GlobalVars extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }


    public void loadImage(final ImageView mPicture, final String uri) {
        Glide.with(getApplicationContext()).load(uri).into(mPicture);
    }
}
