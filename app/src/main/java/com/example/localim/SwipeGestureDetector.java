package com.example.localim;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureDetector extends GestureDetector {


    public SwipeGestureDetector(final serviceContentActivity context){
        super(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent depart, MotionEvent arrivee,float velociteX,float velociteY){
                float deltaX = depart.getX()-arrivee.getX();
                float deltaY = depart.getY()-arrivee.getY();
                // déplacement majoritairement horizontal
                if (Math.abs(deltaX)>Math.abs(deltaY)){
                    // déplacement minimal du swipe
                    if(Math.abs(deltaX) > 50){
                        // Déplacement de la droite vers la gauche
                        if(deltaX<0){
                            //Direction Gauche
                            context.onSwipe(1);
                            return true;
                        }else{
                            context.onSwipe(2);
                            return false;
                        }
                    }

                }
                return false;
            }
        });
    }
}
