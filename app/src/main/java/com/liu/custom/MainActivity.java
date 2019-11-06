package com.liu.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.liu.custom.view.IndicatorSeekBar;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private IndicatorSeekBar indicatorSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indicatorSeekBar = findViewById(R.id.indicator_bar);
        indicatorSeekBar.setMaxPrice(8888);
        indicatorSeekBar.setCustomPrice(3567);
        HashMap<Float, String> priceMap = new HashMap<>();
        priceMap.put(2343f, "Available");
        priceMap.put(5876f, "Best");
        priceMap.put(897f, "Minimum");
        indicatorSeekBar.setPriceMap(priceMap);
        indicatorSeekBar.setOnPriceChangeListener(new IndicatorSeekBar.OnPriceChangeListener() {
            @Override
            public void onPriceChange(int progress, float price) {
                Toast.makeText(MainActivity.this, price + "", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
