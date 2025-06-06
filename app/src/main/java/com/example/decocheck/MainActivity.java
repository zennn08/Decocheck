package com.example.decocheck;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decocheck.adapter.PostAdapter;
import com.example.decocheck.model.Post;
import com.example.decocheck.network.ApiService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList;
    private ProgressBar progressBar;
    private EditText etSearch;
    private ImageView btnSearch;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupSearch();
        loadPosts();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        postList = new ArrayList<>();
        apiService = new ApiService(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> performSearch());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    loadPosts();
                }
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            showLoading(true);
            apiService.searchPosts(query, new ApiService.PostsCallback() {
                @Override
                public void onSuccess(List<Post> posts) {
                    showLoading(false);
                    postList.clear();
                    postList.addAll(posts);
                    adapter.notifyDataSetChanged();

                    if (posts.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Tidak ada artikel yang ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadPosts() {
        showLoading(true);
        apiService.getPosts(new ApiService.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                showLoading(false);
                postList.clear();
                postList.addAll(posts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}