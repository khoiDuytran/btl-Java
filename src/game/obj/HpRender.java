package game.obj;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HpRender {

    private final HP hp;
    public HpRender(HP hp){
        this.hp = hp;
    }
    // khởi tạo đối tượng HP
    protected  void hpRender(Graphics2D g2, Shape shape, double y) {
        if(hp.getCurrentHp() != hp.getMAX_HP()) {
            // Kiểm tra nếu máu hiện tại khác máu tối đa
            double hpY = shape.getBounds().getY() - y - 10;
            // Tính vị trí y cho thanh máu

            g2.setColor(new Color(70, 70, 70));
            // Màu nền thanh máu
            g2.fill(new Rectangle2D.Double(0, hpY, Player.PLAYER_SITE, 2));
            // Vẽ nền thanh máu
            g2.setColor(new Color(253, 91, 91)); // Màu máu
            double hpSize = hp.getCurrentHp() / hp.getMAX_HP() * Player.PLAYER_SITE;
            // Tính kích thước máu
            g2.fill(new Rectangle2D.Double(0, hpY, hpSize, 2)); // vẽ thanh máu
        }

    }

    public boolean updateHP(double cutHP) {
        hp.setCurrentHp(hp.getCurrentHp() - cutHP); // giảm Hp
        return hp.getCurrentHp() > 0; // trả về true nếu còn Hp, ngược lại thì false
    }
    public double getHP() {
        return hp.getCurrentHp(); // trả về Hp hiện tại
    }

    public void resetHP() {
        hp.setCurrentHp(hp.getMAX_HP());  // Đặt lại HP về mức tối đa
    }
}
