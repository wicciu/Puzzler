package pl.brzezinski.witold.puzzler.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;

import junit.framework.Assert;

public class ImageTools {

    public static int getDrawableByNameAsInt(Context context, String name){
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);
        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    public static Drawable getDrawableByNameAsDrawable(Context context, String name){
        return context.getResources().getDrawable(getDrawableByNameAsInt(context,name));
    }
}
