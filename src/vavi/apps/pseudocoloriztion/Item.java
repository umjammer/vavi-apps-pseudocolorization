/*
 * Copyright (c) 2011 by KLab Inc., All rights reserved.
 *
 * Programmed by iphoroid team
 */

package vavi.apps.pseudocoloriztion;

import java.io.Serializable;


/**
 * Item
 *
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (sano-n)
 */
public class Item implements Serializable {

    /** */
    private int id;
    /** */
    private String imageUrl;

    /** */
    public int getId() {
        return id;
    }

    /** */
    public void setId(int id) {
        this.id = id;
    }

    /** */
    public String getImageUrl() {
        return imageUrl;
    }

    /** */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Item)) {
            return false;
        }
        return id == ((Item) o).id;
    }
}
