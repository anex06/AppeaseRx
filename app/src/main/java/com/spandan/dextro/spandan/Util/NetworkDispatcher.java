package com.spandan.dextro.spandan.Util;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class NetworkDispatcher implements ImageLoader.ImageCache {

    public static final NetworkDispatcher shared = new NetworkDispatcher();

    public  ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> cache;

    private NetworkDispatcher() { cache = new LruCache<>(1000); }

    public void begin(Context context) {

        requestQueue = Volley.newRequestQueue(context);
        imageLoader  = new ImageLoader(requestQueue,this);
    }

    public void clear() { requestQueue.getCache().clear(); }

    public boolean isNotInitialized() { return requestQueue == null; }

    public void end() {

        requestQueue.stop();

        imageLoader  = null;
        requestQueue = null;
    }

    public RequestQueue queue()             { return requestQueue;             }
    public <T> void add(Request<T> request) { requestQueue.add(request);       }
    public void deleteAll(String withTag)   { requestQueue.cancelAll(withTag); }

    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        cache.put(url, bitmap);
    }
}