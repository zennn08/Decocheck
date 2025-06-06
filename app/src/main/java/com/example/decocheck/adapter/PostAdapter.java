package com.example.decocheck.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.decocheck.DetailActivity;
import com.example.decocheck.R;
import com.example.decocheck.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set title
        holder.tvTitle.setText(Html.fromHtml(post.getTitle(), Html.FROM_HTML_MODE_LEGACY));

        // Set author
        holder.tvAuthor.setText(post.getAuthor());

        // Format and set date
        holder.tvDate.setText(formatDate(post.getDate()));

        // Set excerpt
        String excerpt = Html.fromHtml(post.getExcerpt(), Html.FROM_HTML_MODE_LEGACY).toString().trim();
        holder.tvExcerpt.setText(excerpt);

        // Load thumbnail image
        if (post.getFeaturedImageUrl() != null && !post.getFeaturedImageUrl().isEmpty()) {
            holder.thumbnailCard.setVisibility(View.VISIBLE);

            RequestOptions requestOptions = new RequestOptions()
                    .transform(new RoundedCorners(24))
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image);

            Glide.with(context)
                    .load(post.getFeaturedImageUrl())
                    .apply(requestOptions)
                    .into(holder.ivThumbnail);
        } else {
            holder.thumbnailCard.setVisibility(View.GONE);
        }

        // Set click listener for entire item
        holder.itemView.setOnClickListener(v -> openDetailActivity(post));

        // Set click listener for read more button
        holder.btnReadMore.setOnClickListener(v -> openDetailActivity(post));

        // Set click listener for thumbnail
        holder.ivThumbnail.setOnClickListener(v -> openDetailActivity(post));
    }

    private void openDetailActivity(Post post) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("post_id", post.getId());
        intent.putExtra("post_title", post.getTitle());
        intent.putExtra("post_content", post.getContent());
        intent.putExtra("post_author", post.getAuthor());
        intent.putExtra("post_date", post.getDate());
        intent.putExtra("post_image", post.getFeaturedImageUrl());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);

            if (date != null) {
                // Calculate days ago
                long diff = System.currentTimeMillis() - date.getTime();
                long days = diff / (24 * 60 * 60 * 1000);

                if (days == 0) {
                    return "Hari ini";
                } else if (days == 1) {
                    return "1 hari yang lalu";
                } else if (days < 7) {
                    return days + " hari yang lalu";
                } else {
                    return outputFormat.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList = newPosts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvDate, tvExcerpt, btnReadMore;
        ImageView ivThumbnail;
        CardView thumbnailCard;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvExcerpt = itemView.findViewById(R.id.tvExcerpt);
            btnReadMore = itemView.findViewById(R.id.btnReadMore);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            thumbnailCard = itemView.findViewById(R.id.thumbnailCard);
        }
    }
}