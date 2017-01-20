package com.mertos_l.cocookingfinaldesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

class ParticipationsAdapter extends ArrayAdapter<ParticipationsFragmentItem> {
    private ArrayList<ParticipationsFragmentItem> listData = new ArrayList<ParticipationsFragmentItem>();
    private Context context;
    private int resourceId;

    ParticipationsAdapter(Context aContext, int layoutResourceId, ArrayList<ParticipationsFragmentItem> listData) {
        super(aContext, layoutResourceId, listData);
        this.listData = listData;
        this.context = aContext;
        this.resourceId = layoutResourceId;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder;
        if (convertView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(resourceId, parent, false);

            holder = new ViewHolder();
            holder.id = (TextView) itemView.findViewById(R.id.id);
            holder.title = (TextView) itemView.findViewById(R.id.title);
            holder.description = (TextView) itemView.findViewById(R.id.description);
            holder.date = (TextView) itemView.findViewById(R.id.date);
            holder.price = (TextView) itemView.findViewById(R.id.price);
            holder.nbr_participants = (TextView) itemView.findViewById(R.id.nombre_participants);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.id.setText(getItem(position).getId());
        holder.title.setText(getItem(position).getTitle());
        holder.description.setText(getItem(position).getDescription());
        holder.date.setText(getItem(position).getDate());
        holder.price.setText(getItem(position).getPrix());
        holder.nbr_participants.setText(getItem(position).getNombre_participants());
        return itemView;
    }

    private static class ViewHolder {
        TextView id;
        TextView title;
        TextView description;
        TextView date;
        TextView price;
        TextView nbr_participants;
    }
}
