package pl.brzezinski.witold.puzzler.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;


public class DrawableSwipeCards {


    private static ArrayList<NamedDrawable> drawable_logos=new ArrayList<>();

    public static void removeFirstDrawableLogo(){
        drawable_logos.remove(0);
    }

    public static Drawable getDrawableFromBuffer(Context context, String drawable_name){
        if(getNamedDrawableByName(drawable_name)!=null){
            return getNamedDrawableByName(drawable_name).getDrawable();
        }
        else {
            Drawable logo_img = ImageTools.getDrawableByNameAsDrawable(context, drawable_name);
            NamedDrawable new_named_drawable = new NamedDrawable(drawable_name, logo_img);
            drawable_logos.add(new_named_drawable);
            return drawable_logos.get(drawable_logos.size() - 1).getDrawable();
        }
    }

    private static NamedDrawable getNamedDrawableByName(String name){
        for (NamedDrawable named_draw:drawable_logos) {
            if(named_draw.getName().equals(name)){
                return named_draw;
            }
        }
        return null;

    }

    private static class NamedDrawable{
        public String getName() {
            return name;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public NamedDrawable(String name,Drawable drawable){
            this.name=name;
            this.drawable=drawable;
        }

        private String name;
        private Drawable drawable;
    }
}
