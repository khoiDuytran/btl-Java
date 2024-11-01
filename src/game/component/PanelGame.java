package game.component;

import game.obj.*;
import game.obj.sound.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PanelGame extends JComponent {
    private Graphics2D g2; // lớp dùng để vẽ các hình dạng 2D
    private BufferedImage image;  // một loại hình ảnh cho phép vẽ trực tiếp lên nó

    // Chiều rộng và chiều cao của khu vực trò chơi
    private int width;
    private int height;

    private Thread thread;   // luồng chính chạy vòng lặp game
    private boolean start = true;
    private Key key; // khai báo đối tượng key xử lý các phím
    private int shotTime;  // thời gian giữa những lần bắn của player
    private boolean bossActive = false; // kiểm tra sự xuất hiện của boss
    private long bossShotTime = 0;  // thời gian lần cuối boss bắn đạn


//    double playTime;
//    DecimalFormat dFormat =new DecimalFormat("#0.00");

    // FPS
    private final int FPS = 60;
    // Thời gian hiển thị một khung hình, được tính bằng nano giây (1 giây có 1 tỷ nano giây)
    public final int TARGET_TIME = 1000000000/FPS;

    //GAME Object
    private Sound sound;  // âm thanh
    private Player player;  // người chơi
    private List<Bullet> bullets;  // danh sách các đối tượng ĐẠN
    private List<Rocket> rockets;  // danh sách các Tên Lửa
    private List<Effect> boomEffects;  // danh sách các hiệu ứng nổ
    private List<BossBullet> bossBullets;   // danh sách đạn từ boss
    private Boss boss;  // trùm cuối

    private int score = 0;  // điểm ban đầu của người chơi

    public void start() {

        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Tạo một hình ảnh với kích thước bằng toàn bộ khu vực hiển thị để vẽ đồ họa vào và sửa dụng định dạng ARGB để hỗ trợ độ trong suốt
        g2 = image.createGraphics();
        // Lấy đối tượng đồ họa từ hình ảnh để có thể vẽ lên đó

        // Thiết lập các tùy chọn để cải thiện chất lượng hiển thị
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // KEY_ANTIALIASING: Kích hoạt tính năng khử răng cưa để đồ họa trông mượt mà hơn.
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            // KEY_INTERPOLATION: Dùng phương pháp 'BILINEAR' để cải thiện chất lượng hiển thị hình ảnh khi chúng được phóng to hoặc thu nhỏ
        thread = new Thread(new Runnable() {   // tạo luồng mới để vòng lặp game chạy liên tục không ảnh hưởng đến cái khác
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();  // thời gian bắt đầu
                    drawBackground();   // vẽ nền game
                    drawGame(); // vẽ các đối tượng game
                    render();  // Cập nhật màn hình bằng cách vẽ nội dung hình ảnh (image) lên screen
                    bossUpdate();

                    //if(player.isAlive()) playTime +=(double) 1/60;
                    long time = System.nanoTime() - startTime;
                    // thời gian đã trôi qua của vòng lặp hiện tại
                    if (time < TARGET_TIME) {
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }
                    // Nếu vòng lặp chạy nhanh hơn 'TARGET_TIME', thì chương trình sẽ ngắt (sleep) một khoảng thời gian để giữ cho FPS luôn ổn định
                }
            }
        });
        initObjectGame();  // Khởi tạo các đối tượng trong trò chơi
        initKeyboard();    // Khởi tạo xử lý các phản hồi từ phím bấm từ người chơi
        initBullets();     // Khởi tạo danh sách các viên đạn để người chơi có thể bắn trong trò chơi
        initBossBullet();  // Khởi tạo danh sách các viên đạn để boss bắn vào player
        thread.start();    // Bắt đầu luồng trò chơi
    }

    // thêm Rockets vào trò chơi
    private void addRocket(){
        Random ran = new Random();
        // tạo số ngẫu nhiên, được dùng để đặt vị trí ban đầu của tên lửa trên trục y

        int locationY = ran.nextInt(height - 50) + 25;
        // Phạm vi đảm bảo tên lửa không xuất hiện quá sát mép trên hoặc mép dưới của màn hình
        Rocket rocket = new Rocket();
        rocket.changeLocation(0, locationY);
        // Đặt vị trí ban đầu của rocket đầu tiên tại tọa độ (0, locationY), ở cạnh trái của màn hình
        rocket.changeAngle(0);
        // Đặt góc di chuyển của tên lửa đầu tiên là 0 độ, có nghĩa là nó sẽ di chuyển từ trái sang phải
        rockets.add(rocket);
        // thêm rocket đầu tiên vào danh sách

        int locationY2 = ran.nextInt(height - 50) + 25;
        // vị trí y ngẫu nhiên của tên lửa thứ hai
        Rocket rocket2 = new Rocket();
        rocket2.changeLocation(width, locationY2);
        // Đặt vị trí ban đầu của rocket2 tại tọa độ (width, locationY2), ở cạnh phải của màn hình
        rocket2.changeAngle(180);
        // Đặt góc di chuyển của rocket2 là 180 độ, nó sẽ di chuyển từ phải sang trái
        rockets.add(rocket2);
        // thêm rocket thứ 2 vào danh sách

        int locationY3 = getHeight() / 2 + 10;
        // vị trí y ngẫu nhiên của tên lửa thứ hai
        Rocket rocket3 = new Rocket();
        rocket3.changeLocation(width, locationY3);
        // Đặt vị trí ban đầu của rocket3 tại tọa độ (width, locationY3), ở cạnh trái của màn hình
        rocket3.changeAngle(0);
        // Đặt góc di chuyển của rocket3 là 180 độ, nó sẽ di chuyển từ trái sang phải
        rockets.add(rocket3);
        // nếu boss xuất hiện, thêm rocket thứ 3 vào danh sách

        int locationY4 = getHeight() / 2;
        // vị trí y ngẫu nhiên của tên lửa thứ 4
        Rocket rocket4 = new Rocket();
        rocket4.changeLocation(width, locationY4);
        // Đặt vị trí ban đầu của rocket4 tại tọa độ (width, locationY4), ở cạnh phải của màn hình
        rocket3.changeAngle(180);
        // Đặt góc di chuyển của rocket4 là 0 độ, nó sẽ di chuyển từ phải sang trái

        if(bossActive){
            rockets.add(rocket4);
        }   // nếu boss xuất hiện, thêm rocket thứ 4 vào danh sách
    }

    private void initObjectGame() {
        sound = new Sound();  // tạo đối tượng âm thanh
        player = new Player(); // tạo đối tượng người chơi
        player.changeLocation(150, 150);
        // đặt vị trí ban đầu của người chơi ở toạ độ (150, 150)
        rockets = new ArrayList<>();    // danh sách các tên lửa
        boomEffects = new ArrayList<>();    // danh sách các hiệu ứng nổ
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start) {
                    addRocket();
                    // thêm tên lửa trong danh sách 'rockets' trong trò chơi

                    if(!bossActive){
                        sleep(2000);
                        // tạm dừng 2 giây trước khi thêm 2 tên lửa mới để tạo sự đều đặn trong game

                    }
                    else{
                        sleep(1000);
                    }
                    // tạm dừng 1 giây trước khi thêm 2 tên lửa mới để tạo sự đều đặn trong game
                }
            }
        }).start();
    }

    private void resetGame() {
        score = 0;
        rockets.clear();
        bullets.clear();
        player.changeLocation(150, 150);
        player.reset();
        bossActive = false;
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus();
        // Yêu cầu focus cho thành phần trò chơi để nó có thể nhận các sự kiện bàn phím từ người dùng
        addKeyListener(new KeyAdapter() {   // thêm keyListener để nhận sự kiên bàn phím
            @Override
            public void keyPressed(KeyEvent e) {    // gọi khi người dùng nhấn xuống một phím
                if(e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(true);
                    // biểu thị rằng phím di chuyển sang trái đang được nhấn
                }
                else if(e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(true);
                    // biểu thị rằng phím di chuyển sang phải đang được nhấn
                }
                else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(true);
                    // biểu thị rằng phím di chuyển tăng tắc đang được nhấn
                }
                else if(e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(true);
                }
                else if(e.getKeyCode() == KeyEvent.VK_K) {
                    key.setKey_k(true);
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {   // gọi khi người dùng nhả 1 phím
                if(e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(false);
                    // biểu thị rằng phím di chuyển sang trái đã được nhả
                }
                else if(e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(false);
                    // biểu thị rằng phím di chuyển sang phải đã được nhả
                }
                else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(false);
                    // biểu thị rằng phím di chuyển tăng tốc đã được nhả
                }
                else if(e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(false);
                }
                else if(e.getKeyCode() == KeyEvent.VK_K) {
                    key.setKey_k(false);
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(false);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                // biến tốc độ góc, dùng để điều chỉnh góc xoay của người chơi khi nhấn các phím điều hướng
                while (start) {
                    if(player.isAlive()) {
                        float angle = player.getAngle();
                        // Lấy góc hiện tại của người chơi để sử dụng và cập nhật
                        if(key.isKey_left()) {
                            angle -= s;
                            // ấn keyleft, góc của player sẽ giảm
                        }
                        if(key.isKey_right()) {
                            angle += s;
                            // ấn keyright, góc của player sẽ tăng
                        }
                        if(key.isKey_j() || key.isKey_k()){
                            if(shotTime == 0){
                            // nêu shotTime = 0 thì tạo đạn mới
                                if(key.isKey_j()) {
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 5, 3f));
                                    // thêm đạn với kích cỡ là 5
                                }
                                else {
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 15, 3f));
                                    // thêm đạn với kích cỡ là 20
                                }
                                sound.soundShoot(); // thêm âm thanh bắn đạn
                            }
                            shotTime++;     // tăng shottime để trì hoãn giữa các lần bắn
                            if(shotTime == 15) {
                                shotTime = 0;
                                // khi shottime = 15, trả shottime lại về 0
                            }
                        }
                        else {
                            shotTime = 0;
                            // nếu không thì mặc định shottime = 0
                        }
                        if(key.isKey_space()) {
                            player.speedUp();
                            // phím space được ấn, player tăng tốc về phia trước
                        }
                        else {
                            player.speedDown();
                            // ngược lại, player giảm dần tốc độ
                        }
                        player.update();
                        // nếu player đi ra ngoài screen
                        if (player.getX() < 0) {
                            player.changeLocation(width, player.getY());
                        } else if (player.getX() > width) {
                            player.changeLocation(0, player.getY());
                        }
                        // cập nhật player xuất hiện ở phía đối diện
                        if (player.getY() < 0) {
                            player.changeLocation(player.getX(), height);
                        } else if (player.getY() > height) {
                            player.changeLocation(player.getX(), 0);
                        }

                        player.changeAngle(angle);
                        // cập nhật góc xoay của player
                        if(!bossActive && score > 3){
                            if(key.isKey_enter()){
                                resetGame();
                            }
                            // trường hợp WinGame, bấm enter để chơi lại
                        }
                    }

                    else {
                        if(key.isKey_enter()) {
                            resetGame();
                            // nếu player chết, ấn enter để bắt đầu lại
                        }

                    }
                    for(int i = 0; i < rockets.size(); i++){
                        // duyệt qua danh sách tên lửa
                        Rocket rocket = rockets.get(i);
                        if(rocket != null){
                            rocket.update();
                            // cập nhật vị trí của nó
                            if(!rocket.check(width,height)) {
                                rockets.remove(rocket);
                                // nếu nó nằm ngoài giới hạn screen, xoá tên lửa khỏi danh sách
                            } else {
                                if(player.isAlive()) {
                                    checkPlayer(rocket);
                                    // ngược lại, nếu player còn sống, kiểm tra va chạm với player
                                }
                            }
                        }
                    }
                    if (bossActive && player.isAlive()) {
                        checkBoss(player);
                    }
                    sleep(5);
                    // Dừng luồng 5ms trước khi lặp lại vòng while để duy trì tốc độ xử lý mượt mà
                }
            }
        }).start();
    }

    private void initBullets() {
        bullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start) {
                    for(int i = 0; i < bullets.size(); i++) {
                        // duyệt qua danh sách bullets và lấy đạn tại chỉ số 'i'
                        Bullet bullet = bullets.get(i);
                        if (bullet != null) {
                            bullet.update();
                            // cập nhật vị trí đạn (di chuyển theo hướng nhất định)
                            checkBullets(bullet);
                            // kiểm tra va chạm
                            if (!bullet.check(width, height)) {
                                bullets.remove(bullet);
                                // nếu đạn bay ra khỏi screen, xoá nó khỏi danh sách
                            }
                        } else {
                            bullets.remove(bullet);
                            // nếu đạn là null, loại khỏi danh sách
                        }
                    }
                    for (int i = 0; i < boomEffects.size(); i++){
                        // duyệt qua danh sách hiệu ứng
                        Effect boomEffect = boomEffects.get(i);
                        if(boomEffect != null) {
                            boomEffect.update();
                            // cập nhật trang thái nổ
                            if(!boomEffect.check()) {
                                boomEffects.remove(boomEffect);
                            }
                            // kiểm tra xem hiệu ứng nổ còn hoạt động hay không. Nếu không, loại bỏ hiệu ứng nổ khỏi danh sách boomEffects
                        } else {
                            boomEffects.remove(boomEffect);
                            // Nếu hiệu ứng nổ là null, loại bỏ nó khỏi danh sách
                        }
                    }
                    sleep(1);
                    // Dừng luồng 1ms trước khi lặp lại vòng while để giảm tải cho CPU và duy trì tốc độ xử lý mượt mà
                }
            }
        }).start();
    }
    private void initBossBullet() {
        bossBullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start) {
                    for(int i = 0; i < bossBullets.size(); i++) {
                        // duyệt qua danh sách bossbullets và lấy đạn tại chỉ số 'i'
                        BossBullet bossbullet = bossBullets.get(i);
                        if (bossbullet != null) {
                           bossbullet.update();
                            // cập nhật vị trí đạn (di chuyển theo hướng nhất định)
                            checkBossBullet(bossbullet);
                            // kiểm tra va chạm
                            if (!bossbullet.check(width, height)) {
                                bullets.remove(bossbullet);
                                // nếu đạn bay ra khỏi screen, xoá nó khỏi danh sách
                            }
                        } else {
                            bossBullets.remove(bossbullet);
                            // nếu đạn là null, loại khỏi danh sách
                        }
                    }
                    for (int i = 0; i < boomEffects.size(); i++){
                        // duyệt qua danh sách hiệu ứng
                        Effect boomEffect = boomEffects.get(i);
                        if(boomEffect != null) {
                            boomEffect.update();
                            // cập nhật trang thái nổ
                            if(!boomEffect.check()) {
                                boomEffects.remove(boomEffect);
                            }
                            // kiểm tra xem hiệu ứng nổ còn hoạt động hay không. Nếu không, loại bỏ hiệu ứng nổ khỏi danh sách boomEffects
                        } else {
                            boomEffects.remove(boomEffect);
                            // Nếu hiệu ứng nổ là null, loại bỏ nó khỏi danh sách
                        }
                    }
                    sleep(1);
                    // Dừng luồng 1ms trước khi lặp lại vòng while để giảm tải cho CPU và duy trì tốc độ xử lý mượt mà
                }
            }
        }).start();
    }
    private boolean bossShootFirst = true; // hướng bắn ban đầu của boss

    private void bossUpdate() {
        if (score == 3 && !bossActive ) {   // nếu điểm = 20 và boss chưa được kích hoạt
            boss = new Boss();
            boss.changeLocation(getWidth() - Boss.BOSS_SITE, getHeight() / 2);  // Xuất hiện tại bên phải screen
            boss.changeAngle(180);    // thay đổi góc của boss
            bossActive = true;  // Boss xuất hiện
            boss.update();
        }

        if (bossActive && player.isAlive()) {
            if (bossShotTime == 0) {
                // Bắn đạn theo góc mà boss đang nhìn
                if(bossShootFirst) {

                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle(), 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 15, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 15, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 70, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 70, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 50, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 50, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 30, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 30, 15, 2f));
                    // thêm các hướng bắn đạn của boss
                }
                else {
//                    Random ran = new Random();
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle(), 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 8, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 8, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 65, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 65, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 45, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 45, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() + 23, 15, 2f));
                    bossBullets.add(new BossBullet(boss.getX(), boss.getY(), boss.getAngle() - 23, 15, 2f));
                    // thêm các hướng bắn đạn của boss
                }
                // Chuyển đổi hướng cho lần bắn tiếp theo
                bossShootFirst = !bossShootFirst;

                sound.soundShoot();  // Chèn âm thanh bắn
            }

            bossShotTime++;
            if (bossShotTime >= 30) {  // Đặt tần suất bắn
                bossShotTime = 0;
            }
            checkBoss(player);  // Kiểm tra va chạm với người chơi
        }
    }

    private void checkBoss(Player player) {
            Area playerArea = new Area(player.getShape());
            playerArea.intersect(boss.getShape());
            // tìm giao nhau giữa hai khu vực hiển thị player và boss
            if (!playerArea.isEmpty()) {
                // có va chạm
                if(!boss.updateHP(player.getHP())){
                    // nếu hp của boss ít hơn hp plyer
                    bossActive = false;
                    // chuyển trạng thái của boss => false
                    sound.soundDestroy();
                    // thêm âm thanh
                    double x = boss.getX() + Rocket.ROCKET_SITE / 2;
                    double y = boss.getY() + Rocket.ROCKET_SITE / 2;
                    // tính toạ độ trung tâm của tên lửa để đặt hiệu ứng nổ tại đó

                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    // Thêm một loạt các hiệu ứng nổ với kích thước, màu sắc và độ trong suốt khác nhau tại vị trí trung tâm của tên lửa, tạo ra hiệu ứng nổ đa dạng


                }
                if (!player.updateHP(boss.getHP())) {
                    // cập nhật hp player ít hơn hp của boss
                    player.setAlive(false);
                    // nếu hết HP, trạng thái sống của player trả về false
                    sound.soundDestroy();
                    // thêm âm thanh phá huỷ

                    double x = player.getX() + Player.PLAYER_SITE / 2;
                    double y = player.getY() + Player.PLAYER_SITE / 2;
                    // tính toạ độ trung tâm player để đặt vị trí nổ

                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    // Tạo thêm nhiều hiệu ứng nổ đa dạng về kích thước, độ trong suốt, và màu sắc
                }
            }

    }

    private void checkBullets(Bullet bullet) {
        for(int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if(rocket != null) { // kiểm tra tên lửa có tồn tại không?
                Area area = new Area(bullet.getShape());
                // gọi area từ hình dạng viên đạn
                area.intersect(rocket.getShape());
                // tìm giao nhau giữa shape của tên lửa và viên đạn
                if(!area.isEmpty()){
                    // khu vực giao nhau không trống => có va chạm
                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                    // khi có va chạm, thêm effect vào boomEffect
                    if(!rocket.updateHP(bullet.getSize())) {
                    // Gọi updateHP() trên tên lửa để giảm HP của tên lửa dựa trên kích thước viên đạn
                        score++;
                        // tăng điểm
                        rockets.remove(rocket);
                        // xoá tên lửa khỏi danh sách
                        sound.soundDestroy();
                        // thêm âm thanh phá huỷ

                        double x = rocket.getX() + Rocket.ROCKET_SITE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SITE / 2;
                        // tính toạ độ trung tâm của tên lửa để đặt hiệu ứng nổ tại đó

                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                        // Thêm một loạt các hiệu ứng nổ với kích thước, màu sắc và độ trong suốt khác nhau tại vị trí trung tâm của tên lửa, tạo ra hiệu ứng nổ đa dạng

                    } else {
                        sound.soundHit();
                        // thêm âm thanh va chạm nếu rocket chưa bị phá huỷ
                    }
                    bullets.remove(bullet);
                    // xoá bỏ đạn khỏi danh sách sau khi nó va chạm
                }
            }
        }

        if(boss != null && bossActive) { // kiểm tra boss có tồn tại không?
            Area area2 = new Area(bullet.getShape());
            // gọi area từ hình dạng viên đạn
            area2.intersect(boss.getShape());
            // tìm giao nhau giữa shape của boss và viên đạn
            if (!area2.isEmpty()) {
                // khu vực giao nhau không trống => có va chạm
                boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                // khi có va chạm, thêm effect vào boomEffect
                if (!boss.updateHP(bullet.getSize())) {
                    // Gọi updateHP() trên boss để giảm HP của boss dựa trên kích thước viên đạn
                    bossActive = false;
                    sound.soundDestroy();
                    // thêm âm thanh phá huỷ

                    double x = boss.getX() + Boss.BOSS_SITE / 2;
                    double y = boss.getY() + Boss.BOSS_SITE / 2;
                    // tính toạ độ trung tâm của tên lửa để đặt hiệu ứng nổ tại đó

                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    // Thêm một loạt các hiệu ứng nổ với kích thước, màu sắc và độ trong suốt khác nhau tại vị trí trung tâm của tên lửa, tạo ra hiệu ứng nổ đa dạng

                } else {
                    sound.soundHit();
                    // thêm âm thanh va chạm nếu rocket chưa bị phá huỷ
                }
                bullets.remove(bullet);
                // xoá bỏ đạn khỏi danh sách sau khi nó va chạm
            }
        }

    }
    private void checkBossBullet(BossBullet bossBullet) {
        //Player player = new Player();
        if(player.isAlive()) { // kiểm tra player có tồn tại không?
            Area area = new Area(bossBullet.getShape());
            // gọi area từ hình dạng viên đạn
            area.intersect(player.getShape());
            // tìm giao nhau giữa shape của player và viên đạn
            if (!area.isEmpty()) {
                // khu vực giao nhau không trống => có va chạm
                boomEffects.add(new Effect(bossBullet.getCenterX(), bossBullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                // khi có va chạm, thêm effect vào boomEffect
                if (!player.updateHP(bossBullet.getSize())) {
                    // Gọi updateHP() trên player để giảm HP của player dựa trên kích thước viên đạn
                    player.setAlive(false);
                    sound.soundDestroy();
                    // thêm âm thanh phá huỷ

                    double x = player.getX() + player.PLAYER_SITE / 2;
                    double y = player.getY() + player.PLAYER_SITE / 2;
                    // tính toạ độ trung tâm của player để đặt hiệu ứng nổ tại đó

                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    // Thêm một loạt các hiệu ứng nổ với kích thước, màu sắc và độ trong suốt khác nhau tại vị trí trung tâm của tên lửa, tạo ra hiệu ứng nổ đa dạng

                } else {
                    sound.soundHit();
                    // thêm âm thanh va chạm nếu rocket chưa bị phá huỷ
                }
                bossBullets.remove(bossBullet);
                // xoá bỏ đạn khỏi danh sách sau khi nó va chạm
            }
        }
    }
    private void checkPlayer(Rocket rocket) {
        if(rocket != null) {
            Area area = new Area(player.getShape());
            area.intersect(rocket.getShape());
            // tìm giao nhau giữa các shape của player và rocket
            if (!area.isEmpty()) {
                // khu vực giao không rỗng => có va chạm
                double rocketHp = rocket.getHP();
                if (!rocket.updateHP(player.getHP())) {
                // Lấy HP hiện tại của tên lửa và cập nhật HP của tên lửa bằng cách trừ đi HP của player
                    rockets.remove(rocket);
                    // loại có tên lửa nếu có va chạm
                    sound.soundDestroy();
                    // thêm âm thanh phá huỷ

                    double x = rocket.getX() + Rocket.ROCKET_SITE / 2;
                    double y = rocket.getY() + Rocket.ROCKET_SITE / 2;
                    // tính toạ độ trung tâm tên lửa để đặt vị trí nổ

                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    // Tạo thêm nhiều hiệu ứng nổ đa dạng về kích thước, độ trong suốt, và màu sắc

                }
                if (!player.updateHP(rocketHp)) {
                // updateHP() cho player, trừ đi HP của player bằng HP của tên lửa
                    player.setAlive(false);
                    // nếu hết HP, trạng thái sống của player trả về false
                    sound.soundDestroy();
                    // thêm âm thanh phá huỷ

                    double x = player.getX() + Player.PLAYER_SITE / 2;
                    double y = player.getY() + Player.PLAYER_SITE / 2;
                    // tính toạ độ trung tâm player để đặt vị trí nổ

                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    // Tạo thêm nhiều hiệu ứng nổ đa dạng về kích thước, độ trong suốt, và màu sắc
                }

            }
        }
    }

    private void drawBackground() {
        g2.setColor(new Color(30, 30, 30));  // MÀU BACKGROUND
        g2.fillRect(0, 0, width, height);
    }

    private void drawGame() {
        if(player.isAlive()){   // HIỂN THỊ MÁY BAY NGƯỜI CHƠI
            player.draw(g2);
        }
        for(int i = 0; i < bullets.size(); i++){  // HIỂN THỊ ĐẠN BẮN RA
            Bullet bullet = bullets.get(i);
            if(bullet != null) {
                bullet.draw(g2);
            }
        }
        for(int i = 0; i < bossBullets.size(); i++) {
            BossBullet bossBullet = bossBullets.get(i);
            if(bossBullet != null) {
                bossBullet.draw(g2);
            }
        }
        for(int i = 0; i < rockets.size(); i++) {  // HIỂN THỊ TÊN LỬA
            Rocket rocket = rockets.get(i);
            if(rocket != null){
                rocket.draw(g2);
            }
        }
        for(int i = 0; i < boomEffects.size(); i++) {    // HIỂN THỊ NỔ TUNG
            Effect boomEffect = boomEffects.get(i);
            if(boomEffect != null) {
                boomEffect.draw(g2);
            }
        }
        if (bossActive) {       // nếu trạng thái của boss = true, hiển thị boss
            boss.draw(g2);
        }

        g2.setColor(Color.LIGHT_GRAY);  // Màu chữ
        g2.setFont(getFont().deriveFont(Font.BOLD, 25f)); // Kích cỡ chữ Score trên screen
//        g2.drawString("TIME: " + dFormat.format(playTime), width - 150, 25);
        if(player.isAlive() && !bossActive && score > 3) {
            for(int i = 0; i < rockets.size(); i++){
                Rocket rocket = rockets.get(i);
                rockets.remove(rocket);
                // loại có tên lửa nếu có va chạm
                sound.soundDestroy();
                // thêm âm thanh phá huỷ

                double x1 = rocket.getX() + Rocket.ROCKET_SITE / 2;
                double y1 = rocket.getY() + Rocket.ROCKET_SITE / 2;
                // tính toạ độ trung tâm tên lửa để đặt vị trí nổ

                boomEffects.add(new Effect(x1, y1, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                boomEffects.add(new Effect(x1, y1, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                boomEffects.add(new Effect(x1, y1, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                boomEffects.add(new Effect(x1, y1, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                boomEffects.add(new Effect(x1, y1, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                // Tạo thêm nhiều hiệu ứng nổ đa dạng về kích thước, độ trong suốt, và màu sắc

            }
            String win = "YOU WIN";
            String point = "SCORE: " + score;
            String textKey = "Enter để tiếp tục";

            g2.setFont(getFont().deriveFont(Font.BOLD, 50f));   //FONT CHỮ ĐẬM VÀ KÍCH THƯỚC 50
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(win, g2);
            double textWidth = r2.getWidth();
            double textHeight = r2.getHeight();
            // trả về rectangle2d chứ chiều cao, chiều rộng của text giúp căn giữa screen
            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;
            g2.drawString(win, (int) x, (int) y + fm.getAscent());
            // Tính toán vị trí x, y để căn giữa chuỗi text trên màn hình, rồi vẽ thông báo "You win" tại vị trí được xác định

            g2.setFont(getFont().deriveFont(Font.BOLD, 30f));   // FONT CHỮ ĐẬM VÀ KÍCH THƯỚC 20
            fm = g2.getFontMetrics();   // đo kích thước của text
            r2 = fm.getStringBounds(point, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            // trả về RECTANGLE2D chứa CHIỀU CAO, CHIỀU RỘNG CỦA text giúp căn giữa SCREEN
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(point, (int) x, (int) y + fm.getAscent() + 60);
            // VỊ TRÍ TRÊN SCREEN ngay dưới you win

            g2.setFont(getFont().deriveFont(Font.BOLD, 20f));   // FONT CHỮ ĐẬM VÀ KÍCH THƯỚC 20
            fm = g2.getFontMetrics();   // đo kích thước của text
            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            // trả về RECTANGLE2D chứa CHIỀU CAO, CHIỀU RỘNG CỦA text giúp căn giữa SCREEN
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey, (int) x, (int) y + fm.getAscent() + 100);
            // VỊ TRÍ TRÊN SCREEN ngay dưới Score

        }
        else {
            g2.drawString("SCORE: " + score, 10, 25);   // VỊ TRÍ TRÊN SCREEN

        }
        if(!player.isAlive()) {
        // nếu người chơi chết => game over
            String text = "GAME OVER";
            String textKey = "Enter để tiếp tục";
            g2.setFont(getFont().deriveFont(Font.BOLD, 50f));  // FONT CHỮ ĐẬM VÀ KÍCH CỠ 50 TRÊN SCREEN
            FontMetrics fm = g2.getFontMetrics();   // ĐO KÍCH THƯỚC CỦA STRING

            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double textWidth = r2.getWidth();
            double textHeight = r2.getHeight();
            // trả về RECTANGLE2D chứa CHIỀU CAO, CHIỀU RỘNG CỦA text giúp căn giữa SCREEN

            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;
            g2.drawString(text, (int) x, (int) y + fm.getAscent());
            // Tính toán vị trí x, y để căn giữa chuỗi text trên màn hình, rồi vẽ thông báo "GAME OVER" tại vị trí được xác định

            g2.setFont(getFont().deriveFont(Font.BOLD, 20f));   // FONT CHỮ ĐẬM VÀ KÍCH THƯỚC 20
            fm = g2.getFontMetrics();   // đo kích thước của text

            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            // trả về RECTANGLE2D chứa CHIỀU CAO, CHIỀU RỘNG CỦA text giúp căn giữa SCREEN

            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey, (int) x, (int) y + fm.getAscent() + 50);
            // VỊ TRÍ TRÊN SCREEN ngay dưới GAMEOVER
        }
    }
    private void render() {
        Graphics g = getGraphics();
        // Lấy 'Graphics' để vẽ lên màn hình hiển thị chính của chương trình
        g.drawImage(image, 0, 0, null);
        // Vẽ toàn bộ 'image' đã được cập nhật trong bộ nhớ vào màn hình tại vị trí (0, 0). image chứa tất cả các đối tượng game đã được vẽ trước đó (như nhân vật, đạn, tên lửa)
        g.dispose();
        // Giải phóng tài nguyên của đối tượng 'Graphics' sau khi vẽ để tiết kiệm bộ nhớ
    }

    public void sleep(long speed) {
        try {
            Thread.sleep(speed);
            // tạm dừng luồng đang chạy trong 1 khoảng thời gian để giữ ổn định tốc độ khung hình

        }catch (InterruptedException ex){
            System.err.println(ex);
            // trong trường hợp luồng bị gián đoạn khi đang tạm dừng, in ra thông báo lỗi
        }
    }

}
