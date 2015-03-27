package com.example.android.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;

/**
 * Created by william on 3/26/15.
 */
public class MySingleton {

    private static Cache cache;
    private static Network network;
    private static MySingleton mInstance;
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static Context mCtx;

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            ///////////////////////mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

            File file = new File(mCtx.getCacheDir(), "test_images");
            // Instantiate the cache
            cache = new DiskBasedCache(file, 4 * 1024 * 1024); // 4MB cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            network = new BasicNetwork(new HurlStack());

            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
    public Cache getCache() {
        return cache;
    }
}
