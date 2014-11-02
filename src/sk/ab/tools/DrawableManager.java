package sk.ab.tools;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 2.3.2013
 * Time: 11:19
 */
public class DrawableManager {
    private Resources resources;
    private final Map<String, Drawable> drawableMap;

    public DrawableManager(Resources resources) {
        this.resources = resources;
        this.drawableMap = new HashMap<String, Drawable>();
    }

    public Drawable fetchDrawable(String urlString) {
        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream is = new java.net.URL(urlString).openStream();

            Drawable drawable = Drawable.createFromStream(is, "src");

            if (drawable != null) {
                drawableMap.put(urlString, drawable);
                Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                        + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                        + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            } else {
                Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
            }

            return drawable;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    imageView.setImageDrawable((Drawable) message.obj);
                }
            };

            Thread thread = new Thread() {
                @Override
                public void run() {
                    //TODO : set imageView to a "pending" image
                    Drawable drawable = fetchDrawable(urlString);
                    Message message = handler.obtainMessage(1, drawable);
                    handler.sendMessage(message);
                }
            };
            thread.start();
        }
    }
}
