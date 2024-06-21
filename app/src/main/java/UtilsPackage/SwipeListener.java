package UtilsPackage;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.poussapoussi.GridFragment;
import gamePackage.Coordinates;

public class SwipeListener implements View.OnTouchListener {

    private static final String TAG = "Swipe position";
    private boolean directSlide = false;
    private boolean waitslide = false;
    private float x1, x2, y1, y2;
    private double angleBetweenPoints;
    private int[] directions = new int[2];
    private static final int MIN_DISTANCE = 50;
    private GestureDetector gestureDetector;
    private Context context;
    private ImageView optionalCell;
    private Coordinates optionalCoordinates;
    private GridFragment myGridFragment;

    public SwipeListener(Context context, GridFragment gridFragment) {
        this.directions[0] = 0;
        this.directions[1] = 0;
        this.context = context;
        this.myGridFragment = gridFragment;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public SwipeListener(Context context, GridFragment gridFragment, Coordinates coordinates, ImageView cell) {
        this.directions[0] = 0;
        this.directions[1] = 0;
        this.context = context;
        this.optionalCell = cell;
        this.optionalCoordinates = coordinates;
        this.myGridFragment = gridFragment;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public int[] getDirections() {
        return this.directions;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                float valueX = x2 - x1;
                float valueY = y2 - y1;
                if (Math.abs(valueX) < MIN_DISTANCE && Math.abs(valueY) < MIN_DISTANCE) {
                    myGridFragment.handleCellClick(optionalCoordinates.getX(), optionalCoordinates.getY());
                    return false;
                }
                this.angleBetweenPoints = getAngle(x1, y1, x2, y2);
                if ((Math.abs(valueX) > MIN_DISTANCE) || (Math.abs(valueY) > MIN_DISTANCE)) {
                    myGridFragment.setCoordinatesToPush(optionalCoordinates);

                     if(inRange(angleBetweenPoints, 0,45) || inRange(angleBetweenPoints, 315, 360)){

                        //Toast.makeText(context, "Right is swiped", Toast.LENGTH_SHORT).show();
                        this.directions[0] = 1;
                        this.directions[1] = 0;

                    }


                     else if(inRange(angleBetweenPoints, 225, 315)){
                        //Toast.makeText(context, "Bottom swipe", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Bottom swipe");
                        this.directions[0] = 0;
                        this.directions[1] = 1;

                    } else if (inRange(angleBetweenPoints, 45, 135)){
                        //Toast.makeText(context, "Up is swiped", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Top swipe");
                        this.directions[0] = 0;
                        this.directions[1] = -1;

                    }
                    else {
                        //Toast.makeText(context, "Left is swiped", Toast.LENGTH_SHORT).show();
                        this.directions[0] = -1;
                        this.directions[1] = 0;
                    }
                }
                myGridFragment.makePushCurrentPlayer(this.directions);
                this.directions = new int[]{0, 0};
                break;
        }


        return false;

    }

    public void setWaitSlide(boolean bool) {
        this.waitslide = bool;
    }

    public void setDirectSlide(boolean bool) {
        this.directSlide = bool;
    }

    public boolean getDirectSlide() {
        return this.directSlide;
    }



    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        return (rad*180/Math.PI + 180)%360;
    }

    private static boolean inRange(double angle, float init, float end){
        return (angle >= init) && (angle < end);
    }

    private void setOptionalAttributes(int i, int j, ImageView cell) {
        optionalCoordinates = new Coordinates(i,j);
        optionalCell = cell;
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
