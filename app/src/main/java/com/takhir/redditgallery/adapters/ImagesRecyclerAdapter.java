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

    private final ArrayList<ImageCard> imageCards;

    public ImagesRecyclerAdapter(ArrayList<ImageCard> imageCards) {
        this.imageCards = imageCards;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        viewHolder.image = imageCards.get(i).getImage();
//        viewHolder.title.setText(imageCards.get(i).getTitle());
        Picasso.get().load(imageCards.get(i).getTitle()).resize(1080, 0).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return imageCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //        TextView title;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            this.title = itemView.findViewById(R.id.title);
            this.image = itemView.findViewById(R.id.imageView);
        }
    }
}
