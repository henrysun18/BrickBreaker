import javax.swing.*;  
import java.awt.*;
import java.awt.event.*; //for ActionListener class  
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;  
import java.lang.Object;
 //extend JFrame (JFrame==me) to allow "this" to refer to 'me' - instance of my class
public class BrickBreaker extends JFrame implements ActionListener, KeyListener{
	
	Container c; //necessary to display anything
    Timer timer=new Timer(10,this); //necessary to display in motion - the frames per second (time in ms,the extended JFrame which is ME)
	
	//game state variables
	int BALL_SIZE=13;
	int BALL_X=290; //where the ball starts (290), (300)
	int BALL_Y=300; //i think BALL_SOUTH / BALL_EAST variables would be inefficient
	int BALL_X_VEL=0;
	int BALL_Y_VEL=0;
	int BALL_MID; //for angled collisions
	int PADDLE_X=260; 
	int PADDLE_Y=450;
	int PADDLE_SPEED=4;
	int PADDLE_WIDTH=120;
	int PADDLE_HEIGHT=15;
	int SCORE=0;
	int BRICK_X; //for each brick
	int BRICK_Y;
	int BRICK_WIDTH=50;

	int bricksLeft=8*8; //rows * columns
	public boolean bricks[][]=new boolean [8][8]; //8 rows, 8 columns;
	boolean started=false;
	boolean collided = false; //prevent corner glitching

	
	//KeyEvent
	public boolean left=false;
	public boolean right=false;
	
	//menu - last because variables are required
	JPanel welcome=new JPanel(); //separates game PANEL from rest of game (more efficient - paintComponent)
	JPanel buttons=new JPanel();
	JPanel game; //instead of paint(), use paintComponent()
	JPanel bottom=new JPanel();
	JLabel score=new JLabel("Death count: " + SCORE);
	JLabel intro=new JLabel("Welcome to Brick Breaker! Made by Henry Sun (with LOTS of help from Mrs. Strelkovska)");//initialize & assign text
	JButton startButton=new JButton("Start Game");
	JButton helpButton=new JButton("Instructions");
	JButton quitButton=new JButton("Quit");
	
	public BrickBreaker(){ //*program name*() is more convenient 
		for (int z=0; z<8; z++) for (int x=0; x<8; x++) bricks[z][x]=true;
		menu();
		timer.start(); //want dynamic GUI? get this
		//Circle c = new Circle (2);
		game=new JPanel(){
			public void paintComponent(Graphics g){ //objects to print on the screen
				super.paintComponent(g); //allows erasing of previous events after each frame
				
				//paints the background
				setBackground(Color.green);
				
				//paints the paddle
				g.setColor(Color.BLUE);
				g.fillRect(PADDLE_X,PADDLE_Y,PADDLE_WIDTH,PADDLE_HEIGHT); //the paddle image (x,y,w,h)
				g.setColor(Color.RED);
				g.drawRect(PADDLE_X,PADDLE_Y,PADDLE_WIDTH,PADDLE_HEIGHT);
				
				//paints the ball
				g.setColor(Color.WHITE); //Color(r,g,b) 0-255 DEFAULT = black
				g.fillOval(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE); //g.drawOval(200,200,20,20);
				g.setColor(Color.BLACK);
				g.drawOval(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE);
				//game over
				
				//checks for HIT bricks and paints UNHIT bricks
				BRICK_X=90; //where to start printing
				BRICK_Y=90;
				
				for (int z=0; z<bricks.length; z++){
					for (int x=0; x<bricks[z].length; x++){
						if(bricks[z][x]==true && isHit(z,x)){ //check for collision of existing bricks
							g.setColor(new Color(200,20,20));
							g.fillRect(BRICK_X,BRICK_Y,BRICK_WIDTH,15);
							g.setColor(Color.BLACK);
							g.drawRect(BRICK_X,BRICK_Y,BRICK_WIDTH,15);
						}
						BRICK_X+=BRICK_WIDTH;
					}
					BRICK_X=90; 
					BRICK_Y+=15; //print on next row
				}
				
				
				
			}
		};	//semicolon needed for method within variable
		c.add(game,BorderLayout.CENTER);
    }
	public void menu(){
		//JFrame
		c=getContentPane(); //want a GUI? get this
		setTitle("_-~==^^BRICK BREAKER^^==~-_");
		setSize(1200,760);
		setVisible(true); //why would you have this false
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		//welcome JPanel
		c.add(welcome,BorderLayout.NORTH);
		welcome.setLayout(new GridLayout(2,1));
		welcome.add(intro); //add JLabel
		welcome.add(buttons);
		
		//score JPanel
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
		helpButton.addKeyListener(this);
		quitButton.addActionListener(this);
	}
	public boolean isHit(int z, int x){
		Rectangle r=new Rectangle(BRICK_X,BRICK_Y,BRICK_WIDTH,15);
		Rectangle b=new Rectangle(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE);
		if (r.intersects(b)){
			BALL_Y_VEL*=-1; //temporary collision
			bricksLeft--; //if its game over
			bricks[z][x]=false;
			return false; //therefore you shouldn't don't do the following graphics commands
		}
		return true;
	}
	
	public void move(){ //THIS INCLUDES COLLISION
		//walls (COLLISION)
		if (BALL_X < 0||BALL_X>(594-BALL_SIZE)) 
			BALL_X_VEL*=-1;
		if (BALL_Y < 0) //top
			BALL_Y_VEL*=-1;
		
		if ( (BALL_Y+BALL_SIZE) <= (PADDLE_Y-20) && collided==true) //SUPER efficiency - when ball goes back up, reset collided ONLY ONCE
			collided=false;
		
		//paddle (COLLISION) - includes angles algorithm (which I invented)
		if ( (BALL_Y+BALL_SIZE) >= (PADDLE_Y-20) && collided==false){ //SUPER efficiency - (check paddle collision ONLY after point)
			/*I've made it so that only one of these if statements can occur each time the ball nears the paddle*/
			if ( (BALL_X+BALL_SIZE) > PADDLE_X && BALL_X < (PADDLE_X+PADDLE_WIDTH)){ //if any part of the ball is in contact with paddle
				
				//---- top ----
				if ( (BALL_Y+BALL_SIZE) >= PADDLE_Y && (BALL_Y+BALL_SIZE) <= (PADDLE_Y+BALL_Y_VEL+PADDLE_SPEED)){ //ball doesnt contact exactly so we need frames of allowance
					
					collided=true;
					
					//center of ball & paddle origin
					int x=BALL_X+(BALL_SIZE/2);
					int y=BALL_Y+(BALL_SIZE/2);
					int origin_x=PADDLE_X+(PADDLE_WIDTH/2);
					int origin_y=PADDLE_Y+(PADDLE_WIDTH/4); //hitting a quarter way of the paddle = 1:1 x:y ratio
					    //Point a=new Point ((int)x,(int)y);
					//calculate angle
					double x_travel=origin_x-x;
					double y_travel=origin_y-y;
					double ratio=x_travel/y_travel; //how many X movements per Y
					System.out.print((int)(Math.round(ratio*3)));
					BALL_X_VEL=-(int)(Math.round(ratio*3));
					BALL_Y_VEL=-3;

					
					//find where the center is relative to paddle
					//an int between 0 and PADDLE_WIDTH
					//if (x<PADDLE_MID) //bounce left
						
					
					
					
					
					//
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
			
				//bottom of paddle (NOT GENEROUS OF COURSE...)
			if (BALL_Y > 580)
				die();
		}
		//finally, decide movement ONLY after collision/no collision calculations
		BALL_X+=BALL_X_VEL;
		BALL_Y+=BALL_Y_VEL;
	}
	public void incomingPopup(int a){  //prevents glitch where button release is not registered after pop-up
		left=false;
		right=false;
		timer.stop();
		switch (a){ //lol i actually used switch
			case 1: JOptionPane.showMessageDialog(null, "ENTER to continue \n(or click OK with mouse)", "PAUSED",JOptionPane.INFORMATION_MESSAGE ); break;
			case 2:	JOptionPane.showMessageDialog(null, "LEFTARROW - move left\nRIGHTARROW - move right", "Instructions",JOptionPane.INFORMATION_MESSAGE ); break;
			case 3: JOptionPane.showMessageDialog(null, "You have lost a life. ENTER to continue \n(or click OK with mouse)", "Dead.",JOptionPane.INFORMATION_MESSAGE ); break;
			case 4: JOptionPane.showMessageDialog(null, "YOU WON!", "GOOD JOB!",JOptionPane.INFORMATION_MESSAGE ); 
		}	
		timer.start();
	}
	public void die(){
		BALL_X_VEL=0;
		BALL_Y_VEL=0;
		BALL_X=290;
		BALL_Y=300;
		SCORE+=1;
		score.setText("Death count: "+SCORE);
		incomingPopup(3); //case 3
		startButton.setText("Start"); 
	}
	public void actionPerformed(ActionEvent e){ //takes input from buttons/mouse/keyboard
		if (e.getSource()==timer){ //simply refresh at 30 fps
			if ( (PADDLE_X+PADDLE_WIDTH) > 10) //prevents paddle exiting to the right
				if (left == true)
					PADDLE_X-=PADDLE_SPEED; 
			if (PADDLE_X < 584) //prevents paddle exiting to the left
				if (right == true)
					PADDLE_X+=PADDLE_SPEED; 
			move();
			game.repaint(); //call paint() on a specific JPanel;
		}
		if (bricksLeft==0){ //restart game (make bricks display randomly?)
			incomingPopup(4);
			startButton.setText("Start Game"); 
			bricksLeft=8*8;
			for (int z=0; z<8; z++) for (int x=0; x<8; x++) bricks[z][x]=true;
			BALL_X=290; //where the ball starts
			BALL_Y=300;
			BALL_X_VEL=0;
			BALL_Y_VEL=0;
			PADDLE_SPEED=6;
			PADDLE_X=260; 
			PADDLE_Y=450;
			PADDLE_WIDTH=80;
		}
		else {
			if (e.getSource()==startButton){ //has two functions
				if (!startButton.getText().equals("Pause")){
					BALL_X_VEL=0;
					BALL_Y_VEL=3;
					startButton.setText("Pause"); 
				}
				else {
					incomingPopup(1);
				}
			}
			else if (e.getSource()==helpButton){
				incomingPopup(2); //case 2
			}
			else if (e.getSource()==quitButton){ //easiest button to program
				System.exit(0);
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		if (key == KeyEvent.VK_LEFT){
			left = true; 
		}	
		if (key == KeyEvent.VK_RIGHT)
			right = true;        	    	
	}
	public void keyReleased(KeyEvent e) { 
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT){
			left = false;
		}
		if (key == KeyEvent.VK_RIGHT){
			right = false;
		}
	}
	public void keyTyped(KeyEvent e){
	}
	
	public static void main(String[] args) {
		new BrickBreaker();
    }
}