package com.mc.englishlearn.przypomnienia;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.R;

public class WiadomoscPowiadamiajaca extends AppCompatActivity {

TextView wiadomoscTekst;

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    setContentView(R.layout.przypomnienie_aktywnosc_wiadomosc);
    wiadomoscTekst = findViewById(R.id.wiadomosc);
    Bundle pakiet = getIntent().getExtras();
    //wywołuję dane, które są przekazywane przez inną intencje
    wiadomoscTekst.setText(pakiet.getString("Wiadomość"));
}


}
