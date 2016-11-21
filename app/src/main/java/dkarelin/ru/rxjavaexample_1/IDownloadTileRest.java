package dkarelin.ru.rxjavaexample_1;



import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;


/**
 * Interface for download tile from backend server
 */

public interface IDownloadTileRest {

    // http://tile.openstreetmap.org/12/2481/1283.png
    /*@GET("/{filename}")
    Observable<ResponseBody> getTileFromBackendRest(@Path("filename") String filename);*/


    @GET("{filename}")
    Call<ResponseBody> getTileFromBackendRest(@Path("filename") String filename);


}
