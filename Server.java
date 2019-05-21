import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import javax.swing.*;

public class Server extends JFrame {
 
 public static void main(String[] args) {
	 new Server().setVisible(true);
}

 public Server() {//�ͻ���
 host="localhost";//һ̨�������о�Ϊ����ip
 //host="10.114.11.4";
 new bmy().start();//���пͻ��ˣ����ӷ����������������з����������߳�����
 this.setLayout(null);
 this.setDefaultCloseOperation(EXIT_ON_CLOSE);
 this.setSize(1100, 750);
 this.setLocationRelativeTo(null);
 this.setResizable(false);
 this.requestFocus(true);
 this.setVisible(true); 
 TetrisPanel tp = new TetrisPanel();//��Ӳٿر�����Ϸ���
 add(tp);
 tp.setBounds(50, 0, 800, 800);
 TetrisPanel1 tq = new TetrisPanel1();//��ӻ�ԭ�Է��������
 tq.setBounds(690, 0, 800, 800);
 add(tq);
 this.addKeyListener( tp.listener );//���̼���
 while(true){
	    tq.repaint();}//���̻߳�ͼ
 }
 
 
 
 private  int message,i,j,xx=4,yy=0,score1,STOP=1;//���ݡ����ꡢ��������ͣ
 private Timer timer;//ʱ����
 private  String host;//������ip��ַ
 private  Socket socket,socket1;//���ӿͻ���
 private  DataInputStream dis;//���ݽ���
 private  DataOutputStream dos;//���ݴ���
 int[][][] map1=new int[2][22][12];��ͼ
 private int blockType1;//��������
 private int turnState1;//��ת״̬
 int shapes[][][] = new int[][][] {//����˹��������
     { { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
       { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 } },
      // S
      { { 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
       { 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0 } },
      // Z
      { { 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 } },
      // J
      { { 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
       { 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
      // O
      { { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
      // L
      { { 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
       { 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
      // T
      { { 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
       { 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
       { 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }}
       };
 class bmy extends Thread {//���ݽ���

  public void run() {
     try {
     newgame();
     socket= new Socket(host,8888);
     dis = new DataInputStream(socket.getInputStream());
        while(true){
        message=dis.readInt();
        if(message==1){
           yy++;
          }
          else if(message==2){
           xx--;
          }
          else if(message==3){
           xx++;
          }
          else if(message==4){
           turnState1 = (turnState1+1)%4;
          }
          else if(message==5){
            for( int a=0; a<4; a++){
             for(int b=0; b<4; b++){
              if( shapes[blockType1][turnState1][a*4+b] ==1 ){
              map1[0][yy+a][xx+b+1] = 1;
              map1[1][yy+a][xx+b+1] = blockType1;
              }
             }
             }
         }
          else if(9<message&message<80){
           xx=4;yy=0;
           blockType1=message/10-1;
           turnState1=message%10;
           }
          else if(150>message&&message>100){
           message=message-100;
           for(int i=message; i>0; i--){
              for(int j=1;j<11;j++){
               
               map1[0][i][j] = map1[0][i-1][j];
               map1[1][i][j] = map1[1][i-1][j];
              }
              }
          }
          else if(message==190){
        	  timer.stop();
        	int option = JOptionPane.showConfirmDialog(null,"��ϲ��Ӯ��");
        			  if (option == JOptionPane.OK_OPTION) {
        				  System.exit(0);
        			  } else if (option == JOptionPane.NO_OPTION) {
           			   System.exit(0);
           			  }
        			  else if (option == JOptionPane.CANCEL_OPTION) {
           			   System.exit(0);
           			  }
          }
          else if(message==195){
        	  timer.stop();
        	int option = JOptionPane.showConfirmDialog(null,"���ֱ�����Ӯһ��");
        			  if (option == JOptionPane.OK_OPTION) {
        				  System.exit(0);
        			  } else if (option == JOptionPane.NO_OPTION) {
           			   System.exit(0);
           			  }
        			  else if (option == JOptionPane.CANCEL_OPTION) {
           			   System.exit(0);
           			  }
          }
        
          else if(message==196){
        	  if(STOP==1){
 				 STOP=0;
 				 timer.stop();
 			 }
 			 else{
 				 STOP=1;
 				 timer.start();
 			 }
          }
          else if(message>200){
        	  score1=message-200;
          }
        }
    }
  
    catch (IOException e) {
    e.printStackTrace();}
 }
  public void newgame(){//��Ϸ��ʼ��
      for( i=0;i<22;i++){
     for(j=0;j<12;j++){
      if(j==0 || j==11){//-1Ϊ����߿�ĸ�
      map1[0][i][j]=-1;}
      else{
      map1[0][i][j]=0;}
      map1[0][21][j]=-1;
     }
     
     }
 }
 }


 class TetrisPanel1 extends JPanel{//����������



  
  
  public void paint(Graphics g) {//��ͼ
      super.paint(g);
      for(j=0;j<16;j++){
         if(shapes[blockType1][turnState1][j]==1){
          g.setColor(Color.pink);
          g.drawRect((j%4+xx+1)*30, (j/4+yy)*30, 30, 30);
          g.setColor(Color.blue);
          g.fillRect((j%4+xx+1)*30+1, (j/4+yy)*30+1, 29, 29);
         }
         }
      for( i=0;i<22;i++){
      for(j=0;j<12;j++){
      if(map1[0][i][j]==-1){
       g.setColor(Color.LIGHT_GRAY);
        g.fillRect(j*30, i*30, 30, 30);
      }
      if(map1[0][i][j]==1){
       if(map1[1][i][j]==0){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.red);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
       if(map1[1][i][j]==1){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.orange);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
       if(map1[1][i][j]==2){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.yellow);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
       if(map1[1][i][j]==3){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.green);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
       if(map1[1][i][j]==4){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.blue);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
       if(map1[1][i][j]==5){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.magenta);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
       if(map1[1][i][j]==6){
        g.setColor(Color.pink);
        g.drawRect(j*30, i*30, 30, 30);
        g.setColor(Color.gray);
        g.fillRect(j*30+1, i*30+1, 29, 29);}
      }
      }
      }
  }
  }


 
 class TetrisPanel extends JPanel{
private int blockType,lastblockType=0;//��������
 private int turnState,lastturnState=0;//��ת״̬
 private int x;
 private int y;
 private int map[][][]=new int[2][13][23];//��ͼ��12��22�С�Ϊ��ֹԽ�磬���鿪�ɣ�13��23��
 private int delay=1000;
 public TimerKeyLister listener=new TimerKeyLister(); //���̼���
 private int score=0;//����

 
 //��Ϸ��ʼ��
 public TetrisPanel(){
	 try {
		socket1=new Socket(host,7777);
		dos = new DataOutputStream(socket1.getOutputStream());
	} catch (IOException e) {
		e.printStackTrace();}
  newGame();
  nextBlock();
 }
 
 //��ʼ��Ϸ
 public void newGame() {
  for( int i=0;i<12;i++){//����
  for(int j=0;j<21;j++){//����
   if(i==0 || i==11){//3Ϊ����߿�ĸ�
   map[0][i][j]=-1;//
   }else{
   map[0][i][j]=0;
   }
  }
  map[0][i][21]=-1;
  }
  delay=1000;
  timer = new Timer(delay,listener);
  timer.start();
 }
 
 //������һ����
 private void nextBlock() {
	  blockType = lastblockType;
	  turnState = lastturnState;
	  try {
			dos.writeInt((blockType+1)*10+turnState);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  lastblockType = (int)(Math.random()*1000)%7;
	  lastturnState = (int)(Math.random()*1000)%4;
	  x=4;
	  y=0;
	  if(crash(x,y,blockType,turnState)==0){
	  timer.stop();
	  try {
			dos.writeInt(190);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  int option = JOptionPane.showConfirmDialog(this,"Game Over!!����������...");
	  if (option == JOptionPane.OK_OPTION) {
	   newGame();
	  } else if (option == JOptionPane.NO_OPTION) {
	   System.exit(0);
	  }
	  }
	 }
	  
	 //���̲���

	 private void down(){
		    try {
				dos.writeInt(1*crash(x,y+1,blockType,turnState));
			} catch (IOException e) {
				e.printStackTrace();
			}
		    if( crash(x,y+1,blockType,turnState)==0 ){
		  add(x,y,blockType,turnState);
		  nextBlock();
		  }else{
		  y++;
		  }


		  repaint();
		 }
		 private void left() {
		    try {
				dos.writeInt(2*crash(x-1,y,blockType,turnState));
			} catch (IOException e) {
				e.printStackTrace();
			}
		    if(x>=0){
		  x -= crash(x-1,y,blockType,turnState);
		  }

		repaint();
		 }
		 private void right() {
		    try {
				dos.writeInt(3*crash(x+1,y,blockType,turnState));
			} catch (IOException e) {
				e.printStackTrace();
			}
		    if(x<8){
		  x += crash(x+1,y,blockType,turnState);
		  }

		repaint();
		 }
		 private void turn() {
		  try {
				dos.writeInt(4*crash(x,y,blockType,(turnState+1)%4));
			} catch (IOException e) {
				e.printStackTrace();
			}
		  if(crash(x,y,blockType,(turnState+1)%4)==1 ){
		  turnState = (turnState+1)%4;
		  }
		  

		  repaint();
		 }
 private void Stop() {
			 
			 try {
					dos.writeInt(196);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 if(STOP==1){
				 STOP=0;
				 timer.stop();
			 }
			 else{
				 STOP=1;
				 timer.start();
			 }
		 }
	 
	 private void add(int x, int y, int blockType, int turnState) {
	  for( int a=0; a<4; a++){
	  for(int b=0; b<4; b++){
	   if( shapes[blockType][turnState][a*4+b] ==1 ){
	   map[0][x+b+1][y+a] = 1;
	   map[1][x+b+1][y+a] = blockType;
	   }
	  }
	  }
	  try {
			dos.writeInt(5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  tryDelLine();
	 }
	 
	 //����
	 private void tryDelLine(){
	  int c;
	  for(int b=0;b<21;b++){
	  c=0;
	  for(int a=1;a<=10;a++){
	   c += map[0][a][b];
	  }
	  if(c==10){
		  try {
				dos.writeInt(100+b);
			} catch (IOException e) {
				e.printStackTrace();
			}
	   
	   for(int d=b; d>0; d--){
	   for(int e=0;e<11;e++){
	    map[0][e][d] = map[0][e][d-1];
	    map[1][e][d] = map[1][e][d-1];
	   }
	   }
	   score +=100;
	   try {
			dos.writeInt(200+score);
		} catch (IOException e) {
			e.printStackTrace();
		}
	   if(score==3000){
		   try {
				dos.writeInt(195);
			} catch (IOException e) {
				e.printStackTrace();
			}
	   }
	   delay -=30;//ÿ����һ�������Ѷ�
	   timer.setDelay(delay);
	  }
	  }
	 
	 }
 
 //�ж��Ƿ�����ײ
 private int crash(int x, int y, int blockType, int turnState){
  for( int a=0; a<4; a++){
  for(int b=0; b<4; b++){
  if( (shapes[blockType][turnState][a*4+b] & map[0][x+b+1][y+a]) ==1 ){
   return 0;//��ײ
   }
  }
  }
  return 1;//û����ײ
 }
 
 //��ͼ
 public void paint(Graphics g) {
  super.paint(g);
  g.setColor(new Color(153,51,205));
  for(int j=0;j<16;j++){
  if(shapes[blockType][turnState][j]==1){
   g.setColor(Color.pink);
   g.drawRect((j%4+x+1)*30, (j/4+y)*30, 30, 30);
   g.setColor(Color.BLUE);
   g.fillRect((j%4+x+1)*30+1, (j/4+y)*30+1, 29, 29);
  }
  }
  for( int i=0;i<12;i++){//����
  for(int j=0;j<22;j++){//����
   if(map[0][i][j]==-1){
   g.setColor(Color.LIGHT_GRAY);
   g.fillRect(i*30, j*30, 30, 30);
   }else if(map[0][i][j]==1){
    
    if(map[1][i][j]==0){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.red);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
    if(map[1][i][j]==1){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.orange);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
    if(map[1][i][j]==2){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.yellow);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
    if(map[1][i][j]==3){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.green);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
    if(map[1][i][j]==4){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.blue);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
    if(map[1][i][j]==5){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.magenta);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
    if(map[1][i][j]==6){
     g.setColor(Color.pink);
     g.drawRect(i*30, j*30, 30, 30);
     g.setColor(Color.gray);
     g.fillRect(i*30+1, j*30+1, 29, 29);}
   }
  }
  for(int m=0;m<16;m++){
   if(shapes[lastblockType][lastturnState][m]==1){
    g.setColor(Color.pink);
    g.drawRect((m%4)*25+450, (m/4)*25+80, 25, 25);
    g.setColor(Color.GREEN);
    g.fillRect((m%4)*25+451, (m/4)*25+81, 24, 24);
   }
  }
  }
	g.setColor(Color.blue);
	g.setFont(new Font("����", Font.PLAIN, 15));
    g.drawString("��Ϸ���������ɰ��ո����ͣ���߼���", 376, 470);
    g.setFont(new Font("����", Font.PLAIN, 25));
    g.setColor(Color.RED);
    g.drawString("�������3000��", 405, 210);
    g.drawString("���ķ�����", 435, 270);
    g.drawString("score=" + score, 450, 300);
    g.drawString("�����ֵķ�����", 405, 370);
    g.drawString("score=" + score1, 450, 400);
    g.setFont(new Font("����", Font.PLAIN, 30));
    g.setColor(Color.BLACK);
    g.drawString("��һ��������", 405, 40);}
 
 class TimerKeyLister extends KeyAdapter implements ActionListener{
  public void actionPerformed(ActionEvent e) {
  down();
  }
  public void keyPressed(KeyEvent e) {

  switch(e.getKeyCode()){
   case KeyEvent.VK_DOWN:
   down();break;
   case KeyEvent.VK_LEFT:
   left();break;
   case KeyEvent.VK_RIGHT:
   right();break;
   case KeyEvent.VK_UP:
   turn();break;
   case KeyEvent.VK_SPACE:
   Stop();break;
  }
   
  }
  }
}}