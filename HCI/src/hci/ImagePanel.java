package hci;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import hci.utils.*;

/**
 * Handles image editing panel
 * @author Michal
 *
 */
public class ImagePanel extends JPanel implements MouseListener {
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * image to be tagged
	 */
	BufferedImage image = null;

	ImageLabeller windowB;
	boolean move;
	int indexP;
	boolean edit = false;
	boolean blue;
	
	String currentFile = ImageLabeller.currentFileOut;
	
	int indexEdit;
	/**
	 * list of current polygon's vertices 
	 */
	ArrayList<Point> currentPolygon = new ArrayList<Point>();
	/**
	 * list of all the names given to each of the polygons
	 */
	ArrayList<String> polygonNames = new ArrayList<String>();
	
	/**
	 * list of polygons
	 */
	ArrayList<ArrayList<Point>> polygonsList = new ArrayList<ArrayList<Point>>();

	/**
	 * default constructor, sets up the window properties
	 */
	public ImagePanel(ArrayList<ArrayList<Point>> objects) {
		currentPolygon = new ArrayList<Point>();
		polygonsList = objects;
		this.setVisible(true);

		Dimension panelSize = new Dimension(800, 600);
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);

		addMouseListener(this);
	}

	/**
	 * extended constructor - loads image to be labelled
	 * @param imageName - path to image
	 * @throws Exception if error loading the image
	 */
	public ImagePanel(String imageName,ArrayList<ArrayList<Point>> objects) throws Exception{
		this(objects);
		image = ImageIO.read(new File(imageName));
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
			System.out.println("SCALING TO " + newWidth + "x" + newHeight );
			Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(scaledImage, 0, 0, this);
		}
		
		if (image.getWidth() < 800 || image.getHeight() < 600) {
			int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
			System.out.println("SCALING TO " + newWidth + "x" + newHeight );
			Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(scaledImage, 0, 0, this);
		}
	}
	
	/**
	 * extended constructor - loads the polygon names across
	 * @param nameObjects
	 * @throws Exception if error with getting the names across
	 */
	public ImagePanel(String imageName,ArrayList<ArrayList<Point>> objects,ArrayList<String> polyNames) throws Exception{
		this(imageName, objects);
		polygonNames = polyNames;
	}

	/**
	 * Displays the image
	 */
	public void ShowImage() {

		Graphics g = this.getGraphics();
		
		if(g==null)
		{
			
		}
		
		if (image != null) {
			g.drawImage(
					image, 0, 0, null);
		}
	}

	public ArrayList<ArrayList<Point>> returnPolygons(){
		return polygonsList;
	}

	public ArrayList<String> returnPolyNames(){
		return polygonNames;
	}
	
	public void removePolys(){
		polygonNames = new ArrayList<String>();
		polygonsList = new ArrayList<ArrayList<Point>>();
		currentPolygon = new ArrayList<Point>();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		//display image
		ShowImage();

		//display all the completed polygons
		if(polygonsList.isEmpty()){
			return;
		}else{
		for(ArrayList<Point> polygon : polygonsList) {
			if(windowB.blue && windowB.blueIndex==polygonsList.indexOf(polygon)){
				blue = true;
			}else{
				blue = false;
			}
			drawPolygon(polygon);
			drawPolygon(polygon);
			finishPolygon(polygon);

		}

		//display current polygon
		drawPolygon(currentPolygon);
		}
	}

	/**
	 * displays a polygon without last stroke
	 * @param polygon to be displayed
	 */
	public void drawPolygon(ArrayList<Point> polygon) {
		Graphics2D g = (Graphics2D)this.getGraphics();
		for(int i = 0; i < polygon.size(); i++) {
			if(blue){
				g.setColor(Color.BLUE);
			}else{
				g.setColor(Color.GREEN);
			}
			Point currentVertex = polygon.get(i);
			if (i != 0) {
				Point prevVertex = polygon.get(i - 1);
				g.drawLine(prevVertex.getX(), prevVertex.getY(), currentVertex.getX(), currentVertex.getY());
			}
			if((i==polygon.size()-1)&&blue){
				g.setColor(Color.RED);
				} 
			if((i==0) && blue){
				g.setColor(Color.WHITE);
			}
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10, 10);
		}
	}

	/**
	 * displays last stroke of the polygon (arch between the last and first vertices)
	 * @param polygon to be finished
	 */
	public void finishPolygon(ArrayList<Point> polygon) {
		//if there are less than 3 vertices than nothing to be completed
		if (polygon.size() >= 3) {
			Point firstVertex = polygon.get(0);
			Point lastVertex = polygon.get(polygon.size() - 1);

			Graphics2D g = (Graphics2D)this.getGraphics();
			if(blue){
				g.setColor(Color.BLUE);
			}else{
			g.setColor(Color.GREEN);
			}
			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex.getX(), lastVertex.getY());
		}else{
		}
	}

	/**
	 * moves current polygon to the list of polygons and makes space for a new one
	 */
	public void addNewPolygon(String name) {
		//finish the current polygon if any
		if (currentPolygon != null ) {
				polygonNames.add(name);
				finishPolygon(currentPolygon);
				polygonsList.add(currentPolygon);
		}else{
			JOptionPane.showMessageDialog(null, "No points to save.");
		}
		currentPolygon = new ArrayList<Point>();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		int x = e.getX();
		int y = e.getY();
		
		if (x > image.getWidth() || y > image.getHeight() ) {
			System.out.println("huh?");
			return;
		}
		
		if(edit){
			for(Point pointy: currentPolygon){
				if(x<(pointy.getX()+8)&&(x>(pointy.getX()-8))&&(y>(pointy.getY()-8))&&(y<(pointy.getY()+8))){
					indexP =currentPolygon.indexOf(pointy);
					currentPolygon.remove(indexP);
					System.out.println("Point1 "+indexP);
					try {
						windowB.setSize(800,750);
						windowB.validate();
						windowB.repaint();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					move = true;
					return;
				}
			}
		}
		
		for(ArrayList<Point> currenty: polygonsList){
		for(Point pointy: currenty){
		if(x<(pointy.getX()+8)&&(x>(pointy.getX()-8))&&(y>(pointy.getY()-8))&&(y<(pointy.getY()+8))){
			JOptionPane.showMessageDialog(null, "Cannot place a point ontop of another.");
			return;
		}
		}
		}
		
		Graphics2D g = (Graphics2D)this.getGraphics();

		//if the left button than we will add a vertex to poly
		if (e.getButton() == MouseEvent.BUTTON1) {
			if(move){
				move = false;
				currentPolygon.add(indexP, new Point(x,y));
				try {
					windowB.setSize(800,750);
					windowB.validate();
					windowB.repaint();;
				} catch (Exception e1) {
				}
			}
			else{
			if(edit){
			g.setColor(Color.BLUE);
			}else{
			g.setColor(Color.GREEN);
			}
			
			if (currentPolygon.size() != 0) {
				Point lastVertex = currentPolygon.get(currentPolygon.size() - 1);
				g.drawLine(lastVertex.getX(), lastVertex.getY(), x, y);
			}
			g.fillOval(x-5,y-5,10,10);

			currentPolygon.add(new Point(x,y));
			System.out.println(x + " " + y);
			}
		} 
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}