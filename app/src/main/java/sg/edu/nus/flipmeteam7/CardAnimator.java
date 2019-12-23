package sg.edu.nus.flipmeteam7;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CardAnimator extends AppCompatActivity {

    AnimatorSet turnRightOut = new AnimatorSet();
    AnimatorSet turnLeftIn = new AnimatorSet();
//    boolean isCardFaceDown = false;
    View cardFaceDown;
    View cardFaceUp;
    Context context;

    void setCardFaceDown(View cardFaceDown) {
        this.cardFaceDown = cardFaceDown;
    }

    void setCardFaceUp(View cardFaceUp) {
        this.cardFaceUp = cardFaceUp;
    }

    public CardAnimator(Context context){
        this.context = context;
    }

//    void findViews(View cardImage) {
//        cardFaceDown = cardBack;
//        cardFaceUp = cardImage;
//    }

    void loadAnimations() {
        turnRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
        turnLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animation);
    }


    void closingCard(View cardFaceDown, View cardFaceUp) {
        turnRightOut.setTarget(cardFaceDown);
        turnLeftIn.setTarget(cardFaceUp);
        turnRightOut.start();
        turnLeftIn.start();

    }

    void openingCard(View cardFaceDown, View cardFaceUp) {
        turnRightOut.setTarget(cardFaceUp);
        turnLeftIn.setTarget(cardFaceDown);
        turnRightOut.start();
        turnLeftIn.start();
    }

    void changeCameraDistance(Context context) {
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density;
        scale *= distance;
        if (cardFaceUp == null) Log.d("margohpolo", "cardFaceUp is null");
        if (cardFaceDown == null) Log.d("margohpolo", "cardFaceDown is null");
        if (scale == 0.0f) Log.d("margohpolo", "scale is null");
        cardFaceUp.setCameraDistance(scale);
        cardFaceDown.setCameraDistance(scale);
    }

//    void imperioCardo(View cardImage) {
////        Drawable myDrawable = getResources().getDrawable(R.drawable.small_logo_woodbg);
////        cardFaceUp.setImageDrawable(myDrawable);
//        findViews(cardImage);
////        changeCameraDistance();
////        loadAnimations();
//    }



}
