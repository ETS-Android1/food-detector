package lukaszb.food.detector.fooddetector.prediction_with_url;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;

public class FetchRawData {

    private DownloadStatus downloadStatus;
    private String downloadError;
    private final Executor executor;
    private static final String TAG = "GetRawData";

    public FetchRawData(Executor executor) {
        this.downloadStatus = DownloadStatus.IDLE;
        this.executor = executor;
    }

    public void fetch(String urlToFetch, FetchCallback<FetchResponse> callback){
        callback.onStart();
        executor.execute(new Runnable() {
            @Override
            public void run() {
               FetchResponse res = fetchAsync(urlToFetch);
               callback.onComplete(res);
            }
        });

    }


    private HttpURLConnection createConnection (String urlToFetch){
            HttpURLConnection connection = null;

            if(urlToFetch == null){
                downloadStatus = DownloadStatus.NOT_INITIALISED;
                return null;
            }

            try{
                downloadStatus = DownloadStatus.PROCESSING;
                URL url = new URL(urlToFetch);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int response  = connection.getResponseCode();
                Log.d(TAG, "createConnection: The response code was " + response);
                return connection;

            } catch (MalformedURLException e) {
                Log.e(TAG, "createConnection: Invalid url" + e.getMessage());
                downloadError = e.getMessage();
            } catch (SecurityException e){
                Log.e(TAG, "createConnection: Security exception. Needs permission?" + e.getMessage());
                downloadError = e.getMessage();
            } catch (IOException e){
                Log.e(TAG, "createConnection: Error while connecting" +e.getMessage());
                downloadError = e.getMessage();
            }
            downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            return null;
    }


    private String readFetchResult(HttpURLConnection connection){

        try {
            StringBuilder result = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String line;
            while ( null != (line = reader.readLine())){
                result.append(line).append("\n");
            }

            reader.close();

            downloadStatus = DownloadStatus.SUCCESS;
            return result.toString();

        } catch (IOException e){
            Log.e(TAG, "getFetchResult: " +e.getMessage());
            downloadError = e.getMessage();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }

        downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    private FetchResponse fetchAsync(String urlToFetch){
        HttpURLConnection connection = createConnection(urlToFetch);
        if (connection == null){
            return new FetchResponse(null ,downloadStatus, downloadError);
        }

        String result = readFetchResult(connection);

        if (result == null){
           return new FetchResponse(null ,downloadStatus, downloadError);
        }

        return new FetchResponse(result ,downloadStatus, downloadError);
    }

}

