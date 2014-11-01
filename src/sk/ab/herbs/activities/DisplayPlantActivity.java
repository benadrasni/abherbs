package sk.ab.herbs.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantCardsFragment;
import sk.ab.herbs.fragments.PlantGalleryFragment;
import sk.ab.herbs.fragments.PlantInfoFragment;
import sk.ab.herbs.fragments.PlantTaxonomyFragment;
import sk.ab.tools.DrawableManager;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class DisplayPlantActivity extends ActionBarActivity {
    private DrawableManager drawableManager;

    private Plant plant;
    private PlantInfoFragment infoFragment;
    private PlantGalleryFragment galleryFragment;
    private PlantTaxonomyFragment taxonomyFragment;

    /**
     * Called when the commons is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawableManager = new DrawableManager(getResources());

        // set the Content View
        setContentView(R.layout.list_frame);

        PlantCardsFragment plantCardsFragment = new PlantCardsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.list_frame, plantCardsFragment)
                .commit();
    }

    @Override
    public void onStart() {
        plant = getIntent().getExtras().getParcelable("plant");
        //setPlant(TestPlants.getPlant(plantHeader.getPlantId()));
        super.onStart();
    }

    public DrawableManager getDrawableManager() {
        return drawableManager;
    }

    public Plant getPlant() {
        return plant;
    }

    private PlantInfoFragment getInfoFragment() {
        if (infoFragment == null) {
            infoFragment = new PlantInfoFragment();
        }
        return infoFragment;
    }

    private PlantGalleryFragment getGalleryFragment() {
        if (galleryFragment == null) {
            galleryFragment = new PlantGalleryFragment();
        }
        return galleryFragment;
    }

    private PlantTaxonomyFragment getTaxonomyFragment() {
        if (taxonomyFragment == null) {
            taxonomyFragment = new PlantTaxonomyFragment();
        }
        return taxonomyFragment;
    }
}
