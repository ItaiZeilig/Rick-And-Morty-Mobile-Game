package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<Integer> imagesId;
    private ArrayList<Score> scores;
    private Context mContext;
    private Score userScore;


    public RecyclerViewAdapter(Context mContext, ArrayList<Integer> imagesId, ArrayList<Score> scores,Score userScore) {
        this.imagesId = imagesId;
        this.scores = scores;
        this.mContext = mContext;
        this.userScore = userScore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(imagesId.get(position));

        holder.name.setText(scores.get(position).getUserName());
        holder.score.setText(String.valueOf(scores.get(position).getScore()));

        if(userScore != null && scores.get(position).getScore() == userScore.getScore() &&
                scores.get(position).getUserName().equals(userScore.getUserName()) ){
            holder.itemLayout.setBackgroundColor(Color.YELLOW);
        }

//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
//
//                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(mContext, GalleryActivity.class);
//                intent.putExtra("image_url", mImages.get(position));
//                intent.putExtra("image_name", mImageNames.get(position));
//                mContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView score;
        private RelativeLayout itemLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.name);
            this.score = itemView.findViewById(R.id.score);
            this.itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }
}
