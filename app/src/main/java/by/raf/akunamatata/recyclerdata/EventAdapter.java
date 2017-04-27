package by.raf.akunamatata.recyclerdata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.Event;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private List<Event> mDataset;
    private Context mContext;
    static class EventHolder extends RecyclerView.ViewHolder {
        private ImageView mEventPicture;
        private TextView mEventTitle;
        private TextView mEventDetails;
        private TextView mEventCount;

        EventHolder(View v) {
            super(v);
            mEventDetails = (TextView) v.findViewById(R.id.event_detail_text);
            mEventPicture = (ImageView) v.findViewById(R.id.event_image);
            mEventTitle = (TextView) v.findViewById(R.id.event_title);
            mEventCount = (TextView) v.findViewById(R.id.event_count);
        }
    }

    public EventAdapter(Context context, ArrayList<Event> myDataset) {
        mContext = context;

        mDataset = myDataset;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new EventHolder(v);
    }


    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Picasso.with(mContext.getApplicationContext()).load(mDataset.get(position).getPicture())
                .resize(100,100).centerCrop().into(holder.mEventPicture);
        holder.mEventTitle.setText(mDataset.get(position).getTitle());
        holder.mEventDetails.setText(mDataset.get(position).getDescription());
        holder.mEventCount.setText(String.valueOf(mDataset.get(position).getCount()));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


