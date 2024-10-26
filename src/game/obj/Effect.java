package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Effect {
    private final double x;
    private final double y;
    private final double max_distance;
    // khoảng cách tối đa mảnh nổ có thể di chuyển
    private final int max_size;
    // kích thước tối đa của từng mảnh
    private final Color color;
    // màu cảu mảnh nổ
    private final int totalEffect;
    // số lượng mảnh trong hiệu ứng nổ
    private final float speed;
    // tốc độ di chuyển của mảnh nổ
    private double current_distance;
    // khoảng cách hiện tại mà các mảnh đã di chuyển
    private ModelBoom booms[];
    // mảng các đối tượng ModelBoom đại diện cho các mảnh nổ
    private float alpha = 1f;
    // độ mờ của các mảnh, giúp hiệu ứng mất dần khi khoảng cách tăng lên

    public Effect(double x, double y, int totalEffect, int max_size, int max_distance, float speed, Color color) {
        this.x = x;
        this.y = y;
        this.totalEffect = totalEffect;
        this.max_size = max_size;
        this.max_distance = max_distance;
        this.speed = speed;
        this.color = color;
        // gán các giá trị ban đầu cho thuộc tinh của lớp
        createRandom();
        // gọi hàm khởi tạo ngẫu nhiên các mảnh nổ
    }

    private void createRandom() {
        booms = new ModelBoom[totalEffect];
        float per = 360f / totalEffect;
        // xác định khoảng cách góc giữa các mảnh nổ
        Random ran = new Random();
        for(int i = 1; i <= totalEffect; i++) {
            int r = ran.nextInt((int) per) + 1;
            int boomSize = ran.nextInt(max_size) + 1;
            float angle = i * per + r;
            // xác định góc và kích thước ngẫu nhiên của từng mảnh
            booms[i -1] = new ModelBoom(boomSize, angle);
            // tạo ModelBoom với kích thước và góc ngẫu nhiên
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = new AffineTransform();
        Composite oldComposite = g2.getComposite();
        // lưu trạng thái ban đầu

        g2.setColor(color);     // đặt màu của các mảnh hiệu ứng
        g2.translate(x, y);     // di chuyển gốc tạo độ cảu Graphícs2D về vị trí x, y(điểm xuất phát của hiệu ứng)
        for(ModelBoom b : booms) {
            double bx = Math.cos(Math.toRadians(b.getAngle())) * current_distance;
            double by = Math.sin(Math.toRadians(b.getAngle())) * current_distance;
            // tính toạ độ góc của mảnh dự trên góc theo radian và khoảng cách hiện tại
            double boomSize = b.getSize();
            // xác định kích thước của mảnh hiệu ứng
            double space = boomSize / 2;
            // xác định khoảng cách giữa toạ độ được tính và góc toạ độ trung tâm của mảnh
            if(current_distance >= max_distance - (max_distance * 0.7f)) {
                alpha = (float) ((max_distance - current_distance) / (max_distance * 0.7f));
                // 'alpha' giảm dần khi 'current_distance' gần đạt đến max_distance để tạo hiệu ứng mờ dần
            }
            if(alpha > 1){
                alpha = 1;
            }
            else if(alpha < 0) {
                alpha = 0;
            }
            // đảm bảo rằng 'alpha' luôn nằm từ 0 -> 1
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            // Thiết lập độ mờ cho từng mảnh bằng cách dùng AlphaComposite với giá trị alpha
            g2.fill(new Rectangle2D.Double(bx - space, by - space, boomSize, boomSize));
            // Vẽ mảnh dưới dạng hình chữ nhật nhỏ có tọa độ 'bx - space' và 'by - space' với kích thước 'boomSize' x 'boomSize'
        }

        g2.setComposite(oldComposite);
        g2.setTransform(oldTransform);
        // khôi phục trạng thái ban đầu để các phép vẽ sau không vị ảnh hưởng bỏi những thay đổi trong draw
    }

    public void update() {
        current_distance += speed;
    }
    // Tăng khoảng cách hiên tại theo speed để tạo chuyển động cho các mảnh

    public boolean check() {
        return current_distance < max_distance;
    }
    // Trả về true nếu hiệu ứng chưa đạt khoảng cách tối đa và tiếp tục hoạt động; ngược lại trả về false để dừng hiệu ứng
}
