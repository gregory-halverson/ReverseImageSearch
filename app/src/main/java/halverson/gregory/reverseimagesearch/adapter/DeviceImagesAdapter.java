package halverson.gregory.reverseimagesearch.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import halverson.gregory.reverseimagesearch.R;

// Adapter class for image grid
public class DeviceImagesAdapter extends BaseAdapter
{
    // Activity
    private Activity activity;

    // Image loader
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    // Data
    ArrayList<String> sortedImageUriStringList = new ArrayList<String>();

    // Constructor
    public DeviceImagesAdapter(Activity activity, ImageLoader imageLoader, DisplayImageOptions options)
    {
        // Invoke BaseAdapter default constructor
        super();

        // Copy attributes
        this.activity = activity;
        this.imageLoader = imageLoader;
        this.options = options;
    }

    // Get number of items to show in list
    @Override
    public int getCount()
    {
        return sortedImageUriStringList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        final ViewHolder gridViewImageHolder;

        // Make a new view if there isn't already one
        if (convertView == null)
        {
            view = activity.getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
            gridViewImageHolder = new ViewHolder();
            gridViewImageHolder.imageView = (ImageView) view.findViewById(R.id.image);
            gridViewImageHolder.imageView.setMaxHeight(80);
            gridViewImageHolder.imageView.setMaxWidth(80);
            view.setTag(gridViewImageHolder);
        }
        else
        {
            // Recycle view
            gridViewImageHolder = (ViewHolder) view.getTag();
        }

        imageLoader.displayImage(sortedImageUriStringList.get(position), gridViewImageHolder.imageView, options);

        return view;
    }

    public void setUriStringList(ArrayList<String> uriStringList)
    {
        this.sortedImageUriStringList = uriStringList;
    }

    public String getUriAtPosition(int position)
    {
        return sortedImageUriStringList.get(position);
    }

    private static class ViewHolder
    {
        ImageView imageView;
    }
}