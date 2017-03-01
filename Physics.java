package Physics;
//Made by Andrew Xue
//a3xue@edu.uwaterloo.ca
// Basic physics engine! Click and drag to dynamically spawn objects! Not Finished.
// KNOWN BUGS:
//    Occasional incorrect bounce resolution. Usually on the Y-axis
//    Cause: Unknown

//    X-axis bouncing when multiple blocks are placed on top of one another
//    Cause: Fringe Case in collision detection
//    Reproduction: Put a layer of blocks on the bottom. Place another block with high xvelocity and low y velocity
//    Block will bounce of the edges of the blocks forming a layer on the bottom

//    Blocks completely clipping through each other when one block falls onto another
//    Cause: Collision detection failure
//    Reproduction: Spawn one block with minimal x velocity. Spawn two blocks directly above with no x velocity.

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Physics {
	JFrame window = new JFrame();	
	boolean mousepress=false;
	int tomouse=-35;
	int startmousex;
	int startmousey;
	int endmousex;
	int endmousey;
	double bouncecoefficient = -0.5;
	double collisioncoefficient = -0.5;
	
	ArrayList<object> objectlst = new ArrayList<object>();
	
	public static void main(String[] args) {
		new Physics().go();
	}
	
	// Each object in this engine has a x and y coordinate, and x and y velocity vector, and a boolean 
	// whether it is effected by gravity
	private class object{
		double x;
		double y;
		double xvel;
		double yvel;
		boolean gravity;
		private object(double startx,double starty,double startxvel,double startyvel,boolean startgrav){
			x = startx;
			y = starty;
			xvel = startxvel;
			yvel = startyvel;
			gravity = startgrav;
		}
	}
	
	// Creating the window and adding all the aspects of the engine and a mouse event listener
	private void go(){
		window.setSize(1000, 1000);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.add(new physicsgrid());
		window.addMouseListener(new mouseevents());
		window.addMouseMotionListener(new mouseevents());
		
		gravityandsuchthings();
	}
	
	// Spawns an object at the position when you click, where the x and y velocities are inherited
	// from the distance the mouse is dragged.
	private class mouseevents implements MouseListener,MouseMotionListener{
		public void mouseClicked(MouseEvent event){}
		public void mousePressed(MouseEvent event){
			// When the mouse is pressed, the x and y coordinates are stored in preperation for the
			// new block's coordinates.
			mousepress = true;
			startmousex = event.getX();
			startmousey = event.getY()+tomouse;
			endmousex = event.getX();
			endmousey = event.getY()+tomouse;
		}
		public void mouseReleased(MouseEvent event){
			mousepress = false;
			double newx = startmousex-25;
			double newy = startmousey-25;
			
			// If the block is spawned outside the borders of the windows, it is moved inside the window
			if (newx<5){newx=10;}
			if (newx>940){newx=940;}
			if (newy<5){newy=10;}
			if (newy>910){newy=910;}
			
			// Creates a new object with properties inherited from the x and y coordinates of the mouse click
			objectlst.add(new object(newx,newy,-(double)(endmousex-startmousex)/50,-(double)(endmousey-startmousey)/50,true));
		}
		public void mouseEntered(MouseEvent event){}
		public void mouseExited(MouseEvent event){}
		public void mouseDragged(MouseEvent event) {
			// Changes the end coordinates of the line created by clicking and dragging the line
			endmousex = event.getX();
			endmousey = event.getY()+tomouse;
		}
		public void mouseMoved(MouseEvent event) {}
	}
	
	private void gravityandsuchthings(){
		while(true){
	
			for (int i=0;i<objectlst.size();i++){
				// Bouncing off of the right wall
				if (objectlst.get(i).x>=940){
					objectlst.get(i).xvel*=bouncecoefficient;
					objectlst.get(i).x+=objectlst.get(i).xvel;
				}
				// Bouncing off of the left wall
				if (objectlst.get(i).x<=5){
					objectlst.get(i).xvel*=bouncecoefficient;
					objectlst.get(i).x+=objectlst.get(i).xvel;
				}
				
				// Bouncing off of the bottom wall.
				if (objectlst.get(i).y>=910){
					objectlst.get(i).yvel*=bouncecoefficient;
					objectlst.get(i).y+=objectlst.get(i).yvel;
					// Because of the nature of the physics engine, an object will never come to
					// rest naturally. This reduces the object's yvelocity to 0 if it is below
					// a certain threshold
					if (objectlst.get(i).yvel>-0.08){
						objectlst.get(i).yvel=0;
						objectlst.get(i).y=910;
					}
				}
				// Bouncing off of the top wall
				if (objectlst.get(i).y<=5){
					objectlst.get(i).yvel*=bouncecoefficient;
					objectlst.get(i).y+=objectlst.get(i).yvel;
				}
				
				// Applies gravity for the objects
				if (objectlst.get(i).gravity){
					objectlst.get(i).yvel+=0.05;}
				
				// Collision detection and resolution
				for (int k=0;k<objectlst.size();k++){
					if (k!=i&&(objectlst.get(i).x+50+objectlst.get(i).xvel>=objectlst.get(k).x&&objectlst.get(i).x+objectlst.get(i).xvel<=objectlst.get(k).x+50)
							&&(objectlst.get(i).y+50+objectlst.get(i).yvel>objectlst.get(k).y&&objectlst.get(i).y+objectlst.get(i).yvel<objectlst.get(k).y+50)
							){
						
						// If collision is on the x-axis
						if (objectlst.get(i).x+50<=objectlst.get(k).x||objectlst.get(i).x>=objectlst.get(k).x+50){
							objectlst.get(i).xvel*=collisioncoefficient;
							// If velocities of the two blocks are opposite, they both change directions
							if (objectlst.get(i).xvel*objectlst.get(k).xvel>0)
							objectlst.get(k).xvel*=collisioncoefficient;}
						
						// If collision is on the y-axis
						if (objectlst.get(i).y+50<=objectlst.get(k).y||objectlst.get(i).y>=objectlst.get(k).y+50){
							objectlst.get(i).yvel*=collisioncoefficient;
							// If velocities of the two blocks are opposite, they both change directions
							if (objectlst.get(i).yvel*objectlst.get(k).xvel>0)
							objectlst.get(k).yvel*=collisioncoefficient;}
						
						// Reduces y velocity for 0 if below a certain threshold.
						if (objectlst.get(i).yvel<0.1&&objectlst.get(i).yvel>-0.1){
							objectlst.get(i).yvel=0;
						}
						if (objectlst.get(k).yvel<0.1&&objectlst.get(k).yvel>-0.1){
							objectlst.get(k).yvel=0;
						}
					}
				}
				
				// Changes the position of the blocks based on their velocities
				objectlst.get(i).x+=objectlst.get(i).xvel;
				objectlst.get(i).y+=objectlst.get(i).yvel;
			}
			try {Thread.sleep(10);} catch(Exception exp){System.out.println("Runtime Error");}
			window.repaint();
		}
	}
	
	// Draws all the elements of the grid
	private class physicsgrid extends JComponent {
		public void paintComponent(Graphics g){
			Graphics2D grap = (Graphics2D) g;
			grap.setColor(Color.RED);
			grap.fillRect(0, 0, 1050, 1050);
			grap.setColor(Color.BLACK);
			grap.fillRect(5,5,985,955);
			grap.setColor(Color.WHITE);
			
			// Draws line created from dragging the mouse
			if (mousepress){
				grap.drawLine(startmousex,startmousey,endmousex, endmousey);
			}
			
			// Draws in all the objects
			for (int i=0;i<objectlst.size();i++){
				grap.setColor(Color.RED);
				grap.fillRect((int)objectlst.get(i).x, (int)objectlst.get(i).y, 50, 50);
				grap.setColor(Color.WHITE);
				grap.fillRect((int)objectlst.get(i).x+2, (int)objectlst.get(i).y+2, 46, 46);
			}
		}
	}
}
