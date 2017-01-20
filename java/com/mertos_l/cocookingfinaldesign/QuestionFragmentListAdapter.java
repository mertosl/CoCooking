package com.mertos_l.cocookingfinaldesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

class QuestionFragmentListAdapter extends BaseAdapter {
    private ArrayList<QuestionReponseFragmentListItem> listData;
    private LayoutInflater layoutInflater;

    QuestionFragmentListAdapter(Context aContext, ArrayList<QuestionReponseFragmentListItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_question_reponse_list_item, null);
            holder = new ViewHolder();
            holder.photo = (CircleImageView) convertView.findViewById(R.id.photo);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.sujet = (TextView) convertView.findViewById(R.id.sujet);
            holder.meal = (TextView) convertView.findViewById(R.id.meal);
            holder.user = (TextView) convertView.findViewById(R.id.user);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.message.setText(listData.get(position).getMessage());
        holder.sujet.setText(listData.get(position).getSujet());
        holder.meal.setText(listData.get(position).getMeal());
        holder.user.setText(listData.get(position).getUser());
        holder.date.setText(listData.get(position).getDate());
        if (listData.get(position) != null)
            holder.photo.setImageBitmap(listData.get(position).getPhoto());
        return convertView;
    }

    private static class ViewHolder {
        CircleImageView photo;
        TextView message;
        TextView sujet;
        TextView meal;
        TextView user;
        TextView date;
    }
}
