package by.raf.akunamatata.recyclerdata;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import by.raf.akunamatata.R;
import by.raf.akunamatata.fragments.AkunaMatataFragment;
import by.raf.akunamatata.fragments.SampleFragment;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.User;
import by.raf.akunamatata.model.managers.UserManager;

import static by.raf.akunamatata.fragments.SampleFragment.COLOR_BACK;
import static by.raf.akunamatata.fragments.SampleFragment.COLOR_PINK;
import static by.raf.akunamatata.fragments.SampleFragment.COLOR_YELLOW;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private List<Event> mDataset;
    private AkunaMatataFragment.Callbacks mCallbacks;
    private Context mContext;
    class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mEventPicture;
        private TextView mEventAuthor;
        private TextView mEventTitle;
        private TextView mEventDetails;
        private DecoView mEventCount;
        private TextView mEventDate;
        private Button mEventAction;
        private int series1Index;
        private int series2Index;
        private int series3Index;



        EventHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mEventDetails = (TextView) v.findViewById(R.id.event_detail_text);
            mEventPicture = (ImageView) v.findViewById(R.id.event_image);
            mEventAuthor = (TextView) v.findViewById(R.id.event_author);
            mEventTitle = (TextView) v.findViewById(R.id.event_title);
            mEventCount = (DecoView) v.findViewById(R.id.circle_view);
            mEventDate = (TextView) v.findViewById(R.id.event_date);
            mEventAction = (Button) v.findViewById(R.id.button_will_be);

        }

        @Override
        public void onClick(View view) {
            mCallbacks.onEventSelected(getAdapterPosition());
        }
    }

    public EventAdapter(Context context, AkunaMatataFragment.Callbacks callbacks, ArrayList<Event> myDataset) {
        mContext = context;
        mDataset = myDataset;
        mCallbacks = callbacks;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new EventHolder(v);
    }

    protected void setupEvents(EventHolder holder, Event currentEvent) {


        HashMap<String, Integer> users = currentEvent.getUsers();
        Iterator it = users.entrySet().iterator();
        int full = 0;
        int mans = 0;
        int girls = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int mask = (int) pair.getValue();
            if ((mask & User.GENDER_MAN) > 0) {
                mans++;
            } else if((mask & User.GENDER_WOMAN) > 0) {
                girls++;
            }
            full++;
        }
        float lineWidth = 15f;
        holder.mEventCount.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(lineWidth)
                .build());

        holder.mEventCount.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(0)
                .build());
        if(full >0) {

            SeriesItem series1Item = new SeriesItem.Builder(SampleFragment.COLOR_BLUE)
                    .setRange(0, full, 0)
                    .setLineWidth(lineWidth)
                    .setCapRounded(false)
                    .build();

            holder.series1Index = holder.mEventCount.addSeries(series1Item);

            SeriesItem series2Item = new SeriesItem.Builder(COLOR_PINK)
                    .setRange(0, full, 0)
                    .setLineWidth(lineWidth)
                    .setCapRounded(false)
                    .build();

            holder.series2Index = holder.mEventCount.addSeries(series2Item);

            SeriesItem series3Item = new SeriesItem.Builder(COLOR_YELLOW)
                    .setRange(0, full, 0)
                    .setLineWidth(lineWidth)
                    .setCapRounded(false)
                    .build();
            holder.series3Index = holder.mEventCount.addSeries(series3Item);

            holder.mEventCount.addEvent(new DecoEvent.Builder(full)
                    .setIndex(holder.series1Index)
                    .setDuration(0)
                    .build());
            if(full - mans > 0) {

                holder.mEventCount.addEvent(new DecoEvent.Builder(full - mans)
                        .setIndex(holder.series2Index)
                        .setDuration(0)
                        .build());
            }
            if(full - mans - girls > 0) {
                holder.mEventCount.addEvent(new DecoEvent.Builder(full - mans - girls)
                        .setIndex(holder.series3Index)
                        .setDuration(0)
                        .build());
            }
        }
    }
    @Override
    public void onBindViewHolder(final EventHolder holder, int position) {
        Picasso.with(mContext.getApplicationContext()).load(mDataset.get(position).getPicture())
                .resize(100,100).centerCrop().into(holder.mEventPicture);
        final Event currentEvent = mDataset.get(position);
        holder.mEventTitle.setText(currentEvent.getTitle());
        holder.mEventAuthor.setText(currentEvent.getAuthor());
        holder.mEventDetails.setText(currentEvent.getDescription());
        DecoView decoView = holder.mEventCount;
        decoView.executeReset();
        decoView.deleteAll();
        setupEvents(holder, currentEvent);
        Locale ru = new Locale("ru", "RU", "RU");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, ru);
        holder.mEventDate.setText(df.format(currentEvent.getDateStart()));
        final String myId = UserManager.getInstance().getUID();
        if(myId == null) {
            holder.mEventAction.setVisibility(View.INVISIBLE);
        } else {
            holder.mEventAction.setVisibility(View.VISIBLE);
            if (currentEvent.getUsers().containsKey(myId)) {
                holder.mEventAction.setText(mContext.getString(R.string.button_will_not_be));
            } else {
                holder.mEventAction.setText(mContext.getString(R.string.button_will_be));
            }
            holder.mEventAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int stringId;

                    if (currentEvent.getUsers().containsKey(myId)) {
                        stringId = R.string.button_will_be;
                        currentEvent.getUsers().remove(myId);
                    } else {
                        currentEvent.getUsers().put(myId, UserManager.getInstance().getCurrentUserMask(mContext));
                        stringId = R.string.button_will_not_be;
                    }
                    holder.mEventAction.setText(mContext.getString(stringId));
                    DataProvider.getInstance(mContext).updateEntity(currentEvent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}


