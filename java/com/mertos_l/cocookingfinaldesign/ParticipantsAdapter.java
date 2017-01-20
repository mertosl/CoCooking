package com.mertos_l.cocookingfinaldesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class ParticipantsAdapter extends ArrayAdapter<ParticipantsFragmentItem> {
    private ArrayList<ParticipantsFragmentItem> listData = new ArrayList<ParticipantsFragmentItem>();
    private Context context;
    private int resourceId;

    ParticipantsAdapter(Context aContext, int fragment_participants_item, ArrayList<ParticipantsFragmentItem> listData) {
        super(aContext, fragment_participants_item, listData);
        this.listData = listData;
        this.context = aContext;
        this.resourceId = fragment_participants_item;
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
            holder.photo = (CircleImageView) itemView.findViewById(R.id.photo);
            holder.user = (TextView) itemView.findViewById(R.id.user);
            holder.price = (TextView) itemView.findViewById(R.id.price);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.id.setText(getItem(position).getId());
        holder.user.setText(getItem(position).getUser());
        holder.price.setText(getItem(position).getPrix());
        if (getItem(position).getPhoto() != null)
            holder.photo.setImageBitmap(getItem(position).getPhoto());
        return itemView;
    }

    private static class ViewHolder {
        TextView id;
        CircleImageView photo;
        TextView user;
        TextView price;
    }
}