package com.mertos_l.cocookingfinaldesign;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

class QuestionRepasAdapter extends ArrayAdapter<QuestionRepasFragmentItem> {
    private Context mContext;
    private int questionResId;
    private ArrayList<QuestionRepasFragmentItem> data = new ArrayList<QuestionRepasFragmentItem>();
    private Resources resources;

    QuestionRepasAdapter(Context applicationContext, int questionResId, ArrayList<QuestionRepasFragmentItem> data) {
        super(applicationContext, questionResId, data);
        this.mContext = applicationContext;
        this.questionResId = questionResId;
        this.data = data;
        this.resources = applicationContext.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder = null;

        if (itemView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(questionResId, parent, false);
            holder = new ViewHolder();
            holder.subject = (TextView) itemView.findViewById(R.id.subject);
            holder.content = (TextView) itemView.findViewById(R.id.content);
            holder.user = (TextView) itemView.findViewById(R.id.user);
            holder.photo = (CircleImageView) itemView.findViewById(R.id.photo);
            holder.date = (TextView) itemView.findViewById(R.id.date);
            holder.content_ = (TextView) itemView.findViewById(R.id.content_);
            holder.user_ = (TextView) itemView.findViewById(R.id.user_);
            holder.date_ = (TextView) itemView.findViewById(R.id.date_);
            holder.containerq = (LinearLayout) itemView.findViewById(R.id.container_question);
            holder.containerr = (LinearLayout) itemView.findViewById(R.id.container_reponse);
            itemView.setTag(holder);
        }
        else {
            holder = (ViewHolder) itemView.getTag();
        }
        QuestionRepasFragmentItem item = getItem(position);
        if (item != null) {
            if (item.getIndex() == 0) {
                holder.containerq.setVisibility(View.VISIBLE);
                holder.containerr.setVisibility(View.GONE);
                holder.subject.setText(item.getSubject());
                holder.content.setText(item.getContent());
                holder.user.setText(item.getUser());
                holder.date.setText(item.getDate());
                if (item.getPhoto() != null)
                    holder.photo.setImageBitmap(item.getPhoto());
            } else if (item.getIndex() == 1) {
                holder.containerq.setVisibility(View.GONE);
                holder.containerr.setVisibility(View.VISIBLE);
                holder.user_.setText(item.getUser());
                holder.date_.setText(item.getDate());
                holder.content_.setText(item.getContent());
            }
        }
        return itemView;
    }

    private static class ViewHolder {
        TextView subject;
        TextView content;
        TextView date;
        TextView user;
        TextView content_;
        TextView date_;
        TextView user_;
        LinearLayout containerq;
        LinearLayout containerr;
        CircleImageView photo;
    }

}
