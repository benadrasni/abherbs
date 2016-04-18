package sk.ab.herbs.tools;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 5.3.2013
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class GetResource {

  public static int getResourceDrawable(String name, Context context, int defaultResource) {
    int nameResourceID = context.getResources().getIdentifier(name, "drawable", context.getApplicationInfo().packageName);
    if (nameResourceID == 0) {
      return defaultResource;
    } else {
      return nameResourceID;
    }
  }

  public static String getResourceString(String name, Context context, String defaultString) {
    int nameResourceID = context.getResources().getIdentifier(name, "string", context.getApplicationInfo().packageName);
    if (nameResourceID == 0) {
      return defaultString;
    } else {
      return context.getString(nameResourceID);
    }
  }
}
