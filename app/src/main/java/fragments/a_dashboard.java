package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tpc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class a_dashboard extends Fragment {

    FloatingActionButton dash_newButton;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_a_dashboard, container, false);
        dash_newButton = view.findViewById(R.id.dash_newButton);
        dash_newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEventDialog();
            }
        });

        return view;
    }

    private void newEventDialog() {

    }
}