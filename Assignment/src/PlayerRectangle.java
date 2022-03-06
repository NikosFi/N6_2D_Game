public class PlayerRectangle {

     int x;
     int y;
     int width;
     int height;

    public PlayerRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x+80;
    }

    public void setY(int y) {
        this.y = y+70;
    }


    public void setWidth(int width) {
        this.width = width-165;
    }

    public void setHeight(int height) {
        this.height = height-144;
    }


}
