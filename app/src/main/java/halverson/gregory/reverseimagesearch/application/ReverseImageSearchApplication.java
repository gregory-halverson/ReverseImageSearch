package halverson.gregory.reverseimagesearch.application;

/**
 * Created by Gregory on 5/5/2015.
 */
import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ReverseImageSearchApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        configureDefaultImageLoader(getApplicationContext());
    }

    //    set up your default configuration here
    public static void configureDefaultImageLoader(Context context)
    {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .build();

        ImageLoaderConfiguration defaultConfiguration
                = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(defaultConfiguration);
    }
}
