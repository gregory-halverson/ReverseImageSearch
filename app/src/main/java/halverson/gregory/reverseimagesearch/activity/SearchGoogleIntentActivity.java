package halverson.gregory.reverseimagesearch.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import halverson.gregory.reverseimagesearch.FileValidator;
import halverson.gregory.reverseimagesearch.GoogleImageHash;
import halverson.gregory.reverseimagesearch.R;

// Activity that intercepts view and share intents for images
public class SearchGoogleIntentActivity extends ActionBarActivity
{
    // Asynchronous task for fetching Google hash of image
    class GoogleImageHashJob extends AsyncTask<Bitmap, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Bitmap [] bitmap)
        {
            try
            {
                // Send bitmap to google hash server
                String hash = GoogleImageHash.hashFromBitmap(bitmap[0]);

                // Send hash link to browser
                Intent openHashURLinBrowser = new Intent(Intent.ACTION_VIEW);
                openHashURLinBrowser.setData(Uri.parse(hash));
                startActivity(openHashURLinBrowser);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return 0;
        }

        // Close splash screen after hash has been fetched and browser intent sent
        @Override
        protected void onPostExecute(Integer result)
        {
            if (result == 0)
                SearchGoogleIntentActivity.this.finish();
        }
    }

    // Load splash screen
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Setup activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_image_search_google);

        // Load intent
        Intent intent = getIntent();
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // Load image
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUri.toString());
        imageView.setImageBitmap(bitmap);

        // Run hash job
        GoogleImageHashJob job = new GoogleImageHashJob();
        job.execute(bitmap);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reverse_image_search_google, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
