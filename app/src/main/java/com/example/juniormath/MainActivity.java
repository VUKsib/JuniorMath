package com.example.juniormath;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btstart;
    private Button btcont;
    private Button btexit;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // скрывает шторку и экранные кнопки
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // кнопка "начать" игру
        btstart = findViewById(R.id.btstart);
        btstart.setVisibility(View.VISIBLE);

        //кнопка "продолжить" игру
        btcont = findViewById(R.id.btcont);
        btcont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntoNewActivity();
            }
        });

        // Проверяем, нужно ли отображать кнопку "Продолжить"
        if (getScore() > 0) {
            btcont.setVisibility(View.VISIBLE);
            btstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStartConfirmationDialog();
                }
            });
        } else {
            btcont.setVisibility(View.GONE);
            btstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIntoNewActivity();
                }
            });
        }

        // кнопка выхода из приложения
        btexit = findViewById(R.id.btexit);
        btexit.setOnClickListener(v -> exitMyGame());
    }
    public void startIntoNewActivity() // начинаем игру по нажатию этой кнопки и переходим в новую активность
    {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent); finish();
    }

    private int getScore() //получаем данные о счете
    {
        SharedPreferences save = getSharedPreferences(MainActivity2.PREFERENCES_NAME, MODE_PRIVATE);
        return save.getInt(MainActivity2.SCORE_KEY, 0);
    }

    private void saveScore(int score) //для начала игры с нуля
    {
        SharedPreferences preferences = getSharedPreferences(MainActivity2.PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(MainActivity2.SCORE_KEY, score);
        editor.apply();
    }

    public void exitMyGame() // выходим из игры
    {
        System.exit(0);
    }

    private void showStartConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите начать сначала?\nВесь прогресс будет потерян.")
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //пользователь подтвердил начало новой игры
                        saveScore(0);
                        startIntoNewActivity();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //пользователь отменил начало новой игры
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}