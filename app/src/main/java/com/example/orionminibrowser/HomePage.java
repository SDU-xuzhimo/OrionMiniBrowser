package com.example.orionminibrowser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        SVGImageView logo = (SVGImageView) findViewById(R.id.Logo);
        SVGImageView search = (SVGImageView) findViewById(R.id.search);
        TextView inputPromptBox=(TextView) findViewById(R.id.InputPromptBox);

        try {
            SVG svg1 = SVG.getFromResource(HomePage.this, R.raw.logo);
            logo.setSVG(svg1);

            SVG svg2 = SVG.getFromResource(HomePage.this, R.raw.search);
            search.setSVG(svg2);

        }catch(Exception e){
            e.printStackTrace();
        }


        inputPromptBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拉起网址输入页
                Intent intent = new Intent(HomePage.this,InputPage.class);
                startActivity(intent);
            }
        });
    }
}