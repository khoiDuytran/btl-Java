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
        // vẽ các cạnh của tên lửa bằng cách xác định các tọa độ tại những điểm khác nhau

        rocketShap = new Area(p);
        // khu vực hiển thị hình tên lửa
    }

    public static final double ROCKET_SITE = 50;  // kích thước tên lửa
    private double x;
    private double y;
    private final float speed = 0.8f;   // tốc độ di chuyển
    private float angle = 0;   // góc tên lửa hướng tới
    private final Image image;  // lưu trữ hình ảnh tên lửa
    private final Area rocketShap;   // lưu trữ hình dạng tên lửa

    // Hàm thay đổi vị trí
    public void changeLocation(double x, double y) {
        this.x =x;
        this.y =y;
    }

    // cập nhật vị trí dựa trên góc và tốc dộ
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    // thay đổi góc
    public void changeAngle(float angle) {
        if(angle < 0) {
            angle = 359;

        }
        else if(angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }


    // vẽ tên lửa
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        // Lưu lại trạng thái biến đổi ban đầu
        g2.translate(x, y);
        // Dịch chuyển hệ tọa độ đến (x, y)
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), ROCKET_SITE / 2, ROCKET_SITE / 2);
        // Xoay ảnh quanh tâm của nó (ROCKET_SITE là kích thước ảnh)
        g2.drawImage(image, tran, null);
        // Vẽ ảnh với phép biến đổi mới
        Shape shap = getShape();
        // Lấy đối tượng Shape để vẽ hoặc kiểm tra va chạm
        hpRender(g2, shap, y);
        // Vẽ thanh máu hoặc xử lý gì đó liên quan đến shape và vị trí y
        g2.setTransform(oldTransform);
        // Khôi phục trạng thái biến đổi ban đầu

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
        // Dịch chuyển đến tọa độ (x, y)
        afx.rotate(Math.toRadians(angle), ROCKET_SITE / 2, ROCKET_SITE / 2);
        // Xoay quanh tâm của hình (ROCKET_SITE / 2, ROCKET_SITE / 2)
        return new Area(afx.createTransformedShape(rocketShap));
        // Trả về một đối tượng Area mới từ hình đã được biến đổi

    }

    // kiểm tra xem tên lửa có nằm trong giới hạn của màn hình không ?
    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        // Lấy kích thước bao quanh của hình
        if(x <= -size.getWidth() || y < -size.getHeight() || x > width || y > height) {
            return false;
            // Nếu đối tượng nằm ngoài màn hình
        } else {
            return true;
            // Nếu đối tượng còn nằm trong màn hình
        }
    }
}
