import game2D.Animation;
import game2D.Sound;
import game2D.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends Sprite {
    PlayerRectangle playerRectangle;

    public Player(Animation animation) {
        super(animation);
        playerRectangle = new PlayerRectangle((int) getX() + 80,(int) getY() + 70 , 0,0);
    }

    public void createCollisionDetector() {
        playerRectangle.setX((int)getX());
        playerRectangle.setY((int)getY());
        playerRectangle.setHeight(getHeight());
        playerRectangle.setWidth(getWidth());
    }

    public void drawBoundingBox(Graphics2D g){
        g.drawRect( (int) getX() + 80,(int) getY() + 70 , getWidth()-165,getHeight()-144);
    }



}
