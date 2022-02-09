package lukaszb.food.detector.fooddetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ConfirmPhotoCamera extends Fragment {

    View view;
    Context context;
    ImageView imageView;
    Uri uri;


    OkHttpClient client = new OkHttpClient.Builder().build();
    ConfirmPhotoLocalStorage.Service service = new Retrofit.Builder().baseUrl(MainActivity.API_URL)
            .client(client).build().create(ConfirmPhotoLocalStorage.Service.class);

    private String[] food_names = new String[5];
    private int[] percentages = new int[5];


    public ConfirmPhotoCamera() {
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
        return inflater.inflate(R.layout.fragment_confirm_photo_camera, container, false);
    }



    ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        uri = data.getData();
                        imageView.setImageURI(uri);
                    }
                }
            });

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureActivityResultLauncher.launch(takePictureIntent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        this.context = view.getContext();
        imageView = view.findViewById(R.id.photo_preview);
        uri = Uri.parse(ConfirmPhotoCameraArgs.fromBundle(getArguments()).getUri());
        imageView.setImageURI(uri);

//      Click listener for "Take photo once again" button
        Button onceAgain = view.findViewById(R.id.button_take_photo_again);
        onceAgain.setOnClickListener(view1 -> dispatchTakePictureIntent());

//      "Yes,Proceed" OnClickListener
        Button proceed = view.findViewById(R.id.button_confirm_photo_camera);
        proceed.setOnClickListener(view12 -> {

            File file = new File(ConfirmPhotoLocalStorage.getRealPathFromURI(context,uri));
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            Call<ResponseBody> req = service.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        assert response.body() != null;
                        String rawBody = response.body().string();
                        assert response.body().string() != null;
                        ParseJson parser = new ParseJson(rawBody);
                        parser.parse();
                        percentages = parser.percentages;
                        food_names = parser.food_names;

                        NavDirections action = ConfirmPhotoCameraDirections.
                                actionConfirmPhotoToPredictionResult(
                                        null, food_names, percentages, uri.toString());

                        Navigation.findNavController(view12).navigate(action);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AssertionError e){
                       Toast.makeText(
                                view12.getContext(),
                                R.string.connection_failed,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(
                            view12.getContext(),
                            R.string.connection_failed,
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

    }
}