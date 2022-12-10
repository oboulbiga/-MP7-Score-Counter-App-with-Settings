

package com.example.scorecounter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import static android.Manifest.permission.CALL_PHONE;

public class WinnerActivity extends AppCompatActivity {
    private TextView winnerText;
    private String winningTeam;
    private String team1Name;
    private String team2Name;
    private int difference;
    private TextView enterNum;
    private Button share;
    private Button restart;
    private Button findNearYou;
    private FloatingActionButton backButton;
    boolean canEnterNumber;

    private SharedPreferences sharedPreferences;
    private LinearLayout winnerLayout;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        winnerLayout = (LinearLayout)findViewById(R.id.winnerLayout);

        canEnterNumber = false;
        winnerText = (TextView)findViewById(R.id.winnerScreenText);

        share = (Button)findViewById(R.id.Share);
        restart = (Button)findViewById(R.id.redoButton);
        findNearYou = (Button)findViewById(R.id.nearYou);
        enterNum = (TextView)findViewById(R.id.enterNumberHere);
        enterNum.setVisibility(View.INVISIBLE);
        backButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        backButton.setVisibility(View.INVISIBLE);


        Intent getValue = getIntent();
        boolean team1Wins = getValue.getBooleanExtra(MainActivity.TEAM1WINS,false);
        team1Name = getValue.getStringExtra(MainActivity.TEAM1NAME);
        team2Name = getValue.getStringExtra(MainActivity.TEAM2NAME);
        difference = getValue.getIntExtra(MainActivity.DIFFERENCE, 0);

        if (team1Wins == true){
            winnerText.setText("CONGRATULATIONS\n" + team1Name + " You won by " + difference + " points!");
            winningTeam = team1Name;
        } else {
            winnerText.setText("CONGRATULATIONS\n" + team2Name + " You won by " + difference + " points!");
            winningTeam = team2Name;
        }



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        String winnerBackground = sharedPreferences.getString("winnerImagePreference", "nothing");

        Log.d("WINNER", winnerBackground);

        TypedArray winnerImages = getResources().obtainTypedArray(R.array.winner_values);

        if(winnerBackground.matches("res/drawable/thumbsup.jpeg")){
            winnerLayout.setBackgroundResource(winnerImages.getResourceId(0, -1));
        }else if(winnerBackground.matches("res/drawable-v24/trophybackground.png")){
            winnerLayout.setBackgroundResource(winnerImages.getResourceId(1, -1));
        } else if(winnerBackground.matches("res/drawable/medal.jpeg")){
            winnerLayout.setBackgroundResource(winnerImages.getResourceId(2, -1));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState2) {
        super.onSaveInstanceState(outState2);
        outState2.putString("whoWon", winnerText.getText().toString());
        outState2.putString("currNum", enterNum.getText().toString());
        outState2.putBoolean("canEnter", canEnterNumber);
        outState2.putString("team1", team1Name);
        outState2.putString("team2", team2Name);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState2) {
        super.onRestoreInstanceState(savedInstanceState2);
        savedInstanceState2.getInt("whoWon");
        savedInstanceState2.getInt("canEnter");
        savedInstanceState2.getString("currNum");
        savedInstanceState2.getString("team1");
        savedInstanceState2.getString("team2");
    }

    public void redoButtonCode(View view){
        Intent mainActiv = new Intent(this, MainActivity.class);
        startActivity(mainActiv);
    }

    public void sendMessageButton(View view){
        String messageToSend = "Hey! Just letting you know that " + winningTeam + " won by " + difference + " points!";
        String preferredNum = sharedPreferences.getString("contactPreference", "404");

        SmsManager.getDefault().sendTextMessage(preferredNum, null, messageToSend, null,null);
        Toast messageSentAlert = Toast.makeText(this, "Message sent!" , Toast.LENGTH_LONG);
        messageSentAlert.show();
    }

    public void checkPermissionAndCall(View view){
        String userPhoneNumber = sharedPreferences.getString("contactPreference", "404");

        if (ContextCompat.checkSelfPermission(WinnerActivity.this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            if(userPhoneNumber.matches("404") || userPhoneNumber.matches("")) {
                new AlertDialog.Builder(this)
                        .setTitle("Phone Number Not Found")
                        .setMessage("Please enter the number you would like to call in your user settings under 'Contacts' to make a phone call")
                        .setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goToSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(goToSettings);
                            }
                        })
                        .setNegativeButton("Forget It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }else {
                Uri phoneNum = Uri.parse("tel:" + sharedPreferences.getString("contactPreference", "404"));
                Intent callIntent = new Intent(Intent.ACTION_CALL, phoneNum);
                startActivity(callIntent);
            }
        } else {
            requestCallPermission();
        }
    }

    private void requestCallPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Requesting Permission")
                .setMessage("To call your friend, we need permission to use your phone!")
                .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(WinnerActivity.this, new String[]{CALL_PHONE}, 1); //grand permission
                        //showEnterNumTextField(); //bring them to the text box so they can enter their number
                    }
                })
                .setNegativeButton("Nah...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void openMap(View view){
        Uri searchUri = Uri.parse("geo:0,0?q=volleyball+near+me");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);

        Intent showChooserIntent = Intent.createChooser(mapIntent, "Choose a map app!");
        try {
            startActivity(showChooserIntent);
        }catch (ActivityNotFoundException e){
            Log.d("OH NO", "Activity not found to open a map!!");
        }
    }
}