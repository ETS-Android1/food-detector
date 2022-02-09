package lukaszb.food.detector.fooddetector.prediction_with_url;

public class FetchResponse{
    private final String result;
    private final DownloadStatus downloadStatus;
    private final String responseError ;

    public FetchResponse(String result, DownloadStatus downloadStatus, String responseError) {
        this.result = result;
        this.downloadStatus = downloadStatus;
        this.responseError = responseError;
    }

    public String getResult() {
        return result;
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public String getResponseError() {
        return responseError;
    }

}
