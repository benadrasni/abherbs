package sk.ab.herbsplus.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.BaseActivity;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.SubscriptionActivity;

/**
 *
 * Created by adrian on 2/9/2018.
 */

public class UtilsPlus {

    public static AlertDialog SubscriptionDialog(final Activity activity) {
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.subscription)
                .setMessage(R.string.subscription_info)
                .setIcon(R.drawable.ic_flower_black_24dp)

                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                })
                .setPositiveButton(R.string.subscribe, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Intent intent = new Intent(activity, SubscriptionActivity.class);
                        activity.startActivity(intent);
                    }
                })

                .create();
    }

    public static AlertDialog LoginDialog(final Activity activity, int message) {
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.login)
                .setMessage(message)
                .setIcon(R.drawable.ic_flower_black_24dp)

                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                })
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        LoginActivity(activity);
                    }
                })

                .create();
    }

    public static void LoginActivity(Activity activity) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                AndroidConstants.REQUEST_SIGN_IN);
    }

    public static void saveToken(BaseActivity activity) {
        String token = activity.getSharedPreferences().getString(AndroidConstants.TOKEN_KEY, null);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (token != null && currentUser != null) {
            DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
            // by user, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_USERS_TOKEN)
                    .setValue(token);
        }
    }

    public static Uri addCameraPhoto(AppCompatActivity activity, String plantName) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        Uri mCurrentPhotoUri = null;
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity, plantName);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoUri = FileProvider.getUriForFile(activity, "sk.ab.herbsplus.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                activity.startActivityForResult(takePictureIntent, AndroidConstants.REQUEST_TAKE_PHOTO);
            }
        }
        return mCurrentPhotoUri;
    }

    public static Uri addGalleryPhoto(AppCompatActivity activity, String plantName) {
        Intent pickPictureIntent = new Intent();
        pickPictureIntent.setType("image/*");
        pickPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        pickPictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Uri mCurrentPhotoUri = null;
        if (pickPictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity, plantName);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, "sk.ab.herbsplus.fileprovider", photoFile);
                pickPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mCurrentPhotoUri = photoURI;
                activity.startActivityForResult(pickPictureIntent, AndroidConstants.REQUEST_PICK_PHOTO);
            }
        }
        return mCurrentPhotoUri;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static File createImageFile(AppCompatActivity activity, String plantName) throws IOException {
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String prefix = "unknown_";
        if (plantName != null) {
            String[] names = plantName.toLowerCase().split(" ");
            if (names.length > 1) {
                prefix = names[0].substring(0, 1) + names[1].substring(0, 1) + "_";
            }
        }
        return File.createTempFile(prefix, ".jpg", storageDir);
    }
}
