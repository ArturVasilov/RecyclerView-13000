package ru.arturvasilov.recyclerview.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.recyclerview.demo.snap.FixedSnapHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        DemoAdapter adapter = new DemoAdapter(createDemoItems());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new FixedSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    private List<DemoItem> createDemoItems() {
        List<DemoItem> demoItems = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            demoItems.add(new DemoItem(String.valueOf(i + 1)));
        }
        return demoItems;
    }
}
