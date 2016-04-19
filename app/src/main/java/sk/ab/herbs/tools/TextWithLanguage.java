package sk.ab.herbs.tools;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import sk.ab.herbs.Constants;

/**
 *
 * Created by adrian on 15.3.2015.
 */
@SuppressLint("ParcelCreator")
public class TextWithLanguage implements Parcelable {
    private Map<Integer, String> texts;

    public TextWithLanguage() {
        this.texts = new HashMap<Integer, String>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(texts.size());
        for(Map.Entry<Integer,String> entry : texts.entrySet()){
            dest.writeInt(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public void add(Integer key, String value) {
        texts.put(key, value);
    }

    public String getText(int language) {
        String text = texts.get(language);
        if (text == null) {
            text = texts.get(Constants.DEFAULT_LANGUAGE);
        }
        if (text == null) {
            text = "";
        }
        return text;
    }

    public boolean isText(int language) {
        return texts.get(language) != null;
    }
}