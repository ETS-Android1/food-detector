package lukaszb.food.detector.fooddetector;

import android.Manifest;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;


public class UrlForm extends Fragment {

    String url;
    View view;
    Set<String> extensions = new HashSet<>();

    public UrlForm() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extensions.add("jpg");
        extensions.add("png");
        extensions.add("jpeg");
        extensions.add("bmp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_url_form, container, false);
    }

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    Picasso.get()
                            .load(url)
                            .fetch(new Callback() {
                                @Override
                                public void onSuccess() {
                                    // Move to preview fragment
                                    NavDirections action = UrlFormDirections.actionUrlFormToConfirmPhotoUrl(url);
                                    Navigation.findNavController(view).navigate(action);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(
                                            view.getContext(),
                                            R.string.connection_failed,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            });
                } else {
                    Snackbar.make(view,
                            "Without internet access app won't be able to download image form the internet.",
                            Snackbar.LENGTH_INDEFINITE).setAction("OK",
                                view -> Toast.makeText(view.getContext(),
                                    R.string.feature_unavailable,
                                    Toast.LENGTH_LONG).show()).show();
                }
            });


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        Button upload_button = view.findViewById(R.id.upload_button);
        EditText url_form = view.findViewById(R.id.url_input);
//      Click listener for "Upload URL" button
        upload_button.setOnClickListener(view1 -> {
            try {
                url = url_form.getText().toString();
                assert extensions.contains(url.substring(url.lastIndexOf(".") + 1));
                requestPermission.launch(Manifest.permission.INTERNET);
            } catch (AssertionError e){
                Toast.makeText(view.getContext(), "Address has to point to an image. Make sure that it ends with proper extension like .jpg for example",
                        Toast.LENGTH_SHORT).show();
            }

        });

    }
}