package dkarelin.ru.rxjavaexample_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Загрузка файлов тайлов с сервера
 * адрес вида zoom/x/y.png
 *
 *
 */
public class RxDownloadFileActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Context context;

    private TextView tvMessage;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_download_file);

        context = this;

        tvMessage = (TextView) findViewById(R.id.tvMessage);
        imageView = (ImageView) findViewById(R.id.imageView);
    }


    /**
     * Start Rx download
     * @param view
     */
    public void rxDownloadFiles(View view) {
        Log.d(TAG, "Button download PUSHED !!!");
        ArrayList<String> listUrls = new ArrayList<>();
        listUrls.add("12/2481/1283.png");
        listUrls.add("12/2482/1283.png");
        listUrls.add("12/2483/1283.png");
        listUrls.add("12/2481/1284.png");
        listUrls.add("12/2482/1284.png");
        listUrls.add("12/2483/1284.png");


        // Create Retrofir REST adapter
        GetTilesRestAdapter restAdapter = new GetTilesRestAdapter();

        // OKHttp
        /*OkHttpClient okHttpClient = new OkHttpClient();
        // http://tile.openstreetmap.org/12/2481/1283.png
        Request request = new Request.Builder().url("http://tile.openstreetmap.org/12/2481/1283.png").build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                // TODO: Нельзя делать вывод перед InputStream - это приведёт к ошибке
                File file = new File(Environment.getExternalStorageDirectory(), "testTile.png");
                InputStream is = response.body().byteStream();
                OutputStream os = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int len = is.read(buffer);
                while (len != -1) {
                    os.write(buffer, 0, len);
                    len = is.read(buffer);
                }

                is.close();
                os.close();
            }
        });*/

        // Picasso.with(this).load("http://tile.openstreetmap.org/12/2481/1283.png").into(new MyTarget("yay.png"));

        // TEST download TILE !!!
        /*restAdapter.getTileFromBackend("12/2481/1283.png").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Log.d(TAG, "On response: " + response.message());

                //Headers headers = response.headers();
                //Log.d(TAG, "HEADERS: " + headers.toString());
                if (response.isSuccessful()) {

                    Log.d(TAG, "RESPONSE file length: " + response.body().contentLength() + " kb");

                } else {
                    Log.d(TAG, "Not success request");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.d(TAG, "RESPONSE FAILURE");
                t.printStackTrace();
            }
        });*/



        // Rx download list TILES
        /*Observable.from(listUrls)
                .flatMap(url -> makeImageDirs(url))
                .flatMap(url -> loadBitmap(Picasso.with(context), url))
                .doOnCompleted(() -> {
                    // Упаковываем все тайлы в архив
                    tilesToZip();
                    // Удаляем исходники тайлов
                    deleteTilesSource(new File(Environment.getExternalStorageDirectory(),
                            "custom-tiles/tilespack"));
                })
                .observeOn(Schedulers.io())
                .subscribe();*/


        RxCustomTilesOSM tileDownlod = new RxCustomTilesOSM(this, "http://tile.openstreetmap.org/");
        tileDownlod.downloadTiles(listUrls);


    }





    /**
     * Метод создаёт дирректории под файлы талов
     *
     * @param fileName - 12/2481/1283.png
     * @return         - Возвращает Observer<полный путь к файлу на сервере>
     *                   http://tile.openstreetmap.org/12/2481/1283.png
     */
    private Observable<String> makeImageDirs(String fileName) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String baseFolder = "custom-tiles/tilespack";
                String[] splits = fileName.split("/");
                String zoom = splits[0];
                String xCoord = splits[1];
                String yTile = splits[2];

                File archName = new File(Environment.getExternalStorageDirectory(), baseFolder);
                if (!archName.exists()) {
                    archName.mkdir();           // Папка для хранения .zip с тайлами уже существует !!!
                }
                File zoomDir = new File(archName, zoom + "/" + xCoord);
                if (!zoomDir.exists()) {
                    zoomDir.mkdirs();
                }

                subscriber.onNext("http://tile.openstreetmap.org/" + fileName);
                subscriber.onCompleted();
            }
        });
    }


    /**
     * Return observable load bitmap
     * @param picasso
     * @param imageUrl - полный путь к файлу на сервере
     *                 - http://tile.openstreetmap.org/12/2481/1283.png
     * @return - Observable<Bitmap>
     */
    private Observable<Bitmap> loadBitmap(Picasso picasso, String imageUrl) {
        Log.d(TAG, "Observable -loadImage-: " + imageUrl);
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        saveBitmapOnDevice(bitmap, imageUrl);

                        subscriber.onNext(bitmap);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        subscriber.onError(new Exception("failed to load " + imageUrl));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                subscriber.add(new Subscription() {
                    private boolean unSubscribed;
                    @Override
                    public void unsubscribe() {
                        picasso.cancelRequest(target);
                        unSubscribed = true;
                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return unSubscribed;
                    }
                });
                picasso.load(imageUrl).into(target);
            }
        });
    }


    /**
     * Сохраняем изображение тайла на устройстве
     * @param bitmap - bitmap file
     * @param serverUrl - http://tile.openstreetmap.org/12/2481/1283.png
     */
    private void saveBitmapOnDevice(Bitmap bitmap, String serverUrl) {
        File file =
                new File(Environment.getExternalStorageDirectory(),
                        "custom-tiles/tilespack/" + convertUrl2fileName(serverUrl));

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Log.d(TAG, "Save file: " + Thread.currentThread());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Curr thread: " + Thread.currentThread());
    }


    /**
     * Метод конвертирует полный адрес файла
     * с сервера в название файла
     * формата (zoom/x/y.png)
     * @param url - http://tile.openstreetmap.org/12/2481/1283.png
     * @return    - 12/2481/1283.png
     */
    private String convertUrl2fileName(String url) {
        String[] splits = url.split("/");
        StringBuilder sb = new StringBuilder();
        sb.append(splits[3]);
        sb.append("/");
        sb.append(splits[4]);
        sb.append("/");
        sb.append(splits[5]);
        return sb.toString();
    }


    /**
     * Запаковываем в .ZIP
     * @param
     */
    private void tilesToZip() {
        File base =
                new File(Environment.getExternalStorageDirectory(),
                        "custom-tiles/tilespack");

        File outZipFile =
                new File(Environment.getExternalStorageDirectory(),
                        "custom-tiles/tilespack.zip");

        try {
            zipFolder(base.getAbsolutePath(), outZipFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Zip dir and files recursive
     * @param srcFolder
     * @param destZipFile
     * @throws Exception
     */
    static public void zipFolder(String srcFolder, String destZipFile)
            throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }


    /**
     * Add folder to .ZIP
     * @param path
     * @param srcFolder
     * @param zip
     * @throws Exception
     */
    static private void addFolderToZip(String path, String srcFolder,
                                       ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);
        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
                        + fileName, zip);
            }
        }
    }

    /**
     * Add file to .ZIP
     * @param path
     * @param srcFile
     * @param zip
     * @throws Exception
     */
    static private void addFileToZip(String path, String srcFile,
                                     ZipOutputStream zip) throws Exception {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }



    /**
     * Метод удаляет дирректорию с файлами
     * тайлов после упаковки в .ZIP
     * @param fileOrDirectory
     */
    void deleteTilesSource(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteTilesSource(child);
        fileOrDirectory.delete();
    }



}
