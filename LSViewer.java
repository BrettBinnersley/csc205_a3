/* LSViewer.java

   B. Bird - 02/09/2016
*/

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.*;
import java.awt.BasicStroke;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JComponent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import java.util.ArrayList;
import java.util.ArrayDeque;






class Canvas205 extends JComponent{

	private static final long serialVersionUID = 1L;
	
	public static final int CANVAS_SIZE_X = 800;
	public static final int CANVAS_SIZE_Y = 600;

	private int LS_iterations = 0;
	LSystem L_system;

	

	public Canvas205(LSystem L){
		L_system = L;
		
		setDoubleBuffered(true);
		setSize(CANVAS_SIZE_X,CANVAS_SIZE_Y);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				handleMouseDown(x,y,e.getButton());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				handleMouseUp(x,y,e.getButton());
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				handleMouseMove(x,y);
			}
		});
		addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				HandleKeyDown(e);
			}
			@Override
			public void keyReleased(KeyEvent e){
				HandleKeyUp(e);
			}
		});
		setFocusable(true);
		requestFocusInWindow();

	}

	private int canvasWidth(){
		return CANVAS_SIZE_X;
	}
	private int canvasHeight(){
		return CANVAS_SIZE_Y;
	}
	
	/* Event handlers and drawing functions */
	
	private void handleMouseDown(int x, int y, int button_number){
		
		//Reject the point if it's out of bounds
		if (x < 0 || x >= canvasWidth())
			return;
		if (y < 0 || y >= canvasHeight())
			return;	
	}		
	

	private void handleMouseUp(int x, int y, int button_number){
		
		//Reject the point if it's out of bounds
		if (x < 0 || x >= canvasWidth())
			return;
		if (y < 0 || y >= canvasHeight())
			return;
	}		

	private void handleMouseMove(int x, int y){
		
		//Reject the point if it's out of bounds
		if (x < 0 || x >= canvasWidth())
			return;
		if (y < 0 || y >= canvasHeight())
			return;
	}
	
	private void HandleKeyDown(KeyEvent e){
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_UP){
			LS_iterations++;
		}else if (keyCode == KeyEvent.VK_DOWN){
			LS_iterations = (LS_iterations == 0)? 0: LS_iterations-1;
		}
		repaint();
	}
	
	private void HandleKeyUp(KeyEvent e){
		int keyCode = e.getKeyCode();
	}
	

	
	public void drawFrame(double frame_delta_ms){

		double frame_delta_seconds = frame_delta_ms/1000.0;

		//repaint(); //This results in the paintComponent method below being called.
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(new Color(0,0,0));
		g2d.fillRect(0,0,canvasWidth(),canvasHeight());
		
		
		System.out.println("Drawing with "+LS_iterations+" iterations");
		String SystemString = L_system.GenerateSystemString(LS_iterations);
		System.out.println("System String: "+SystemString);
		
		
		AffineTransform viewportTransform = new AffineTransform();
		viewportTransform.concatenate(Translation(CANVAS_SIZE_X/2, CANVAS_SIZE_Y));
		viewportTransform.concatenate(Scale(1, -1));
		viewportTransform.concatenate(Scale(CANVAS_SIZE_X/100.0, CANVAS_SIZE_Y/100.0));
		g2d.setTransform(viewportTransform);
	
		//Replace this with actual drawing code...
		draw_leaf(g2d);

		

	}
	
	private AffineTransform Rotation(double radians){
		return AffineTransform.getRotateInstance(radians);
	}
	private AffineTransform Translation(double tx, double ty){
		return AffineTransform.getTranslateInstance(tx,ty);
	}
	private AffineTransform Scale(double sx, double sy){
		return AffineTransform.getScaleInstance(sx,sy);
	}
	private AffineTransform Copy(AffineTransform T){
		return new AffineTransform(T);
	}
	
	private void draw_leaf(Graphics2D g){
		double[] vx = {0,1.0 ,1.25,   1,  0,  -1,-1.25,-1};
		double[] vy = {0,0.75,1.75,2.75,4.0,2.75, 1.75,0.75};
		int numVerts = 8;
		//We can't use a 'Polygon' object since those require integer coordinates
		//Instead, a Path2D (which generalizes a polygon) can be used instead.
		Path2D.Double P = new Path2D.Double();
		P.moveTo(vx[0],vy[0]);
		for (int i = 1; i < numVerts; i++)
			P.lineTo(vx[i],vy[i]);
		g.setColor(new Color(64,224,0));
		g.fill(P);
		g.setColor(new Color(64, 128, 0));
		g.setStroke(new BasicStroke(0.25f));
		g.draw(P);
	}
	
	
}



public class LSViewer {

	private Canvas205 canvas;
	private JFrame viewerWindow;
	
	
	private LSViewer(LSystem L) {
		initialize(L);
	}
	

	private void initialize(LSystem L) {
		viewerWindow = new JFrame();
		viewerWindow.setTitle("CSC 205 - Spring 2016");
		viewerWindow.setBounds(100, 100, canvas.CANVAS_SIZE_X, canvas.CANVAS_SIZE_Y+25);
		viewerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		canvas = new Canvas205(L);
		viewerWindow.getContentPane().add(canvas, BorderLayout.CENTER);
		
	}
	
	private void start_render_loop(){
		Thread t = new Thread()
		{
		 public void run()
		 {
				try {
					frame_loop();
				} catch (Exception e) {
					e.printStackTrace();
				}
		 }
		};
		t.start();
	}
	
	private void frame_loop(){
		//nanoTime returns the time in nanoseconds (so divide by 1000000000 to recover the time in seconds)
		long last_frame = System.nanoTime();
		while (true){
			long this_frame = System.nanoTime();
			long frame_delta = this_frame-last_frame;
			
			//Convert the frame delta to milliseconds
			double frame_delta_ms = frame_delta/1000000.0;
			canvas.drawFrame(frame_delta_ms);
			last_frame = this_frame;
		}
	}
	
	
	
	public static void spawn(final LSystem L) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LSViewer window = new LSViewer(L);
					window.viewerWindow.setVisible(true);
					window.start_render_loop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args){
		if (args.length < 1){
			System.err.println("Usage: java LSViewer <input file>");
			return;
		}
		String inputFile = args[0];
		LSystem L = LSystem.ParseFile(inputFile);
		if (L == null){
			System.err.println("Unable to parse "+inputFile);
			return;
		}
		spawn(L);
	}
	
	/* Prints an error message and exits (intended for user errors) */
	private static void ErrorExit(String errorMessage, Object... formatArgs){
		System.err.printf("ERROR: " + errorMessage + "\n",formatArgs);
		System.exit(0);
	}
	/* Throws a runtime error (intended for logic errors) */
	private static void ErrorAbort(String errorMessage, Object... formatArgs){
		throw new Error(String.format(errorMessage,formatArgs));
	}

}

