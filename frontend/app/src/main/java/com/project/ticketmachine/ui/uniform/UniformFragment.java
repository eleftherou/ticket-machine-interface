package com.project.ticketmachine.ui.uniform;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.project.ticketmachine.InitializeTextToSpeach;
import com.project.ticketmachine.MainActivity;
import com.project.ticketmachine.MyExpandableListAdapter;
import com.project.ticketmachine.Payment;
import com.project.ticketmachine.ProductScreen;

import com.project.ticketmachine.R;
import com.project.ticketmachine.TicketsInfo;
import com.project.ticketmachine.databinding.FragmentUniformBinding;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UniformFragment extends Fragment {

    private static FragmentUniformBinding binding;
    private static MaterialButton[] uniform_ticket_arrayBox;
    private static MaterialButton[] uniform_card_arrayBox;
    private static TextView[] uniform_ticket_arrayDuration;
    private static TextView[] uniform_ticket_arrayCost;
    private static TextView[] uniform_card_arrayDuration;
    private static TextView[] uniform_card_arrayCost;
    public static boolean inited = false;

    String product_kind = null;

    InitializeTextToSpeach initializeTextToSpeach;



    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UniformViewModel uniformViewModel =
                new ViewModelProvider(this).get(UniformViewModel.class);

        binding = FragmentUniformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeTextToSpeach = new InitializeTextToSpeach(getContext());

        final Handler handler = new Handler();

        if (MainActivity.TTS) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initializeTextToSpeach.speak("Επιλέξτε προϊόν ενιαίου.");
                }
            }, 100);
        }

        final TextView textView = binding.textHome;
        uniformViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //boxes
        uniform_ticket_arrayBox = new MaterialButton[6];
        uniform_ticket_arrayBox[0] = binding.uniformBox1;
        uniform_ticket_arrayBox[1] = binding.uniformBox2;
        uniform_ticket_arrayBox[2] = binding.uniformBox3;
        uniform_ticket_arrayBox[3] = binding.uniformBox4;
        uniform_ticket_arrayBox[4] = binding.uniformBox5;
        uniform_ticket_arrayBox[5] = binding.uniformBox6;

        uniform_card_arrayBox = new MaterialButton[8];
        uniform_card_arrayBox[0] = binding.uniformBox1Card;
        uniform_card_arrayBox[1] = binding.uniformBox2Card;
        uniform_card_arrayBox[2] = binding.uniformBox3Card;
        uniform_card_arrayBox[3] = binding.uniformBox4Card;
        uniform_card_arrayBox[4] = binding.uniformBox5Card;
        uniform_card_arrayBox[5] = binding.uniformBox6Card;
        uniform_card_arrayBox[6] = binding.uniformBox7Card;
        uniform_card_arrayBox[7] = binding.uniformBox8Card;

        //tickets

        uniform_ticket_arrayDuration = new TextView[6];
        uniform_ticket_arrayDuration[0] = binding.uniformDurationBox1;
        uniform_ticket_arrayDuration[1] = binding.uniformDurationBox2;
        uniform_ticket_arrayDuration[2] = binding.uniformDurationBox3;
        uniform_ticket_arrayDuration[3] = binding.uniformDurationBox4;
        uniform_ticket_arrayDuration[4] = binding.uniformDurationBox5;
        uniform_ticket_arrayDuration[5] = binding.uniformDurationBox6;

        uniform_ticket_arrayCost = new TextView[6];
        uniform_ticket_arrayCost[0] = binding.uniformCostBox1;
        uniform_ticket_arrayCost[1] = binding.uniformCostBox2;
        uniform_ticket_arrayCost[2] = binding.uniformCostBox3;
        uniform_ticket_arrayCost[3] = binding.uniformCostBox4;
        uniform_ticket_arrayCost[4] = binding.uniformCostBox5;
        uniform_ticket_arrayCost[5] = binding.uniformCostBox6;

        //cards

        uniform_card_arrayDuration = new TextView[8];
        uniform_card_arrayDuration[0] = binding.uniformDurationBox1Card;
        uniform_card_arrayDuration[1] = binding.uniformDurationBox2Card;
        uniform_card_arrayDuration[2] = binding.uniformDurationBox3Card;
        uniform_card_arrayDuration[3] = binding.uniformDurationBox4Card;
        uniform_card_arrayDuration[4] = binding.uniformDurationBox5Card;
        uniform_card_arrayDuration[5] = binding.uniformDurationBox6Card;
        uniform_card_arrayDuration[6] = binding.uniformDurationBox7Card;
        uniform_card_arrayDuration[7] = binding.uniformDurationBox8Card;

        uniform_card_arrayCost = new TextView[8];
        uniform_card_arrayCost[0] = binding.uniformCostBox1Card;
        uniform_card_arrayCost[1] = binding.uniformCostBox2Card;
        uniform_card_arrayCost[2] = binding.uniformCostBox3Card;
        uniform_card_arrayCost[3] = binding.uniformCostBox4Card;
        uniform_card_arrayCost[4] = binding.uniformCostBox5Card;
        uniform_card_arrayCost[5] = binding.uniformCostBox6Card;
        uniform_card_arrayCost[6] = binding.uniformCostBox7Card;
        uniform_card_arrayCost[7] = binding.uniformCostBox8Card;


        if (MainActivity.user == null){
            product_kind = "Ticket";
        }
        else{
            if (MainActivity.user.get("Category").equals("Anonymus"))
                product_kind = "Ticket";
            else
                product_kind = (String) MainActivity.user.get("Type");
        }

        if (Objects.equals(product_kind, "Ticket")){
            // ticket - uniform
            if (inited){
                fill_tickets_uniform();
            }
        }
        else{
            // card - uniform
            if (inited){
                fill_cards_uniform();
            }
        }

        for (int i = 0; i < uniform_card_arrayBox.length; i++){

            int finalI = i;
            MaterialButton box = uniform_card_arrayBox[i];
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent paymentScreen = new Intent(getActivity(), Payment.class);
                    paymentScreen.putExtra("userID", MainActivity.user.get("userID").toString());
                    paymentScreen.putExtra("duration", uniform_card_arrayDuration[finalI].getText().toString());
                    paymentScreen.putExtra("price", uniform_card_arrayCost[finalI].getText().toString());
                    paymentScreen.putExtra("ticketID", uniform_card_arrayBox[finalI].getResources().getResourceEntryName(uniform_card_arrayBox[finalI].getId()));
                    paymentScreen.putExtra("kind", "Uniform");
                    paymentScreen.putExtra("Type", "Card");

                    //  Log.e("send_card_ticket" , String.valueOf(airport_card_arrayBox[finalI].getAccessibilityClassName()));
                    UniformFragment.this.startActivity(paymentScreen);
                }
            });
        }


        for (int i = 0; i < uniform_ticket_arrayBox.length; i++){
            int finalI = i;
            MaterialButton box = uniform_ticket_arrayBox[i];
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent paymentScreen = new Intent(getActivity(), Payment.class);
                    paymentScreen.putExtra("userID", MainActivity.user.get("userID").toString());
                    paymentScreen.putExtra("duration", uniform_ticket_arrayDuration[finalI].getText().toString());
                    paymentScreen.putExtra("price", uniform_ticket_arrayCost[finalI].getText().toString());
                    paymentScreen.putExtra("ticketID", uniform_ticket_arrayBox[finalI].getResources().getResourceEntryName(uniform_ticket_arrayBox[finalI].getId()));
                    paymentScreen.putExtra("kind", "Uniform");
                    paymentScreen.putExtra("Type", "Ticket");

                    //   Log.e("send_ticket" , uniform_ticket_arrayDuration[finalI].getText().toString());
                    UniformFragment.this.startActivity(paymentScreen);
                }
            });
        }


        // on cancel button
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UniformFragment.inited = false;
                Intent intent = new Intent(getActivity(),com.project.ticketmachine.MainActivity.class);
                startActivity(intent);

                try {
                    finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        binding.cardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TicketsInfo().createInfoTicketDialog(getContext(), getLayoutInflater(), initializeTextToSpeach, "Uniform");

            }
        });

        binding.ticketInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TicketsInfo().createInfoTicketDialog(getContext(), getLayoutInflater(), initializeTextToSpeach, "Uniform");
            }
        });

        return root;
    }

    @SuppressLint("SetTextI18n")
    public static void fill_tickets_uniform(){
        binding.cardView.setVisibility(View.INVISIBLE);
        binding.ticketView.setVisibility(View.VISIBLE);

        int i = 1;
        for (Document document : ProductScreen.list) {
            if (Objects.equals(document.get("TicketID"), "uniform_box" + i)) {
                uniform_ticket_arrayDuration[i - 1].setText((String) document.get("Name"));
                uniform_ticket_arrayCost[i - 1].setText("Τιμή : " + (String) document.get("Standard Price") + " €");
                i++;
            }
        }
        inited = true;


    }

    @SuppressLint("SetTextI18n")
    public static void fill_cards_uniform(){

        binding.ticketView.setVisibility(View.INVISIBLE);
        binding.cardView.setVisibility(View.VISIBLE);
        // card - uniform


        int i = 1;
        for (Document document: ProductScreen.list){
            if (Objects.equals(document.get("TicketID"), "uniform_box" + i + "_card")){
                uniform_card_arrayDuration[i-1].setText((String)document.get("Name"));

                if (Objects.equals(MainActivity.user.get("Category"), "Student"))
                    uniform_card_arrayCost[i-1].setText("Τιμή : "+(String)document.get("Student Price")+" €");
                else
                    uniform_card_arrayCost[i-1].setText("Τιμή : "+(String)document.get("Standard Price")+" €");

                i++;
            }

        }

        inited = true;

    }






    @Override
    public void onDestroyView() {
        binding = null;
        initializeTextToSpeach.destroy();
        super.onDestroyView();
    }
}
