package lukaszb.food.detector.fooddetector;


import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;


public class MainMenu extends Fragment {

    View view;

    public MainMenu() {
        // Required empty public constructor

    }


// Code dealing with staring Camera intent and permission request

    ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        String uri = data.getData().toString();
                        NavDirections action = MainMenuDirections.actionMainMenuToConfirmPhoto(uri);
                        Navigation.findNavController(view).navigate(action);

                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionCamera = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureActivityResultLauncher.launch(takePictureIntent);
                } else {
                    Snackbar.make(view,
                            R.string.camera_permission_denied,
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getContext(),
                                    R.string.feature_unavailable,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).show();
                }
            });
//    <---------------------------- END ----------------------------------->

// Code dealing with staring photo picker intent and permission request
    ActivityResultLauncher<Intent> pickPhotoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() ==Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        String uri = data.getData().toString();

                        NavDirections action = MainMenuDirections.
                                actionMainMenuToConfirmPhotoLocalStorage(uri);
                        Navigation.findNavController(view).navigate(action);
                    }
                }
            });
    private final ActivityResultLauncher<String> requestPermissionLocalStorage = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    pickPhotoActivityResultLauncher.launch(photoPickerIntent);
                } else {
                    Snackbar.make(view,
                            R.string.local_permission_denied,
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getContext(),
                                    R.string.feature_unavailable,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).show();
                }
            });
//    <---------------------------- END ----------------------------------->

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

//      Camera button click listener
        CardView camera_button = view.findViewById(R.id.camera_button);
        camera_button.setOnClickListener(view1 -> requestPermissionCamera.launch(Manifest.permission.READ_EXTERNAL_STORAGE));

//      Local storage click listener
        CardView local_storage = view.findViewById(R.id.local_storage_button);
        local_storage.setOnClickListener(view12 -> requestPermissionLocalStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE));

//      URL button click listener
        CardView url_button = view.findViewById(R.id.url_button);
        url_button.setOnClickListener(view13 -> {
            NavDirections action = MainMenuDirections.actionMainMenuToUrlForm();
            Navigation.findNavController(view13).navigate(action);
        });

    }

}
