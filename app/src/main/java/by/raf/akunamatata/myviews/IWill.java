package by.raf.akunamatata.myviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.managers.UserManager;

/**
 * Created by raf on 5/3/17.
 */

public class IWill extends android.support.v7.widget.AppCompatButton {
    public IWill(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setClickListener(final Event currentEvent, final String myId, final Context context) {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stringId;
                if (currentEvent.getUsers().containsKey(myId)) {
                    stringId = R.string.button_will_be;
                    currentEvent.getUsers().remove(myId);
                } else {
                    currentEvent.getUsers().put(myId, UserManager.getInstance().getCurrentUserMask(context));
                    stringId = R.string.button_will_not_be;
                }
                setText(context.getString(stringId));
                DataProvider.getInstance().updateEntity(currentEvent);
            }
        });
    }
}
