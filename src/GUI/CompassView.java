/*
 * Catcher, CompassView.java
 *
 * License: GPL v2
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */

package GUI;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


/**
 * @author richie
 */
public class CompassView extends Canvas implements IView {

    private IViewNavigator viewNavigator;

    /**
     * constructor
     */
    public CompassView(IViewNavigator viewNavigator) {
        setFullScreenMode(true);
        this.viewNavigator = viewNavigator;
    } 
    
    /**
     * paint
     */
    public void paint(Graphics g) {
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0, 0, 0);
        g.drawString("CompassView",0,0,Graphics.TOP|Graphics.LEFT);
        
        // Draw compass
        //int bearing = 0;
        int maxX = getWidth();
        int maxY = getHeight();
        int compassDia = (maxX<maxY ? maxX:maxY)-10;
        g.drawArc(5, 5, compassDia, compassDia, 0, 360);
        g.drawArc(15, 15, compassDia-20, compassDia-20, 0, 360);

        int x1,x2,y1,y2;

        x1 = maxX >> 1;
        x2 = (int)Math.sin(10)*100;
        y1 = maxX >> 1;
        y2 = (int)Math.cos(10)*100;
//        x1=x2=y1=y2=0; // remove

        g.drawLine(x1, y1, x2, y2);
    }
    
    /**
     * Called when a key is pressed.
     */
    protected  void keyPressed(int keyCode) {
        switch(getGameAction(keyCode)) {
            case LEFT:
                viewNavigator.ShowPrevious(this);
                break;
            case RIGHT:
                viewNavigator.ShowNext(this);
                break;
        }
    }
    
    /**
     * Called when a key is released.
     */
    protected  void keyReleased(int keyCode) {
    }

    /**
     * Called when a key is repeated (held down).
     */
    protected  void keyRepeated(int keyCode) {
    }
    
    /**
     * Called when the pointer is dragged.
     */
    protected  void pointerDragged(int x, int y) {
    }

    /**
     * Called when the pointer is pressed.
     */
    protected  void pointerPressed(int x, int y) {
    }

    /**
     * Called when the pointer is released.
     */
    protected  void pointerReleased(int x, int y) {
    }

    public void activate() {

    }

    public void deactivate() {

    }
    
}
