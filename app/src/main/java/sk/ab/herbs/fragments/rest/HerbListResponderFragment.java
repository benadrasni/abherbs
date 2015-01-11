package sk.ab.herbs.fragments.rest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.ab.herbs.Constants;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.activities.FilterPlantsActivity;
import sk.ab.herbs.service.RESTResponderFragment;
import sk.ab.herbs.service.RESTService;
import sk.ab.tools.DrawableManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 14.5.2013
 * Time: 20:53
 * <p/>
 * Response to "count" rest service
 */
public class HerbListResponderFragment extends RESTResponderFragment {
    private static String TAG = HerbListResponderFragment.class.getName();

    public void getList() {
        FilterPlantsActivity activity = (FilterPlantsActivity) getActivity();

        if (activity != null) {

            Intent intent = new Intent(activity, RESTService.class);
            intent.setData(Uri.parse(Constants.REST_ENDPOINT + Constants.REST_LIST));

            Bundle params = new Bundle();
            StringBuilder query = new StringBuilder("{");
            query.append("\"langId\":\"");
            query.append(Constants.getLanguage());
            query.append("\"");
            StringBuilder attributes = new StringBuilder();

            if (!activity.getFilter().isEmpty()) {
                for (Map.Entry<Integer, Integer> entry : activity.getFilter().entrySet()) {
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
            }
            query.append(",\"filterAttributes\":[");
            query.append(attributes);
            query.append("]");

            query.append(",\"attributes\":[");
            query.append(Constants.PLANT_NAME);
            query.append("," + Constants.PLANT_PHOTO_URL);
            query.append("," + Constants.PLANT_FAMILY);
            query.append("]");

            query.append(",\"from\":0");
            query.append(",\"number\":10");
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
            FilterPlantsActivity activity = (FilterPlantsActivity) getActivity();
            activity.setResults(getListFromJson(result));
        } else {
            Log.e(TAG, "Failed to load data. Check your internet settings.");
        }
    }

    private static List<PlantHeader> getListFromJson(String json) {
        List<PlantHeader> result = new ArrayList<>();

        try {
            JSONObject herbList = new JSONObject(json);
            Iterator<String> keys = herbList.keys();
            while (keys.hasNext()) {

                String plantId = keys.next();
                JSONObject attributes = herbList.getJSONObject(plantId);
                JSONArray name = null;
                if (attributes.has(""+Constants.PLANT_NAME+"_0")) {
                    name = attributes.getJSONArray(""+Constants.PLANT_NAME+"_0");
                }
                JSONArray url = null;
                if (attributes.has(""+Constants.PLANT_PHOTO_URL +"_0")) {
                    url = attributes.getJSONArray(""+Constants.PLANT_PHOTO_URL +"_0");
                }
                JSONArray family = null;
                if (attributes.has(""+Constants.PLANT_FAMILY+"_0")) {
                    family = attributes.getJSONArray(""+Constants.PLANT_FAMILY+"_0");
                }

                PlantHeader plantHeader = new PlantHeader(Integer.parseInt(plantId),
                        name != null ? name.getString(0) : "",
                        url != null ? url.getString(0) : "",
                        family != null ? family.getString(0) : "",
                        family != null ? Integer.parseInt(family.getString(1)) : 0);

                DrawableManager.getDrawableManager().fetchDrawableOnThread(plantHeader.getUrl());

                result.add(plantHeader);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON.", e);
        }

        return result;
    }

}
