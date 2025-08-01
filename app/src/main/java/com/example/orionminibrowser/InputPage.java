package com.example.orionminibrowser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InputPage extends AppCompatActivity {

    private EditText inputBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_page);

        inputBox = (EditText) findViewById(R.id.InputBox);
        ImageButton button = (ImageButton) findViewById(R.id.InputBoxBack);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.InputBoxBack)
                    finish();
            }
        });


        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_GO||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)){

                    hideKeyboard();

                    Intent intent = new Intent(InputPage.this,BrowsePage.class);

                    intent.putExtra("URL",inputBox.getText().toString());

                    startActivity(intent);
                    new Handler().postDelayed(() -> finish(), 300);
                    return true;
                }

                return false;
            }
        });


    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(InputPage.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}