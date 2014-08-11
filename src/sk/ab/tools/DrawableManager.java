package sk.ab.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
//import com.google.webp.libwebp;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 2.3.2013
 * Time: 11:19
 *
 */
public class DrawableManager {
  private Resources resources;
  private final Map<String, Drawable> drawableMap;

//  static {
//    System.loadLibrary("webp");
//  }

  public DrawableManager(Resources resources) {
    this.resources = resources;
    this.drawableMap = new HashMap<String, Drawable>();
  }

  public Drawable fetchDrawable(String urlString) {
    Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
    try {
      InputStream is = new java.net.URL(urlString).openStream();

      Drawable drawable;
//      if (urlString.endsWith("webp")) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        int next = is.read();
//        while (next > -1) {
//          bos.write(next);
//          next = is.read();
//        }
//        bos.flush();
//        byte[] webp = bos.toByteArray();
//
//        drawable = new BitmapDrawable(resources, webpToBitmap(webp));
//      } else {
        drawable = Drawable.createFromStream(is, "src");
//      }

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
    }

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

//  synchronized private Bitmap webpToBitmap(byte[] encoded) {
//    int[] width = new int[] { 0 };
//    int[] height = new int[] { 0 };
//    byte[] decoded = libwebp.WebPDecodeARGB(encoded, encoded.length, width, height);
//
//    int[] pixels = new int[decoded.length / 4];
//    ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);
//
//    return Bitmap.createBitmap(pixels, width[0], height[0], Bitmap.Config.ARGB_8888);
//  }
}
