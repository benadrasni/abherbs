package sk.ab.herbs.fragments.rest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.ab.herbs.Constants;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.activities.ListPlantsActivity;
import sk.ab.herbs.service.RESTResponderFragment;
import sk.ab.herbs.service.RESTService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.10.2014
 * Time: 20:53
 * <p/>
 * Response to "detail" rest service
 */
public class HerbDetailResponderFragment extends RESTResponderFragment {
    private static String TAG = HerbDetailResponderFragment.class.getName();

    public void getDetail() {
        //ListPlantsActivity activity = (ListPlantsActivity) getActivity();
        DisplayPlantActivity activity = (DisplayPlantActivity) getActivity();

        if (activity != null) {

            Intent intent = new Intent(activity, RESTService.class);
            intent.setData(Uri.parse(Constants.REST_ENDPOINT + Constants.REST_DETAIL));

            Bundle params = new Bundle();
            StringBuffer query = new StringBuffer("{");
            query.append("\"langId\":"+Constants.getLanguage());
            query.append(",\"objectId\":"+activity.getPlantHeader().getPlantId());
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
            //ListPlantsActivity activity = (ListPlantsActivity) getActivity();
            DisplayPlantActivity activity = (DisplayPlantActivity) getActivity();
            activity.setPlant(getDetailFromJson(activity.getPlantHeader(), result));
            activity.invalidateOptionsMenu();
        } else {
            Log.e(TAG, "Failed to load data. Check your internet settings.");
        }
    }

    private static Plant getDetailFromJson(PlantHeader plantHeader, String json) {
        Plant result = new Plant(plantHeader);
        result.setSpecies(plantHeader.getTitle());

        try {
            JSONObject herbList = new JSONObject(json);
            Iterator<String> keys = herbList.keys();
            if (keys.hasNext()) {

                String plantId = keys.next();
                JSONObject attributes = herbList.getJSONObject(plantId);
                if (attributes.has(""+Constants.PLANT_IMAGE_URL+"_0")) {
                    result.setBack_url(attributes.getJSONArray("" + Constants.PLANT_IMAGE_URL + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_FLOWER +"_0")) {
                    result.setFlower(attributes.getJSONArray("" + Constants.PLANT_FLOWER + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_INFLORESCENCE +"_0")) {
                    result.setInflorescence(attributes.getJSONArray("" + Constants.PLANT_INFLORESCENCE + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_FRUIT+"_0")) {
                    result.setFruit(attributes.getJSONArray("" + Constants.PLANT_FRUIT + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_STEM+"_0")) {
                    result.setStem(attributes.getJSONArray("" + Constants.PLANT_STEM + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_LEAF+"_0")) {
                    result.setLeaf(attributes.getJSONArray("" + Constants.PLANT_LEAF + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_HABITAT+"_0")) {
                    result.setHabitat(attributes.getJSONArray("" + Constants.PLANT_HABITAT + "_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_SPECIES_LATIN+"_0")) {
                    result.setSpecies_latin(attributes.getJSONArray(""+Constants.PLANT_SPECIES_LATIN+"_0").getString(0));
                }

                if (attributes.has(""+Constants.PLANT_DOMAIN+"_0")) {
                    result.setDomain(attributes.getJSONArray(""+Constants.PLANT_DOMAIN+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_KINGDOM+"_0")) {
                    result.setKingdom(attributes.getJSONArray(""+Constants.PLANT_KINGDOM+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_SUBKINGDOM+"_0")) {
                    result.setSubkingdom(attributes.getJSONArray(""+Constants.PLANT_SUBKINGDOM+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_LINE+"_0")) {
                    result.setLine(attributes.getJSONArray(""+Constants.PLANT_LINE+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_BRANCH+"_0")) {
                    result.setBranch(attributes.getJSONArray(""+Constants.PLANT_BRANCH+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_PHYLUM+"_0")) {
                    result.setPhylum(attributes.getJSONArray(""+Constants.PLANT_PHYLUM+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_CLS+"_0")) {
                    result.setCls(attributes.getJSONArray(""+Constants.PLANT_CLS+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_ORDER+"_0")) {
                    result.setOrder(attributes.getJSONArray(""+Constants.PLANT_ORDER+"_0").getString(0));
                }
                if (attributes.has(""+Constants.PLANT_GENUS+"_0")) {
                    result.setGenus(attributes.getJSONArray(""+Constants.PLANT_GENUS+"_0").getString(0));
                }

                int rank = 0;
                List<String> names = new ArrayList<String>();
                while (attributes.has(""+Constants.PLANT_ALT_NAMES+"_"+rank)) {
                    JSONArray values = attributes.getJSONArray(""+Constants.PLANT_ALT_NAMES+"_"+rank);
                    if (values.getInt(2) == Constants.getLanguage()) {
                        names.add(values.getString(0));
                    }
                    rank++;
                }
                result.setNames(names);

                rank = 0;
                List<String> photo_urls = new ArrayList<String>();
                while (attributes.has(""+Constants.PLANT_PHOTO_URL+"_"+rank)) {
                    photo_urls.add(attributes.getJSONArray(""+Constants.PLANT_PHOTO_URL+"_"+rank).getString(0));
                    rank++;
                }
                result.setPhoto_urls(photo_urls);

            }

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON.", e);
        }

        return result;
    }

}
