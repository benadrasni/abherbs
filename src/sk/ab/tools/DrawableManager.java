package sk.ab.tools;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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
    private static DrawableManager drawableManager = new DrawableManager();
    private static final Map<String, DownloadedDrawable> drawableMap = new HashMap<String, DownloadedDrawable>();

    private class DownloadedDrawable {
        private Drawable drawable;
        private ImageView imageView;

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        private ImageView getImageView() {
            return imageView;
        }

        private void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, Drawable> {
        private String url;

        protected Drawable doInBackground(String... urls) {
            url = urls[0];
            return fetchDrawable(url);
        }

        protected void onPostExecute(Drawable result) {
            if (result != null) {

                DownloadedDrawable dd = drawableMap.get(url);
                dd.setDrawable(result);
                Log.d(this.getClass().getSimpleName(), "got a drawable: " + result.getBounds() + ", "
                        + result.getIntrinsicHeight() + "," + result.getIntrinsicWidth() + ", "
                        + result.getMinimumHeight() + "," + result.getMinimumWidth());

                if (dd.getImageView() != null) {
                    dd.getImageView().setImageDrawable(result);
                }
            } else {
                Log.w(this.getClass().getSimpleName(), "could not get drawable");
            }
        }
    }

    public static DrawableManager getDrawableManager() {
        return drawableManager;
    }

    private DrawableManager() {
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
        DownloadedDrawable dd = drawableMap.get(urlString);
        if (dd != null) {
            Drawable d = dd.getDrawable();
            if (d != null) {
                imageView.setImageDrawable(d);
            } else {
                dd.setImageView(imageView);
            }
        } else {
            executeDownload(urlString, imageView);
        }
    }

    public void fetchDrawableOnThread(final String urlString) {
        if (!drawableMap.containsKey(urlString)) {
            executeDownload(urlString, null);
        }
    }

    private void executeDownload(final String urlString, final ImageView imageView) {
        DownloadedDrawable dd = new DownloadedDrawable();
        dd.setImageView(imageView);
        drawableMap.put(urlString, dd);
        DownloadFilesTask task = new DownloadFilesTask();
        task.execute(urlString);
    }

}
