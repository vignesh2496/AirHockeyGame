package org.dyn4j;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

public abstract class SimulationFrame extends JFrame
{
	//Input from form
	String PlayerName;
	String OpponentName;
	String ConnectToIP;
	double TimeInMin;
	
	//Details for the game
	ServerSocket serverSocketM;
	Socket clientSocketM;
	ServerSocket serverSocketP;
	Socket clientSocketP;
	ServerSocket serverSocketS;
	Socket clientSocketS;
	ServerSocket serverSocketOP;
	Socket clientSocketOP;
	Socket socketM;
	Socket socketP;
	Socket socketS;
	Socket socketOP;
	DataInputStream dinM = null;
	DataOutputStream doutM = null;
	DataInputStream dinP = null;
	DataOutputStream doutP = null;
	DataInputStream dinS = null;
	DataOutputStream doutS = null;
	DataInputStream dinOP = null;
	DataOutputStream doutOP = null;
	int scoreSelf = 0;
	int scoreOpp = 0;
	boolean end = false;
	long START, CURRENT;
	Image imgNormal = new ImageIcon(this.getClass().getResource("airhockey.jpg")).getImage();
	Image imgVictory = new ImageIcon(this.getClass().getResource("trophy.jpg")).getImage();
	Image imgDefeat = new ImageIcon(this.getClass().getResource("sword.jpg")).getImage();

	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/** The canvas to draw to */
	protected final Canvas canvas;
	
	/** The dynamics engine */
	protected final World world;
	
	/** The pixels per meter scale factor */
	protected final double scale;
	
	/** True if the simulation is exited */
	private boolean stopped;
	
	/** True if the simulation is paused */
	private boolean paused;
	
	/** The time stamp for the last iteration */
	private long last;
	

	public SimulationFrame(String name, double scale) 
	{
		super(name);
		
		// set the scale
		this.scale = scale;
		
		// create the world
		this.world = new World();
		
		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// add a window listener
		this.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				// before we stop the JVM stop the simulation
				stop();
				super.windowClosing(e);
			}
		});
		
		// create the size of the window
		Dimension size = new Dimension(1300, 700);
		
		// create a canvas to paint to 
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);
		
		// add the canvas to the JFrame
		this.add(this.canvas);
		
		// make the JFrame not resizable
		this.setResizable(false);
		
		// size everything
		this.pack();
		
		// setup the world
		this.initializeWorld();
	}
	
	 public String msToString(long ms)
	 {
	     long totalSecs = ms/1000;
	        long hours = (totalSecs / 3600);
	        long mins = (totalSecs / 60) % 60;
	        long secs = totalSecs % 60;
	        String minsString = (mins == 0)
	            ? "00"
	            : ((mins < 10)
	               ? "0" + mins
	               : "" + mins);
	        String secsString = (secs == 0)
	            ? "00"
	            : ((secs < 10)
	               ? "0" + secs
	               : "" + secs);

	        return  minsString + ":" + secsString;
	  }
	 
	protected abstract void initializeWorld();
	
	// Called by run()

	private void start()
	{
		try
		{
			serverSocketM = new ServerSocket(9060);
			serverSocketP = new ServerSocket(9062);
			serverSocketS = new ServerSocket(9064);
			serverSocketOP = new ServerSocket(9068);
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try 
		{
			try 
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientSocketM = new Socket(ConnectToIP, 9060 );
			clientSocketP = new Socket(ConnectToIP, 9062 );
			clientSocketS = new Socket(ConnectToIP, 9064 );
			clientSocketOP = new Socket(ConnectToIP, 9068 );
		}
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			socketM = serverSocketM.accept();
			socketP = serverSocketP.accept();
			socketS = serverSocketS.accept();
			socketOP = serverSocketOP.accept();
		}
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try 
		{
			dinM = new DataInputStream(clientSocketM.getInputStream());
			dinP = new DataInputStream(clientSocketP.getInputStream());
			dinS = new DataInputStream(clientSocketS.getInputStream());
			dinOP = new DataInputStream(clientSocketOP.getInputStream());
		}
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try 
		{
			doutM = new DataOutputStream(socketM.getOutputStream());
			doutP = new DataOutputStream(socketP.getOutputStream());
			doutS = new DataOutputStream(socketS.getOutputStream());
			doutOP = new DataOutputStream(socketOP.getOutputStream());
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// initialize the last update time
		this.last = System.nanoTime();
		
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);

		this.canvas.createBufferStrategy(2);

		Thread thread = new Thread()
		{
			public void run() 
			{
				START = System.currentTimeMillis();
				while (!isStopped()) 
				{
					if (!paused) 
					{
						gameLoop();
					}
				}
			}
		};
		
		try 
		{
			doutOP.writeUTF(PlayerName);;
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		try
		{
			OpponentName = dinOP.readUTF();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		thread.setDaemon(true);
		
		// start the game loop
		thread.start();
		
		//Close all sockets after the end of the game
		/*try
		{
			serverSocketM.close();
			clientSocketM.close();
			serverSocketP.close();
			clientSocketP.close();
			serverSocketS.close();
			clientSocketS.close();
			serverSocketOP.close();
			clientSocketOP.close();
			
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	private void gameLoop() 
	{
		// get the graphics object to render to
		Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();

		// reset the view
		this.clear(g);
		if(end == true)
			this.pause();
		
		// get the current time
        long time = System.nanoTime();
        
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        
        // set the last time
        this.last = time;
        
    	// convert from nanoseconds to seconds
    	double elapsedTime = (double)diff ;
    
    	if(end == false)
    	{
    		// render anything about the simulation (will render the World objects)
    		this.render(g, elapsedTime);
    	}
    		
        
        // update the World
        this.update(g, elapsedTime);
		
		// dispose of the graphics object
		g.dispose();
		
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost())
		{
			strategy.show();
		}
		
		// Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
	}

	protected void clear(Graphics2D g) 
	{
		final int w = this.canvas.getWidth();
		final int h = this.canvas.getHeight();
		g.setFont(new Font("Courier New", Font.BOLD, 40));
		g.setColor(Color.WHITE);
		
		if(end == true)
		{	
			if(scoreSelf >= scoreOpp)
			{	
				g.drawImage(imgVictory, 0, 0, w, h,this);
				g.drawString(PlayerName + " Wins !!!!", 490, 90);
			}
				
			else
			{
				g.drawImage(imgDefeat, 0, 0, w, h,this);
				g.drawString(OpponentName + " Wins !!!!", 440, 90);
			}
		}
		
		else
		{	
			g.drawImage(imgNormal, 0, 0, w, h,this);
			g.setColor(Color.BLUE);
			g.fillRect(90, 80, 1115, 540);
			g.setColor(Color.WHITE);
			g.drawLine(652, 80, 652, 610);
			g.drawOval(602, 310, 100, 100);
		}
	}

	@SuppressWarnings("deprecation")
	protected void render(Graphics2D g, double elapsedTime)
	{
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Point prev = MouseInfo.getPointerInfo().getLocation();
		double last = System.nanoTime();
		SimulationBody puck1 = (SimulationBody) this.world.getBody(6);
		SimulationBody puck2 = (SimulationBody) this.world.getBody(7);
		SimulationBody mallet1 = (SimulationBody) this.world.getBody(4);
		SimulationBody mallet2 = (SimulationBody) this.world.getBody(5);
		
		
		Vector2 puckCentre1 = puck1.getWorldCenter();
		Vector2 puckCentre2 = puck2.getWorldCenter();
		Point pC1 = new Point((int) puckCentre1.x,(int) puckCentre1.y);
		Point pC2 = new Point((int) puckCentre2.x,(int) puckCentre2.y);
		Vector2 malletCentre1 = mallet1.getWorldCenter();
		Vector2 malletCentre2 = mallet2.getWorldCenter();
		Point mC1 = new Point((int) malletCentre1.x,(int) malletCentre1.y);
		Point mC2 = new Point((int) malletCentre2.x,(int) malletCentre2.y);
		
		if(pC1.x<=720)
		{
			scoreSelf++;
		}
		
		if(pC2.x<=720)
		{
			scoreSelf++;
		}
		
		try 
		{
			doutS.writeDouble(scoreSelf);
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try 
		{
			scoreOpp = (int)dinS.readDouble();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) 
		{
			// get the object
			SimulationBody body = (SimulationBody) this.world.getBody(i);
				
			Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		
			if(i==4) 
			{
				if(pC1.distance(mC1)<=60)
				{
					Vector2 force = new Vector2((pC1.x-mC1.x)*1000000,(pC1.y-mC1.y)*1000000);
					Vector2 force2 = new Vector2((mC1.x-pC1.x)*1000000,(mC1.y-pC1.y)*1000000);
					for(int c=0;c<10;c++)
					{
						puck1.applyForce(force);
						mallet1.applyForce(force2);
					}
				}
				
				if(pC2.distance(mC1)<=60)
				{
					Vector2 force = new Vector2((pC2.x-mC1.x)*1000000,(pC2.y-mC1.y)*1000000);
					Vector2 force2 = new Vector2((mC1.x-pC2.x)*1000000,(mC1.y-pC2.y)*1000000);
					for(int c=0;c<10;c++)
					{	
						puck2.applyForce(force);
						mallet1.applyForce(force2);
					}
				}
				
				else if(mousePoint.y >= 150 && mousePoint.y <= 630 && mousePoint.x >= 720 && mousePoint.x <= 1230)
				{
					double time = System.nanoTime();
			        double diff = time - last;	
			        	
			        if(mousePoint.distance(pC1)<=60)
					{
			        	Vector2 force = new Vector2((pC1.x-mC1.x)*1000000,(pC1.y-mC1.y)*1000000);
			        	Vector2 force2 = new Vector2((mC1.x-pC1.x)*1000000,(mC1.y-pC1.y)*1000000);
			        	for(int c=0;c<10;c++)
						{
							puck1.applyForce(force);
							mallet1.applyForce(force2);
						}
					}
			        
			        if(mousePoint.distance(pC2)<=60)
					{
			        	Vector2 force = new Vector2((pC1.x-mC1.x)*1000000,(pC1.y-mC1.y)*1000000);
			        	Vector2 force2 = new Vector2((mC1.x-pC2.x)*1000000,(mC1.y-pC2.y)*1000000);
						for(int c=0;c<10;c++)
						{
							puck1.applyForce(force);
							mallet1.applyForce(force2);
						}
					}
			        
			        else
			        {
			        	mallet1.translateToOrigin();
			        	mallet1.translate(mousePoint.x - 55, mousePoint.y - 40);
						mallet1.setLinearVelocity((mousePoint.x - prev.x)/diff * 1000, (mousePoint.y - prev.y)/diff * 1000);
			        }
					
					prev.x = mousePoint.x;
					prev.y = mousePoint.y;
					last = time;
				}
				
				try 
				{
					doutM.writeDouble(malletCentre1.x);
					doutM.writeDouble(malletCentre1.y);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(i==5)
			{
				double oppX = 0;
				double oppY = 0;
			
				try 
				{
					oppX = dinM.readDouble();
					oppY = dinM.readDouble();
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				body.translateToOrigin();
				body.translate(1230-oppX+75,oppY);
			}
			
			if(i==6)
			{
				if(pC1.distance(mC2)<=60)
				{
					Vector2 force = new Vector2((pC1.x-mC2.x)*1000000,(pC1.y-mC2.y)*1000000);
					for(int c=0;c<10;c++)
						puck1.applyForce(force);
				}
				
				if(pC1.distance(pC2)<=45)
				{
					Vector2 force = new Vector2((pC1.x-pC2.x)*1000000,(pC1.y-pC2.y)*1000000);
					for(int c=0;c<10;c++)
						puck1.applyForce(force);
				}
				
				try 
				{
					doutP.writeDouble(pC1.x);
					doutP.writeDouble(pC1.y);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if(i==7)
			{
				double oppX = 0;
				double oppY = 0;
					
				try 
				{
					oppX = dinP.readDouble();
					oppY = dinP.readDouble();
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				body.translateToOrigin();
				body.translate(1230-oppX+75,oppY);
			
			}
				
			//draw the object
			CURRENT = System.currentTimeMillis();
			g.setFont(new Font("Courier New", Font.BOLD, 20));
			g.setColor(Color.RED);
			g.drawString(PlayerName + " : "+this.scoreSelf, 990, 35);
			g.drawString(OpponentName + " : "+this.scoreOpp, 100, 35);
			g.setFont(new Font("Courier New", Font.BOLD, 40));
			g.drawString(msToString(CURRENT-START), 590, 45);
			if(CURRENT-START >= TimeInMin * 60000)
				end = true;
			this.render(g, elapsedTime, body);
		}
	}
	
	protected void render(Graphics2D g, double elapsedTime, SimulationBody body)
	{
		// draw the object
		body.render(g, this.scale);
	}
	
	protected void update(Graphics2D g, double elapsedTime)
	{
        // update the world with the elapsed time
        this.world.update(elapsedTime);
	}
	
	public synchronized void stop() 
	{
		this.stopped = true;
	}
	
	public boolean isStopped()
	{
		return this.stopped;
	}
	
	public synchronized void pause() 
	{
		this.paused = true;
	}
	
	public boolean isPaused() 
	{
		return this.paused;
	}
	
	// Everything starts here
	public void run()
	{
		// set the look and feel to the system look and feel
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		
		// show it
		this.setVisible(true);
		
		// start it
		this.start();
	}
}
