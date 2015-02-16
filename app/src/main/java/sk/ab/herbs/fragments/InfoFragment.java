package sk.ab.herbs.fragments;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import sk.ab.herbs.Constants;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.tools.Margin;
import sk.ab.tools.Utils;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class InfoFragment extends Fragment {

    private static final int INFO_SECTIONS = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_card_info, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            setInfo(((DisplayPlantActivity) getActivity()).getPlant(), getView());
        }
    }

    private void setInfo(Plant plant, View convertView) {
        TextView firstRow = (TextView) convertView.findViewById(R.id.first_row);
        StringBuilder firstRowText = new StringBuilder();
        firstRowText.append(plant.getDescWithHighlight(getResources().getString(R.string.plant_height),
                ""+plant.getHeight_from()+"-"+plant.getHeight_to())+" "+ Constants.HEIGHT_UNIT+"   ");
        firstRowText.append(plant.getDescWithHighlight(getResources().getString(R.string.plant_flowering),
                ""+ Utils.getMonthName(plant.getFlowering_from()-1)+"-"+Utils.getMonthName(plant.getFlowering_to() - 1)));
        firstRow.setText(Html.fromHtml(firstRowText.toString()));

        if (plant.getDescription() != null) {
            TextView upImage = (TextView) convertView.findViewById(R.id.up_image);
            upImage.setText(Html.fromHtml(plant.getDescription()));
        }

        final int[][] spanIndex = new int[2][INFO_SECTIONS];
        final StringBuilder text = new StringBuilder();
        String[][] sections = { {getResources().getString(R.string.plant_flowers), plant.getFlower()},
                {getResources().getString(R.string.plant_inflorescences), plant.getInflorescence()},
                {getResources().getString(R.string.plant_fruits), plant.getFruit()},
                {getResources().getString(R.string.plant_leaves), plant.getLeaf()},
                {getResources().getString(R.string.plant_stem), plant.getStem()},
                {getResources().getString(R.string.plant_habitat), plant.getHabitat()}
        };

        for(int i = 0; i < INFO_SECTIONS; i++ ) {
            spanIndex[0][i] = text.length();
            spanIndex[1][i] = text.length() + sections[i][0].length();
            text.append(sections[i][0]);
            text.append(": ");
            text.append(sections[i][1]);
            text.append(" ");
            //text.append("\n");
        }

        final TextView nextToImage = (TextView) convertView.findViewById(R.id.next_to_image);
        final ImageView drawing = (ImageView) convertView.findViewById(R.id.plant_background);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        drawing.setMaxWidth(size.x / 3);
        final SpannableString ss = new SpannableString(text.toString());
        for(int i = 0; i < INFO_SECTIONS; i++ ) {
            ss.setSpan(new StyleSpan(Typeface.BOLD), spanIndex[0][i], spanIndex[1][i],
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (plant.getBack_url() != null) {
            ImageLoader.getInstance().displayImage(plant.getBack_url(), drawing, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    int leftMargin = loadedImage.getWidth() + 10;
                    int height = loadedImage.getHeight();
                    ss.setSpan(new Margin(height / (int) (nextToImage.getLineHeight() * nextToImage
                            .getLineSpacingMultiplier() + nextToImage.getLineSpacingExtra()) + 2,
                            leftMargin), 0, ss.length(), Spanned.SPAN_PARAGRAPH);
                    nextToImage.setText(ss);
                }
            });

        } else {
            nextToImage.setText(ss);
        }
    }
}

