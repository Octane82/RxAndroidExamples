package dkarelin.ru.rxjavaexample_1;




import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit rest adapter
 */

public class GetTilesRestAdapter {

    private final static String SERVER_BASE_URL = "http://tile.openstreetmap.org/";

    private IDownloadTileRest downloadTileRest;


    public GetTilesRestAdapter() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        downloadTileRest = retrofit.create(IDownloadTileRest.class);
    }


    /**
     * Download one tile from backend server
     * @param tileName
     * @return
     */
    public Call<ResponseBody> getTileFromBackend(String tileName) {
        return downloadTileRest.getTileFromBackendRest(tileName);
    }

}
