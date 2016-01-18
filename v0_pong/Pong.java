import javax.swing.*;  
import java.awt.*;
import java.awt.event.*; //for ActionListener class  
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;  
 //extend JFrame (JFrame==me) to allow "this" to refer to 'me' - instance of my class
public class Pong extends JFrame implements ActionListener, KeyListener{
    //create GUI and show it || prepare to listen for action (mouse+buttons+key)
	
	Container c; //Container class > object
    Timer timer=new Timer(15,this); //the frames per second (time in ms,the extended JFrame which is ME

	//game variables
	int SCORE_P1=0;
	int SCORE_P2=0;
	int PADDLE_WIDTH=80;
	int PADDLE_HEIGHT=15;
	int PADDLE_X=260; 
	int PADDLE_Y=440;
	int PADDLE_WIDTH_b=80;
	int PADDLE_HEIGHT_b=15;
	int PADDLE_SPEED=5;
	int PADDLE_X_b=260; 
	int PADDLE_Y_b=40;
	int BALL_SIZE=20;
	int BALL_X=290; //where the ball starts
	int BALL_Y=300;
	int BALL_X_VEL=0;
	int BALL_Y_VEL=0;
	int frameCount=0;
	boolean started=false;
	boolean collided=false;
	//KeyEvent
	public boolean left=false;
	public boolean right=false;
	public boolean q=false;
	public boolean w=false;
	JPanel welcome=new JPanel(); //separates game PANEL from rest of game (more efficient - paintComponent)
	JPanel buttons=new JPanel();
	JPanel game; //instead of paint(), use paintComponent()
	JPanel bottom=new JPanel();
	JButton startButton=new JButton("Start Game");
	JButton helpButton=new JButton("Instructions");
	JButton quitButton=new JButton("Quit");
	JLabel score=new JLabel("P1 (top): "+SCORE_P1+"    P2 (bottom): "+SCORE_P2);
	JLabel intro=new JLabel("Welcome to Brick Breaker! Made by Henry Sun (with LOTS of help from Mrs. Strelkovska)");//initialize & assign text
	
	public Pong(){ //*program name*() is more convenient 
		
		//entire JFrame
		c=getContentPane(); //want a GUI? get this
		setTitle("_-~==^^BRICK BREAKER^^==~-_");
		setSize(600,600);
		setVisible(true); //why would you have this false
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		menu();
		

		timer.start(); //want dynamic GUI? get this
		game=new JPanel(){
			public void paintComponent(Graphics g){ //objects to print on the screen
				super.paintComponent(g); //allows erasing of previous events after each frame
				setBackground(Color.green);
				g.fillRect(PADDLE_X,PADDLE_Y,PADDLE_WIDTH,PADDLE_HEIGHT); //the paddle image (x,y,w,h)
				g.fillRect(PADDLE_X_b,PADDLE_Y_b,PADDLE_WIDTH_b,PADDLE_HEIGHT_b); //the paddle image (x,y,w,h)
				g.setColor(new Color(100,50,70)); //Color(r,g,b) 0-255 DEFAULT = black
				//g.drawOval(200,200,20,20);
				g.fillOval(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE); //the ball
			}
		};	//semicolon needed for method within variable
		c.add(game,BorderLayout.CENTER);
    }
	public void menu(){
		//welcome JPanel
		c.add(welcome,BorderLayout.NORTH);
		welcome.setLayout(new GridLayout(2,1));
		welcome.add(intro); //add JLabel
		welcome.add(buttons);
		
		
		c.add(bottom,BorderLayout.SOUTH);
		bottom.add(score);
		//buttons JPanel
		buttons.setLayout(new GridLayout(1,3));
		buttons.add(startButton); //add JButton to buttons JPanel
		buttons.add(helpButton);
		buttons.add(quitButton);
		startButton.addActionListener(this); //when pressed, execute ActionEvent method
		startButton.addKeyListener(this); //only allows keyboard input AFTER starting game
		helpButton.addActionListener(this);
		quitButton.addActionListener(this);
	}
	public void move(){
		
		//COLLISION!!!!
		
		if (BALL_X < 0||BALL_X>(594-BALL_SIZE)) //left and right wall
			BALL_X_VEL*=-1;
		
		if ( (BALL_Y+BALL_SIZE) <= (PADDLE_Y-20) || BALL_Y <= (PADDLE_Y_b+20) && collided==true) //when ball goes back up, reset collided
			collided=false;
		
		if ( (BALL_Y+BALL_SIZE) >= (PADDLE_Y-20) && collided==false){ //efficiency (only do this below a certan point)
		
		//paddle (COLLISION) - includes angles algorithm (which I invented)
			if ( (BALL_Y+BALL_SIZE) >= (PADDLE_Y-20) && collided==false){ //SUPER efficiency - (check paddle collision ONLY after point)
				/*I've made it so that only one of these if statements can occur each time the ball nears the paddle*/
				if ( (BALL_X+BALL_SIZE) > PADDLE_X && BALL_X < (PADDLE_X+PADDLE_WIDTH)){ //if any part of the ball is in contact with paddle
					
					//---- top ----
					if ( (BALL_Y+BALL_SIZE) >= PADDLE_Y && (BALL_Y+BALL_SIZE) <= (PADDLE_Y+BALL_Y_VEL+PADDLE_SPEED)){ //ball doesnt contact exactly so we need frames of allowance
						BALL_Y_VEL*=-1;
						collided=true;
						//PADDLE_MID=(PADDLE_X+PADDLE_WIDTH)/2;
						//BALL_MID=(BALL_X+(BALL_SIZE/2));
						//PADDLE_IMPACT=PADDLE_MID-BALL_MID; //mid of paddle to edge
						

					}
					//---- sides & corners (generous - hits back up) ----
					//wow, i made one piece of code apply for both sides
					else if ( (BALL_Y+BALL_SIZE) >= PADDLE_Y && BALL_Y <= (PADDLE_Y+PADDLE_HEIGHT)){ 
							BALL_X_VEL*=-1;
							BALL_Y_VEL*=-1;
							collided=true;
						}
				}
			}
		}
		if ( (BALL_Y) <= (PADDLE_Y_b+40)) //effiency - check for any contact with paddle below a specific point
			if (BALL_Y <= (PADDLE_Y_b+PADDLE_HEIGHT) && BALL_Y > (PADDLE_Y_b-10)) //ball doesnt contact exactly on the top coord
				if ( (BALL_X+BALL_SIZE) > (PADDLE_X_b+PADDLE_HEIGHT) && BALL_X < (PADDLE_X_b+PADDLE_WIDTH)) //if any part of the ball is in contact with paddle
					BALL_Y_VEL*=-1;
		if (BALL_Y > 600){
			SCORE_P1+=1;
			die();
		}
		if  (BALL_Y < -40){
			SCORE_P2+=1;
			die();			
		}
		BALL_X+=BALL_X_VEL;
		BALL_Y+=BALL_Y_VEL;
	}
	public void die(){
		BALL_X_VEL=0;
		BALL_Y_VEL=0;
		BALL_X=290;
		BALL_Y=300;
		score.setText("P1 (top): "+SCORE_P1+"    P2 (bottom): "+SCORE_P2);
		left=false; //prevents glitch where button release is not registered
		right=false; //because pop-up message interrupts KeyEvent
		//JOptionPane.showMessageDialog(null, "You have lost a life. Press OK to continue", "Dead.",JOptionPane.INFORMATION_MESSAGE );
	}
	
	public void actionPerformed(ActionEvent e){ //takes input from buttons/mouse/keyboard
		if (e.getSource()==timer){ //simply refresh at 30 fps
			if (left)
				PADDLE_X-=5; //ball left/right movement
			if (right)
				PADDLE_X+=5; //ball up/down movement
			if (q)
				PADDLE_X_b-=5;
			if (w)
				PADDLE_X_b+=5;
			move();
			game.repaint(); //call paint() on a specific JPanel;
			frameCount+=2;
		}
		/*if (frameCount/170!=0){ //difficulty factor FIX THIS
			if (BALL_Y_VEL > 0){
				BALL_X_VEL=(int)Math.random()*3+5;
				BALL_Y_VEL=(int)Math.random()*5+3;
			}
			if (BALL_X_VEL < 0) {
				BALL_X_VEL=(int)Math.random()*-3-5;		
				BALL_Y_VEL=(int)Math.random()*-5-3;
			}
			
			frameCount=1;
		}*/
		else{
			if (e.getSource()==startButton){
				BALL_X_VEL=4;
				BALL_Y_VEL=4;
				startButton.setText("Pause"); 
				
				
				
				
				//HOW TO MAKE START BUTTON CHANGE TEXT AND FUNCTIONALITY?
				
				
				
			}
			else if (e.getSource()==helpButton){
				JOptionPane.showMessageDialog(null, "\nNOTE: I WAS TOO LAZY TO MAKE COLLISION FOR P1 \nTO INCLUDE SIDES SO P2 GETS AN UNFAIR ADVANTAGE\n\n\nQ - move top LEFT (P1)\nW - move top RIGHT (P1)\n\nLEFTARROW - move bottom LEFT (P2)\nRIGHTARROW - move bottom RIGHT (P2)", "Instructions",JOptionPane.INFORMATION_MESSAGE );
			}
			else if (e.getSource()==quitButton){ //easiest button to program
				System.exit(0);
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		System.out.println(key);
		if (key == KeyEvent.VK_LEFT)
			left = true; 
		if (key == KeyEvent.VK_RIGHT)
			right = true;     
		if (key == KeyEvent.VK_Q)
			q = true;        	
		if (key == KeyEvent.VK_W) 
			w = true;    		
	}
	public void keyReleased(KeyEvent e) { //
	System.out.println("key");
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT)
			left = false;
		if (key == KeyEvent.VK_RIGHT)
			right = false;
		if (key == KeyEvent.VK_Q)
			q = false;        	
		if (key == KeyEvent.VK_W) 
			w = false;  
	}
	public void keyTyped(KeyEvent e){
		System.out.println("key");
	}
	
	public static void main(String[] args) {
		new Pong();
    }
}