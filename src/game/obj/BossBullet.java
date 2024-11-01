package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class BossBullet {

    private double x;
    private double y;
    private final Shape shape;
    // khai bảo hình dạng
    private final Color color = new Color(182, 170, 170);
    // khai báo màu
    private final float angle;
    // góc bắn ra
    private double size;
    // khai báo kích thước
    private float speed = 1f;
    // tốc độ của đạn

    public BossBullet(double x, double y, float angle, double size, float speed) {
        x += Boss.BOSS_SITE / 2-(size / 2);
        y += Boss.BOSS_SITE / 2-(size / 2);
        // Đạn được căn giữa so với người chơi, bằng cách lấy tâm của người chơi trừ đi nửa kích thước của viên đạn.
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.size = size;
        this.speed = speed;
        shape = new Ellipse2D.Double(0, 0, size, size);
        // Khởi tạo hình dạng viên đạn
    }

    // hàm tính toán vị trí mới của đối tượng dựa trên góc xoay và tốc độ
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
        // chuyển từ độ -> radian
    }

    // kiểm tra xem đạn có ở trong giới hạn screen hay không?
    public boolean check(int width, int height) {
        if(x <= -size || y <- size || x > width || y > height) {
            return false;
        }
        else {
            return true;
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        // lưu trạng thái ban đầu
        g2.setColor(color);
        // thiết lập màu sắc
        g2.translate(x, y);
        g2.fill(shape);
        // vẽ hình dạng
        g2.setTransform(oldTransform);
        // khôi phục trạng thái ban đầu
    }
    public Shape getShape(){
        return new Area(new Ellipse2D.Double(x, y, size, size));
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getSize() {
        return size;
    }
    public double getCenterX() {
        return x + size / 2;
    }
    public double getCenterY() {
        return y + size / 2;
    }
}
