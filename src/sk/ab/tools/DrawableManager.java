package sk.ab.tools;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
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

    private class DownloadFilesTask extends AsyncTask<String, Void, Drawable> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public DownloadFilesTask(final ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        protected Drawable doInBackground(String... urls) {
            url = urls[0];
            return fetchDrawable(url);
        }

        protected void onPostExecute(Drawable result) {
            if (result != null) {

                drawableMap.put(url, result);
                Log.d(this.getClass().getSimpleName(), "got a drawable: " + result.getBounds() + ", "
                        + result.getIntrinsicHeight() + "," + result.getIntrinsicWidth() + ", "
                        + result.getMinimumHeight() + "," + result.getMinimumWidth());

                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    imageView.setImageDrawable(result);
                }
            } else {
                Log.w(this.getClass().getSimpleName(), "could not get drawable");
            }
        }
    }

    public DrawableManager(Resources resources) {
        this.resources = resources;
        this.drawableMap = new HashMap<String, Drawable>();
    }

    public Drawable fetchDrawable(String urlString) {
        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream inputStream = null;
            try {
                inputStream = new java.net.URL(urlString).openStream();

                Drawable drawable = Drawable.createFromStream(inputStream, "src");

                return drawable;
            } catch (MalformedURLException e) {
                Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
        }
        return null;
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
        } else {
            DownloadFilesTask task = new DownloadFilesTask(imageView);
            task.execute(urlString);
        }
    }
}
