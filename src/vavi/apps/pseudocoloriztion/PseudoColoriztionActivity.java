/*
 * Copyright (c) 2011 by KLab Inc., All rights reserved.
 *
 * Programmed by iphoroid team
 */

package vavi.apps.pseudocoloriztion;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import org.klab.iphoroid.widget.flowview.FlowView;
import org.klab.iphoroid.widget.support.DownloadTask;
import org.klab.iphoroid.widget.support.DownloadTask.DefaultDawnloadTask;
import org.klab.iphoroid.widget.support.HasImage;


/**
 * PseudoColoriztionActivity. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/10 umjammer initial version <br>
 */
public class PseudoColoriztionActivity extends Activity implements HasImage {

    // for application
    public static Drawable loadingIcon;
    public static Drawable noImageIcon;

    /** for image management */
    private HasImage.AdapterViewOnScrollListener flowViewOnScrollListener;

    /** for image management */
    public int getScrollState() {
        return flowViewOnScrollListener.getScrollState();
    }

    /** portrait */
    private FlowView flowView;

    /** for intent */
    public static final String INTENT_EXTRA_SELECTION = "selection";

    /** @see #onSaveInstanceState(Bundle) */
    public static final String SAVED_INSTANCE_STATE_SELECTION = "selection";
   
    /** */
    private int selection;

    /** */
    private Intent resultIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        OptionsMenuUtil.createOptionsMenu(this, menu);
        return true;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.flow_view);

        // application
        Resources r = getResources();
        loadingIcon = r.getDrawable(R.drawable.loading_thumbnail);
        noImageIcon = r.getDrawable(R.drawable.noimage_thumbnail);
        
        int defaultSelection;
        if (savedInstanceState != null) {
            defaultSelection = savedInstanceState.getInt(SAVED_INSTANCE_STATE_SELECTION);
        } else {
            defaultSelection = getIntent().getIntExtra(INTENT_EXTRA_SELECTION, 0);
        }
Log.d("FlowViewActivity", "selection: " + defaultSelection);

        // UI
        this.flowView = (FlowView) findViewById(R.id.flowView);
        this.flowViewOnScrollListener = new HasImage.AdapterViewOnScrollListener();
        flowView.setOnScrollListener(flowViewOnScrollListener);
        flowView.setOnViewSwitchListener(new FlowView.ViewSwitchListener() {
            @Override
            public void onSwitched(View view, int position) {
Log.d("FlowViewActivity", "select 1: " + position);
                setSelection(position);
            }
        });

        // data
        load(defaultSelection);

        // result
        resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
    }

    private void load(final int defaultSelection) {
        // data
        DownloadTask<Void, List<Item>> task = null;
        task = new DefaultDawnloadTask<Void, List<Item>>(this, "Network Error") {
            @Override
            public List<Item> download(Void... params) throws Exception {
                return ItemDao.getInstance().getImages();
            }
            @Override
            public void setResult(List<Item> items) {
                flowView.setAdapter(new FlowViewItemAdapter(PseudoColoriztionActivity.this, items));

                flowView.setSelection(defaultSelection);
Log.d("FlowViewActivity", "selection 2: " + defaultSelection);
            }
        };
        try {
            task.execute();
        } catch (RejectedExecutionException e) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage(e.getMessage()).show();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);

       outState.putInt(SAVED_INSTANCE_STATE_SELECTION, selection);
Log.d("FlowViewActivity", "here 1: " + selection);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // for image management
        HasImage.Util.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // for image management
        HasImage.Util.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // for image management
        HasImage.Util.onDestroy(this);
    }

    /** */
    private void setSelection(int selection) {
        this.selection = selection;
    }
}