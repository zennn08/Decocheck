package com.example.decocheck;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private ImageView ivFeaturedImage, btnBack;
    private CardView imageCard;
    private ProgressBar progressBar;

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
        setupBackButton();
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
            progressBar = findViewById(R.id.progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBackButton() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadPostData() {
        showLoading(true);

        try {
            // Get data from intent
            String title = getIntent().getStringExtra("post_title");
            String content = getIntent().getStringExtra("post_content");
            String author = getIntent().getStringExtra("post_author");
            String date = getIntent().getStringExtra("post_date");
            String imageUrl = getIntent().getStringExtra("post_image");

            // Set title
            if (tvTitle != null) {
                if (title != null && !title.isEmpty()) {
                    tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvTitle.setText("Artikel");
                }
            }

            // Set author
            if (tvAuthor != null) {
                if (author != null && !author.isEmpty()) {
                    tvAuthor.setText(author);
                } else {
                    tvAuthor.setText("Admin");
                }
            }

            // Set date
            if (tvDate != null) {
                if (date != null && !date.isEmpty()) {
                    tvDate.setText(formatDate(date));
                } else {
                    tvDate.setText("Tanggal tidak tersedia");
                }
            }

            // Set content
            if (tvContent != null) {
                if (content != null && !content.isEmpty()) {
                    String cleanContent = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString();

                    // Clean up content
                    cleanContent = cleanContent.replaceAll("\\s+", " ").trim(); // Remove extra whitespace
                    cleanContent = cleanContent.replaceAll("\\[.*?\\]", ""); // Remove shortcodes
                    cleanContent = cleanContent.replaceAll("&nbsp;", " "); // Replace non-breaking spaces
                    cleanContent = cleanContent.replaceAll("&amp;", "&"); // Fix ampersands
                    cleanContent = cleanContent.replaceAll("&lt;", "<"); // Fix less than
                    cleanContent = cleanContent.replaceAll("&gt;", ">"); // Fix greater than

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
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageCard.setVisibility(View.VISIBLE);

                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new RoundedCorners(24))
                            .placeholder(R.drawable.ic_image)
                            .error(R.drawable.ic_image);

                    Glide.with(this)
                            .load(imageUrl)
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