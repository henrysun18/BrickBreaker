import javax.swing.*;  
import java.awt.*;
import java.awt.event.*;      
 
public class ReactionTyping extends JFrame implements ActionListener{
    //create GUI and show it
	Timer timer=new Timer(500,this); //the frames per second (time in ms,the extended JFrame which is ME)
	int x=50;
	int y=70;
	JLabel label;
	JButton asd; //create button
	Container c; //Container class > object
    
	public ReactionTyping(){
        //a text inside the window
        label=new JLabel("This is all I have so far...");
		asd=new JButton("asdasd"); //...initialize button asd;
        c=getContentPane(); //the top bar of the window
		c.setLayout(new FlowLayout());
		c.add(label); //...place button asd into container window
		c.add(asd); //to add buttons to the container
		timer.start(); //after initializing window, start timer method
    }
 
    public static void main(String[] args) {
     ReactionTyping jtyping=new ReactionTyping ();
	 jtyping.setSize(600,600);
	 jtyping.setVisible(true);//
	 jtyping.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	
	public void move(){
		//put collision here
		//if something happens, how to move it?
		x+=10;
	}
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==timer){
			move();
			repaint(); //call paint();
		}
	}
	
	public void paint(Graphics g){ //repaints the screen
		super.paint(g); //allows erasing of previous events after each frame
		g.drawRect(100,100,80,150); //drawRect (top left coord,, size,,)
		g.setColor(new Color(100,50,70)); //Color(r,g,b) 0-255 DEFAULT = black
		g.fillRect(50,40,80,150);  //fillRect (top left coord, size w,h)
		g.setColor(new Color(200,10,170)); 
		g.drawOval(x,y,20,20);
	
	}
}