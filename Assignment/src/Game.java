
import game2D.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 */
@SuppressWarnings("serial")

public class Game extends GameCore {


    int xOffset = 0;
    int yOffset = 0;
    // Useful game constants
    static int screenWidth = 512;
    static int screenHeight = 384;

    private double angle = 45;

    float lift = 0.005f;
    float gravity = 0.0003f;

    // Game state flags
    boolean flap = false;

    // Game resources
    Animation landing;
    Animation attack1;
    Animation attack2;
    Animation jump;
    Animation fall;
    Animation run;
    Animation takeHit;
    Animation death;

    boolean canMoveLeft;
    boolean canMoveRight;
    boolean canMoveUp;
    boolean canMoveDown;

    Player player = null;
    ArrayList<Sprite> clouds = new ArrayList<Sprite>();

    TileMap tmap = new TileMap();    // Our tile map, note that we load it in init()

    long total;                    // The score will be the total time elapsed since a crash

    /**
     * The obligatory main method that creates
     * an instance of our class and starts it running
     *
     * @param args The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {

        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false, screenWidth, screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers
     */
    public void init() {
        Sprite s;    // Temporary reference to a sprite

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");

        //init angle
        setSize(tmap.getPixelWidth() / 2, tmap.getPixelHeight());
        setVisible(true);

//        // Create a set of background sprites that we can
        // rearrange to give the illusion of motion
        landing = new Animation();
        landing.loadAnimationFromSheet("images/Idle.png", 8, 1, 150);
        attack1 = new Animation();
        // TODO: Remember to do the background for this image
        attack1.loadAnimationFromSheet("images/4ColumnAttack.png", 4, 1, 135);
        attack2 = new Animation();
        attack2.loadAnimationFromSheet("images/Attack2.png", 6, 1, 200);
        jump = new Animation();
        jump.loadAnimationFromSheet("images/Jump.png", 2, 1, 200);
        fall = new Animation();
        fall.loadAnimationFromSheet("images/Fall.png", 2, 1, 200);
        run = new Animation();
        run.loadAnimationFromSheet("images/Run.png", 8, 1, 200);
        takeHit = new Animation();
        takeHit.loadAnimationFromSheet("images/TakeHit.png", 4, 1, 200);

//        // Initialise the player with an animation
        player = new Player(landing);


        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/6.png"), 1000);

        // Create 3 clouds at random positions off the screen
        // to the right
        for (int c = 0; c < 3; c++) {
            s = new Sprite(ca);
            s.setX(screenWidth + (int) (Math.random() * 200.0f));
            s.setY(30 + (int) (Math.random() * 150.0f));
            s.setVelocityX(-0.02f);
            s.show();
            clouds.add(s);
        }
        initialiseGame();
        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame() {
        total = 0;
        player.setX(40);
        player.setY(200);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();
    }

    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g) {
        // Be careful about the order in which you draw objects - you
        // should draw the background first, then work your way 'forward'

        // First work out how much we need to shift the view
        // in order to see where the player is.
        int xo = xOffset;
        int yo = yOffset;

        // If relative, adjust the offset so that
        // it is relative to the player

        // ...?
        //changes the background color to the color of black
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Apply offsets to sprites then draw them
        for (Sprite s : clouds) {
            s.setOffsets(xo, yo);
            s.draw(g);
        }

        // Apply offsets to player and draw 
        player.setOffsets(xo, yo);
        g.setColor(Color.RED);
        player.drawBoundingBox(g);
        player.draw(g);

        // TODO: don't set velocity on velocity points
//        AffineTransform transform = new AffineTransform();
//        transform.translate(Math.round(player.getX()), Math.round(player.getY()));
//        int width = player.getImage().getWidth(null);
//        int height = player.getImage().getHeight(null);

//        g.drawImage(player.getImage(), transform, null);
//        g.setColor(Color.magenta);
//        player.drawBoundingBox(g);

        // Apply offsets to tile map and draw  it
        tmap.draw(g, xo, yo);


        // Show score and status information
        String msg = String.format("Score: %d", total / 100);
        g.setColor(Color.darkGray);
        g.drawString(msg, getWidth() - 80, 50);
    }

    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    public void update(long elapsed) {
        // Todo : Move the movement into the update
        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
        player.createCollisionDetector();
        player.setAnimationSpeed(1.0f);
        xOffset =((150-player.playerRectangle.getX())/2) * 2;

        if (flap) {
            player.setAnimationSpeed(1.8f);
            player.setVelocityY(-0.04f);
        }


        for (Sprite s : clouds) {
            s.update(elapsed);
        }
        // Now update the sprites animation and position
        player.update(elapsed);

        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        checkTileCollision(player, tmap);
//        if (checkScreenEdge(player)) {
//
//        }
    }

    /**
     * Checks and handles collisions with the edge of the screen
     *
     * @param s       The Sprite to check collisions for
     * @param tmap    The tile map to check
     * @param elapsed How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed) {
        // This method just checks if the sprite has gone off the bottom screen.
        // Ideally you should use tile collision instead of this approach
        if (s.getY() + s.getHeight() > tmap.getPixelHeight()) {
            // Put the player back on the map 1 pixel above the bottom
            s.setY(tmap.getPixelHeight() - s.getHeight() - 1);
            // and make them bounce
//        	s.setVelocityY(-s.getVelocityY());
        }
    }

    public boolean boundingBoxCollision(Sprite s1, Sprite s2) {
        return false;
    }

    // TODO : Organize collision detection in class n methods
//    public boolean isCollidingLeft(float sx,float sy,TileMap tmap){
//
//    }

    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     *
     * @param s    The Sprite to check collisions for
     * @param tmap The tile map to check
     */
    public void checkTileCollision(Sprite s, TileMap tmap) {
        // Take a note of a sprite's current position
        // set left corner on colision box
        float sx = player.playerRectangle.getX();
        float sy = player.playerRectangle.getY();


        // Find out how wide and how tall a tile is
        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        // Divide the sprite’s x coordinate by the width of a tile, to get
        // the number of tiles across the x axis that the sprite is positioned at
        int xtile = (int) (sx / tileWidth);
        // The same applies to the y coordinate
        int ytile = (int) (sy / tileHeight);
        // What tile character is at the top left of the sprite s?
        char ch = tmap.getTileChar(xtile, ytile);


        if (ch != '.') // If it's not a dot (empty space), handle it
        {
            // Here we just stop the sprite.
            System.out.println("CollidingTopLeft");
            canMoveLeft = false;
            canMoveUp = false;
//            player.stop();
            // You should move the sprite to a position that is not colliding
        } else {
            canMoveLeft = true;
            canMoveUp = true;
        }

        // We need to consider the other corners of the sprite
        // The above looked at the top left position, let's look at the bottom left.
        xtile = (int) (sx / tileWidth);
        ytile = (int) ((sy + player.playerRectangle.getHeight()) / tileHeight);
        ch = tmap.getTileChar(xtile, ytile);

        // If it's not empty space
        if (ch != '.') {
            // Let's make the sprite bounce
            System.out.println("here is bottom left");
            canMoveLeft = false;
            canMoveDown = false;
//    		s.setVelocityY(-s.getVelocityY()); // Reverse velocity
        }else {
            canMoveLeft = true;
            canMoveDown = true;
        }


        xtile = (int) ((sx + player.playerRectangle.getWidth()) / tileWidth);
        ytile = (int) ((sy) / tileHeight);
        ch = tmap.getTileChar(xtile, ytile);

        // If it's not empty space
        if (ch != '.') {
            // Let's make the sprite bounce
            System.out.println("here is top right");
            canMoveRight = false;
            canMoveUp = false;
//    		s.setVelocityY(-s.getVelocityY()); // Reverse velocity
        }
        else {
            canMoveRight = true;
            canMoveUp = true;
        }

        xtile = (int) ((sx + player.playerRectangle.getWidth()) / tileWidth);
        ytile = (int) ((sy + player.playerRectangle.getHeight()) / tileHeight);
        ch = tmap.getTileChar(xtile, ytile);

        // If it's not empty space
        if (ch != '.') {
            // Let's make the sprite bounce
            System.out.println("here is bottom right");
            canMoveRight = false;
            canMoveDown = false;
//    		s.setVelocityY(-s.getVelocityY()); // Reverse velocity
        } else {
            canMoveRight = true;
            canMoveDown = true;
        }
    }

    public boolean checkScreenEdge(Sprite s) {
        if (player.playerRectangle.getX() > getWidth()/2) {

            System.out.println("middle");
            return true;
        }
        if (s.getY() > getHeight()) {
            s.setY(0);
            return true;
        }
        return false;
    }

    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     *
     * @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {

        //if(key == KeyEvent.VK_RIGHT){
        // player.setAnimation(attack1);
        //}
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) stop();

        if (key == KeyEvent.VK_A) {
            player.setAnimation(attack1);
        }

        if (key == KeyEvent.VK_RIGHT) {
            player.setVelocityX(!canMoveRight ? 0.0f : 0.1F);
            player.setAnimation(run);

            // move from here and handle later while redrawing the tile map
            checkScreenEdge(player);
        }
        if (key == KeyEvent.VK_LEFT) {
            player.setVelocityX(!canMoveLeft ? 0.0f : -0.1F);
            player.setAnimation(run);
//            player.setVelocityX(-0.1F);
        }
        if (key == KeyEvent.VK_UP) {
//            flap = true;
            player.setVelocityY(!canMoveUp ? 0.0f : -0.1F);
            player.setAnimation(jump);
//            player.setVelocityY(0.2F);
        }

//        if (key == KeyEvent.VK_UP) {
//            flap = true;
//            player.setAnimation(jump);
//            player.setVelocityY(0.2F);
//        }


        if (key == KeyEvent.VK_S) {
            // Example of playing a sound as a thread
            Sound s = new Sound("sounds/caw.wav");
            s.start();
        }
    }


    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        // Switch statement instead of lots of ifs...
        // Need to use break to prevent fall through.
        switch (key) {
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_UP:
                flap = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_LEFT:
                player.setAnimation(landing);
                player.setVelocityX(0);
                break;
            default:
                break;// pass landing here
        }
    }
}
