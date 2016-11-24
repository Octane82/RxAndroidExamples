package dkarelin.ru.rxjavaexample_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Загрузка тайлов с сервера и упаковка в архив .ZIP
 */
public class RxCustomTilesOSM {

    private final String TAG = this.getClass().getSimpleName();

    private final String TILE_DIR_PATH = "custom-tiles/tilespack";


    private Context context;
    private String tileServer;


    /**
     * Constructor
     *
     * @param context
     * @param tileServer - ("http://tile.openstreetmap.org/")
     */
    public RxCustomTilesOSM(Context context, String tileServer) {
        this.context = context;
        this.tileServer = tileServer;
    }



    /**
     * Загрузить тайлы OSM на устройство
     * @param listUrls
     */
    public void downloadTiles(ArrayList<String> listUrls) {
        Observable.from(listUrls)
                .flatMap(url -> makeImageDirs(url))
                .flatMap(url -> loadBitmap(Picasso.with(context), url))
                .doOnCompleted(() -> {
                    // Упаковываем все тайлы в архив
                    tilesToZip();
                    // Удаляем исходники тайлов
                    deleteTilesSource(new File(Environment.getExternalStorageDirectory(),
                            TILE_DIR_PATH));
                })
                .observeOn(Schedulers.io())
                .subscribe();
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
                String[] splits = fileName.split("/");
                String zoom = splits[0];
                String xCoord = splits[1];

                File archName = new File(Environment.getExternalStorageDirectory(), TILE_DIR_PATH);
                if (!archName.exists()) {
                    archName.mkdir();                                                               // Папка для хранения .zip с тайлами уже существует !!!
                }
                File zoomDir = new File(archName, zoom + "/" + xCoord);
                if (!zoomDir.exists()) {
                    zoomDir.mkdirs();
                }

                subscriber.onNext(tileServer + fileName);
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
                        subscriber.onError(new Exception("Failed to load " + imageUrl));            // TODO: Сделать проверку ошибки
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
                        TILE_DIR_PATH + convertUrl2fileName(serverUrl));
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        sb.append("/");
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
                        TILE_DIR_PATH);
        File outZipFile =
                new File(Environment.getExternalStorageDirectory(),
                        TILE_DIR_PATH + ".zip");
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
     private void zipFolder(String srcFolder, String destZipFile)
            throws Exception {
        ZipOutputStream zip;
        FileOutputStream fileWriter;
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
    private void addFolderToZip(String path, String srcFolder,
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
    private void addFileToZip(String path, String srcFile,
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
    private void deleteTilesSource(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteTilesSource(child);
        fileOrDirectory.delete();
    }



}
