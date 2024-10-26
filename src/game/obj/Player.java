package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Player extends HpRender {

    public Player() {
        super(new HP(50, 50));
        this.image = new ImageIcon(getClass().getResource("/game/image/plane.png")).getImage();
        this.image_speed = new ImageIcon(getClass().getResource("/game/image/plane_speed.png")).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(0, 15);
        p.lineTo(20, 5);
        p.lineTo(PLAYER_SITE + 15, PLAYER_SITE / 2);
        p.lineTo(20, PLAYER_SITE - 5);
        p.lineTo(0, PLAYER_SITE - 15);
        playerShap = new Area(p);
    }

    public static final double PLAYER_SITE = 64;
    // kích thước player
    private double x;
    private double y;
    private final float MAX_SPEED = 1f;
    // tốc độ tối đa
    private float speed = 0f;
    // tốc độ hiện tại
    private float angle = 0f;
    // góc xoay của player
    private final Area playerShap;
    // hình dạng player
    private final Image image;
    // hình ảnh đứng yên của player
    private final Image image_speed;
    // hình ảnh di chuyển của player
    private boolean speedUp;
    // trạng thái tăng tốc
    private boolean alive = true;
    // trạng thái kiểm tra còn sống hay không

    // hàm cập nhật tạo độ tới vị trí mới
    public void changeLocation(double x, double y) {
        this.x =x;
        this.y =y;
    }

    // hàm tính toán vị trí mới của đối tượng dựa trên góc xoay và tốc độ
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
        // chuyển từ độ sang radian
    }

    // hàm cập nhật góc quay
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
        // lưu lại trạng thái ban đầu của player

        g2.translate(x, y);
        // di chuyển hệ tạo độ tới (x, y)
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), PLAYER_SITE / 2, PLAYER_SITE / 2);
        // xoay ảnh xung quanh nó
        g2.drawImage(speedUp ? image_speed : image, tran, null);
        // biểu thức điều kiện để chọn hình ảnh dựa trên trạng thái speedUp
        hpRender(g2, getShape(), y);
        // vẽ thanh máu
        g2.setTransform(oldTransform);
        // khôi phục trạng thái ban đầu

//        g2.setColor(new Color(12,173, 84));
//        g2.draw(getShape());
//        g2.draw(getShape().getBounds());
    }
    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        // dịch chuyển tới toạ độ x, y
        afx.rotate(Math.toRadians(angle), PLAYER_SITE / 2, PLAYER_SITE / 2);
        // xoay quanh tâm của hình
        return new Area(afx.createTransformedShape(playerShap));
        // Trả về một đối tượng Area mới từ hình đã được biến đổi
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

    public void speedUp() {
        speedUp = true;
        // kích hoạt trạng thái tăng tốc
        if(speed > MAX_SPEED) {
            speed = MAX_SPEED;
            // đảm bảo player không vượt quá tốc độ tối đa
        }
        else {
            speed += 0.01f;
            // tăng tốc từ từ
        }
    }

    public void speedDown() {
        speedUp = false;
        // tắt trạng thái tăng tốc
        if(speed <= 0){
            speed = 0;
            // đảm bảo player dừng hẳn
        }
        else {
            speed -= 0.007f;
            // giảm tốc từ từ
        }
    }
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // Đặt lại
    public void reset() {
        alive = true;
        resetHP();
        angle = 0;
        speed = 0;
    }
}
