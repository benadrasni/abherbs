package sk.ab.herbs.fragments.rest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sk.ab.herbs.Constants;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.service.RESTResponderFragment;
import sk.ab.herbs.service.RESTService;
import sk.ab.tools.Keys;
import sk.ab.tools.TextWithLanguage;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.10.2014
 * Time: 20:53
 * <p/>
 * Response to "translate" rest service
 */
public class TranslateResponderFragment extends RESTResponderFragment {
    private static String TAG = TranslateResponderFragment.class.getName();

    public void getTranslation(String source, String target, List<TextWithLanguage> textWithLanguages) {
        DisplayPlantActivity activity = (DisplayPlantActivity) getActivity();

        if (activity != null) {

            Bundle params = new Bundle();
            params.putString("key", Keys.TRANSLATE_API_KEY);
            params.putString("source", source);
            params.putString("target", target);

            int language = Constants.getLanguage(source);
            String[] sParams = new String[textWithLanguages.size()*2];
            int i = 0;
            for (TextWithLanguage textWithLanguage : textWithLanguages) {
                sParams[i] = "q";
                sParams[i+1] = textWithLanguage.getText(language);
                i = i+2;
            }

            Intent intent = new Intent(activity, RESTService.class);
            intent.setData(Uri.parse(Constants.REST_ENDPOINT_TRANSLATE));
            intent.putExtra(RESTService.EXTRA_HTTP_VERB, RESTService.GET);
            intent.putExtra(RESTService.EXTRA_PARAMS, params);
            intent.putExtra(RESTService.EXTRA_PARAMS_LIST, sParams);
            intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, getResultReceiver());

            activity.startService(intent);
        }
    }

    @Override
    public void onRESTResult(int code, String result) {

        if (code == 200 && result != null) {
            DisplayPlantActivity activity = (DisplayPlantActivity) getActivity();
            if (activity != null) {
                activity.setTranslation(getTranslateFromJson(result));
            }
        } else {
            Log.e(TAG, "Failed to load data. Check your internet settings.");
        }
    }

    private static List<String> getTranslateFromJson(String json) {
        List<String> result = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray translations = jsonObject.getJSONObject("data").getJSONArray("translations");

            for (int i = 0; i < translations.length(); i++) {
                result.add((String)((JSONObject)translations.get(i)).get("translatedText"));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON.", e);
        }
        return result;
    }

}
