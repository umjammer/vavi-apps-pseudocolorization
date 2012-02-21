/*
 * Copyright (c) 2011 by KLab Inc., All rights reserved.
 *
 * Programmed by iphoroid team
 */

package vavi.apps.pseudocoloriztion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.os.Environment;


/**
 * ItemDao.
 *
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (sano-n)
 */
public class ItemDao {

    /** */
    protected ItemDao() {
    }

    /**
     * シングルトン　インスタンス
     */
    private static ItemDao instance = new ItemDao();

    /**
     * シングルトン　インスタンスを返します
     */
    public static ItemDao getInstance() {
        return instance;
    }

    /**
     * 指定された PurchaseStatus に適切なデータのリストを返します.
     */
    public List<Item> getImages() throws IOException {
        List<Item> result = new ArrayList<Item>();
        ZipFile zipFile = new ZipFile(new File(String.format("%s/pseudocoloriztion/test.zip", Environment.getExternalStorageDirectory())));
        Enumeration<? extends ZipEntry> e = zipFile.entries();
        int i = 0;
        while (e.hasMoreElements()) {
            ZipEntry zipEntry = e.nextElement();
            Item item = new Item();
            item.setId(i++);
            item.setImageUrl(zipEntry.getName());
            result.add(item);
        }
        return result;
    }
}

/* */
