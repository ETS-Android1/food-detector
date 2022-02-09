package lukaszb.food.detector.fooddetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public class ConfirmPhotoLocalStorage extends Fragment {

    View view;
    Context context;
    Uri uri;
    ImageView preview;
    OkHttpClient client = new OkHttpClient.Builder().build();
    Service service = new Retrofit.Builder().baseUrl(MainActivity.API_URL)
            .client(client).build().create(Service.class);

    private String[] food_names = new String[5];
    private int[] percentages = new int[5];

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    interface Service {
        @Multipart
        @POST("/local")
        Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
    }


    public ConfirmPhotoLocalStorage() {
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
        return inflater.inflate(R.layout.fragment_confirm_photo_local_storage, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        this.context = view.getContext();
        this.uri = Uri.parse(ConfirmPhotoLocalStorageArgs.fromBundle(getArguments()).getUri());
        this.preview = view.findViewById(R.id.photo_preview_local);
        preview.setImageURI(this.uri);

//      Pick different photo OnClickListener
        Button differentPhoto = view.findViewById(R.id.button_different_local);
        differentPhoto.setOnClickListener(view1 -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            pickPhotoActivityResultLauncher.launch(photoPickerIntent);
        });

//      "Yes,Proceed" OnClickListener
        Button proceed = view.findViewById(R.id.button_confirm_photo_local);
        proceed.setOnClickListener(view12 -> {

            File file = new File(getRealPathFromURI(context,uri));
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

                        NavDirections action = ConfirmPhotoLocalStorageDirections.
                                actionConfirmPhotoLocalStorageToPredictionResult(
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

    ActivityResultLauncher<Intent> pickPhotoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();
                        preview.setImageURI(uri);
                    }
                }
            });
}
