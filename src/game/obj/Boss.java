package game.obj;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Boss extends HpRender {

    private boolean isActive = false; // Boss chỉ xuất hiện khi score >= 20
    private Player player; // Tham chiếu đến đối tượng Player
    private int score = 0; // Giả sử có biến score
    private boolean isEntering = false;   // trạng thái xem boss có đang di chuyển vào màn hình
    private int screenWidth, screenHeight;  // kích thước màn hình

    public Boss() {
        super(new HP(100, 100));
        this.image = new ImageIcon(getClass().getResource("/game/image/boss.png")).getImage();
        Path2D p =new Path2D.Double();
        p.moveTo(0,BOSS_SITE / 2);
        p.lineTo(30,20);
        p.lineTo(BOSS_SITE - 10, 26);
        p.lineTo(BOSS_SITE + 20, BOSS_SITE - 26);
        p.lineTo(30, BOSS_SITE - 20);
        bossShap = new Area(p);
        this.player = player;   // nhận đối tượng player để bán đạn về hướng người chơi
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Ban đầu boss nằm ngoài màn hình (ví dụ: bên trái)
        this.x = -BOSS_SITE; // Nằm ngoài màn hình bên trái
        this.y = screenHeight / 2 - BOSS_SITE / 2; // Ở giữa theo trục Y
        isEntering = false; // Chưa di chuyển vào màn hình
    }

    public static final double BOSS_SITE = 100;
    private double x;
    private double y;
    private final float speed = 0.8f;
    private float angle = 0;
    private final Image image;
    private final Area bossShap;
    private int hitCount = 0;
    private long lastShootTime = 0;  // thời gian lần cuối boss bắn đạn

    // Boss chỉ xuất hiện khi điểm >= 20
    public void setScore(int score) {
        this.score = score;
        if (score >= 20) {
            isActive = true;
        }
    }

    public void update() {
        if (!isActive) return; // Nếu boss chưa kích hoạt, không cập nhật

        // Nếu boss đang di chuyển vào màn hình
        if (isEntering) {
            if (x < 50) { // Di chuyển cho đến khi boss vào trong màn hình
                x += speed;
            } else {
                isEntering = false; // Đã vào màn hình, dừng di chuyển
            }
        } else {
            // Sau khi vào màn hình, boss có thể bắn đạn
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime > 1000) { // Bắn mỗi 1 giây
                shootAtPlayer();
                lastShootTime = currentTime;
            }
        }
    }

    // Tính toán hướng bắn đạn về phía người chơi
    private void shootAtPlayer() {
        double playerX = player.getX();
        double playerY = player.getY();

        double deltaX = playerX - this.x;
        double deltaY = playerY - this.y;
        double angleToPlayer = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Tạo viên đạn bắn về hướng người chơi
        Bullet bullet = new Bullet(this.x, this.y, (float) angleToPlayer, 10, 5);  // đạn có kích thước 10 và tốc độ là 5
        // Thêm logic để thêm viên đạn vào màn chơi hoặc danh sách bullet
    }




    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), BOSS_SITE / 2, BOSS_SITE / 2);
        g2.drawImage(image, tran, null);
        Shape shap = getShape();
        hpRender(g2, shap, y);
        g2.setTransform(oldTransform);


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
        afx.rotate(Math.toRadians(angle), BOSS_SITE / 2, BOSS_SITE / 2);
        return new Area(afx.createTransformedShape(bossShap));
    }

    // Tăng hitCount và kiểm tra nếu boss bị đánh bại
    public void hit() {
        hitCount++;
        if (hitCount >= 7) {
            // Logic khi boss bị đánh bại
            isActive = false; // Boss biến mất khi bị đánh 7 lần
        }
    }
}
