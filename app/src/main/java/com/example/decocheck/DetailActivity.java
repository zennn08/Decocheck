package com.example.decocheck;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDate, tvContent;
    private ImageView ivFeaturedImage, btnBack, btnShare;
    private CardView imageCard;
    private ProgressBar progressBar;

    private String postTitle, postContent, postAuthor, postDate, postImageUrl;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Handle system bars properly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getIntentData();
        setupButtons();
        loadPostData();
    }

    private void initViews() {
        try {
            tvTitle = findViewById(R.id.tvTitle);
            tvAuthor = findViewById(R.id.tvAuthor);
            tvDate = findViewById(R.id.tvDate);
            tvContent = findViewById(R.id.tvContent);
            ivFeaturedImage = findViewById(R.id.ivFeaturedImage);
            imageCard = findViewById(R.id.imageCard);
            btnBack = findViewById(R.id.btnBack);
            btnShare = findViewById(R.id.btnShare);
            progressBar = findViewById(R.id.progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIntentData() {
        // Get data from intent
        postId = getIntent().getIntExtra("post_id", 0);
        postTitle = getIntent().getStringExtra("post_title");
        postContent = getIntent().getStringExtra("post_content");
        postAuthor = getIntent().getStringExtra("post_author");
        postDate = getIntent().getStringExtra("post_date");
        postImageUrl = getIntent().getStringExtra("post_image");
    }

    private void setupButtons() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Share button
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> shareArticle());
        }
    }

    private void shareArticle() {
        try {
            String shareTitle = postTitle != null ? Html.fromHtml(postTitle, Html.FROM_HTML_MODE_LEGACY).toString() : "Artikel Menarik";
            String shareText = shareTitle + "\n\n" +
                    "Baca artikel lengkapnya di aplikasi Decocheck!\n\n" +
                    "Download aplikasi Decocheck untuk membaca lebih banyak artikel menarik.";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            // Create chooser
            Intent chooser = Intent.createChooser(shareIntent, "Bagikan artikel melalui:");

            // Check if there are apps that can handle this intent
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                Toast.makeText(this, "Tidak ada aplikasi untuk berbagi", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membagikan artikel", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPostData() {
        showLoading(true);

        try {
            // Set title
            if (tvTitle != null) {
                if (postTitle != null && !postTitle.isEmpty()) {
                    tvTitle.setText(Html.fromHtml(postTitle, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvTitle.setText("Artikel");
                }
            }

            // Set author
            if (tvAuthor != null) {
                if (postAuthor != null && !postAuthor.isEmpty()) {
                    tvAuthor.setText(postAuthor);
                } else {
                    tvAuthor.setText("Admin");
                }
            }

            // Set date
            if (tvDate != null) {
                if (postDate != null && !postDate.isEmpty()) {
                    tvDate.setText(formatDate(postDate));
                } else {
                    tvDate.setText("Tanggal tidak tersedia");
                }
            }

            // Set content
            if (tvContent != null) {
                if (postContent != null && !postContent.isEmpty()) {
                    String cleanContent = Html.fromHtml(postContent, Html.FROM_HTML_MODE_LEGACY).toString();

                    // Clean up content
                    cleanContent = cleanContent.replaceAll("\\s+", " ").trim(); // Remove extra whitespace
                    cleanContent = cleanContent.replaceAll("\\[.*?\\]", ""); // Remove shortcodes
                    cleanContent = cleanContent.replaceAll("&nbsp;", " "); // Replace non-breaking spaces
                    cleanContent = cleanContent.replaceAll("&amp;", "&"); // Fix ampersands
                    cleanContent = cleanContent.replaceAll("&lt;", "<"); // Fix less than
                    cleanContent = cleanContent.replaceAll("&gt;", ">"); // Fix greater than
                    cleanContent = cleanContent.replaceAll("&quot;", "\""); // Fix quotes

                    if (!cleanContent.isEmpty()) {
                        tvContent.setText(cleanContent);
                    } else {
                        tvContent.setText("Konten tidak tersedia");
                    }
                } else {
                    tvContent.setText("Konten tidak tersedia");
                }
            }

            // Load featured image
            if (imageCard != null && ivFeaturedImage != null) {
                if (postImageUrl != null && !postImageUrl.isEmpty()) {
                    imageCard.setVisibility(View.VISIBLE);

                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new RoundedCorners(24))
                            .placeholder(R.drawable.ic_image)
                            .error(R.drawable.ic_image);

                    Glide.with(this)
                            .load(postImageUrl)
                            .apply(requestOptions)
                            .into(ivFeaturedImage);
                } else {
                    imageCard.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (tvTitle != null) {
                tvTitle.setText("Error memuat artikel");
            }
            if (tvContent != null) {
                tvContent.setText("Terjadi kesalahan saat memuat artikel");
            }
        }

        showLoading(false);
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
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
        return dateStr != null ? dateStr : "Tanggal tidak tersedia";
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}