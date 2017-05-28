package by.raf.akunamatata.model;

import android.app.Application;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;


public class GlobalVars extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }


    public Target<GlideDrawable> loadImage(ImageView mPicture, final String uri) {
        return Glide.with(getApplicationContext()).load(uri).dontTransform().into(mPicture);
    }
}
