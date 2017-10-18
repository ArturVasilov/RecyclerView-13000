package ru.arturvasilov.recyclerview.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.recyclerview.demo.snap.FixedSnapHelper;
import ru.arturvasilov.recyclerview.demo.swipe.OnDismissListener;
import ru.arturvasilov.recyclerview.demo.swipe.SwipeDismissAnimator;
import ru.arturvasilov.recyclerview.demo.swipe.SwipeDismissTouchCallback;

public class MainActivity extends AppCompatActivity implements OnDismissListener {

    private DemoAdapter mDemoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mDemoAdapter = new DemoAdapter(createDemoItems(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mDemoAdapter);
        recyclerView.setItemAnimator(new SwipeDismissAnimator());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDismissTouchCallback(ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                DemoItem demoItem = (DemoItem) viewHolder.itemView.getTag(R.id.demo_item_key);
                mDemoAdapter.removeItem(demoItem);
            }
        });
        //itemTouchHelper.attachToRecyclerView(recyclerView);

        SnapHelper snapHelper = new FixedSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onItemDismissed(@NonNull View itemView, @NonNull DemoItem demoItem) {
        mDemoAdapter.removeItem(demoItem);
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
