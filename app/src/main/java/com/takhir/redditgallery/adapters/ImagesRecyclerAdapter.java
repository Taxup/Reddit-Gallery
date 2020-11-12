package com.takhir.redditgallery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.takhir.redditgallery.R;
import com.takhir.redditgallery.models.ImageCard;

import java.util.ArrayList;


public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ImagesRecyclerAdapter";

    private final ArrayList<ImageCard> imageCards;
    private OnLongImageListener onLongImageListener;

    public ImagesRecyclerAdapter(ArrayList<ImageCard> imageCards, OnLongImageListener onLongImageListener) {
        this.imageCards = imageCards;
        this.onLongImageListener = onLongImageListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_card, viewGroup, false);
        return new ViewHolder(view, onLongImageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String url = imageCards.get(i).getUrl();
        imageCards.get(i).setImage(viewHolder.image);
        Picasso.get().load(url).resize(1080, 0).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return imageCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ImageView image;
        OnLongImageListener onLongImageListener;

        public ViewHolder(@NonNull View itemView, OnLongImageListener onLongImageListener) {
            super(itemView);
            this.image = itemView.findViewById(R.id.imageView);
            this.onLongImageListener = onLongImageListener;

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            onLongImageListener.onLongImageClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnLongImageListener {
        void onLongImageClick(int position);
    }

}
