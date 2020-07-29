package com.example.kromka.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kromka.Models.Answer;
import com.example.kromka.Models.DbHelper;
import com.example.kromka.Models.Question;
import com.example.kromka.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static android.widget.LinearLayout.LayoutParams;

public class MainActivity extends AppCompatActivity {
    Button btnNext, btnBack;  //нижние кнопки, которые в зависимости от текущего вопроса выполняют разные действия
    TextView txtQuestionCount, txtQuestionText, txtWordOnCard;  //текстовые поля, на которых отображается различная информация
    LinearLayout container;  //контейнер, в который будут добавляться кнопки и доп.описание, если есть
    RelativeLayout containerCard;  //карточка, которая меняет размер в зависимости от устройства (78% от высоты)
    RelativeLayout global; //контейнер-родитель всех элементов
    DisplayMetrics dm;  //позволяет получить размеры экрана
    int height, width;  //высота и ширина экрана
    DbHelper dbHelper;  //экземпляр класса для работы с бд
    int currentQuestionNumber = 0;  //номер текущего вопроса
    Question currentQuestion; //текущий вопрос
    ArrayList<Question> questions;  //массив вопросов
    ArrayList<Drawable> progressbar = new ArrayList<>();  //изображения индикатора пройденности
    int[] currentPoints = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю строку состояния:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //подгрузка ресурса
        setContentView(R.layout.activity_main);
        //инициализация переменных
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        txtQuestionCount = findViewById(R.id.txtQuestionCount);
        txtQuestionText = findViewById(R.id.txtQuestionText);
        container = findViewById(R.id.linearContainer);
        global = findViewById(R.id.theMainLayout);
        containerCard = findViewById(R.id.containerCard);
        txtWordOnCard = findViewById(R.id.txtWordOnTopOfCard);
        dbHelper = new DbHelper(this);
        //изменение размера элементов в зависимости от размера экрана
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        height = dm.heightPixels;
        width = dm.widthPixels;
        containerCard.getLayoutParams().height = (int) (height * 0.78);
        //работа с первым вопросом
        questions = dbHelper.getAllQuestions();
        currentQuestion = questions.get(currentQuestionNumber);
        addAllProgressBars();
        drawCurrentState();
    }

    /**
     * Обработчик нажатий на кнопку вперед
     * Если currentPoints = -1, то есть ничего не выбрано, польвателя об этом оповестит
     * Если currentQuestionNumber < 12, то загружается вопрос и ответы к нему
     * Если currentQuestionNumber = 12, то загружается результат
     * Если currentQuestionNumber = 13, то опрос обнуляется и начинается заново
     */
    public void onNextClicked(View v) {
        if (currentPoints[currentQuestionNumber] == -1) {
            Toast.makeText(this, "Выберите один вариант ответа", Toast.LENGTH_SHORT).show();
        } else {
            if (currentQuestionNumber < questions.size()-1) {
//                currentPoints = -1;
                container.removeAllViews();
                currentQuestionNumber++;
                currentQuestion = questions.get(currentQuestionNumber);
                drawCurrentState();
            } else if (currentQuestionNumber == questions.size()-1) {
                container.removeAllViews();
                currentQuestionNumber++;
                drawFinalPage();
            } else if (currentQuestionNumber == questions.size()) {
                currentQuestionNumber = 0;
                currentPoints = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0};
                questions = dbHelper.getAllQuestions();
                currentQuestion = questions.get(currentQuestionNumber);
                container.removeAllViews();
                txtWordOnCard.setText("Вопрос");
                txtQuestionCount.setVisibility(View.VISIBLE);
                drawCurrentState();
            }
        }
    }

    public void onBackClicked(View v) {
        if (currentQuestionNumber == questions.size()){
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            currentQuestionNumber--;
            currentQuestion = questions.get(currentQuestionNumber);
            container.removeAllViews();
            drawCurrentState();
        }
    }

    /**
     * Функция, отрисовывающая вопрос, дополнение к нему (если есть) и кнопки-варианты ответа
     * Если currentQuestionNumber = 12, то текст кнопки меняется на "Получить результат"
     * Если currentQuestionNumber = 0, то помимо кнопок загружается и текст, что такое Кромка
     */
    private void drawCurrentState() {
        txtQuestionCount.setText((currentQuestionNumber + 1) + " из " + questions.size());
        txtQuestionText.setText(currentQuestion.getTitle());
        btnNext.getLayoutParams().width = (int) (width / 2.3);
        btnNext.setText("Вперед");
        btnBack.setText("Назад");
        btnBack.getLayoutParams().width = (int) (width / 2.3);
        btnBack.setVisibility(View.VISIBLE);
        try {
            global.removeViewAt(2);
        } catch (NullPointerException e) {
            Log.e("MyError", "Первый imageview, нечего удалять");
        }
        ImageView imageView = new ImageView(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins((int) (width * 0.0625), (int) (height * 0.19), (int) (width * 0.0625), 0);
        imageView.setLayoutParams(params);
        imageView.setImageDrawable(progressbar.get(currentQuestionNumber));
        global.addView(imageView);
        if (currentQuestion.getSubTitle() != null) {
            TextView textView = new TextView(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 10, 0, 10);
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(20);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setPadding(40, 0, 40, 10);
            textView.setText(currentQuestion.getSubTitle());
            container.addView(textView);
        }
        ArrayList<Answer> answers = currentQuestion.getAnswers();
        for (final Answer answer : answers) {
            final Button button = new Button(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (height * 0.07));
            layoutParams.setMargins(width / 20, 15, width / 20, 0);
            button.setLayoutParams(layoutParams);
            button.setText(answer.getText());
            button.setTextSize(17);
            button.setTextColor(getResources().getColor(R.color.black));
            button.setAllCaps(false);
            button.setId(answers.indexOf(answer));
            if (answer.isChosen()) {
                button.setBackground(getResources().getDrawable(R.drawable.button_active));
                button.setTextColor(getResources().getColor(R.color.white));
            } else {
                button.setBackground(getResources().getDrawable(R.drawable.button_non_active));
                button.setTextColor(getResources().getColor(R.color.black));
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                        disableExcessButtons(button.getId());
                    } else {
                        button.setBackground(getResources().getDrawable(R.drawable.button_non_active));
                        button.setTextColor(getResources().getColor(R.color.black));
                        answer.setChosen(false);
                        currentPoints[currentQuestionNumber] = -1;
                    }
                }
            });
            container.addView(button);
        }
        if (currentQuestionNumber == 0) {
            TextView textView = new TextView(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, height / 30, 0, 0);
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(15);
            textView.setPadding(width / 25, 0, width / 25, 0);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setText(getResources().getText(R.string.text_start));
            container.addView(textView);
            btnBack.setVisibility(View.GONE);
            btnNext.getLayoutParams().width = (int) (width/1.09);
        } else if (currentQuestionNumber == 12) {
            btnNext.setText("Результат");
        }
    }

    /**
     * Функция, которая меняет стиль нажатой кнопки с ответом на активный (синий)
     * Так же, она меняет стиль остальных кнопой на неактивный (серый)
     * В этой функции идет присвоение баллов за текущий вопрос
     */
    private void disableExcessButtons(int id) {
        ArrayList<Answer> answers = currentQuestion.getAnswers();
        for (Answer answer : answers) {
            Button button = findViewById(answers.indexOf(answer));
            if (answers.indexOf(answer) == id) {
                button.setBackground(getResources().getDrawable(R.drawable.button_active));
                button.setTextColor(getResources().getColor(R.color.white));
                answer.setChosen(true);
                currentPoints[currentQuestionNumber] = answer.getPoints();
            } else {
                button.setBackground(getResources().getDrawable(R.drawable.button_non_active));
                button.setTextColor(getResources().getColor(R.color.black));
                answer.setChosen(false);
            }
        }
    }

    /**
     * Функция, в которой происходит отрисовка финальной страницы
     */
    private void drawFinalPage() {
        txtQuestionText.setText("Прогноз пятилетней выживаемости составляет " + getLiveChance());
        btnNext.setText("Повторить");
        btnBack.setText("Выход");
        TextView textView = new TextView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, height / 30, 0, 0);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(16);
        textView.setPadding(width / 20, 0, width / 20, 0);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(getResources().getText(R.string.text_end));
        container.addView(textView);
        txtWordOnCard.setText("Заключение");
        txtQuestionCount.setVisibility(View.INVISIBLE);
    }

    /**
     * Функция, возвращающая определенную строку в зависимости от количества набранных баллов
     */
    private String getLiveChance() {
        int sum = 0;
        for (int i: currentPoints){
            sum+=i;
        }
        switch (sum) {
            case 3:
                return "98,5 %";
            case 4:
                return "96,5 %";
            case 5:
                return "от 92 до 95 %";
            case 6:
                return "91 %";
            case 7:
                return "от 85 до 88 %";
            case 8:
                return "от 80 до 81 %";
            case 9:
                return "от 76 до 80 %";
            case 10:
                return "от 72 до 73 %";
            case 11:
                return "от 55 до 57 %";
            case 12:
                return "от 52 до 56 %";
            case 13:
                return "от 41 до 46 %";
        }
        if (sum <= 2) {
            return "99 %";
        } else return "от 33 до 36 %";
    }

    /**
     * Функция для добавления картинок индикатора прогресса в массив
     */
    private void addAllProgressBars() {
        progressbar.add(0, getResources().getDrawable(R.drawable.navigation1));
        progressbar.add(1, getResources().getDrawable(R.drawable.navigation2));
        progressbar.add(2, getResources().getDrawable(R.drawable.navigation3));
        progressbar.add(3, getResources().getDrawable(R.drawable.navigation4));
        progressbar.add(4,
                getResources().getDrawable(R.drawable.navigation5));
        progressbar.add(5, getResources().getDrawable(R.drawable.navigation6));
        progressbar.add(6, getResources().getDrawable(R.drawable.navigation7));
        progressbar.add(7, getResources().getDrawable(R.drawable.navigation8));
        progressbar.add(8, getResources().getDrawable(R.drawable.navigation9));
        progressbar.add(9, getResources().getDrawable(R.drawable.navigation10));
        progressbar.add(10, getResources().getDrawable(R.drawable.navigation11));
        progressbar.add(11, getResources().getDrawable(R.drawable.navigation12));
        progressbar.add(12, getResources().getDrawable(R.drawable.navigation13));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}