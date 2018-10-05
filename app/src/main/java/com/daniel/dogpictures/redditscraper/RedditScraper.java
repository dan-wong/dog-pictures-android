package com.daniel.dogpictures.redditscraper;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daniel.dogpictures.RedditImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RedditScraper {
    private static final String ENDPOINT_URL_FORMAT = "https://www.reddit.com/r/%s/.json?limit=%d&after=%s";
    private static final int LIMIT = 20;

    public static void getImagesFromSubreddit(Context context, String subreddit, String lastImageID, final RedditScraperCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        @SuppressLint("DefaultLocale")
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                String.format(ENDPOINT_URL_FORMAT, subreddit, LIMIT, lastImageID),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<RedditImage> redditImages = new ArrayList<>();
                        try {
                            JSONArray responseArray = response.getJSONObject("data")
                                    .getJSONArray("children");

                            for (int i=0; i<responseArray.length(); i++) {
                                RedditImage redditImage = parseRedditImageFromJSON(responseArray.getJSONObject(i));
                                if (redditImage == null || !isImage(redditImage)) {
                                    continue;
                                }
                                redditImages.add(redditImage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.imagesReturned(redditImages);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.imagesReturned(new ArrayList<RedditImage>());
                    }
                }
        );
        queue.add(request);
    }

    private static boolean isImage(RedditImage redditImage) {
        return redditImage.url.contains("jpeg") ||
                redditImage.url.contains("jpg") ||
                redditImage.url.contains("png");
    }

    private static RedditImage parseRedditImageFromJSON(JSONObject response) {
        try {
            return new RedditImage(
                    getId(response),
                    getImageUrl(response),
                    getTitle(response)
            );
        } catch (JSONException e) {
            return null;
        }
    }

    private static String getId(JSONObject jsonObject) throws JSONException {
        return jsonObject.getJSONObject("data").getString("name");
    }

    private static String getImageUrl(JSONObject jsonObject) throws JSONException {
        return jsonObject.getJSONObject("data").getString("url");
    }

    private static String getTitle(JSONObject jsonObject) throws JSONException {
        return jsonObject.getJSONObject("data").getString("title");
    }
}
