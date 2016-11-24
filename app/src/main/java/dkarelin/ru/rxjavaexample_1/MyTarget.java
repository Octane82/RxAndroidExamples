package dkarelin.ru.rxjavaexample_1;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * For Picasso save image to target
 */
public class MyTarget implements Target {

    private final String TAG = this.getClass().getSimpleName();

    private final String BASE_FOLDER_NAME = "custom-tiles";
    private final String TILE_ARCHIVE_NAME = "tilespack";

    private String fileName;

    /**
     * Constructor
     * @param fileName
     */
    public MyTarget(String fileName) {
        Log.d(TAG, "Start MyTarget");
        this.fileName = fileName;
    }


    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
       /* new Thread(new Runnable() {
            @Override
            public void run() {*/

                FileOutputStream out;
                File file = parseTileName(fileName);
                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    Log.d(TAG, "Save file: " + Thread.currentThread());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Curr thread: " + Thread.currentThread());
           /* }
        }).start();*/
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d(TAG, "Failed tile loaded");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


    /**
     * Метод парсит строку имени тайла и создаёт
     * дирректории с файлами
     *
     * @param tileName - zoom/x/y.png
     * @return File - path to tile
     */
    private File parseTileName(String tileName) {
        Log.d(TAG, "Parse file name");
        String[] splits = tileName.split("/");
        String zoom = splits[0];
        String xCoord = splits[1];
        String yTile = splits[2];

        File archName =
                new File(Environment.getExternalStorageDirectory(),
                        BASE_FOLDER_NAME + "/" + TILE_ARCHIVE_NAME);                                // Папка для хранения .zip с тайлами уже существует !!!
        if (!archName.exists()) {
            archName.mkdir();

        }
        File zoomDir = new File(archName, zoom + "/" + xCoord);
        if (!zoomDir.exists()) {
            zoomDir.mkdirs();
        }

        return new File(zoomDir, yTile);
    }



}
