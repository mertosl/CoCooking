package com.mertos_l.cocookingfinaldesign;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

class ResultatRechercheGridAdapter extends ArrayAdapter<ResultatRechercheGridItem> {
    private Context mContext;
    private int resourceId;
    private ArrayList<ResultatRechercheGridItem> data = new ArrayList<ResultatRechercheGridItem>();
    private Resources resources;

    ResultatRechercheGridAdapter(Context context, int fragment_resultats_recherche_item, ArrayList<ResultatRechercheGridItem> data) {
        super(context, fragment_resultats_recherche_item, data);
        this.mContext = context;
        this.resourceId = fragment_resultats_recherche_item;
        this.data = data;
        this.resources = context.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder = null;

        if (itemView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.titre = (TextView) itemView.findViewById(R.id.title);
            holder.places = (TextView) itemView.findViewById(R.id.places);
            holder.price = (TextView) itemView.findViewById(R.id.price);
            holder.photo = (ImageView) itemView.findViewById(R.id.photo);
            holder.date = (TextView) itemView.findViewById(R.id.date);
            itemView.setTag(holder);
        }
        else {
            holder = (ViewHolder) itemView.getTag();
        }
        ResultatRechercheGridItem item = getItem(position);
        if (item != null) {
            holder.titre.setText(item.getTitle());
            holder.places.setText(item.getPlaces_free());
            holder.price.setText(item.getPrice());
            holder.date.setText(item.getDate());
            if (item.getPhoto() != null)
                holder.photo.setImageBitmap(item.getPhoto());
        }
        return itemView;
    }

    private static class ViewHolder {
        TextView titre;
        TextView places;
        TextView price;
        TextView date;
        ImageView photo;
    }

    public final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return resources.getColor(id);
        }
    }
}
