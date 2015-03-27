/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.recyclerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.android.common.logger.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class RecyclerViewFragment extends Fragment {

    RequestQueue queue;
    StringRequest stringRequest;
    ArrayList<ImageData> imagesList;

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 21;
    public static final String TAG2 = "GetImage";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RadioButton mLinearLayoutRadioButton;
    protected RadioButton mGridLayoutRadioButton;

    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    final protected ArrayList<ImageData> mDataset= new ArrayList<ImageData>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        queue = MySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
        String url = "https://api.flickr.com/services/feeds/photos_public.gne?tags=boston";

        // Request a string response from the provided URL. Get Images (list of urls)
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            imagesList = (ArrayList<ImageData>) parseXMLImageData(response);
                            initDataset();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error XML url transfers. That didn't work!");
            }
        });
        // Set the tag on the request.
        stringRequest.setTag(TAG2);
        // Add the request to the RequestQueue.

        if (MySingleton.getInstance(getActivity()).getRequestQueue()!=null){
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new CustomAdapter(mDataset);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        mLinearLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.linear_layout_rb);
        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            }
        });

        mGridLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.grid_layout_rb);
        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }
        });

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Generates Data for RecyclerView's adapter.
     */
    public void initDataset() {

        byte[] bytes;
        Bitmap bmp;
        ImageRequest imageRequest;
        String url;
        mDataset.clear();
        if (imagesList!=null){
            for (int i = 0; i < imagesList.size(); i++) {
                // Show list of images (urls) got as response
                Log.d(TAG, "url to get image >>> ("+i+") :"+ imagesList.get(i).getImageUrl());

                url = imagesList.get(i).getImageUrl();

                if(MySingleton.getInstance(getActivity()).getRequestQueue().getCache().get(url)!=null){
                    System.out.println("sout   >>>>>>>>>>>>>>>>>>  found the image in diskcache");
                    bytes = MySingleton.getInstance(getActivity()).getRequestQueue().getCache().get(url).data;
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mDataset.add(new ImageData(bmp));
                } else {
                    System.out.println("sout   >>>>>>>>>>>>>>>>>>  getting the image from the web");
                    // Request an Image response from the provided URL.
                    imageRequest = new ImageRequest( url,
                            new Response.Listener<Bitmap>() {
                                //////final String u = url;
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    ///mImageView.setImageBitmap(bitmap);
                                    System.out.println("sout   >>>>>>>>>>>>>>>>>>  processing response getting the image from the web");
                                    mDataset.add(new ImageData(bitmap));
                                }
                            }, 0, 0, ImageView.ScaleType.FIT_CENTER,null,
                            new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "Error during Image transfer. That didn't work!");
                                }
                            });
                    // Set the tag on the request.
                    imageRequest.setTag(TAG2);
                    // Add the request to the RequestQueue.
                    if (MySingleton.getInstance(getActivity()).getRequestQueue()!=null){
                        MySingleton.getInstance(getActivity()).addToRequestQueue(imageRequest);
                    }
                }
            }
        }
    }

    @Override
    public void onStop () {
        super.onStop();
        if ((MySingleton.getInstance(getActivity()).getRequestQueue()!=null)) {
            MySingleton.getInstance(getActivity()).getRequestQueue().cancelAll(TAG2);
        }
    }

    private static ArrayList<ImageData> parseXMLImageData(final String response) throws XmlPullParserException, IOException {
        ArrayList<ImageData> temp = new ArrayList<ImageData>();
        ImageData poi=null;

        String text="";

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( response ) );
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagname = xpp.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:

                    if (tagname.equalsIgnoreCase("link")) {
                        if(xpp.getAttributeCount()==3){
                            if ((xpp.getAttributeValue(0).equals("enclosure"))&&(xpp.getAttributeValue(1).equals("image/jpeg"))){
                                poi = new ImageData(xpp.getAttributeValue(2).replace("_b.jpg","_q.jpg"));
                                temp.add(poi);
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
            eventType = xpp.next();
        }
        return temp;
    }

}
