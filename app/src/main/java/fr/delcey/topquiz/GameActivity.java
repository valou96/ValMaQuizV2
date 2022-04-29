package fr.delcey.topquiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import fr.delcey.topquiz.model.Question;
import fr.delcey.topquiz.model.QuestionBank;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RESULT_SCORE = "RESULT_SCORE";

    private static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    private static final String BUNDLE_STATE_QUESTION_COUNT = "BUNDLE_STATE_QUESTION_COUNT";
    private static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";

    private static final int INITIAL_QUESTION_COUNT = 4;

    private TextView mTextViewQuestion;
    private Button mAnswerButton1;
    private Button mAnswerButton2;
    private Button mAnswerButton3;
    private Button mAnswerButton4;

    private int mScore;
    private int mRemainingQuestionCount;
    private QuestionBank mQuestionBank;

    private boolean mEnableTouchEvents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        mEnableTouchEvents = true;

        mTextViewQuestion = findViewById(R.id.game_activity_textview_question);
        mAnswerButton1 = findViewById(R.id.game_activity_button_1);
        mAnswerButton2 = findViewById(R.id.game_activity_button_2);
        mAnswerButton3 = findViewById(R.id.game_activity_button_3);
        mAnswerButton4 = findViewById(R.id.game_activity_button_4);

        // Use the same listener for the four buttons.
        // The view id value will be used to distinguish the button triggered
        mAnswerButton1.setOnClickListener(this);
        mAnswerButton2.setOnClickListener(this);
        mAnswerButton3.setOnClickListener(this);
        mAnswerButton4.setOnClickListener(this);

        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION_COUNT);
            mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        } else {
            mScore = 0;
            mRemainingQuestionCount = INITIAL_QUESTION_COUNT;
            mQuestionBank = generateQuestionBank();
        }

        displayQuestion(mQuestionBank.getCurrentQuestion());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION_COUNT, mRemainingQuestionCount);
        outState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int index;

        if (v == mAnswerButton1) {
            index = 0;
        } else if (v == mAnswerButton2) {
            index = 1;
        } else if (v == mAnswerButton3) {
            index = 2;
        } else if (v == mAnswerButton4) {
            index = 3;
        } else {
            throw new IllegalStateException("Unknown clicked view : " + v);
        }

        if (index == mQuestionBank.getCurrentQuestion().getAnswerIndex()) {
            Toast.makeText(this, "Bonne réponse", Toast.LENGTH_SHORT).show();
            mScore++;
        } else {
            Toast.makeText(this, "Mauvaise réponse", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;

                mRemainingQuestionCount--;

                if (mRemainingQuestionCount <= 0) {
                    endGame();
                } else {
                    displayQuestion(mQuestionBank.getNextQuestion());
                }
            }
        }, 2_000);
    }

    private void displayQuestion(final Question question) {
        // Set the text for the question text view and the four buttons
        mTextViewQuestion.setText(question.getQuestion());
        mAnswerButton1.setText(question.getChoiceList().get(0));
        mAnswerButton2.setText(question.getChoiceList().get(1));
        mAnswerButton3.setText(question.getChoiceList().get(2));
        mAnswerButton4.setText(question.getChoiceList().get(3));
    }

    private void endGame() {
        // No question left, end the game
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("bien joué !!!")
            .setMessage("tu as " + mScore + " points")
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_SCORE, mScore);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            })
            .create()
            .show();
    }

    private QuestionBank generateQuestionBank() {
        Question question1 = new Question(
            "Quel est le nom du chien dans le film 'Je suis une légende'",
            Arrays.asList(
                "Samantha",
                "Rodolphe",
                "Smith",
                "Rex"
            ),
            0
        );

        Question question2 = new Question(
            "En quelle année sommes nous d'apres le calendrier Grégorien ?",
            Arrays.asList(
                "768",
                "2022",
                "3054",
                "2492"
            ),
            1
        );
        Question question3 = new Question(
                "Quel est le meilleur professeur pour les BTS SIO 1 ?",
                Arrays.asList(
                        "Mme Abry",
                        "Mr Barbier",
                        "Mr Buffet",
                        "Mme Piel"
                ),
                2
        );
        Question question4 = new Question(
                "Quel est le premier président de la 5eme république Française ?",
                Arrays.asList(
                        "G.Pompidou",
                        "V.Giscart d'estain",
                        "B.Obama",
                        "C.De Gaulle"
                ),
                3
        );
        Question question5 = new Question(
                "Quel est le premier youtuber Français ?",
                Arrays.asList(
                        "Squeezy",
                        "Michou",
                        "Pewdiepied",
                        "Logan Paul"
                ),
                0
        );

        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5));
    }
}
