package lukaszb.food.detector.fooddetector;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class PredictionResult extends Fragment {

    private View view;
    private String[] foodNames;
    private int[] percentages;

    public PredictionResult() {
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
        return inflater.inflate(R.layout.fragment_prediction_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        ImageView imageView = view.findViewById(R.id.confirm_photo_image);
        String url = PredictionResultArgs.fromBundle(getArguments()).getUrl();
        if (url != null){
            Picasso.get().load(url).into(imageView);
        } else {
            Uri uri = Uri.parse(PredictionResultArgs.fromBundle(getArguments()).getUri());
            imageView.setImageURI(uri);
        }


        // Get predictions
        this.percentages = PredictionResultArgs.fromBundle(getArguments()).getPercentages();
        this.foodNames = PredictionResultArgs.fromBundle(getArguments()).getFoodNames();

        displayResults();
//      Main menu button onClickListener
        Button goToMainMenu = view.findViewById(R.id.back_to_main_menu);
        goToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = PredictionResultDirections.actionPredictionResultToMainMenu();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void displayResults(){
        ProgressBar bar1 = view.findViewById(R.id.bar1);
        bar1.setProgress(percentages[4]);
        TextView probability1 = view.findViewById(R.id.food_probability1);
        String prob1 = percentages[4] + " %";
        probability1.setText(prob1);
        TextView label1 = view.findViewById(R.id.food_label1);
        label1.setText(foodNames[4]);

        ProgressBar bar2 = view.findViewById(R.id.bar2);
        bar2.setProgress(percentages[3]);
        TextView probability2 = view.findViewById(R.id.food_probability2);
        String prob2 = percentages[3] + " %";
        probability2.setText(prob2);
        TextView label2 = view.findViewById(R.id.food_label2);
        label2.setText(foodNames[3]);

        ProgressBar bar3 = view.findViewById(R.id.bar3);
        bar3.setProgress(percentages[2]);
        TextView probability3 = view.findViewById(R.id.food_probability3);
        String prob3 = percentages[2] + " %";
        probability3.setText(prob3);
        TextView label3 = view.findViewById(R.id.food_label3);
        label3.setText(foodNames[2]);

        ProgressBar bar4 = view.findViewById(R.id.bar4);
        bar4.setProgress(percentages[1]);
        TextView probability4 = view.findViewById(R.id.food_probability4);
        String prob4 = percentages[1] + " %";
        probability4.setText(prob4);
        TextView label4 = view.findViewById(R.id.food_label4);
        label4.setText(foodNames[1]);

        ProgressBar bar5 = view.findViewById(R.id.bar5);
        bar5.setProgress(percentages[0]);
        TextView probability5 = view.findViewById(R.id.food_probability5);
        String prob5 = percentages[0] + " %";
        probability5.setText(prob5);
        TextView label5 = view.findViewById(R.id.food_label5);
        label5.setText(foodNames[0]);

    }

}