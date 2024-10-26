package game.obj.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Sound {

    private final URL shoot;
    private final URL hit;
    private final URL destroy;
    // lưu đường dẫn đến các tệp âm thanh

    public Sound() {
        this.shoot = this.getClass().getResource("/game/obj/sound/shoot.wav");
        this.hit = this.getClass().getResource("/game/obj/sound/hit.wav");
        this.destroy = this.getClass().getResource("/game/obj/sound/destroy.wav");
        // lấy đường dẫn tới các tệp âm thanh và gán đường dẫn cho các giá trị shoot, hit, destroy
    }

    public void soundShoot() {
        play(shoot);
    }

    public void soundHit() {
        play(hit);
    }

    public void soundDestroy() {
        play(destroy);
    }

    private void play(URL url) {
    // phương thức chính thực hiện việc phát âm thanh từ URL được truyền vào
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            //  tạo một 'AudioInputStream' từ tệp âm thanh chỉ định bởi URL. Đây là luồng dữ liệu âm thanh để phát
            Clip clip = AudioSystem.getClip();
            // đại diện cho một đoạn âm thanh có thể phát nhiều lần
            clip.open(audioIn);
            // mở clip với dữ liệu từ audioIn để chuẩn bị phát âm thanh
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    // theo dõi trạng thái của clip
                    if(event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        //  khi âm thanh kết thúc, cập nhật đóng clip để giải phóng tài nguyên
                    }
                }
            });
            audioIn.close();
            clip.start();
            // phát âm thanh ngay lập tức
        }catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            // IOException: Lỗi xảy ra khi không thể truy cập dữ liệu của tệp âm thanh.
            // LineUnavailableException: Lỗi xảy ra khi clip không thể lấy tài nguyên âm thanh cần thiết.
            // UnsupportedAudioFileException: Lỗi xảy ra nếu tệp âm thanh không được hỗ trợ.
            System.err.println(e);
            // in thông báo lỗi nếu có
        }
    }
}
