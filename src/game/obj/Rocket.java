package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Rocket extends HpRender {

    public Rocket() {
        super(new HP(20, 20));
        this.image = new ImageIcon(getClass().getResource("/game/image/rocket.png")).getImage();
        Path2D p =new Path2D.Double();
        p.moveTo(0,ROCKET_SITE / 2);
        p.lineTo(15,10);
        p.lineTo(ROCKET_SITE - 5, 13);
        p.lineTo(ROCKET_SITE + 10, ROCKET_SITE - 13);
        p.lineTo(15, ROCKET_SITE - 10);
        rocketShap = new Area(p);
    }

    public static final double ROCKET_SITE = 50;
    private double x;
    private double y;
    private final float speed = 0.8f;
    private float angle = 0;
    private final Image image;
    private final Area rocketShap;


    public void changeLocation(double x, double y) {
        this.x =x;
        this.y =y;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    public void changeAngle(float angle) {
        if(angle < 0) {
            angle = 359;

        }
        else if(angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), ROCKET_SITE / 2, ROCKET_SITE / 2);
        g2.drawImage(image, tran, null);
        Shape shap = getShape();
        hpRender(g2, shap, y);
        g2.setTransform(oldTransform);

//        g2.setColor(new Color(36, 214, 63));
//        g2.draw(shap);
//        g2.draw(shap.getBounds2D());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), ROCKET_SITE / 2, ROCKET_SITE / 2);
        return new Area(afx.createTransformedShape(rocketShap));
    }

    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        if(x <= -size.getWidth() || y < -size.getHeight() || x > width || y > height) {
            return false;
        } else {
            return true;
        }
    }
}
