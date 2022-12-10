

package com.example.scorecounter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Keeping Track";
    public static final String TEAM1WINS = "com.example.scorecounter.TEAM1WINS";
    public static final String TEAM1NAME = "com.example.scorecounter.TEAM1NAME";
    public static final String TEAM2NAME = "com.example.scorecounter.TEAM2NAME";
    public static final String DIFFERENCE = "com.example.scorecounter.DIFFERENCE";
    public boolean team1Wins = false;
    public boolean teamsEntered = false;
    private int team1Score;
    private int team2Score;
    private int difference;
    private TextView instructions;
    private TextView team1ScoreText;
    private TextView team2ScoreText;
    private TextView team1Title;
    private TextView team2Title;
    private TextView titleText;
    private EditText enterTeam1;
    private EditText enterTeam2;
    private Button beginButton;
    private Button team1Button;
    private Button team2Button;

    private SharedPreferences sharedPreferences;
    private LinearLayout mainLinearLayout;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLinearLayout = (LinearLayout)findViewById(R.id.mainLayout);
        team1ScoreText = (TextView)findViewById(R.id.team1Score);
        team2ScoreText = (TextView)findViewById(R.id.team2Score);
        team1Title = (TextView)findViewById(R.id.team1Text);
        team2Title = (TextView)findViewById(R.id.team2Text);
        instructions = (TextView) findViewById(R.id.instructions);
        titleText = (TextView)findViewById(R.id.titleText);
        enterTeam1 = (EditText)findViewById(R.id.enterTeam1);
        enterTeam2 = (EditText)findViewById(R.id.enterTeam2);
        team1Button = (Button)findViewById(R.id.team1Button);
        team2Button = (Button)findViewById(R.id.team2Button);
        beginButton = (Button)findViewById(R.id.beginButton);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        String background = sharedPreferences.getString("sportPreference", "nothing");
        //set background of main layout based on preferences
        TypedArray mainPictures = getResources().obtainTypedArray(R.array.sports_values);


        if(background.matches("res/drawable/basketballbackground.jpg")){
            mainLinearLayout.setBackgroundResource(mainPictures.getResourceId(2, -1));
            titleText.setText("Basketball Score Counter");
        }else if(background.matches("res/drawable/soccerbackground.jpg")){
            mainLinearLayout.setBackgroundResource(mainPictures.getResourceId(1, -1));
            titleText.setText("Soccer Score Counter");
        } else if(background.matches("res/drawable/volleyballbackgroundo.png")){
            mainLinearLayout.setBackgroundResource(mainPictures.getResourceId(0, -1));
            titleText.setText("Volleyball Score Counter");
        }

        hideCounterViews();

        if (savedInstanceState != null){
            team1Score = savedInstanceState.getInt("t1Score");
            team2Score = savedInstanceState.getInt("t2Score");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("t1Score", team1Score);
        outState.putInt("t2Score", team2Score);
        outState.putString("t2ScoreText", team2ScoreText.getText().toString());
        outState.putString("t1ScoreText", team1ScoreText.getText().toString());
        outState.putBoolean("entered", teamsEntered);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getInt("t1Score");
        savedInstanceState.getInt("t2Score");
        savedInstanceState.getString("t1ScoreText");
        savedInstanceState.getString("t2ScoreText");
        savedInstanceState.getString("t1Name");
        savedInstanceState.getString("t2Name");
        savedInstanceState.getBoolean("entered");

        if (savedInstanceState.getBoolean("entered") == true){
            showCounterViews();
        }
    }

    public void hideCounterViews(){
        instructions.setVisibility(View.INVISIBLE);
        team1Button.setVisibility(View.INVISIBLE);
        team2Button.setVisibility(View.INVISIBLE);
        team1ScoreText.setVisibility(View.INVISIBLE);
        team2ScoreText.setVisibility(View.INVISIBLE);
        team1Title.setVisibility(View.INVISIBLE);
        team2Title.setVisibility(View.INVISIBLE);
    }

    public void showCounterViews(){
        teamsEntered = true;
        instructions.setVisibility(View.VISIBLE);
        team1Button.setVisibility(View.VISIBLE);
        team2Button.setVisibility(View.VISIBLE);
        team1ScoreText.setVisibility(View.VISIBLE);
        team2ScoreText.setVisibility(View.VISIBLE);
        team1Title.setVisibility(View.VISIBLE);
        team2Title.setVisibility(View.VISIBLE);
        enterTeam1.setVisibility(View.INVISIBLE);
        enterTeam1.setEnabled(false);
        enterTeam2.setVisibility(View.INVISIBLE);
        enterTeam2.setEnabled(false);
        beginButton.setVisibility(View.INVISIBLE);
        team1Title.setText(enterTeam1.getText().toString());
        team2Title.setText(enterTeam2.getText().toString());
        team1ScoreText.setText(Integer.toString(team1Score));
        team2ScoreText.setText(Integer.toString(team2Score));
    }

    public void beginButtonCode(View view){
        Toast noNameMessage = Toast.makeText(this,"BOTH teams need to be filled in!", Toast.LENGTH_LONG);
        String team1Name = enterTeam1.getText().toString();
        String team2Name = enterTeam2.getText().toString();


        if (team1Name.matches("")){
            noNameMessage.show();
        } else if (team2Name.matches("")){
            noNameMessage.show();
        } else {
            showCounterViews();
        }
    }


    public void updateTeam1Score(View view){
        Intent changeToWinner = new Intent(this,WinnerActivity.class);
        team1Score++;
        team1ScoreText.setText(String.valueOf(team1Score));
        difference = team1Score - team2Score;

        if (team1Score == 5) {
            team1Wins = true;

            team1Score = 5;
            changeToWinner.putExtra(TEAM1WINS, team1Wins);
            changeToWinner.putExtra(TEAM1NAME, team1Title.getText());
            changeToWinner.putExtra(TEAM2NAME, team2Title.getText());
            changeToWinner.putExtra(DIFFERENCE, difference);
            startActivity(changeToWinner);
        }
    }


    public void updateTeam2Score(View view){
        Intent changeToWinner = new Intent(this,WinnerActivity.class);
        team2Score = team2Score + 1;
        team2ScoreText.setText(String.valueOf(team2Score));
        difference = team2Score - team1Score;

        if (team2Score == 5) {
            team2Score = 5;
            changeToWinner.putExtra(TEAM1WINS, team1Wins);
            changeToWinner.putExtra(TEAM1NAME, team1Title.getText());
            changeToWinner.putExtra(TEAM2NAME, team2Title.getText());
            changeToWinner.putExtra(DIFFERENCE, difference);
            startActivity(changeToWinner);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.countermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.SettingsOption:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



