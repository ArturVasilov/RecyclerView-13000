package ru.arturvasilov.recyclerview.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.recyclerview.demo.animation.DemoItemAnimator;
import ru.arturvasilov.recyclerview.demo.snap.FixedSnapHelper;
import ru.arturvasilov.recyclerview.demo.swipe.OnDismissListener;
import ru.arturvasilov.recyclerview.demo.swipe.SwipeDismissAnimator;

public class MainActivity extends AppCompatActivity implements OnDismissListener, DemoAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private DemoAdapter demoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        demoAdapter = new DemoAdapter(createDemoItems(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(demoAdapter);

        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDismissTouchCallback(ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                DemoItem demoItem = (DemoItem) viewHolder.itemView.getTag(R.id.demo_item_key);
                demoAdapter.removeItem(demoItem);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);*/

        SnapHelper snapHelper = new FixedSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        RecyclerView.ItemAnimator itemAnimator = new DemoItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onItemDismissed(@NonNull View itemView, @NonNull DemoItem demoItem) {
        demoAdapter.removeItem(demoItem);
    }

    @Override
    public void onItemClick(@NonNull DemoItem demoItem, int position) {
        int number = new SecureRandom().nextInt(900) + 100;
        demoAdapter.addItem(new DemoItem(String.valueOf(number)), position);
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
