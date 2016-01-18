import javax.swing.*;  
import java.awt.*;
import java.awt.event.*; //for ActionListener class  
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;  
import java.lang.Object;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;
 //extend JFrame (JFrame==me) to allow "this" to refer to 'me' - instance of my class
public class BrickBreaker extends JFrame implements ActionListener, KeyListener{
	
	Container c; //necessary to display anything
    Timer timer=new Timer(14,this); //necessary to display in motion - the frames per second (time in ms,the extended JFrame which is ME)
	//game state variables
	int BALL_SIZE=25;
	int BALL_X=994/2-(BALL_SIZE/2); //where the ball starts (290), (300)
	int BALL_Y=450; //i think BALL_SOUTH / BALL_EAST variables would be inefficient
	int BALL_X_VEL=0;
	int BALL_Y_VEL=0;
	int BALL_SPEED=3; //variable "ratio" - multiplier
	
	int PADDLE_WIDTH=150;
	int PADDLE_HEIGHT=15;
	int PADDLE_X=994/2-(PADDLE_WIDTH/2); //exact middle of game panel
	int PADDLE_Y=590-50;
	int PADDLE_SPEED=6;
	
	int BRICK_WIDTH=71;
	int BRICK_HEIGHT=28;
	int BRICK_X=994/2-(5*BRICK_WIDTH); //where to start printing
	int BRICK_Y=88; //BRICKS ABOVE THE HORIZON
	
	int powerUp=0; 
	int POWERUP_X=0; //only can have one at once
	int POWERUP_Y=0;
	boolean activePowerUp=false;
	public boolean invincible=false;
	
	int livesLeft=3;
	int bricksLeft=10*10; //rows * columns
	public int bricks[][]=new int [10][10]; //rows and columns
	public boolean collided=false;

	//KeyEvent
	public boolean left=false;
	public boolean right=false;
	
	//menu - last because variables are required
	JPanel welcome=new JPanel(); //separates game PANEL from rest of game (more efficient - paintComponent)
	JPanel buttons=new JPanel();
	JPanel game; //instead of paint(), use paintComponent()
	JPanel bottom=new JPanel();
	JLabel score=new JLabel("Lives Left: " + livesLeft);
	JLabel intro=new JLabel("Welcome to Brick Breaker! Made by Henry Sun (with LOTS of help from Ms. Strelkovska)");//initialize & assign text
	JButton startButton=new JButton("Start Game");
	JButton helpButton=new JButton("Instructions");
	JButton quitButton=new JButton("Quit");
	
	//images
	ImageIcon background=new ImageIcon("files\\background.gif");
	ImageIcon paddle=new ImageIcon("files\\paddle.jpg");
	ImageIcon brick=new ImageIcon("files\\brick_normal.jpg");
	ImageIcon brickx=new ImageIcon("files\\brick_powerup.jpg");
	ImageIcon power1=new ImageIcon("files\\powerup1.jpg");
	ImageIcon power2=new ImageIcon("files\\powerup2.gif");
	ImageIcon power3=new ImageIcon("files\\powerup3.gif");	
	
	public BrickBreaker(){ //*program name*() is more convenient 
	
		for (int z=0; z<10; z++)
			for (int x=0; x<10; x++)
				bricks[z][x]=(int)(Math.random()*10)+1; //slim chance to get powerup (1-3)
		music();
		menu();
		timer.start(); //want dynamic GUI? get this
		//Circle c = new Circle (2);
		game=new JPanel(){
			public void paintComponent(Graphics g){ //objects to print on the screen
				super.paintComponent(g); //allows erasing of previous events after each frame
				
				//paints the background
				g.drawImage(background.getImage(),0,0,this.getWidth(),this.getHeight(),null);
				
				//paints the paddle
				g.drawImage(paddle.getImage(),PADDLE_X,PADDLE_Y,PADDLE_WIDTH,PADDLE_HEIGHT,null);
				
				//paints the ball
				g.setColor(Color.WHITE); //Color(r,g,b) 0-255 DEFAULT = black
				g.fillOval(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE); //g.drawOval(200,200,20,20);
				g.setColor(Color.BLACK);
				g.drawOval(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE);
				
				//paints the bricks
				BRICK_X=994/2-(5*BRICK_WIDTH); //where to start printing
				BRICK_Y=88;
				for (int z=0; z<bricks.length; z++){
					for (int x=0; x<bricks[z].length; x++){
						if(bricks[z][x]>0 && isHit(z,x)){ //check for collision of existing bricks
							if (bricks[z][x] > 1)
								g.drawImage(brick.getImage(),BRICK_X,BRICK_Y,BRICK_WIDTH,BRICK_HEIGHT,null);
							else
								g.drawImage(brickx.getImage(),BRICK_X,BRICK_Y,BRICK_WIDTH,BRICK_HEIGHT,null);
						}
						BRICK_X+=BRICK_WIDTH;
					}
					BRICK_X=994/2-(5*BRICK_WIDTH); 
					BRICK_Y+=BRICK_HEIGHT; //print on next row
				}

				collided=false; //prevents hitting two bricks cancelling each other out
				
				//powerups
				if (activePowerUp){
					Rectangle powerup = new Rectangle (POWERUP_X,POWERUP_Y,50,50);
					Rectangle paddle = new Rectangle (PADDLE_X,PADDLE_Y,PADDLE_WIDTH,PADDLE_HEIGHT);
					if (powerUp<=3) //ORB OF RA = 1/7 chance
						powerUp=1;
					else if (powerUp<=6) 
						powerUp=4;
					else
						powerUp=7;
					switch (powerUp){
						case 1: g.drawImage(power1.getImage(),POWERUP_X,POWERUP_Y,50,50,null); break;
						case 4: g.drawImage(power2.getImage(),POWERUP_X,POWERUP_Y,50,50,null); break;
						case 7: g.drawImage(power3.getImage(),POWERUP_X,POWERUP_Y,50,50,null);
					}
					if (powerup.intersects (paddle)){
						if (powerUp==1)
							PADDLE_WIDTH+=40;
						else if (powerUp==4){
							PADDLE_SPEED+=2;
							if (BALL_Y_VEL > 0)
								BALL_Y_VEL+=2;
							else 
								BALL_Y_VEL-=2;
							BALL_SPEED++;
						}
						else{
							powerUp3();
						}
						activePowerUp=false;
					}
					POWERUP_Y+=2;
				}
			}
		};	//semicolon needed for method within variable
		c.add(game,BorderLayout.CENTER);
    }
	public void defaultGameState(){
		BALL_SIZE=25;
		BALL_X=994/2-(BALL_SIZE/2); //rest game state variables (but not bricks)
		BALL_Y=450;
		BALL_X_VEL=0;
		BALL_Y_VEL=0;
		BALL_SPEED=3;
		
		PADDLE_WIDTH=150;
		PADDLE_X=994/2-(PADDLE_WIDTH/2); 
		PADDLE_Y=590-50;
		PADDLE_SPEED=6;

		activePowerUp=false;
		invincible = false;
	}
	public void menu(){
		//JFrame
		c=getContentPane(); //want a GUI? get this
		setTitle("_-~==^^BRICK BREAKER^^==~-_");
		setSize(1000,700); //actually 994*690
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
		int insideX; //to calculate where to bounce - brick collision
		int insideY;
		if (BALL_X < BRICK_X)
			insideX = (BALL_X+BALL_SIZE) - BRICK_X; //how much ball is covering the brick
		else if (BALL_X+BALL_SIZE > BRICK_X+BRICK_WIDTH)
			insideX = (BRICK_X+BRICK_WIDTH) - BALL_X;
		else
			insideX = BALL_SIZE;
			
		if (BALL_Y < BRICK_Y)
			insideY = (BALL_Y+BALL_SIZE) - BRICK_Y;
		else if (BALL_Y+BALL_SIZE > BRICK_Y+BRICK_HEIGHT)
			insideY = (BRICK_Y+BRICK_HEIGHT) - BALL_Y;
		else 
			insideY = BALL_SIZE;

		Rectangle r=new Rectangle(BRICK_X,BRICK_Y,BRICK_WIDTH,BRICK_HEIGHT);
		Rectangle b=new Rectangle(BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE);
		if (r.intersects(b) && !collided){ 
			 //prevents double hit glitch but still allows both to hit
			if (!invincible){ //ORB OF RA kills everything
				if (insideX >= insideY) BALL_Y_VEL*=-1;
				if (insideY >= insideX) BALL_X_VEL*=-1;
			}
			//powerups
			if (bricks[z][x]==1){ //random number out of 1-20 set
				activePowerUp=true;
				powerUp=(int)(Math.random()*8+1); //1-8
				POWERUP_X=BRICK_X;
				POWERUP_Y=BRICK_Y;
			}
			bricksLeft--; //if its game over
			bricks[z][x]=0;
			collided=true;
			return false; 
		}
		return true;
	}
	public void move(){ //THIS INCLUDES COLLISION
		//walls (COLLISION)
		if (BALL_X < 0){
			BALL_X=0; //so it doesnt get stuck
			BALL_X_VEL*=-1;
		}
		else if (BALL_X>(this.getWidth()-BALL_SIZE)) {
			BALL_X=994-BALL_SIZE;
			BALL_X_VEL*=-1;
		} 
		if (BALL_Y < 0){ //top
			BALL_Y_VEL*=-1;
		}
		
		Rectangle a=new Rectangle (BALL_X,BALL_Y,BALL_SIZE,BALL_SIZE);
		Rectangle b=new Rectangle (PADDLE_X,PADDLE_Y,PADDLE_WIDTH,PADDLE_HEIGHT);
		if (a.intersects(b)&&!collided){
			//center of ball & paddle origin
			int x=BALL_X+(BALL_SIZE/2);
			int y=BALL_Y+(BALL_SIZE/2);
			int origin_x=PADDLE_X+(PADDLE_WIDTH/2);
			int origin_y=PADDLE_Y+(PADDLE_WIDTH/4); //hitting a quarter way of the paddle = 1:1 x:y ratio
			//calculate angle
			double x_travel=origin_x-x;
			double y_travel=origin_y-y;
			double ratio=x_travel/y_travel; //how many X movements per Y
			//actual ratio of movement
			if (ratio>0){ //left
				BALL_X_VEL=-(int)Math.ceil (ratio*BALL_SPEED); //so 90degree is rare
				if (BALL_X_VEL < -BALL_SPEED*3)
					BALL_X_VEL=-BALL_SPEED*3; //speed limit
			}
			else { //right
				BALL_X_VEL=-(int)Math.floor(ratio*BALL_SPEED); //so 90degree is almost impossible
				if (BALL_X_VEL > BALL_SPEED*2)
					BALL_X_VEL=BALL_SPEED*3; //speed limit
			}
			BALL_Y_VEL=-BALL_SPEED-3;
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
			case 2:	JOptionPane.showMessageDialog(null, "          LEFTARROW      - move left\n          RIGHTARROW    - move right\n          R                           - restart the game\n\nBROWN BRICKS are normal.\nRED BRICKS have hidden power-ups.\n\n\nThere are two power-ups and one secret ORB OF RA hidden inside the RED BRICKS. \n\n\n\n\n\n\n\nHAVE FUN!", "Instructions",JOptionPane.INFORMATION_MESSAGE ); break;
			case 3: JOptionPane.showMessageDialog(null, "You have lost a life. ENTER to continue \n(or click OK with mouse)", "Dead.",JOptionPane.INFORMATION_MESSAGE ); break;
			case 4: JOptionPane.showMessageDialog(null, "Good job!", "YOU WON!",JOptionPane.INFORMATION_MESSAGE ); break;
			case 5: JOptionPane.showMessageDialog(null, "ENTER to restart \n(or click OK with mouse)", "YOU LOSE",JOptionPane.INFORMATION_MESSAGE ); 
		}	
		timer.start();
	}

	public void actionPerformed(ActionEvent e){
		if (e.getSource()==timer){ //simply refresh at 30 fps
			if ( (PADDLE_X+PADDLE_WIDTH) > 10) //prevents paddle exiting to the right
				if (left == true)
					PADDLE_X-=PADDLE_SPEED; 
			if (PADDLE_X < 984) //prevents paddle exiting to the left
				if (right == true)
					PADDLE_X+=PADDLE_SPEED; 
			move();
			game.repaint(); //call paint() on a specific JPanel;
		}
		if (bricksLeft==0){ //YOU WIN
			bricksLeft=10*10;
			livesLeft=3;
			incomingPopup(4);
			score.setText("Lives Left: "+livesLeft);
			startButton.setText("Start Game"); 
			for (int z=0; z<10; z++)
				for (int x=0; x<10; x++)
					bricks[z][x]=(int)(Math.random()*10)+1; //slim chance to get powerup (1-3)
			defaultGameState();
		}
		if (BALL_Y > 720){
			defaultGameState();
			if (livesLeft>0){ //YOU DIED
				livesLeft-=1;
				incomingPopup(3);
				score.setText("Lives Left: "+livesLeft);
				startButton.setText("Start"); 
			}
			else { //YOU LOSE
				bricksLeft=10*10;
				livesLeft=3;
				incomingPopup(5);
				score.setText("Lives Left: "+livesLeft);
				startButton.setText("Start");
				for (int z=0; z<10; z++)
					for (int x=0; x<10; x++)
						bricks[z][x]=(int)(Math.random()*10)+1; //slim chance to get powerup (1-3)
			} 
		}
		else {
			if (e.getSource()==startButton){ //has two functions
				if (!startButton.getText().equals("Pause")){
					BALL_X_VEL=0;
					BALL_Y_VEL=4;
					startButton.setText("Pause"); 
				}
				else {
					incomingPopup(1);
				}
			}
			else if (e.getSource()==helpButton){
				incomingPopup(2);
			}
			else if (e.getSource()==quitButton){ 
				System.exit(0);
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		if (key == KeyEvent.VK_LEFT)
			left = true; 
		if (key == KeyEvent.VK_RIGHT)
			right = true; 
		if (key == KeyEvent.VK_R) //restart conditions
			livesLeft=0; 
	}
	public void keyReleased(KeyEvent e) { 
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT)
			left = false;
		if (key == KeyEvent.VK_RIGHT)
			right = false;
		if (key == KeyEvent.VK_R) //restart prompt
			BALL_Y=721; 
	}
	public void keyTyped(KeyEvent e){
	}
	public void music(){
		try{
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File("files\\bgm.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	    catch(UnsupportedAudioFileException a) {
			System.out.println(a);
		}
		catch(IOException b) {
			System.out.println(b);
		}
		catch(LineUnavailableException c) {
			System.out.println(c);
		}
	
	}
	public void powerUp3(){
		try{
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File("files\\powerUp3.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.loop(0);
		}
	    catch(UnsupportedAudioFileException a) {
			System.out.println(a);
		}
		catch(IOException b) {
			System.out.println(b);
		}
		catch(LineUnavailableException c) {
			System.out.println(c);
		}
		BALL_SIZE=300;
		invincible=true;
	}
	public static void main(String[] args){
		new BrickBreaker();
    }
}