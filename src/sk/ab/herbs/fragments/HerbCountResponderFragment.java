package sk.ab.herbs.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import sk.ab.commons.BaseActivity;
import sk.ab.herbs.Constants;
import sk.ab.herbs.activities.FilterPlantsActivity;
import sk.ab.herbs.service.RESTResponderFragment;
import sk.ab.herbs.service.RESTService;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 14.5.2013
 * Time: 20:53
 * To change this template use File | Settings | File Templates.
 */
public class HerbCountResponderFragment extends RESTResponderFragment {
  private static String TAG = HerbCountResponderFragment.class.getName();

  //private int count;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    getCount();
  }

  public void getCount() {
    FilterPlantsActivity activity = (FilterPlantsActivity) getActivity();

    if (activity != null) {
      // This is where we make our REST call to the service. We also pass in our ResultReceiver
      // defined in the RESTResponderFragment super class.

      // We will explicitly call our Service since we probably want to keep it as a private
      // component in our app. You could do this with Intent actions as well, but you have
      // to make sure you define your intent filters correctly in your manifest.
      Intent intent = new Intent(activity, RESTService.class);
      intent.setData(Uri.parse("http://appsresource.appspot.com/rest/count"));

      // Here we are going to place our REST call parameters. Note that
      // we could have just used Uri.Builder and appendQueryParameter()
      // here, but I wanted to illustrate how to use the Bundle params.
      Bundle params = new Bundle();
      StringBuffer filter = new StringBuffer("{");
      filter.append("\"objectTypeId\":\"" + Constants.FLOWERS + "\"");
      StringBuffer attributes = new StringBuffer();

      if (!activity.getFilter().isEmpty()) {
        for(Map.Entry<Integer, Object> entry : activity.getFilter().entrySet()) {
          attributes.append("{");
          attributes.append("\"attributeId\":\"");
          attributes.append(entry.getKey());
          attributes.append("\",");
          attributes.append("\"valueId\":\"");
          attributes.append(entry.getValue());
          attributes.append("\"}");
        }
        filter.append(",\"filterAttributes\":[");
        filter.append(attributes);
        filter.append("]");
      }
      filter.append("}");
      params.putString("query", filter.toString());

      intent.putExtra(RESTService.EXTRA_HTTP_VERB, RESTService.POST);
      intent.putExtra(RESTService.EXTRA_PARAMS, params);
      intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, getResultReceiver());

      // Here we send our Intent to our RESTService.
      activity.startService(intent);
    }
  }

  @Override
  public void onRESTResult(int code, String result) {
    // Here is where we handle our REST response. This is similar to the
    // LoaderCallbacks<D>.onLoadFinished() call from the previous tutorial.

    // Check to see if we got an HTTP 200 code and have some data.
    if (code == 200 && result != null) {

      // For really complicated JSON decoding I usually do my heavy lifting
      // with Gson and proper model classes, but for now let's keep it simple
      // and use a utility method that relies on some of the built in
      // JSON utilities on Android.
      //count = getCountFromJson(result);

      BaseActivity activity = (BaseActivity)getActivity();
      activity.setCount(getCountFromJson(result));
      activity.invalidateOptionsMenu();
    }
    else {
      Log.e(TAG, "Failed to load data. Check your internet settings.");
    }
  }

  private static int getCountFromJson(String json) {
    int result = 0;

    try {
      //JSONObject countJSON = (JSONObject) new JSONTokener(json).nextValue();
      //result = Integer.parseInt(countJSON.getString("count"));
      result = (Integer) new JSONTokener(json).nextValue();
    }
    catch (JSONException e) {
      Log.e(TAG, "Failed to parse JSON.", e);
    }

    return result;
  }

}
