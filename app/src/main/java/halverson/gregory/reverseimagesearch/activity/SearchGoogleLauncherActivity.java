package halverson.gregory.reverseimagesearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import halverson.gregory.reverseimagesearch.R;
import halverson.gregory.reverseimagesearch.adapter.DeviceImagesAdapter;
import halverson.gregory.reverseimagesearch.thread.DeviceImagesJob;

public class SearchGoogleLauncherActivity extends ActionBarActivity
{
    // View
    private GridView gridView;
    private ProgressBar spinner;

    // Image loader
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    // Adapter
    DeviceImagesAdapter adapter;

    // Thread
    DeviceImagesJob thread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_google_launcher);

        // Instantiate image loader
        imageLoader = ImageLoader.getInstance();

        // Set options for image loader
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        // Get handle of image grid
        gridView = (GridView) findViewById(R.id.searchGoogleGridView);

        // Get handle of spinner
        spinner = (ProgressBar) findViewById(R.id.searchGoogleSpinner);

        // Set adapter for image grid data
        adapter = new DeviceImagesAdapter(this, imageLoader, options);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(SearchGoogleLauncherActivity.this, SearchGoogleIntentActivity.class);
                //Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(adapter.getUriAtPosition(position)));
                startActivity(intent);
                //Toast.makeText(SearchGoogleLauncherActivity.this, adapter.getUriAtPosition(position), Toast.LENGTH_LONG).show();
            }
        });

        thread = new DeviceImagesJob(this, adapter);
        thread.execute();
    }

    @Override
    public void onBackPressed()
    {
        thread.cancel(true);

        super.onBackPressed();
    }

    public void hideSpinner()
    {
        spinner.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_google_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
