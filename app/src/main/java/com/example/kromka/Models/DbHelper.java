package com.example.kromka.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    /**
     * Класс для управления и получения содержимого из бд
     * */


    //данные о бд
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Kromka";
    //Вопросы
    public static final String TABLE_QUESTIONS = "Questions";
    public static final String QUESTION_KEY_ID = "_id";
    public static final String QUESTION_TEXT = "questionText";
    public static final String QUESTION_SUBTITLE = "subtitle";
    //Ответы
    public static final String TABLE_ANSWERS = "Answers";
    public static final String ANSWER_TEXT = "answerText";
    public static final String ANSWER_POINTS = "points";
    public static final String ANSWER_QUESTION = "question";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_QUESTIONS + " ( " + QUESTION_KEY_ID + " integer, " +
                QUESTION_TEXT + " text, " + QUESTION_SUBTITLE + " text, " + " primary key ( " + QUESTION_KEY_ID + " ))";
        db.execSQL(sql);
        sql = "create table " + TABLE_ANSWERS + " ( " + ANSWER_TEXT + " text, " + ANSWER_POINTS +
                " integer, " + ANSWER_QUESTION + " integer, foreign key ( " + ANSWER_QUESTION + " ) references " +
                TABLE_QUESTIONS + " ( " + QUESTION_KEY_ID + " ))";
        db.execSQL(sql);
        addAllInfoToDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void addAllInfoToDatabase(SQLiteDatabase db) {
        //1
        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(new Answer("Женский", 0));
        answers.add(new Answer("Мужской", 2));
        Question question = new Question("Пол больного:", null, answers);
        addQuestionWithAnswers(question, db);
        //2
        answers.clear();
        answers.add(new Answer("От 60 до 64 лет", 1));
        answers.add(new Answer("От 65 до 69 лет", 2));
        answers.add(new Answer("От 70 до 74 лет", 3));
        answers.add(new Answer("От 75 до 79 лет", 4));
        answers.add(new Answer("От 80 до 84 лет", 5));
        answers.add(new Answer("85 лет и более", 7));
        question = new Question("Возраст больного:", null, answers);
        addQuestionWithAnswers(question, db);
        //3
        answers.clear();
        answers.add(new Answer("Да", 1));
        answers.add(new Answer("Нет", 0));
        question = new Question("Перенесенный ранее ИМ", "(инфаркт миокарда)", answers);
        addQuestionWithAnswers(question, db);
        //4
        answers.clear();
        answers.add(new Answer("Да", 1));
        answers.add(new Answer("Нет", 0));
        question = new Question("Нарушение усвоения глюкозы", "(сахарный диабет)", answers);
        addQuestionWithAnswers(question, db);
        //5
        answers.clear();
        answers.add(new Answer("Да", 2));
        answers.add(new Answer("Нет", 0));
        question = new Question("Аневризма ЛЖ", "(левого желудочка сердца)", answers);
        addQuestionWithAnswers(question, db);
        //6
        answers.clear();
        answers.add(new Answer("Да", 2));
        answers.add(new Answer("Нет", 0));
        question = new Question("ФВ менее 35%", "(уровень фракции выброса сердца)", answers);
        addQuestionWithAnswers(question, db);
        //7
        answers.clear();
        answers.add(new Answer("Да", 2));
        answers.add(new Answer("Нет", 0));
        question = new Question("СКФ менее 30%", "(скорость клубочковой фильтрации почек)", answers);
        addQuestionWithAnswers(question, db);
        //8
        answers.clear();
        answers.add(new Answer("Да", 1));
        answers.add(new Answer("Нет", 0));
        question = new Question("Низкое содержание гемоглобина", "(анемия)", answers);
        addQuestionWithAnswers(question, db);
        //9
        answers.clear();
        answers.add(new Answer("Да", 1));
        answers.add(new Answer("Нет", 0));
        question = new Question("Артериальная гипертензия", "(САД более 140 мм. рт. ст.)", answers);
        addQuestionWithAnswers(question, db);
        //10
        answers.clear();
        answers.add(new Answer("Да", 1));
        answers.add(new Answer("Нет", 0));
        question = new Question("Общий холестерин выше 200 мг/дл:", "(ОХС)", answers);
        addQuestionWithAnswers(question, db);
        //11
        answers.clear();
        answers.add(new Answer("Больше 40", 1));
        answers.add(new Answer("Меньше 25", 1));
        answers.add(new Answer("Другие показатели", 0));
        question = new Question("Индекс массы тела", "(ИМТ)", answers);
        addQuestionWithAnswers(question, db);
        //12
        answers.clear();
        answers.add(new Answer("Да", 2));
        answers.add(new Answer("Нет", 0));
        question = new Question("Табакокурение:", null, answers);
        addQuestionWithAnswers(question, db);
        //13
        answers.clear();
        answers.add(new Answer("Да", 2));
        answers.add(new Answer("Нет", 0));
        question = new Question("Наличие затруднений при длительной ходьбе",
                "(например: сложно пройти несколько кварталов)", answers);
        addQuestionWithAnswers(question, db);
    }

    public void addQuestionWithAnswers(Question question, SQLiteDatabase database) {
        String text = question.getTitle();
        String subtitle = question.getSubTitle();
        ArrayList<Answer> answers = question.getAnswers();
        String sql = "insert into " + TABLE_QUESTIONS + " ( " + QUESTION_TEXT + ", " + QUESTION_SUBTITLE +
                " ) values ( '" + text + "', '" + subtitle + "' )";
        database.execSQL(sql);
        int id = getQuestionId(question.getTitle(), database);
        for (Answer answer : answers) {
            sql = "insert into " + TABLE_ANSWERS + " ( " + ANSWER_TEXT + ", " + ANSWER_POINTS + ", " +
                    ANSWER_QUESTION + " ) values ( '" + answer.getText() + "', " + answer.getPoints() + ", " +
                    id + " )";
            database.execSQL(sql);
        }
    }

    public int getQuestionId(String title, SQLiteDatabase database) {
        String sql = "select " + QUESTION_KEY_ID + " from " + TABLE_QUESTIONS + " where " +
                QUESTION_TEXT + " = '" + title + "'";
        Cursor cursor = database.rawQuery(sql, null);
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(QUESTION_KEY_ID));
        }
        return id;
    }

    public ArrayList<Question> getAllQuestions() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Question> questions = new ArrayList<>();
        String sql = "select * from " + TABLE_QUESTIONS;
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String title, subtitle = null;
            ArrayList<Answer> answers = new ArrayList<>();
            title = cursor.getString(cursor.getColumnIndex(QUESTION_TEXT));
            subtitle = cursor.getString(cursor.getColumnIndex(QUESTION_SUBTITLE));
            int id = getQuestionId(title, database);
            sql = "select * from " + TABLE_ANSWERS + " where " + ANSWER_QUESTION + " = " + id;
            Cursor cursor1 = database.rawQuery(sql, null);
            while (cursor1.moveToNext()) {
                String text = cursor1.getString(cursor1.getColumnIndex(ANSWER_TEXT));
                int points = cursor1.getInt(cursor1.getColumnIndex(ANSWER_POINTS));
                answers.add(new Answer(text, points));
            }
            if (subtitle == null || subtitle.equals("") || subtitle.equals("null")) {
                questions.add(new Question(title, null, answers));
            } else questions.add(new Question(title, subtitle, answers));
        }
        return questions;
    }
}
