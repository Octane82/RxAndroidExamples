package dkarelin.ru.rxjavaexample_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Загрузка файлов тайлов с сервера
 * адрес вида zoom/x/y.png
 *
 *
 */
public class RxDownloadFileActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_download_file);

        tvMessage = (TextView) findViewById(R.id.tvMessage);
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
        /*listUrls.add("12/2483/1283.png");
        listUrls.add("12/2481/1284.png");
        listUrls.add("12/2482/1284.png");
        listUrls.add("12/2483/1284.png");*/

        // Create Retrofir REST adapter
        GetTilesRestAdapter restAdapter = new GetTilesRestAdapter();

        // TEST download TILE !!!
        restAdapter.getTileFromBackend("12/2481/1283.png").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "On response: " + response.message());

                Headers headers = response.headers();
                Log.d(TAG, "HEADERS: " + headers.toString());
                if (response.isSuccessful()) {

                    Log.d(TAG, "RESPONSE file length: " + response.body().contentLength() + " kb");

                } else {
                    Log.d(TAG, "Not success request");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "RESPONSE FAILURE");
                t.printStackTrace();
            }
        });

        // Rx download list TILES
        /*Observable.from(listUrls)
                //.map(s -> s + "\n")
                .flatMap(s -> restAdapter.getTileFromBackend(s))
                .subscribe(s -> {
                    //tvMessage.setText(s);
                    //showLog(s);
                    try {
                        showLog(s.string());
                        //s.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });*/



    }


    private void showLog(String s) {
        Log.d(TAG, s);
    }


    /**
     * tvMessage.setText("\nfhgfdd\nhggg\npuhhhhh");
     tvMessage.setText("\nkjdjvkdv");
     tvMessage.setText("\nfhkckbdk");
     */

}
