package com.example.decocheck.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.decocheck.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static final String BASE_URL = "https://decocheck.web.id/wp-json/wp/v2/";
    private static final String POSTS_URL = BASE_URL + "posts?_embed";
    private static final String SEARCH_URL = BASE_URL + "posts?search=";

    private RequestQueue requestQueue;
    private Context context;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public interface PostsCallback {
        void onSuccess(List<Post> posts);
        void onError(String error);
    }

    public void getPosts(PostsCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                POSTS_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Post> posts = parsePosts(response);
                        callback.onSuccess(posts);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ApiService", "Error: " + error.getMessage());
                        callback.onError("Gagal memuat data. Periksa koneksi internet Anda.");
                    }
                }
        );

        requestQueue.add(request);
    }

    public void searchPosts(String query, PostsCallback callback) {
        String searchUrl = SEARCH_URL + query + "&_embed";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                searchUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Post> posts = parsePosts(response);
                        callback.onSuccess(posts);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ApiService", "Search Error: " + error.getMessage());
                        callback.onError("Gagal mencari artikel. Periksa koneksi internet Anda.");
                    }
                }
        );

        requestQueue.add(request);
    }

    private List<Post> parsePosts(JSONArray response) {
        List<Post> posts = new ArrayList<>();

        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject postObj = response.getJSONObject(i);
                Post post = new Post();

                post.setId(postObj.getInt("id"));

                // Title
                if (postObj.has("title")) {
                    JSONObject titleObj = postObj.getJSONObject("title");
                    post.setTitle(titleObj.getString("rendered"));
                }

                // Content
                if (postObj.has("content")) {
                    JSONObject contentObj = postObj.getJSONObject("content");
                    post.setContent(contentObj.getString("rendered"));
                }

                // Excerpt
                if (postObj.has("excerpt")) {
                    JSONObject excerptObj = postObj.getJSONObject("excerpt");
                    post.setExcerpt(excerptObj.getString("rendered"));
                }

                // Date
                post.setDate(postObj.getString("date"));

                // Author
                if (postObj.has("_embedded")) {
                    JSONObject embedded = postObj.getJSONObject("_embedded");
                    if (embedded.has("author")) {
                        JSONArray authorArray = embedded.getJSONArray("author");
                        if (authorArray.length() > 0) {
                            JSONObject author = authorArray.getJSONObject(0);
                            post.setAuthor(author.getString("name"));
                        }
                    }
                }

                // Featured Image
                if (postObj.has("_embedded")) {
                    JSONObject embedded = postObj.getJSONObject("_embedded");
                    if (embedded.has("wp:featuredmedia")) {
                        JSONArray mediaArray = embedded.getJSONArray("wp:featuredmedia");
                        if (mediaArray.length() > 0) {
                            JSONObject media = mediaArray.getJSONObject(0);
                            if (media.has("source_url")) {
                                post.setFeaturedImageUrl(media.getString("source_url"));
                            }
                        }
                    }
                }

                posts.add(post);
            }
        } catch (JSONException e) {
            Log.e("ApiService", "JSON Parse Error: " + e.getMessage());
        }

        return posts;
    }
}