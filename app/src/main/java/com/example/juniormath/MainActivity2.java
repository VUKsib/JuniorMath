package com.example.juniormath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    Random rand = new Random();
    public static final String PREFERENCES_NAME = "MyGamePreferences";
    public static final String SCORE_KEY = "score";
    private int score; //счетчик правильных ответов
    private int operation; //переменная для генерации нужной ариф. операции
    private int x1, y1, x2, y2; //переменные арифметических операций
    private double res1, res2; //результат арифм. операций для сравнения
    private Button btback; // "назад"
    private TextView btLess; // "меньше"
    private TextView btEqual; // "равно"
    private TextView btMore; // "больше"
    private TextView equation1, equation2; //поля вывода арифм. операций
    private TextView totalScore; //поле вывода текущего счета
    Dialog dialog;
    Dialog endDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Получаем сохраненный счет
        score = getScore();

        equation1 = findViewById(R.id.equation1); //левое уравнение
        equation2 = findViewById(R.id.equation2); //правое уравнение
        totalScore = findViewById(R.id.totalScore); //текущий счет

        btLess = findViewById(R.id.btLess); //кнопка "Меньше"
        btEqual = findViewById(R.id.btEqual); //кнопка "Равно"
        btMore = findViewById(R.id.btMore); //кнопка "Больше"

        // скрывает шторку и экранные кнопки
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //вызов диалогового окна с правилами игры
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //скрываем заголовок
        dialog.setContentView(R.layout.previewdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачный фон
        dialog.setCancelable(false);//окно не закрыть кнопкой назад
        //кнопка "Продолжить"
        Button btncontinue = dialog.findViewById(R.id.btncontinue);
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //закрываем диалоговое окно
            }
        });
        dialog.show(); //показать окно с правилами

        //вызов диалогового окна в конце игры
        endDialog = new Dialog(this);
        endDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //скрываем заголовок
        endDialog.setContentView(R.layout.enddialog);
        endDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачный фон
        endDialog.setCancelable(false);//окно не закрыть кнопкой назад
        //кнопка "Продолжить"
        Button btncontinue2 = endDialog.findViewById(R.id.btncontinue);
        btncontinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    score = 0;
                    saveScore(score);
                } catch (Exception e) {}
                endDialog.dismiss(); //закрываем диалоговое окно
            }
        });

        //кнопка "назад" в виде стрелки
        btback = findViewById(R.id.btback);
        btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(intent); finish();
                }catch (Exception e) {

                }
            }
        });

        //подключаем анимацию "правильно или нет"
        final Animation a = AnimationUtils.loadAnimation(MainActivity2.this,R.anim.alpha);

        updateScore(); //выводим на экран текущий счет
        generateRandomNumbers(); //генерируем переменные и номер ариф. операции
        updateOperation(); //выводим полученные уравнения на экран

        //обрабатываем нажатие на кнопку "Меньше"
        btLess.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    //происходит при касании экрана по кнопке
                    btEqual.setEnabled(false);//блокируем кнопку "равно"
                    btMore.setEnabled(false);//блокируем кнопку "больше"
                    btLess.setBackgroundResource(R.drawable.style_btn_comparison_purple66_press);
                }
                else if (event.getAction()==MotionEvent.ACTION_UP) {
                    //происходит при отпускании пальца от экрана
                    btLess.setEnabled(false);//блокируем кнопку которую нажали
                    if (res1 < res2) {
                        btLess.setBackgroundResource(R.drawable.style_comp_true);
                        score++; //если правильно счет увеличится на 1
                    }
                    else if (res1 == res2) {
                        btLess.setBackgroundResource(R.drawable.style_comp_false);
                        btEqual.setBackgroundResource(R.drawable.style_stroke_true);
                        score = Math.max(0, score - 3); //если неправильно, то счет уменьшится на 3, но не будет меньше 0
                    }
                    else if (res1 > res2) {
                        btLess.setBackgroundResource(R.drawable.style_comp_false);
                        btMore.setBackgroundResource(R.drawable.style_stroke_true);
                        score = Math.max(0, score - 3);
                    }
                    //запускаем анимацию правильного ответа
                    btLess.startAnimation(a);
                    btEqual.startAnimation(a);
                    btMore.startAnimation(a);

                    updateScore(); //выводим на экран текущий счет
                    if (score == 50) {
                        endDialog.show(); //показать окно конца игры
                    }
                    else
                    {
                        //сохранение набранных очков
                        saveScore(score);

                        generateRandomNumbers(); //генерируем переменные и номер ариф. операции

                        //делаем остановку, для отображения правильного ответа
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //устанавливаем начальный стиль кнопок после паузы
                                btLess.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btEqual.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btMore.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btLess.setEnabled(true);//включаем кнопку "меньше"
                                btEqual.setEnabled(true);//включаем кнопку "равно"
                                btMore.setEnabled(true);//включаем кнопку "больше"
                                updateOperation(); //выводим полученные уравнения на экран
                            }
                        }, 2000);
                    }
                }
                return true;
            }
        });

        //обрабатываем нажатие на кнопку "Равно"
        btEqual.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    //происходит при касании экрана по кнопке
                    btLess.setEnabled(false);//блокируем кнопку "меньше"
                    btMore.setEnabled(false);//блокируем кнопку "больше"
                    btEqual.setBackgroundResource(R.drawable.style_btn_comparison_purple66_press);
                }
                else if (event.getAction()==MotionEvent.ACTION_UP) {
                    //происходит при отпускании пальца от экрана
                    btEqual.setEnabled(false);//блокируем кнопку "равно"
                    if (res1 == res2) {
                        btEqual.setBackgroundResource(R.drawable.style_comp_true);
                        score++;
                    }
                    else if (res1 < res2) {
                        btEqual.setBackgroundResource(R.drawable.style_comp_false);
                        btLess.setBackgroundResource(R.drawable.style_stroke_true);
                        score = Math.max(0, score - 3);
                    }
                    else if (res1 > res2) {
                        btEqual.setBackgroundResource(R.drawable.style_comp_false);
                        btMore.setBackgroundResource(R.drawable.style_stroke_true);
                        score = Math.max(0, score - 3);
                    }
                    //запускаем анимацию правильного ответа
                    btLess.startAnimation(a);
                    btEqual.startAnimation(a);
                    btMore.startAnimation(a);

                    btLess.setEnabled(true);//включаем кнопку "меньше"
                    btMore.setEnabled(true);//включаем кнопку "больше"

                    updateScore(); //выводим на экран текущий счет
                    if (score == 50){
                        endDialog.show(); //показать окно конца игры
                    }
                    else
                    {
                        //сохранение набранных очков
                        saveScore(score);

                        generateRandomNumbers(); //генерируем переменные и номер ариф. операции

                        //делаем остановку, для отображения правильного ответа
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //устанавливаем начальный стиль кнопок после паузы
                                btLess.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btEqual.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btMore.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btLess.setEnabled(true);//включаем кнопку "меньше"
                                btEqual.setEnabled(true);//включаем кнопку "равно"
                                btMore.setEnabled(true);//включаем кнопку "больше"
                                updateOperation(); //выводим полученные уравнения на экран
                            }
                        }, 2000);
                    }
                }
                return true;
            }
        });

        //обрабатываем нажатие на кнопку "Больше"
        btMore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    //происходит при касании экрана по кнопке
                    btLess.setEnabled(false);//блокируем кнопку "меньше"
                    btEqual.setEnabled(false);//блокируем кнопку "равно"
                    btMore.setBackgroundResource(R.drawable.style_btn_comparison_purple66_press);
                }
                else if (event.getAction()==MotionEvent.ACTION_UP) {
                    //происходит при отпускании пальца от экрана
                    btMore.setEnabled(false);//блокируем кнопку "равно"
                    if (res1 > res2) {
                        btMore.setBackgroundResource(R.drawable.style_comp_true);
                        score++;
                    }
                    else if (res1 < res2) {
                        btMore.setBackgroundResource(R.drawable.style_comp_false);
                        btLess.setBackgroundResource(R.drawable.style_stroke_true);
                        score = Math.max(0, score - 3);
                    }
                    else if (res1 == res2) {
                        btMore.setBackgroundResource(R.drawable.style_comp_false);
                        btEqual.setBackgroundResource(R.drawable.style_stroke_true);
                        score = Math.max(0, score - 3);
                    }
                    //запускаем анимацию правильного ответа
                    btLess.startAnimation(a);
                    btEqual.startAnimation(a);
                    btMore.startAnimation(a);

                    btLess.setEnabled(true);//включаем кнопку "меньше"
                    btEqual.setEnabled(true);//включаем кнопку "равно"

                    updateScore(); //выводим на экран текущий счет
                    if (score == 50){
                        endDialog.show(); //показать окно конца игры
                    }
                    else
                    {
                        //сохранение набранных очков
                        saveScore(score);

                        generateRandomNumbers(); //генерируем переменные и номер ариф. операции

                        //делаем остановку, для отображения правильного ответа
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //устанавливаем начальный стиль кнопок после паузы
                                btLess.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btEqual.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btMore.setBackgroundResource(R.drawable.style_btn_comparison_purple80);
                                btLess.setEnabled(true);//включаем кнопку "меньше"
                                btEqual.setEnabled(true);//включаем кнопку "равно"
                                btMore.setEnabled(true);//включаем кнопку "больше"
                                updateOperation(); //выводим полученные уравнения на экран
                            }
                        }, 2000);
                    }
                }
                return true;
            }
        });
    }

    //сохраняем счет
    private void saveScore(int score) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SCORE_KEY, score);
        editor.apply();
    }

    //извлекаем данные о счете
    private int getScore() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getInt(SCORE_KEY, 0);
    }

    //генерируем случайные числа для арифметических операций в зависимости от счета
    private void generateRandomNumbers() {
        // первый уровень сложности
        if (score < 5) {
            operation = rand.nextInt(2) + 1; // выбрать операцию сложение или вычитание
            //генерируем числа от 0 до 5
            x1 = rand.nextInt(6);
            y1 = rand.nextInt(6);
            x2 = rand.nextInt(6);
            y2 = rand.nextInt(6);
        }
        // второй уровень сложности
        else if (score >= 5 && score < 10) {
            operation = rand.nextInt(2) + 1; // выбрать операцию сложение или вычитание
            //генерируем числа от 0 до 10
            x1 = rand.nextInt(11);
            y1 = rand.nextInt(11);
            x2 = rand.nextInt(11);
            y2 = rand.nextInt(11);
        }
        // третий уровень сложности
        else if (score >= 10 && score < 15) {
            operation = rand.nextInt(2) + 1; // выбрать операцию сложение или вычитание
            //генерируем числа от 0 до 20
            x1 = rand.nextInt(21);
            y1 = rand.nextInt(21);
            x2 = rand.nextInt(21);
            y2 = rand.nextInt(21);
        }
        // четвертый уровень сложности
        else if (score >= 15 && score < 20) {
            operation = 3; // выбрать операцию умножение
            //генерируем числа от 0 до 5
            x1 = rand.nextInt(6);
            y1 = rand.nextInt(6);
            x2 = rand.nextInt(6);
            y2 = rand.nextInt(6);
        }
        // пятый уровень сложности
        else if (score >= 20 && score < 25) {
            operation = 3; // выбрать операцию умножение
            //генерируем числа от 0 до 10
            x1 = rand.nextInt(11);
            y1 = rand.nextInt(11);
            x2 = rand.nextInt(11);
            y2 = rand.nextInt(11);
        }
        // шестой уровень сложности
        else if (score >= 25 && score < 30) {
            operation = 4; // выбрать операцию деление
            //генерируем числа от 0 до 5
            x1 = rand.nextInt(6) + 1;
            y1 = rand.nextInt(6) + 1;
            x2 = rand.nextInt(6) + 1;
            y2 = rand.nextInt(6) + 1;
        }
        // седьмой уровень сложности
        else if (score >= 30 && score < 35) {
            operation = 4; // выбрать операцию деление
            //генерируем числа от 0 до 10
            x1 = rand.nextInt(10) + 1;
            y1 = rand.nextInt(10) + 1;
            x2 = rand.nextInt(10) + 1;
            y2 = rand.nextInt(10) + 1;
        }
        // восьмой уровень сложности
        else if (score >= 35 && score < 40) {
            operation = rand.nextInt(3) + 1; // выбрать случайную операцию, кроме деления
            //генерируем числа от 0 до 15
            x1 = rand.nextInt(16);
            y1 = rand.nextInt(16);
            x2 = rand.nextInt(16);
            y2 = rand.nextInt(16);
        }
        // девятый уровень сложности
        else if (score >= 40 && score < 45) {
            operation = rand.nextInt(4) + 1; // выбрать случайную операцию
            //генерируем числа от 5 до 20
            x1 = rand.nextInt(16) + 5;
            y1 = rand.nextInt(16) + 5;
            x2 = rand.nextInt(16) + 5;
            y2 = rand.nextInt(16) + 5;
        }
        // десятый уровень сложности
        else if (score >= 45 && score < 50) {
            operation = rand.nextInt(4) + 1; // выбрать случайную операцию
            //генерируем числа от 1 до 50
            x1 = rand.nextInt(50) + 1;
            y1 = rand.nextInt(50) + 1;
            x2 = rand.nextInt(50) + 1;
            y2 = rand.nextInt(50) + 1;
        }
    }

    //считаем результат и выводим на экран арифм. операции
    private void updateOperation() {
        switch (operation) {
            //сложение
            case 1:
                res1 = x1 + y1; //получаем первый результат
                res2 = x2 + y2; //получаем второй результат
                equation1.setText(x1 + " + " + y1); //выводим первый рез.
                equation2.setText(x2 + " + " + y2); // выводим второй рез.
                break;
            //вычитание
            case 2:
                if (x1 >= y1) {
                    res1 = x1 - y1;
                    equation1.setText(x1 + " - " + y1);
                } else {
                    res1 = y1 - x1;
                    equation1.setText(y1 + " - " + x1);
                }
                if (x2 >= y2) {
                    res2 = x2 - y2;
                    equation2.setText(x2 + " - " + y2);
                } else {
                    res2 = y2 - x2;
                    equation2.setText(y2 + " - " + x2);
                }
                break;
            //умножение
            case 3:
                res1 = x1 * y1;
                res2 = x2 * y2;
                equation1.setText(x1 + " x " + y1);
                equation2.setText(x2 + " x " + y2);
                break;
            //деление
            case 4:
                if (x1 >= y1) {
                    res1 = (double) x1 / y1;
                    equation1.setText(x1 + " \u00F7 " + y1);
                } else {
                    res1 = (double) y1 / x1;
                    equation1.setText(y1 + " \u00F7 " + x1);
                }
                if (x2 >= y2) {
                    res2 = (double) x2 / y2;
                    equation2.setText(x2 + " \u00F7 " + y2);
                } else {
                    res2 = (double) y2 / x2;
                    equation2.setText(y2 + " \u00F7 " + x2);
                }
                break;
            default:
                res1 = 0;
                res2 = 0;
                break;
        }
    }
    public void updateScore() {
        totalScore.setText("" + score);
    }

    //сравниваем уравнения и присваиваем метку
    private void checkAnswer(String choice) {
        boolean correct;

        switch (choice) {
            case "less":
                correct = res1 < res2;
                break;
            case "equal":
                correct = res1 == res2;
                break;
            case "more":
                correct = res1 > res2;
                break;
            default:
                correct = false;
                break;
        }
        if (correct) {
            score += 1;
        } else {
            score = Math.max(0, score - 3);
        }

        updateScore();
        // Генерация новых чисел для следующего раунда
        generateRandomNumbers();
        updateOperation();
    }

    //системная кнопка "Назад"
    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent); finish();
        }catch (Exception e){

        }
    }
}