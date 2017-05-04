package by.raf.akunamatata.recyclerdata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.raf.akunamatata.R;
import by.raf.akunamatata.fragments.AkunaMatataFragment;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.managers.UserManager;
import by.raf.akunamatata.myviews.IWill;
import by.raf.akunamatata.myviews.MyDecoView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private List<Event> mDataset;
    private AkunaMatataFragment.Callbacks mCallbacks;
    private Context mContext;
    private Event currentEvent;

    public EventAdapter(Context context, AkunaMatataFragment.Callbacks callbacks, ArrayList<Event> myDataset) {
        mContext = context;
        mDataset = myDataset;
        mCallbacks = callbacks;
    }

    class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mEventPicture;
        private TextView mEventAuthor;
        private TextView mEventTitle;
        private TextView mEventDetails;
        private MyDecoView mEventCount;
        private TextView mEventDate;
        private IWill mEventAction;


        EventHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mEventDetails = (TextView) v.findViewById(R.id.event_detail_text);
            mEventPicture = (ImageView) v.findViewById(R.id.event_image);
            mEventAuthor = (TextView) v.findViewById(R.id.event_author);
            mEventTitle = (TextView) v.findViewById(R.id.event_title);
            mEventCount = (MyDecoView) v.findViewById(R.id.circle_view);
            mEventDate = (TextView) v.findViewById(R.id.event_date);
            mEventAction = (IWill) v.findViewById(R.id.button_will_be);

        }

        @Override
        public void onClick(View view) {
            mCallbacks.onEventSelected(getAdapterPosition());
        }
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventHolder(v);
    }


    @Override
    public void onBindViewHolder(final EventHolder holder, int position) {
        Picasso.with(mContext.getApplicationContext()).load(mDataset.get(position).getPicture())
                .resize(100, 100).centerCrop().into(holder.mEventPicture);
        currentEvent = mDataset.get(position);
        holder.mEventTitle.setText(currentEvent.getTitle());
        holder.mEventAuthor.setText(currentEvent.getAuthor());
        holder.mEventDetails.setText(currentEvent.getDescription());
        MyDecoView decoView = holder.mEventCount;
        float stats[] = currentEvent.mygetStat();

        if(decoView.isEmpty()) {
            decoView.setupMiniEvents(stats);
        } else {
            decoView.renew(stats);

        }
        Locale ru = new Locale("ru", "RU", "RU");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, ru);
        holder.mEventDate.setText(df.format(currentEvent.getDateStart()));
        final String myId = UserManager.getInstance().getUID();
        if (myId == null) {
            holder.mEventAction.setVisibility(View.INVISIBLE);
        } else {
            holder.mEventAction.setVisibility(View.VISIBLE);
            if (currentEvent.getUsers().containsKey(myId)) {
                holder.mEventAction.setText(mContext.getString(R.string.button_will_not_be));
            } else {
                holder.mEventAction.setText(mContext.getString(R.string.button_will_be));
            }
            holder.mEventAction.setClickListener(currentEvent, myId, mContext);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}


