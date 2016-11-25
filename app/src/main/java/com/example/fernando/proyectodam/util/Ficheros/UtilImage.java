package com.example.fernando.proyectodam.util.Ficheros;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Fernando on 27/10/2016.
 */

public class UtilImage {

    public static Bitmap getBitmapFromByteArray( byte[] res ) {

        return BitmapFactory.decodeByteArray(res, 0, res.length);
    }

    public static byte[] getByteArrayFromBitmap( Bitmap bmp ) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 0, bytes);

        return bytes.toByteArray();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getImageResize( Bitmap bmp , int reqWidth, int reqHeight) {

        byte[] res = getByteArrayFromBitmap(bmp);

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0, res.length, options);
    }

    public static Bitmap getImageResize( byte[] res , int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0, res.length, options);
    }

    //Metodo para obtener el string de una determinada imagen
    public static String getStringFromBitmap(Bitmap bitmapPicture) {

        int COMPRESSION_QUALITY = 100;
        String encodedImage = "";

        if ( bitmapPicture != null ) {

            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                    byteArrayBitmapStream);
            byte[] b = byteArrayBitmapStream.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        }

        return encodedImage;
    }

    public static Bitmap getBitmapFromString(String jsonString) {

        if ( jsonString.isEmpty() ) return null;

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


}
