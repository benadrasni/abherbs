package sk.ab.herbsplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import sk.ab.common.util.Utils;
import sk.ab.herbsplus.entity.OfflineFile;

/**
 * Download and unzip all new offline files
 *
 * Created by adrian on 11.3.2017.
 */
public class StorageLoading {

    /**
     * Unzipping download file on background with progress dialog
     *
     */
    private class UnpackZip extends AsyncTask<Void, Integer, Integer> {

        private File _zipFile;
        private int _size;
        private int _processedFiles;

        UnpackZip(File zipFile) {
            _zipFile = zipFile;
            _processedFiles = 0;
            _size = 0;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setProgress(0);

            try {
                ZipFile zf = new ZipFile(_zipFile.getAbsolutePath());
                _size = zf.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            int per = (int) (100.0 * ((float) progress[0] / _size));
            progressDialog.setProgress(per);
        }

        @Override
        protected void onPostExecute(Integer result) {
            filesCount--;
            _zipFile.delete();
            if (filesCount == 0) {
                saveLastUpdateTime();
                finish();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            InputStream is;
            ZipInputStream zis;
            int totalSize = 0;
            try {
                String path = activity.getApplicationContext().getFilesDir() + "/";
                String filename;
                is = new FileInputStream(_zipFile);
                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;
                byte[] buffer = new byte[1024];
                int count;
                while ((ze = zis.getNextEntry()) != null) {
                    filename = ze.getName();

                    if (ze.isDirectory()) {
                        File fmd = new File(path + filename);
                        if (fmd.mkdirs()) {
                            continue;
                        } else {
                            throw new IOException("Failed to create directory structure");
                        }
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(path + filename);

                    while ((count = zis.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, count);
                        totalSize += count;
                    }

                    fileOutputStream.close();
                    zis.closeEntry();
                    _processedFiles++;
                    publishProgress(_processedFiles);
                }

                zis.close();
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }

            return totalSize;
        }
    }

    private Activity activity;
    private ProgressDialog progressDialog;
    private long filesCount;
    private List<Long> filesTime;

    public StorageLoading(Activity activity) {
        this.activity = activity;
    }

    public void downloadOfflineFiles() {
        FirebaseStorage storage = FirebaseStorage.getInstance(SpecificConstants.STORAGE);
        final StorageReference storageRef = storage.getReference(SpecificConstants.OFFLINE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mFirebaseRef = database.getReference(SpecificConstants.OFFLINE);

        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot offline) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage(activity.getResources().getString(R.string.synchronizing));

                SharedPreferences preferences = activity.getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
                final long lastUpdateTime = preferences.getLong(SpecificConstants.LAST_UPDATE_TIME_KEY, 0);

                filesCount = offline.getChildrenCount();
                filesTime = new ArrayList<>();
                for (DataSnapshot dataSnapshot : offline.getChildren()) {
                    final OfflineFile offlineFile = dataSnapshot.getValue(OfflineFile.class);
                    final StorageReference fileReference = storageRef.child(offlineFile.getName());

                    fileReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(final StorageMetadata storageMetadata) {
                            if (!activity.isDestroyed() && lastUpdateTime < storageMetadata.getUpdatedTimeMillis()) {
                                progressDialog.show();
                                progressDialog.setProgress(0);

                                // delete offline files
                                Utils.deleteRecursive(activity.getApplicationContext().getFilesDir());

                                filesTime.add(storageMetadata.getUpdatedTimeMillis());

                                final File localFile = new File(activity.getApplicationContext().getFilesDir() + "/" + offlineFile.getName());

                                fileReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        new UnpackZip(localFile).execute();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                    }
                                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                    @SuppressWarnings("VisibleForTests")
                                    @Override
                                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        int progress = (int) (100.0 * ((float) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
                                        progressDialog.setProgress(progress);
                                    }
                                });
                            } else {
                                filesCount--;
                                if (filesCount == 0) {
                                    finish();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            filesCount--;
                            if (filesCount == 0) {
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void finish() {
        if (!activity.isDestroyed()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            activity.finish();
        }
    }

    private void saveLastUpdateTime() {
        long maxTime = 0;
        for (Long time : filesTime) {
            if (time > maxTime) {
                maxTime = time;
            }
        }

        SharedPreferences preferences = activity.getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SpecificConstants.LAST_UPDATE_TIME_KEY, maxTime);
        editor.apply();
    }
}
