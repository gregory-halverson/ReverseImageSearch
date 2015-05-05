package halverson.gregory.reverseimagesearch.thread;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import halverson.gregory.reverseimagesearch.FileValidator;
import halverson.gregory.reverseimagesearch.activity.SearchGoogleLauncherActivity;
import halverson.gregory.reverseimagesearch.adapter.DeviceImagesAdapter;

/**
 * Created by Gregory on 5/5/2015.
 */
public class DeviceImagesJob extends AsyncTask<Void, Void, DeviceImagesJob.ReturnCode>
{
    // Logcat tag
    public static final String TAG = "DeviceImagesJob";

    // Return codes
    public static enum ReturnCode
    {
        INDEX_COMPLETED_WITH_NO_ERROR,
        INDEX_CANCELLED
    }

    // Pointers
    SearchGoogleLauncherActivity activity;
    DeviceImagesAdapter adapter;

    // Data
    ArrayList<String> sortedImageUriStringList;

    // Constructor
    public DeviceImagesJob(SearchGoogleLauncherActivity activity, DeviceImagesAdapter adapter)
    {
        this.activity = activity;
        this.adapter = adapter;

        this.sortedImageUriStringList = new ArrayList<String>();
    }

    @Override
    protected ReturnCode doInBackground(Void [] args)
    {
        Map<String, Long> modifiedDates = new HashMap<String, Long>();

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String path = "";

        // Iterate through cursor
        for (int i = 0; i < count; i++)
        {
            // Check if thread is cancelled
            if (isCancelled())
            {
                cursor.close();

                return ReturnCode.INDEX_CANCELLED;
            }

            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            //Store the path of the image
            path = cursor.getString(dataColumnIndex);

            if (FileValidator.checkFilePath(path))
            {
                Long modifiedDate = FileValidator.getLastModifiedDatePath(path);
                String uriString = FileValidator.decodedUriStringFromFilePathString(path);
                modifiedDates.put(uriString, modifiedDate);
            }
        }

        sortedImageUriStringList = sortByDate(modifiedDates);

        cursor.close();

        if (isCancelled())
            return ReturnCode.INDEX_CANCELLED;

        return ReturnCode.INDEX_COMPLETED_WITH_NO_ERROR;
    }

    private static ArrayList<String> sortByDate(Map<String, Long> modifiedDates)
    {
        ArrayList<String> searchResults = new ArrayList<String>();

        List<Map.Entry<String, Long>> sortableModifiedDates = new ArrayList<Map.Entry<String, Long>>();

        sortableModifiedDates.addAll(modifiedDates.entrySet());

        Collections.sort(sortableModifiedDates, new Comparator<Map.Entry<String, Long>>()
        {
            @Override
            public int compare(Map.Entry<String, Long> left, Map.Entry<String, Long> right)
            {
                return -1 * left.getValue().compareTo(right.getValue());
            }
        });

        for (Map.Entry<String, Long> entry: sortableModifiedDates)
            searchResults.add(entry.getKey());

        return searchResults;
    }

    // Cleanup after search
    @Override
    protected void onPostExecute(ReturnCode result)
    {
        switch (result)
        {
            // Close splash screen after hash has been fetched and browser intent sent
            case INDEX_COMPLETED_WITH_NO_ERROR:
                adapter.setUriStringList(sortedImageUriStringList);
                activity.hideSpinner();
                adapter.notifyDataSetChanged();
                break;

            // Clear memory
            case INDEX_CANCELLED:
                Log.d(TAG, "Index job cancelled");
                break;
        }
    }
}
