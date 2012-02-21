/*
 * Copyright (c) 2011 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.pseudocoloriztion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.klab.iphoroid.widget.support.HasImage;
import org.klab.iphoroid.widget.support.ImageDownloadTask.DefaultImageDownloadHelper;


/**
 * FlowViewItemAdapter. 
 *
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (sano-n)
 * @version 0.00 2011/07/08 sano-n initial version <br>
 */
class FlowViewItemAdapter extends ArrayAdapter<Item> {

    private LayoutInflater inflater;
    private static ColorCurveOp filter;

    static {
        try {
            InputStream is = new FileInputStream(new File(String.format("%s/pseudocoloriztion/test.cur", Environment.getExternalStorageDirectory())));
            ColorCurveOp.Curves curves = new ColorCurveOp.GimpCurvesFactory().getCurves(is);
            is.close();
            filter = new ColorCurveOp(curves);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public FlowViewItemAdapter(Context context, List<Item> objects) {
        super(context, 0, objects);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.flow_view_item, null);

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Item item = getItem(position);
        if (item != null) {
            viewHolder.imageView.setTag(position);
            HasImage.Util.setImage(getContext(), item.getImageUrl(), viewHolder.imageView, new DefaultImageDownloadHelper(null, PseudoColoriztionActivity.noImageIcon) {
                @Override
                public void onPreDownload(ImageView imageView) {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
                @Override
                public void onDownloadSuccess(ImageView imageView) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
                @Override
                public Bitmap doDownload(String param) throws IOException {
                    ZipFile zipFile = new ZipFile(new File(String.format("%s/pseudocoloriztion/test.zip", Environment.getExternalStorageDirectory())));
                    ZipEntry zipEntry = zipFile.getEntry(param);
                    Bitmap bitmap = BitmapFactory.decodeStream(zipFile.getInputStream(zipEntry));
Log.d("FlowViewItemAdapter", bitmap.getWidth() + "x" + bitmap.getHeight());
Log.d("FlowViewItemAdapter", ((View) viewHolder.imageView.getParent()).getWidth() + "x" + ((View) viewHolder.imageView.getParent()).getHeight());
                    Bitmap resizedBitmap = makeResizedImage(bitmap, ((View) viewHolder.imageView.getParent()).getWidth(), ((View) viewHolder.imageView.getParent()).getHeight());
                    bitmap.recycle();
                    Bitmap filterdBitmap = filter.filter(resizedBitmap);
//                    return resizedBitmap;
                    return filterdBitmap;
                }
            }); 
        }

        return view;
    }

    /** */
    protected Bitmap makeResizedImage(Bitmap src, int width, int height) {

        Matrix matrix = new Matrix();
        float sx = (float) width / src.getWidth();
        float sy = (float) height / src.getHeight();
        float s = Math.max(sx, sy);
        matrix.postScale(s, s);

        Bitmap dest = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

        return dest;
    }
}
