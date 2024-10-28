package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Boss extends HpRender {

    public Boss() {
        super(new HP(150, 150));
        // kích thước thanh máu tối đa
        this.image = new ImageIcon(getClass().getResource("/game/image/boss.png")).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(0, BOSS_SITE / 2);
        p.lineTo(30, 20);
        p.lineTo(BOSS_SITE - 10, 26);
        p.lineTo(BOSS_SITE + 20, BOSS_SITE - 26);
        p.lineTo(30, BOSS_SITE - 20);
        // vẽ các cạnh của tên lửa bằng cách xác định các tọa độ tại những điểm khác nhau

        bossShap = new Area(p);
        // khu vực hiển thị của boss

    }

    public static final double BOSS_SITE = 100; // kích thước
    private double x;
    private double y;
    private float angle = 0;    // góc hướng tới
    private final Image image;  // khai báo ảnh
    private final Area bossShap;    // khai báo khu vực hiển thị


    // hàm cập nhật tạo độ tới vị trí mới
    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // hàm tính toán vị trí mới của đối tượng dựa trên góc xoay và tốc độ
    public void update() {
        x += Math.cos(Math.toRadians(angle));
        y += Math.sin(Math.toRadians(angle));
        // toRadians: độ sang radian
    }

    // hàm cập nhật góc quay
    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;

        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }


    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        // lưu lại trạng thái ban đầu
        g2.translate(x, y);
        // chuyển tới tạo độ x, y
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), BOSS_SITE / 2, BOSS_SITE / 2);
        // xoay ảnh xoay quanh nó
        g2.drawImage(image, tran, null);
        // vẽ hình BOSS
        hpRender(g2, getShape(), y);
        // vẽ thanh máu
        g2.setTransform(oldTransform);
        // khôi phục trạng thái ban đầu
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
        // chuyển tới toạ độ x, y
        afx.rotate(Math.toRadians(angle), BOSS_SITE / 2, BOSS_SITE / 2);
        // xoay quanh trung tâm hình
        return new Area(afx.createTransformedShape(bossShap));
        // Trả về một đối tượng Area mới từ hình đã được biến đổi

    }
}