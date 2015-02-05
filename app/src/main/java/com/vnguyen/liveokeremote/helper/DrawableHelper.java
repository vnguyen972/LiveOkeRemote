package com.vnguyen.liveokeremote.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.os.Build;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.thedazzler.droidicon.IconicFontDrawable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DrawableHelper {

    public DrawableHelper() {

    }


    public Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public Bitmap drawable_from_url(String url) throws java.net.MalformedURLException, java.io.IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return x;
    }

    public Drawable buildDrawable(String value, String shape) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getColor(value);
        TextDrawable.IBuilder builder;
        if (shape != null && shape.equalsIgnoreCase("round")) {
            builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig().round();
        } else if (shape != null && shape.equalsIgnoreCase("rect")) {
            builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig().rect();
        } else {
            builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig().roundRect(5);
        }
        TextDrawable drawable = builder.build(value, color);
        return drawable;
    }

    /*
    @deprecated
     */
    public Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setIconAsBackground(String iconName,int color, ImageView img, Context context) {
        IconicFontDrawable icon = new IconicFontDrawable(context);
        icon.setIcon(iconName);
        icon.setIconColor(context.getResources().getColor(color));
        img.setImageDrawable(null);
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= 16) {
            img.setBackground(icon);
        } else {
            img.setBackgroundDrawable(icon);
        }
    }

    public Bitmap detectFace(Bitmap bitmapOrg, int viewWidth, int viewHeight, Resources resources) {
        FaceDetector myFaceDetect;
        FaceDetector.Face[] myFace;
        float myEyesDistance;
        Bitmap resizedBitmap = null;
        try {
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
//            bitmapOrg = BitmapFactory.decodeResource(
//                    resources,
//                    R.drawable.sachin_tendulkar_10102013);

            int targetWidth = bitmapOrg.getWidth();
            int targetHeight = bitmapOrg.getHeight();

            Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                    targetHeight, Bitmap.Config.ARGB_8888);

            RectF rectf = new RectF(0, 0, viewWidth, viewHeight);

            Canvas canvas = new Canvas(targetBitmap);
            Path path = new Path();

            path.addRect(rectf, Path.Direction.CW);
            canvas.clipPath(path);

            canvas.drawBitmap(
                    bitmapOrg,
                    new Rect(0, 0, bitmapOrg.getWidth(), bitmapOrg
                            .getHeight()), new Rect(0, 0, targetWidth,
                            targetHeight), paint);

            Matrix matrix = new Matrix();
            matrix.postScale(1f, 1f);

//            BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
//            bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//
//            bitmapOrg = BitmapFactory.decodeResource(resources,
//                    R.drawable.sachin_tendulkar_10102013,
//                    bitmapFatoryOptions);

            myFace = new FaceDetector.Face[5];
            myFaceDetect = new FaceDetector(targetWidth, targetHeight,
                    5);
            int numberOfFaceDetected = myFaceDetect.findFaces(
                    bitmapOrg, myFace);
            LogHelper.v("Face detected: " + numberOfFaceDetected);
            if (numberOfFaceDetected > 0) {
                PointF myMidPoint = null;
                FaceDetector.Face face = myFace[0];
                myMidPoint = new PointF();
                face.getMidPoint(myMidPoint);
                myEyesDistance = face.eyesDistance() + 20;

                if (myMidPoint.x + viewWidth > targetWidth) {
                    while (myMidPoint.x + viewWidth > targetWidth) {
                        myMidPoint.x--;
                    }
                }
                if (myMidPoint.y + viewHeight > targetHeight) {
                    while (myMidPoint.y + viewHeight > targetHeight) {
                        myMidPoint.y--;
                    }
                }
                resizedBitmap = Bitmap.createBitmap(bitmapOrg,
                        (int) (myMidPoint.x - myEyesDistance),
                        (int) (myMidPoint.y - myEyesDistance),
                        viewWidth, viewHeight, matrix, true);
            } else {
                resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                        viewWidth, viewHeight, matrix, true);
            }
                /* convert Bitmap to resource */
            // Bitmap resizedBitmap = Bitmap.createBitmap(targetBitmap,
            // 0,
            // 0, viewWidth, viewHeight, matrix, true);
//            BitmapDrawable bd = new  BitmapDrawable(resizedBitmap);

//            part2.setBackgroundDrawable(new BitmapDrawable(
//                    getCroppedBitmap(bd.getBitmap())));

        } catch (Exception e) {
            System.out.println("Error1 : " + e.getMessage()
                    + e.toString());
        } finally {
            return resizedBitmap;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        // Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
        // bitmap.getHeight(), Config.ARGB_8888);
        // Canvas canvas = new Canvas(output);
        //
        // final int color = 0xff424242;
        // final Paint paint = new Paint();
        // final Rect rect = new Rect(0, 0, bitmap.getWidth(),
        // bitmap.getHeight());
        //
        // paint.setAntiAlias(true);
        // canvas.drawARGB(0, 0, 0, 0);
        // paint.setColor(color);
        // // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        // canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
        // bitmap.getWidth() / 2, paint);
        // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // canvas.drawBitmap(bitmap, rect, rect, paint);
        // // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        // // return _bmp;
        // return output;

        if (bitmap != null) {
            int targetWidth = bitmap.getWidth();
            int targetHeight = bitmap.getHeight();
            Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(targetBitmap);
            Path path = new Path();
            path.addCircle(((float) targetWidth - 1) / 2,
                    ((float) targetHeight - 1) / 2,
                    (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                    Path.Direction.CCW);

            canvas.clipPath(path);
            Bitmap sourceBitmap = bitmap;
            canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                    sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
                    targetHeight), null);
            return targetBitmap;

        } else {
            return bitmap;
        }
    }
}
