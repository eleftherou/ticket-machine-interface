package com.project.ticketmachine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.ticketmachine.databinding.ActivityCardInfoBinding;
import com.project.ticketmachine.databinding.ActivityMoreScreenBinding;

import org.bson.Document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class CardInfo extends AppCompatActivity {

    ActivityCardInfoBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCardInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(CardInfo.this, MoreScreen.class);
            CardInfo.this.startActivity(myIntent);
        });

        if (!MainActivity.user.get("LastProductId").toString().equals("")){
            String[] params = new String[1];
            params[0] = MainActivity.user.get("LastProductId").toString();

            LoadTicketName loadTicketName = new LoadTicketName();
            loadTicketName.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
        else{
            binding.code.setText(MainActivity.user.get("userID").toString());
            binding.category.setText(MainActivity.user.get("Category").toString());
            binding.name.setText(MainActivity.user.get("userName").toString());
            binding.type.setText(MainActivity.user.get("Type").toString());

        }







    }


    private class LoadTicketName extends AsyncTask<String, String, String> {

        private String ticket_name = null;
        private Socket socket = null;
        private ObjectOutputStream objectOutputStream;
        private ObjectInputStream objectInputStream;

        @SuppressLint("SetTextI18n")
        @Override
        protected String doInBackground(String... strings) {

            String ticketId = strings[0];

            try {
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

                objectOutputStream.writeUTF("GetTicketName");
                objectOutputStream.flush();

                objectOutputStream.writeUTF(ticketId);
                objectOutputStream.flush();

                ticket_name = objectInputStream.readUTF();

                publishProgress("Done!");


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            binding.code.setText(MainActivity.user.get("userID").toString());
            binding.category.setText(MainActivity.user.get("Category").toString());
            binding.name.setText(MainActivity.user.get("userName").toString());
            binding.type.setText(MainActivity.user.get("Type").toString());
            binding.lastProduct.setText((ticket_name).replace("\n", " "));
            binding.ewallet.setText(("Ποσό : "+ MainActivity.user.get("Wallet") + "€"));
        }


    }
}
