/*
 * Copyright (c) 2011 by KLab Inc., All rights reserved.
 *
 * Programmed by iphoroid team
 */

package vavi.apps.pseudocoloriztion;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;


/**
 * OptionsMenuUtil. 
 *
 * @author <a href="mailto:kodama-t@klab.jp">Takuya KODAMA</a> (kodamta-t)
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (sano-n)
 */
public abstract class OptionsMenuUtil {

    public static interface OnReloadMenuItemCkickListener {
        void onReloadMenuItemCkick();
    }

    /** */
    public static void createOptionsMenu(final Context context, Menu menu) {
        OnMenuItemClickListener listener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                case 0:
                    Intent i = new Intent(context, PseudoColoriztionActivity.class);
                    context.startActivity(i);
                    break;
                }
                return false;
            }
        };
        menu.add(0, 0, 0, "Open").setOnMenuItemClickListener(listener);
    }
}
