package sk.ab.herbs.fragments.rest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
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
 * <p/>
 * Response to "count" rest service
 */
public class HerbCountResponderFragment extends RESTResponderFragment {
  private static String TAG = HerbCountResponderFragment.class.getName();

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    getCount();
  }

  public void getCount() {
    FilterPlantsActivity activity = (FilterPlantsActivity) getActivity();

    if (activity != null) {

      Intent intent = new Intent(activity, RESTService.class);
      intent.setData(Uri.parse(Constants.REST_ENDPOINT + Constants.REST_COUNT));

      Bundle params = new Bundle();
      StringBuffer query = new StringBuffer("{");
      query.append("\"objectTypeId\":\"" + Constants.FLOWERS + "\"");
      StringBuffer attributes = new StringBuffer();

      if (!activity.getFilter().isEmpty()) {
        for(Map.Entry<Integer, Object> entry : activity.getFilter().entrySet()) {
          if (attributes.length() > 0) {
              attributes.append(",");
          }
          attributes.append("{");
          attributes.append("\"attributeId\":\"");
          attributes.append(entry.getKey());
          attributes.append("\",");
          attributes.append("\"valueId\":\"");
          attributes.append(entry.getValue());
          attributes.append("\"}");
        }
        query.append(",\"filterAttributes\":[");
        query.append(attributes);
        query.append("]");
      }
      query.append("}");
      params.putString("query", query.toString());

      intent.putExtra(RESTService.EXTRA_HTTP_VERB, RESTService.POST);
      intent.putExtra(RESTService.EXTRA_PARAMS, params);
      intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, getResultReceiver());

      activity.startService(intent);
    }
  }

  @Override
  public void onRESTResult(int code, String result) {

    if (code == 200 && result != null) {
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
      result = (Integer) new JSONTokener(json).nextValue();
    }
    catch (JSONException e) {
      Log.e(TAG, "Failed to parse JSON.", e);
    }

    return result;
  }

}
