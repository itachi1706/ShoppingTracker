package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Objects
 */
public class LegacyBarcode {

    public String format, contents;
    public String stringConcatValue;

    public LegacyBarcode(){}

    public LegacyBarcode(String format, String contents){
        this.format = format;
        this.contents = contents;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setToString(String toString){
        this.stringConcatValue = toString;
    }

    public String toString(){
        return this.stringConcatValue;
    }

}
