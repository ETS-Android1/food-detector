package lukaszb.food.detector.fooddetector;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lukaszb.food.detector.fooddetector.prediction_with_url.DownloadStatus;
import lukaszb.food.detector.fooddetector.prediction_with_url.FetchCallback;
import lukaszb.food.detector.fooddetector.prediction_with_url.FetchRawData;
import lukaszb.food.detector.fooddetector.prediction_with_url.FetchResponse;

public class ConfirmPhotoUrl extends Fragment {

    private Handler uiHandler;
    private View view;
    private String url;
    private String[] food_names = new String[5];
    private int[] percentages = new int[5];

    private final Executor executor = Executors.newFixedThreadPool(1);
    private final FetchRawData fetchRawData = new FetchRawData(executor);


    public ConfirmPhotoUrl() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_photo_url, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        uiHandler = view.getHandler();
        ImageView imageView = view.findViewById(R.id.photo_preview_url);
        url = ConfirmPhotoUrlArgs.fromBundle(getArguments()).getUrl();
        Picasso.get().load(url).into(imageView);


//      Click listener for "...different URL" Button
        Button different_url = view.findViewById(R.id.button_different_url);
        different_url.setOnClickListener(view1 -> {
            NavDirections action = ConfirmPhotoUrlDirections.actionConfirmPhotoUrlToUrlForm();
            Navigation.findNavController(view1).navigate(action);
        });

//      Click listener for "Yes, Proceed".
        Button proceed = view.findViewById(R.id.button_proceed_url);
        String full_url = MainActivity.API_URL + "/url?url=" + url;
        proceed.setOnClickListener(view12 -> fetchRawData.fetch(full_url,callback));
    }

    FetchCallback<FetchResponse> callback = new FetchCallback<FetchResponse>(){
        @Override
        public void onStart() {
            super.onStart();
            System.out.println("Request started");
        }

        @Override
        public void onComplete(FetchResponse response) {
            super.onComplete(response);
            if (response.getDownloadStatus() == DownloadStatus.SUCCESS && response.getResult() != null){
                ParseJson parser = new ParseJson(response.getResult());
                parser.parse();
                percentages = parser.percentages;
                food_names = parser.food_names;

                uiHandler.post(() -> {
                    NavDirections action = ConfirmPhotoUrlDirections.
                            actionConfirmPhotoUrlToPredictionResult(url,
                                    food_names, percentages, null);
                    Navigation.findNavController(view).navigate(action);
                });

            } else {
                uiHandler.post(() -> Toast.makeText(
                        view.getContext(),
                        R.string.connection_failed,
                        Toast.LENGTH_LONG
                ).show());

            }

        }
    };
}