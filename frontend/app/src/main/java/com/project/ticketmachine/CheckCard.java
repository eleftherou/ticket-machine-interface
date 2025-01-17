package com.project.ticketmachine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.ticketmachine.databinding.CheckCardBinding;

import org.bson.Document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.Objects;

public class CheckCard extends AppCompatActivity {

    public static Socket socket = null;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;
    private CheckCardBinding binding;
    private String usage = null;
    private String selected_box = null;
    public static RelativeLayout loading = null;

    InitializeTextToSpeach initializeTextToSpeach;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        System.out.println(extras.get("key"));
        selected_box = (String) extras.get("key");

        binding = CheckCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeTextToSpeach = new InitializeTextToSpeach(getApplicationContext());
        final Handler handler = new Handler();


        loading = binding.loadingPanel;

        if (selected_box.equals("info"))
            usage = "info";
        else if (selected_box.equals("e-wallet"))
            usage = "e-wallet";
        else if (selected_box.equals("recharge"))
            usage = "recharge";
        else
            usage = "other";

        if (selected_box.equals("Card") || selected_box.equals("info") || selected_box.equals("e-wallet") || selected_box.equals("recharge")){
            binding.rechargeTicket.setVisibility(View.INVISIBLE);
            binding.recharge.setVisibility(View.INVISIBLE);
            binding.noRecharge.setVisibility(View.INVISIBLE);


            binding.ticketInfoText.setText("Παρακαλώ τοποθετήστε την κάρτα σας.");
            binding.ticketInfoText.setVisibility(View.VISIBLE);
            binding.cardBarcode.setVisibility(View.VISIBLE);
            binding.sendBarcode.setVisibility(View.VISIBLE);
            binding.MachineImageView.setVisibility(View.VISIBLE);

            if (MainActivity.TTS) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initializeTextToSpeach.speak("Παρακαλώ, τοποθετήστε την κάρτα σας, στο σημείο που δείχνει η εικόνα");
                    }
                }, 100);
            }

        } else {
            if (MainActivity.TTS) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initializeTextToSpeach.speak("Επιλέξτε επιθυμητή ενέργεια");
                    }
                }, 100);
            }
        }



        binding.recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.rechargeTicket.setVisibility(View.INVISIBLE);
                binding.recharge.setVisibility(View.INVISIBLE);
                binding.noRecharge.setVisibility(View.INVISIBLE);

                binding.ticketInfoText.setVisibility(View.VISIBLE);
                binding.cardBarcode.setVisibility(View.VISIBLE);
                binding.sendBarcode.setVisibility(View.VISIBLE);
                binding.MachineImageView.setVisibility(View.VISIBLE);


                if (MainActivity.TTS) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initializeTextToSpeach.speak("Παρακαλώ, τοποθετήστε τo εισιτήριο σας, στο σημείο που δείχνει η εικόνα");
                        }
                    }, 500);
                }

            }
        });

        binding.noRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rechargeTicket.setVisibility(View.INVISIBLE);
                binding.recharge.setVisibility(View.INVISIBLE);
                binding.noRecharge.setVisibility(View.INVISIBLE);
                binding.cancelButton.setVisibility(View.INVISIBLE);
                binding.cancelText1.setVisibility(View.INVISIBLE);

                loading.setVisibility(View.VISIBLE);

                Intent myIntent = new Intent(CheckCard.this, ProductScreen.class);
                myIntent.putExtra("Type", "Simple Ticket");
                CheckCard.this.startActivity(myIntent);



            }
        });

        binding.sendBarcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                //send to the server the barcode code
                String[] params = new String[2];
                params[0] = binding.cardBarcode.getText().toString();
                params[1] = "check";

                if (params[0].length() > 0){

                    binding.ticketInfoText.setVisibility(View.GONE);
                    binding.cardBarcode.setVisibility(View.GONE);
                    binding.sendBarcode.setVisibility(View.GONE);
                    binding.cancelButton.setVisibility(View.GONE);
                    binding.cancelText1.setVisibility(View.GONE);
                    binding.MachineImageView.setVisibility(View.GONE);

                    loading.setVisibility(View.VISIBLE);

                    CheckCode checkCode = new CheckCode();
                    checkCode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence message = "Εισάγετε τον κωδικό της κάρτα σας.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();
                }
            }
        });

        // on cancel button
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckCard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        initializeTextToSpeach.destroy();
        super.onDestroy();
    }

    private class CheckCode extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            if (MainActivity.socket == null){
                //connect to DB
                try {
                    socket = new Socket(MainActivity.MainServerIp , MainActivity.MainServerPort);
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                String ID = strings[0];
                String task = strings[1];

                if (task.equals("check")){
                    Log.e("id" , ID);

                    objectOutputStream.writeUTF("check");
                    objectOutputStream.flush();

                    objectOutputStream.writeUTF(ID);
                    objectOutputStream.flush();

                    String exists = objectInputStream.readUTF();
                    if (exists.equals("Pass")){

                        MainActivity.user = (Document) objectInputStream.readObject();

                        String category = (String) MainActivity.user.get("Category");
                        String type = (String) MainActivity.user.get("Type");
                        String userID = (String) MainActivity.user.get("UserID");

                        Log.e("exists","In db. "+category +" "+type);

                        switch (usage) {
                            case "info": {
                                Intent myIntent = new Intent(CheckCard.this, CardInfo.class);
                                CheckCard.this.startActivity(myIntent);
                                break;
                            }
                            case "e-wallet": {
                                Intent myIntent = new Intent(CheckCard.this, Ewallet.class);
                                CheckCard.this.startActivity(myIntent);
                                break;
                            }
                            case "recharge":
                                if (type.equals("Card")) {
                                    Intent myIntent = new Intent(CheckCard.this, RechargeCode.class);
                                    CheckCard.this.startActivity(myIntent);
                                }
                                break;
                            default:
                                if (Objects.equals(type, selected_box)) {
                                    Intent myIntent = new Intent(CheckCard.this, ProductScreen.class);
                                    myIntent.putExtra("Type", type);
                                    CheckCard.this.startActivity(myIntent);
                                } else {
                                    Log.e("error not match", type);
                                    publishProgress("Done!");
                                }
                                break;
                        }

                    }
                    else{
                        Log.e("exists","Not in db.");
                        publishProgress("Done!");
                    }

                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loading.setVisibility(View.GONE);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            binding.ticketInfoText.setVisibility(View.VISIBLE);
            binding.cardBarcode.setVisibility(View.VISIBLE);
            binding.sendBarcode.setVisibility(View.VISIBLE);
            binding.cancelButton.setVisibility(View.VISIBLE);
            binding.cancelText1.setVisibility(View.VISIBLE);

            Context context = CheckCard.this;
            CharSequence message = "Λάθος κωδικός.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, message, duration);
            toast.show();

        }
    }

}
