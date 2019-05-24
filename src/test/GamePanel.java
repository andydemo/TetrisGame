package test;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

public class GamePanel extends JPanel {//客户端画面

    private DataOutputStream dos;//数据发送

    private int blockType, lastBlockType = 0;//方块类型
    private int turnState, lastTurnState = 0;//旋转状态
    private int x;//当前方块的坐标
    private int y;//当前方块的坐标
    private int map[][][] = new int[2][13][23];//地图：12列22行。为防止越界，数组开成：13列23行
    private int delay;//游戏方块下落速度
    private int score = 0;//分数

    GamePanel(DataOutputStream dos) {
        this.dos = dos;

        new Thread(() -> {
            //
        }).start();
    }

    public void sendMsg(int... parm) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        for (int i : parm) {
            methodName += "_" + i;
        }
        try {
            dos.writeUTF(methodName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //开始游戏
    public void newGame() {
        for (int i = 0; i < 12; i++) {//走列
            for (int j = 0; j < 21; j++) {//走行
                if (i == 0 || i == 11) {//3为界面边框的格
                    map[0][i][j] = -1;//
                } else {
                    map[0][i][j] = 0;
                }
            }
            map[0][i][21] = -1;
        }
        delay = 1000;
    }

    //决定下一方块
    private void nextBlock() {
        blockType = lastBlockType;
        turnState = lastTurnState;
//        try {
//            dos.writeInt((blockType + 1) * 10 + turnState);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        lastBlockType = (int) (Math.random() * 1000) % 7;
        lastTurnState = (int) (Math.random() * 1000) % 4;
        x = 4;
        y = 0;
        if (crash(x, y, blockType, turnState) == 0) {
//            timer.stop();
//            try {
//                dos.writeInt(190);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            int option = JOptionPane.showConfirmDialog(null, "再接再厉吧！！！");
            if (option == JOptionPane.OK_OPTION) {
                System.exit(0);
            } else if (option == JOptionPane.NO_OPTION) {
                System.exit(0);
            } else if (option == JOptionPane.CANCEL_OPTION) {
                System.exit(0);
            }
        }
    }

    //键盘操作

    public void down() {
        if (crash(x, y + 1, blockType, turnState) == 0) {
            add(x, y, blockType, turnState);
            nextBlock();
        } else {
            y++;
        }


        repaint();
    }

    public void left() {
        if (x >= 0) {
            x -= crash(x - 1, y, blockType, turnState);
        }

        repaint();
    }

    public void right() {
        if (x < 8) {
            x += crash(x + 1, y, blockType, turnState);
        }
        repaint();
    }

    public void turn() {
        if (crash(x, y, blockType, (turnState + 1) % 4) == 1) {
            turnState = (turnState + 1) % 4;
        }


        repaint();
    }

    public void Stop() {

//        try {
//            dos.writeInt(196);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (STOP == 1) {
//            STOP = 0;
//            timer.stop();
//        } else {
//            STOP = 1;
//            timer.start();
//        }
    }

    private void add(int x, int y, int blockType, int turnState) {
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                if (Constant.shapes[blockType][turnState][a * 4 + b] == 1) {
                    map[0][x + b + 1][y + a] = 1;
                    map[1][x + b + 1][y + a] = blockType;
                }
            }
        }
        tryDelLine();
    }

    //消行
    private void tryDelLine() {
        int c;
        for (int b = 0; b < 21; b++) {
            c = 0;
            for (int a = 1; a <= 10; a++) {
                c += map[0][a][b];
            }
            if (c == 10) {
//                try {
//                    dos.writeInt(100 + b);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                for (int d = b; d > 0; d--) {
                    for (int e = 0; e < 11; e++) {
                        map[0][e][d] = map[0][e][d - 1];
                        map[1][e][d] = map[1][e][d - 1];
                    }
                }
                score += 100;
//                try {
//                    dos.writeInt(200 + score);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                if (score == 3000) {
//                    try {
//                        dos.writeInt(195);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                delay -= 30;//每消除一行增加难度
//                timer.setDelay(delay);
            }
        }

    }

    //判断是否发生碰撞
    private int crash(int x, int y, int blockType, int turnState) {
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                if ((Constant.shapes[blockType][turnState][a * 4 + b] & map[0][x + b + 1][y + a]) == 1) {
                    return 0;//碰撞
                }
            }
        }
        return 1;//没有碰撞
    }

    //画图
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(153, 51, 205));
        for (int j = 0; j < 16; j++) {
            if (Constant.shapes[blockType][turnState][j] == 1) {
                g.setColor(Color.pink);
                g.drawRect((j % 4 + x + 1) * 30, (j / 4 + y) * 30, 30, 30);
                g.setColor(Color.BLUE);
                g.fillRect((j % 4 + x + 1) * 30 + 1, (j / 4 + y) * 30 + 1, 29, 29);
            }
        }
        for (int i = 0; i < 12; i++) {//走列
            for (int j = 0; j < 22; j++) {//走行
                if (map[0][i][j] == -1) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(i * 30, j * 30, 30, 30);
                } else if (map[0][i][j] == 1) {

                    if (map[1][i][j] == 0) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.red);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                    if (map[1][i][j] == 1) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.orange);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                    if (map[1][i][j] == 2) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.yellow);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                    if (map[1][i][j] == 3) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.green);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                    if (map[1][i][j] == 4) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.blue);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                    if (map[1][i][j] == 5) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.magenta);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                    if (map[1][i][j] == 6) {
                        g.setColor(Color.pink);
                        g.drawRect(i * 30, j * 30, 30, 30);
                        g.setColor(Color.gray);
                        g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                    }
                }
            }
            for (int m = 0; m < 16; m++) {
                if (Constant.shapes[lastBlockType][lastTurnState][m] == 1) {
                    g.setColor(Color.pink);
                    g.drawRect((m % 4) * 25 + 450, (m / 4) * 25 + 80, 25, 25);
                    g.setColor(Color.GREEN);
                    g.fillRect((m % 4) * 25 + 451, (m / 4) * 25 + 81, 24, 24);
                }
            }
        }
        g.setColor(Color.blue);
        g.setFont(new Font("宋体", Font.PLAIN, 15));
        g.drawString("游戏已启动，可按空格键暂停或者继续", 376, 470);
        g.setFont(new Font("宋体", Font.PLAIN, 25));
        g.setColor(Color.RED);
        g.drawString("请先完成3000分", 405, 210);
        g.drawString("您的分数是", 435, 270);
        g.drawString("score=" + score, 450, 300);
        g.drawString("您对手的分数是", 405, 370);
//        g.drawString("score=" + score1, 450, 400);
        g.setFont(new Font("宋体", Font.PLAIN, 30));
        g.setColor(Color.BLACK);
        g.drawString("下一个方块是", 405, 40);
    }
}