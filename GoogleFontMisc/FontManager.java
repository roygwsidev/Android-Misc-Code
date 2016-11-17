import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class FontManager {

    private static FontManager typer = null;

    private HashMap<String, Typeface> fonts = null;
    private Context context = null;

    private FontManager(Context context) {
        this.fonts = new HashMap<>();
        this.context = context;
    }
    /**
     * Get the singleton base method.
     *
     * @param context the context of the activity.
     * @return the Typer class for the singleton.
     */
    public static FontManager set(Context context) {
        if (typer == null)
            typer = new FontManager(context);
        return typer;
    }

    public Typeface getFont(String font) {
        Typeface myFont = fonts.get(font);
        if (myFont == null) {
            myFont = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
            fonts.put(font, myFont);
        }
        return myFont;
    }
}