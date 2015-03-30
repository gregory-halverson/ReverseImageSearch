package halverson.gregory.reverseimagesearch;

import android.graphics.Bitmap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Gregory on 2015-03-29.
 */
public class GoogleImageHash
{
    // Url for uploading image to Google
    private static final String GOOGLE_IMAGE_UPLOAD_URL = "https://www.google.com/searchbyimage/upload";

    // Build request for bitmap
    private static HttpEntity buildRequestForBitmap(Bitmap bitmap) throws UnsupportedEncodingException
    {
        // Convert bitmap to byte array body
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] data = bos.toByteArray();
        String fileName = String.format("File_%d.png",new Date().getTime());
        ByteArrayBody bab = new ByteArrayBody(data, fileName);

        // Create entity
        return MultipartEntityBuilder.create()
                .addPart("encoded_image", bab)
                .addPart("image_url", new StringBody("", ContentType.TEXT_PLAIN))
                .addPart("image_content", new StringBody("", ContentType.TEXT_PLAIN))
                .addPart("filename", new StringBody("", ContentType.TEXT_PLAIN))
                .addPart("h1", new StringBody("en", ContentType.TEXT_PLAIN))
                .addPart("bih", new StringBody("179", ContentType.TEXT_PLAIN))
                .addPart("biw", new StringBody("1600", ContentType.TEXT_PLAIN))
                .build();
    }

    // Build request for image file
    private static HttpEntity buildRequestForFile(File imageFile) throws UnsupportedEncodingException
    {
        // Create entity
        return MultipartEntityBuilder.create()
                .addPart("encoded_image", new FileBody(imageFile))
                .addPart("image_url", new StringBody("", ContentType.TEXT_PLAIN))
                .addPart("image_content", new StringBody("", ContentType.TEXT_PLAIN))
                .addPart("filename", new StringBody("", ContentType.TEXT_PLAIN))
                .addPart("h1", new StringBody("en", ContentType.TEXT_PLAIN))
                .addPart("bih", new StringBody("179", ContentType.TEXT_PLAIN))
                .addPart("biw", new StringBody("1600", ContentType.TEXT_PLAIN))
                .build();
    }

    // Post request and get response
    private static BufferedReader sendRequest(CloseableHttpClient client, HttpPost post, HttpEntity entity) throws IOException
    {
        // Attach entity to post
        post.setEntity(entity);

        // Execute post
        CloseableHttpResponse response = client.execute(post);

        // Open input stream
        InputStream content = response.getEntity().getContent();

        // Open reader
        return new BufferedReader(new InputStreamReader(content));
    }

    // Parse response
    private static String parseResponse(BufferedReader reader) throws IOException
    {
        String line = "";
        String hashURL = "";

        // Search for link in response and extract URL
        while ((line = reader.readLine()) != null)
            if (line.indexOf("HREF") > 0)
                hashURL = line.substring(9, line.length() - 11);

        // Check response for empty string
        if (hashURL.equals(""))
            throw new IOException();

        // To-do: check response for valid URL

        return hashURL;
    }

    // Accepts filename of image and returns hash URL
    public static String hashFromFilename(String filename) throws IOException
    {
        // Open file
        return hashFromFile(new File(filename));
    }

    // Accepts file object of image and returns hash URL
    public static String hashFromFile(File imageFile) throws IOException
    {
        // Open HTTP connection
        CloseableHttpClient client = HttpClients.createDefault();

        // Make post
        HttpPost post = new HttpPost(GOOGLE_IMAGE_UPLOAD_URL);

        // Build request
        HttpEntity entity = buildRequestForFile(imageFile);

        // Send request
        BufferedReader reader = sendRequest(client, post, entity);

        // Parse response
        return parseResponse(reader);
    }

    // Accepts file object of image and returns hash URL
    public static String hashFromBitmap(Bitmap bitmap) throws IOException
    {
        // Open HTTP connection
        CloseableHttpClient client = HttpClients.createDefault();

        // Make post
        HttpPost post = new HttpPost(GOOGLE_IMAGE_UPLOAD_URL);

        // Build request
        HttpEntity entity = buildRequestForBitmap(bitmap);

        // Send request
        BufferedReader reader = sendRequest(client, post, entity);

        // Parse response
        return parseResponse(reader);
    }
}