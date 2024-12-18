package doancanhann;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PlaneShootingGame extends JPanel implements ActionListener, KeyListener {

    private final int WIDTH = 800;//chiều rộng của khu vực trò chơi
    private final int HEIGHT = 600;//chiều cao của khu vực trò chơi
    private int spaceshipX, spaceshipY;//lưu trữ tọa độ x và y của tàu vũ trụ trong trò chơi
    private final ArrayList<int[]> bullets;//lưu trữ các viên đạn
    private final ArrayList<int[]> targets;// lưu trữ các mục tiêu
    private final Timer timer;//quản lý thời gian trong trò chơi.
    private final Random random;//tạo số ngẫu nhiên,tạo ra các mục tiêu,hành động ngẫu nhiên.
    private boolean gameOver;//kiểm tra trạng thái trò chơi

    public PlaneShootingGame() {
        spaceshipX = WIDTH / 2; //đặt tàu vũ trụ ở giữa màn hình theo trục X
        spaceshipY = HEIGHT - 60; // đặt tàu vũ trụ cách đáy màn hình 60 pixel
        bullets = new ArrayList<>(); // khởi tạo danh sách chứa viên đạn
        targets = new ArrayList<>(); // khởi tạo danh sách chứa mục tiêu
        random = new Random();//tạo đối tượng để sinh số ngẫu nhiên
        gameOver = false;//đặt trạng thái trò chơi là đang diễn ra

        // Đặt mục tiêu ban đầu
        for (int i = 0; i < 5; i++) {
            targets.add(new int[]{random.nextInt(WIDTH - 50), random.nextInt(300)});
        }

        timer = new Timer(20, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);  
        if (gameOver) {        //kiểm tra xem trò chơi cớ kết thúc hay không
            g.setColor(Color.RED); //đặt màu vẽ là đỏ
            g.setFont(g.getFont().deriveFont(30f)); //đặt kích thước phông chữ
            g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2); //vẽ dòng chữ game over ở giữa màng hình
            g.drawString("Press E to Restart", WIDTH / 2 - 150, HEIGHT / 2 + 40);
        } else {
            g.setColor(Color.BLUE);  //đặt màu vẽ là xanh dương
            g.fillRect(spaceshipX, spaceshipY, 50, 50);//Vẽ hình vuông đại diện cho tàu vũ trụ.

            for (int[] bullet : bullets) {
                g.setColor(Color.RED);//Đặt màu cho viên đạn là đỏ
                g.fillRect(bullet[0], bullet[1], 5, 10);
            }

            for (int[] target : targets) {
                g.setColor(Color.GREEN);
                g.fillRect(target[0], target[1], 50, 50); //Vẽ mục tiêu ở tọa độ tương ứng với kích thước 50x50 pixel
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            for (int i = 0; i < bullets.size(); i++) {
                int[] bullet = bullets.get(i);
                bullet[1] -= 5; // Đạn di chuyển lên

                if (bullet[1] < 0) {
                    bullets.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < targets.size(); i++) {
                int[] target = targets.get(i);
                target[1] += 1; // Mục tiêu di chuyển xuống

                if (target[1] > HEIGHT) {
                    targets.remove(i);
                    targets.add(new int[]{random.nextInt(WIDTH - 50), 0});
                }
            }

            checkCollision();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameOver && key == KeyEvent.VK_E) {
            resetGame();
        } else if (!gameOver) {
            if (key == KeyEvent.VK_LEFT && spaceshipX > 0) {
                spaceshipX -= 5;
            }

            if (key == KeyEvent.VK_RIGHT && spaceshipX < WIDTH - 50) {
                spaceshipX += 5;
            }

            if (key == KeyEvent.VK_SPACE) {
                bullets.add(new int[]{spaceshipX + 20, spaceshipY});
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void resetGame() {
        spaceshipX = WIDTH / 2;
        spaceshipY = HEIGHT - 60;
        bullets.clear();
        targets.clear();

        // Đặt lại mục tiêu ban đầu
        for (int i = 0; i < 5; i++) {
            targets.add(new int[]{random.nextInt(WIDTH - 50), random.nextInt(300)});
        }

        gameOver = false;
        repaint();
    }

    private void checkCollision() {
        for (int i = 0; i < targets.size(); i++) {
            int[] target = targets.get(i);

            if (spaceshipX < target[0] + 50 && spaceshipX + 50 > target[0] && spaceshipY < target[1] + 50 && spaceshipY + 50 > target[1]) {
                gameOver = true;
                return;
            }

            for (int j = 0; j < bullets.size(); j++) {
                int[] bullet = bullets.get(j);

                if (bullet[0] >= target[0] && bullet[0] <= target[0] + 50 && bullet[1] >= target[1] && bullet[1] <= target[1] + 50) {
                    targets.remove(i);
                    targets.add(new int[]{random.nextInt(WIDTH - 50), 0});
                    bullets.remove(j);
                    j--;
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        PlaneShootingGame game = new PlaneShootingGame();
        frame.setBounds(10, 10, 800, 600);
        frame.setTitle("Plane Shooting Game");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
    }
}
