package ru.arturvasilov.recyclerview.demo.animation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Artur Vasilov
 *         <p>
 *         Small modification of https://github.com/wasabeef/recyclerview-animators
 */
public abstract class BaseItemAnimator extends SimpleItemAnimator implements RecyclerView.ItemAnimator.ItemAnimatorFinishedListener {

    @NonNull
    private final List<ViewHolder> addAnimations = new ArrayList<>();

    @NonNull
    private final List<ViewHolder> removeAnimations = new ArrayList<>();

    @NonNull
    private final List<ViewHolder> moveAnimations = new ArrayList<>();

    @NonNull
    private final List<ViewHolder> pendingRemovals = new ArrayList<>();

    @NonNull
    private final List<MoveInfo> pendingMoves = new ArrayList<>();

    @NonNull
    private final List<ViewHolder> pendingAdditions = new ArrayList<>();

    @NonNull
    private final List<List<ViewHolder>> additionsList = new ArrayList<>();

    @NonNull
    private final List<List<MoveInfo>> movesList = new ArrayList<>();

    @Nullable
    private OnItemsAnimationsFinishedListener animationsFinishedListener;

    private int baseAnimationDelay = 0;

    protected BaseItemAnimator() {
        super();
        setSupportsChangeAnimations(false);
    }

    @Override
    public void runPendingAnimations() {
        boolean removalsPending = !pendingRemovals.isEmpty();
        boolean movesPending = !pendingMoves.isEmpty();
        boolean additionsPending = !pendingAdditions.isEmpty();

        if (!removalsPending && !movesPending && !additionsPending) {
            // nothing to animate
            return;
        }

        // First, remove stuff
        for (ViewHolder holder : pendingRemovals) {
            doAnimateRemove(holder);
        }
        pendingRemovals.clear();

        // Next, move stuff
        if (movesPending) {
            final ArrayList<MoveInfo> moves = new ArrayList<>();
            moves.addAll(pendingMoves);
            movesList.add(moves);
            pendingMoves.clear();
            Runnable mover = () -> {
                for (MoveInfo moveInfo : moves) {
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX,
                            moveInfo.toY);
                }
                moves.clear();
                movesList.remove(moves);
            };
            if (removalsPending) {
                View view = moves.get(0).holder.itemView;
                ViewCompat.postOnAnimationDelayed(view, mover, getRemoveDuration());
            } else {
                mover.run();
            }
        }

        // Next, add stuff
        if (additionsPending) {
            final ArrayList<ViewHolder> additions = new ArrayList<>();
            additions.addAll(pendingAdditions);
            additionsList.add(additions);
            pendingAdditions.clear();
            Runnable adder = () -> {
                for (ViewHolder holder : additions) {
                    doAnimateAdd(holder);
                }
                additions.clear();
                additionsList.remove(additions);
            };
            if (removalsPending || movesPending) {
                long removeDuration = removalsPending ? getRemoveDuration() : 0;
                long moveDuration = movesPending ? getMoveDuration() : 0;
                long totalDelay = removeDuration + moveDuration;
                View view = additions.get(0).itemView;
                ViewCompat.postOnAnimationDelayed(view, adder, totalDelay);
            } else {
                adder.run();
            }
        }
    }

    private void doAnimateRemove(@NonNull final ViewHolder holder) {
        removeAnimations.add(holder);
        animateRemoveHolder(holder);
    }

    private void animateMoveImpl(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            ViewCompat.animate(view).translationX(0);
        }
        if (deltaY != 0) {
            ViewCompat.animate(view).translationY(0);
        }

        moveAnimations.add(holder);
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        animation.setDuration(getMoveDuration()).setListener(new VpaListenerAdapter() {
            @Override
            public void onAnimationStart(View view) {
                dispatchMoveStarting(holder);
            }

            @Override
            public void onAnimationCancel(View view) {
                if (deltaX != 0) {
                    ViewCompat.setTranslationX(view, 0);
                }
                if (deltaY != 0) {
                    ViewCompat.setTranslationY(view, 0);
                }
            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                dispatchMoveFinished(holder);
                moveAnimations.remove(holder);
                dispatchFinishedWhenDone();
            }
        }).start();
    }

    private void doAnimateAdd(@NonNull final ViewHolder holder) {
        addAnimations.add(holder);
        animateAddHolder(holder);
    }

    protected void animateRemoveHolder(@NonNull final ViewHolder holder) {
        dispatchRemoveFinished(holder);
        removeAnimations.remove(holder);
        dispatchFinishedWhenDone();
    }

    /**
     * Check the state osuper.animateRemoveHolder(holder);f currently pending and running animations. If there are none
     * pending/running, call #dispatchAnimationsFinished() to notify any
     * listeners.
     */
    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    protected void animateAddHolder(@NonNull final ViewHolder holder) {
        dispatchAddFinished(holder);
        addAnimations.remove(holder);
        dispatchFinishedWhenDone();
    }

    @Override
    public boolean isRunning() {
        return !pendingAdditions.isEmpty() ||
                !pendingRemovals.isEmpty() ||
                !moveAnimations.isEmpty() ||
                !removeAnimations.isEmpty() ||
                !addAnimations.isEmpty() ||
                !movesList.isEmpty() ||
                !additionsList.isEmpty();
    }

    @Override
    public boolean animateRemove(@NonNull final ViewHolder holder) {
        endAnimation(holder);
        clearView(holder.itemView);
        beforeAnimateRemove(holder);
        pendingRemovals.add(holder);
        return true;
    }

    @Override
    public void endAnimation(ViewHolder item) {
        final View view = item.itemView;
        // this will trigger end callback which should set properties to their target values.
        ViewCompat.animate(view).cancel();

        for (int i = pendingMoves.size() - 1; i >= 0; i--) {
            MoveInfo moveInfo = pendingMoves.get(i);
            if (moveInfo.holder == item) {
                ViewCompat.setTranslationY(view, 0);
                ViewCompat.setTranslationX(view, 0);
                dispatchMoveFinished(item);
                pendingMoves.remove(i);
            }
        }

        for (int i = movesList.size() - 1; i >= 0; i--) {
            List<MoveInfo> moves = movesList.get(i);
            for (int j = moves.size() - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
                if (moveInfo.holder == item) {
                    ViewCompat.setTranslationY(view, 0);
                    ViewCompat.setTranslationX(view, 0);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        movesList.remove(i);
                    }
                    break;
                }
            }
        }

        if (pendingRemovals.remove(item)) {
            clearView(item.itemView);
            dispatchRemoveFinished(item);
        }
        if (pendingAdditions.remove(item)) {
            clearView(item.itemView);
            dispatchAddFinished(item);
        }

        for (int i = additionsList.size() - 1; i >= 0; i--) {
            List<ViewHolder> additions = additionsList.get(i);
            if (additions.remove(item)) {
                clearView(item.itemView);
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    additionsList.remove(i);
                }
            }
        }

        dispatchFinishedWhenDone();
    }

    private static void clearView(@Nullable View view) {
        if (view == null) {
            return;
        }

        ViewCompat.setAlpha(view, 1);
        ViewCompat.setScaleY(view, 1);
        ViewCompat.setScaleX(view, 1);
        ViewCompat.setTranslationY(view, 0);
        ViewCompat.setTranslationX(view, 0);
        ViewCompat.setRotation(view, 0);
        ViewCompat.setRotationY(view, 0);
        ViewCompat.setRotationX(view, 0);
        ViewCompat.setPivotY(view, view.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(view, view.getMeasuredWidth() / 2);
        ViewCompat.animate(view).setInterpolator(null).setStartDelay(0);
    }

    protected void beforeAnimateRemove(@NonNull final ViewHolder holder) {
        // Do nothing
    }

    @Override
    public boolean animateAdd(@NonNull final ViewHolder holder) {
        endAnimation(holder);
        clearView(holder.itemView);
        beforeAnimateAdd(holder);
        pendingAdditions.add(holder);
        return true;
    }

    protected void beforeAnimateAdd(@NonNull final ViewHolder holder) {
        // Do nothing
    }

    @Override
    public boolean animateMove(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            ViewCompat.setTranslationX(view, -deltaX);
        }
        if (deltaY != 0) {
            ViewCompat.setTranslationY(view, -deltaY);
        }
        pendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
        return true;
    }

    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        return true;
    }

    @Override
    public void endAnimations() {
        int count = pendingMoves.size();
        for (int i = count - 1; i >= 0; i--) {
            MoveInfo item = pendingMoves.get(i);
            View view = item.holder.itemView;
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setTranslationX(view, 0);
            dispatchMoveFinished(item.holder);
            pendingMoves.remove(i);
        }

        count = pendingRemovals.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = pendingRemovals.get(i);
            dispatchRemoveFinished(item);
            pendingRemovals.remove(i);
        }
        count = pendingAdditions.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = pendingAdditions.get(i);
            clearView(item.itemView);
            dispatchAddFinished(item);
            pendingAdditions.remove(i);
        }
        if (!isRunning()) {
            return;
        }

        int listCount = movesList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            List<MoveInfo> moves = movesList.get(i);
            count = moves.size();
            for (int j = count - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
                ViewHolder item = moveInfo.holder;
                View view = item.itemView;
                ViewCompat.setTranslationY(view, 0);
                ViewCompat.setTranslationX(view, 0);
                dispatchMoveFinished(moveInfo.holder);
                moves.remove(j);
                if (moves.isEmpty()) {
                    movesList.remove(moves);
                }
            }
        }
        listCount = additionsList.size();

        for (int i = listCount - 1; i >= 0; i--) {
            List<ViewHolder> additions = additionsList.get(i);
            count = additions.size();
            for (int j = count - 1; j >= 0; j--) {
                ViewHolder item = additions.get(j);
                View view = item.itemView;
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
                //this check prevent exception when removal already happened during finishing animation
                if (j < additions.size()) {
                    additions.remove(j);
                }
                if (additions.isEmpty()) {
                    additionsList.remove(additions);
                }
            }
        }
        cancelAll(removeAnimations);
        cancelAll(moveAnimations);
        cancelAll(addAnimations);

        dispatchAnimationsFinished();
    }

    private void cancelAll(@NonNull List<ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            ViewCompat.animate(viewHolders.get(i).itemView).cancel();
        }
    }

    /**
     * Methods below are used to determine when all items animation is finished.
     * {@link RecyclerView.ItemAnimator#isRunning()} internally calls
     * {@link ItemAnimatorFinishedListener#onAnimationsFinished()} and that means that all animations are finished
     */
    @Override
    public void onChangeFinished(@NonNull ViewHolder item, boolean oldItem) {
        isRunning(this);
    }

    @Override
    public void onRemoveFinished(@NonNull ViewHolder item) {
        isRunning(this);
    }

    @Override
    public void onAddFinished(@NonNull ViewHolder item) {
        isRunning(this);
    }

    @Override
    public void onAnimationsFinished() {
        if (animationsFinishedListener != null) {
            animationsFinishedListener.onAllItemsAnimationsFinished();
        }
    }

    protected long getRemoveDelay(@NonNull final ViewHolder holder) {
        return Math.abs(holder.getOldPosition() * getRemoveDuration() / 4);
    }

    protected long getAddDelay(@NonNull final ViewHolder holder) {
        return (Math.abs(holder.getAdapterPosition() * getAddDuration() / 4) + baseAnimationDelay);
    }

    protected void setBaseAnimationDelay(int baseAnimationDelay) {
        this.baseAnimationDelay = baseAnimationDelay;
    }

    public void setAnimationsFinishedListener(@NonNull OnItemsAnimationsFinishedListener animationsFinishedListener) {
        this.animationsFinishedListener = animationsFinishedListener;
    }

    public interface OnItemsAnimationsFinishedListener {
        void onAllItemsAnimationsFinished();
    }

    private static class MoveInfo {

        @NonNull
        private final ViewHolder holder;
        private final int fromY;
        private final int toX;
        private final int toY;
        private final int fromX;

        private MoveInfo(@NonNull ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private static class VpaListenerAdapter implements ViewPropertyAnimatorListener {

        @Override
        public void onAnimationStart(View view) {
            // Do nothing
        }

        @Override
        public void onAnimationEnd(View view) {
            // Do nothing
        }

        @Override
        public void onAnimationCancel(View view) {
            // Do nothing
        }
    }

    protected class DefaultAddVpaListener extends VpaListenerAdapter {

        @NonNull
        private final ViewHolder viewHolder;

        public DefaultAddVpaListener(@NonNull final ViewHolder holder) {
            viewHolder = holder;
        }

        @Override
        public void onAnimationStart(View view) {
            dispatchAddStarting(viewHolder);
        }

        @Override
        public void onAnimationCancel(View view) {
            clearView(view);
        }

        @Override
        public void onAnimationEnd(View view) {
            clearView(view);
            dispatchAddFinished(viewHolder);
            addAnimations.remove(viewHolder);
            dispatchFinishedWhenDone();
        }
    }

    protected class DefaultRemoveVpaListener extends VpaListenerAdapter {

        private final ViewHolder viewHolder;

        public DefaultRemoveVpaListener(@NonNull final ViewHolder holder) {
            viewHolder = holder;
        }

        @Override
        public void onAnimationStart(View view) {
            dispatchRemoveStarting(viewHolder);
        }

        @Override
        public void onAnimationCancel(View view) {
            clearView(view);
        }

        @Override
        public void onAnimationEnd(View view) {
            clearView(view);
            dispatchRemoveFinished(viewHolder);
            removeAnimations.remove(viewHolder);
            dispatchFinishedWhenDone();
        }
    }
}