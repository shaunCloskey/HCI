package hci;
import hci.utils.Point;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;


/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame{

	/**
	 * List of all objects
	 */
	ArrayList<ArrayList<Point>> polygonsList = new ArrayList<ArrayList<Point>>();

	/**
	 * Holds all points in one object
	 */
	ArrayList<Point> currentPolygon = new ArrayList<Point>();


	/**
	 * list of all the names given to each of the polygons
	 */
	ArrayList<String> polygonNames = new ArrayList<String>();

	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;


	static ImageLabeller window = new ImageLabeller();
	
	int indexEdit;
	/**
	 * main window panel
	 */
	JPanel appPanel = null;
	static boolean hello = true;
	boolean blue = false;
	int blueIndex;
	/**
	 *
	 * tool box - put all buttons and stuff here!
	 */
	JPanel toolboxPanel = null;
	
	
	/**
	 * contains all the names of the polygons
	 */
	JList nameList = null;

	/**
	 * image panel - displays image and editing area
	 */
	ImagePanel imagePanel = null;
	
	/**
	 * used for the displaying of all names of the polygons
	 */
	JScrollPane listScroller;
	
	String currentFile = "U1003_0000.jpg";
	
	static String currentFileOut ="U1003_0000";
	
	JFrame loadFrame = new JFrame("load");
	
	String currentNode ="";
	
	/**
	 * handles New Object button action
	 * @throws Exception 
	 */
	public void addNewPolygon() throws Exception {
		
		String name = JOptionPane.showInputDialog("Name the polygon");
		if(name==null){
			return;
		}
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(null, "Object must be given a name!\nObject not saved.");
			return;
		}
		
		imagePanel.addNewPolygon(name);
    	
    	saveFile();
    	window.setupGUI("src/images/" + currentFile);
		window.setSize(800,750);
		window.validate();
		window.repaint();
		
	}
	
	
	
	DefaultMutableTreeNode addNodes(DefaultMutableTreeNode currentTop, File dir) {
	    String currentPath = dir.getPath();
	    DefaultMutableTreeNode currentDir = new DefaultMutableTreeNode(currentPath);
	    if (currentTop != null) { // should only be null at root
	      currentTop.add(currentDir);
	    }
	    
	    Vector ol = new Vector();
	    String[] tmp = dir.list();
	    
	    for (int i = 0; i < tmp.length; i++)
	    	ol.addElement(tmp[i]);
	    
	    Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
	    File f;
	    Vector files = new Vector();
	    
	    // Make two passes, one for Dirs and one for Files. This is #1.
	    for (int i = 0; i < ol.size(); i++) {
	      String thisObject = (String) ol.elementAt(i);
	      String newPath;
	      if (currentPath.equals("."))
	        newPath = thisObject;
	      else
	        newPath = currentPath + File.separator + thisObject;
	      if ((f = new File(newPath)).isDirectory())
	        addNodes(currentDir, f);
	      else
	        files.addElement(thisObject);
	    }
	    // Pass two: for files.
	    for (int fnum = 0; fnum < files.size(); fnum++)
	      currentDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
	    return currentDir;
	  }
	
	//save the polygons before loading
	public void saveFile(){
			imagePanel.setOpaque(true); //content panes must be opaque
			
			//write the objects to a file
	  			
	  			File f = new File(currentFileOut + ".txt");
	  			if(!f.exists())
	  			{
	  				try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
	  				System.out.println("New file \" " + currentFileOut + ".txt\" has been created to the current directory");
	  			}
	  			
	  			
	  			FileWriter filey = null;
				try {
					filey = new FileWriter( currentFileOut + ".txt");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	  			
	  			PrintWriter out = new PrintWriter(filey);
	  			polygonsList = imagePanel.returnPolygons();
	  			polygonNames = imagePanel.returnPolyNames();
	  			
	  			out.println("#");
	  			int polyIndex = 0;
	  			
	  			for (ArrayList<Point> arrayCounter: polygonsList){
	  				out.println(polygonNames.get(polyIndex));
	  				for(Point pointCounter: arrayCounter){
	  						out.println(new Integer(pointCounter.getX()).toString());
	  						out.println(new Integer(pointCounter.getY()).toString());
	  				}
	  				out.println("#");
	  				polyIndex++;
	  			}
	  		polyIndex =0;
			out.close();
	}
	
	
	public void load(File dir){
		System.out.println("tryng to load a file");
		
		loadFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loadFrame.setLayout(new BorderLayout());
		JTree tree = new JTree(addNodes(null, dir));   
	    
	    
	    // Add a listener
	    tree.addTreeSelectionListener(new TreeSelectionListener() {
	      public void valueChanged(TreeSelectionEvent e) {
	        DefaultMutableTreeNode SelectedNode = (DefaultMutableTreeNode) e
	            .getPath().getLastPathComponent();
	        currentNode = "" + SelectedNode;
	        System.out.println("You selected " + SelectedNode);
	      }
	    });

	    JButton loadButton = new JButton("Load");
	    loadButton.setMnemonic(KeyEvent.VK_N);
		loadButton.setSize(50, 20);
		loadButton.setEnabled(true);
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	System.out.println("selected : " + currentNode);
			    	
			    	
			    	saveFile();
			    	
			    	polygonsList = new ArrayList<ArrayList<Point>>();
			    	currentPolygon = new ArrayList<Point>();
			    	polygonNames = new ArrayList<String>();
			    	
			    	imagePanel.polygonsList = new ArrayList<ArrayList<Point>>();
			    	imagePanel.currentPolygon = new ArrayList<Point>();
			    	imagePanel.polygonNames = new ArrayList<String>();
			    	
			    	currentFile = currentNode;
			    	
			    	int stringIndex = currentFile.indexOf(".jpg");
			    	currentFileOut = currentFile.substring(0, stringIndex);
			    	
					try {
						reLoad(currentFile);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
			    	loadFrame.dispose();	
			    	
			}
		});
		loadButton.setToolTipText("Click to confirm the load");
		
	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.setMnemonic(KeyEvent.VK_N);
		cancelButton.setSize(50, 20);
		cancelButton.setEnabled(true);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	System.out.println("cancel file load");
			    	loadFrame.dispose();
			}
		});
		cancelButton.setToolTipText("Click to cancel load");
		
		
	    // Lastly, put the JTree into a JScrollPane.
	    JScrollPane scrollpane = new JScrollPane();
	    scrollpane.getViewport().add(tree);
	    loadFrame.add(BorderLayout.NORTH, scrollpane);
	    
	    //add the buttons for load and cancel to the panel
	    loadFrame.add(BorderLayout.EAST, loadButton);
	    loadFrame.add(BorderLayout.WEST, cancelButton);
		
	    
	    loadFrame.pack();
		loadFrame.setVisible(true);
		
	}
	
	public void reLoad(String file) throws Exception
	{
		
		//needs to load the polygon names and such from file.
		System.out.println("gettingPolys");
		window.getPolygons();
		
		imagePanel.currentPolygon = currentPolygon;
		imagePanel.polygonNames = polygonNames;
		imagePanel.polygonsList = polygonsList;
		
		
		window.setupGUI("src/images/" + currentFile);
		window.setSize(800,750);
		window.validate();
		window.repaint();
		
	}
	
	public void displayList(ArrayList<String> polygonNames) {
		String [] name = new String[polygonNames.size()];
		int polyIndex = 0;
		for(String internalName : polygonNames)
		{
			name[polyIndex] = internalName;
			polyIndex++;
		}
		nameList = new JList(name);
		nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nameList.setLayoutOrientation(JList.VERTICAL);
		nameList.setVisibleRowCount(-1);
		nameList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
					blue = true;
					blueIndex = nameList.getSelectedIndex();
					window.setSize(800,750);
					window.validate();
					window.repaint();
			}
		});
		listScroller = new JScrollPane(nameList);
		listScroller.setPreferredSize(new Dimension(150, 80));
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.paint(g); //update image panel
	}
	/**
	 * sets up application window
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI(final String imageFilename) throws Exception {
		this.addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent event) {
				imagePanel.setOpaque(true); //content panes must be opaque
		  		
		  		int confirmExit=0;
		  		boolean save = false;
		  		//Choose which confirmation message to display
		  		if((imagePanel.currentPolygon).isEmpty()){
		  		String exitMessage = "Are you sure you want to exit?";
		  		confirmExit = JOptionPane.showConfirmDialog(new JFrame(), exitMessage, "Confirm Exit",JOptionPane.YES_NO_OPTION);
		  		}else{
		  			String exitMessage = "Do you want to save the current object before exiting?";
		  			confirmExit = JOptionPane.showConfirmDialog(new JFrame(), exitMessage, "Confirm Exit",JOptionPane.YES_NO_CANCEL_OPTION);	
		  			save = true;
		  		}
		  		if (((confirmExit < 2) && (save))||((!save) && (confirmExit == 0))){
		  			if(save && confirmExit ==0){
		  				imagePanel.addNewPolygon("new unlabeled Polygon");
		  			}
		  			//write the objects to a file
		  			
		  			File f = new File(currentFileOut + ".txt");
		  			if(!f.exists())
		  			{
		  				try {
							f.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
		  				System.out.println("New file \" " + currentFileOut + ".txt\" has been created to the current directory");
		  			}
		  			
		  			
		  			FileWriter filey = null;
					try {
						filey = new FileWriter( currentFileOut + ".txt");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					PrintWriter out = new PrintWriter(filey);
		  			polygonsList = imagePanel.returnPolygons();
		  			polygonNames = imagePanel.returnPolyNames();
		  			
		  			out.println("#");
		  			int polyIndex = 0;
		  			
		  			for (ArrayList<Point> arrayCounter: polygonsList){
		  				if(polyIndex !=polygonNames.size())
		  				{
		  					out.println(polygonNames.get(polyIndex));
		  				}
		  				for(Point pointCounter: arrayCounter){
		  						out.println(new Integer(pointCounter.getX()).toString());
		  						out.println(new Integer(pointCounter.getY()).toString());
		  				}
		  				out.println("#");
		  				polyIndex++;
		  			}
				out.close();

				//Close the program
		  		System.out.println("Bye bye!");
		  		System.exit(0);
		  		}else{
		  		//User has canceled the exit.
		  		System.out.println("Exit aborted.");
		  		}

		  	}
		});

		//setup main window panel
		appPanel = new JPanel();
		if(hello){
		this.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));
		}
		this.setContentPane(appPanel);


        //Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename, polygonsList, polygonNames);
		imagePanel.windowB = window;
		imagePanel.setOpaque(true); //content panes must be opaque

        appPanel.add(imagePanel);

        //create toolbox panel
        toolboxPanel = new JPanel();
        
        //Add button
		JButton saveButton = new JButton("Save Object");
		saveButton.setMnemonic(KeyEvent.VK_N);
		saveButton.setSize(50, 20);
		saveButton.setEnabled(true);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(imagePanel.edit){
					imagePanel.edit = false;
					imagePanel.move = false;
					if(imagePanel.currentPolygon.size()==0){
					String yDeleteMessage = "No points to save.\nDo you want to delete the object entirely?";
					int yDelete = JOptionPane.showConfirmDialog(new JFrame(), yDeleteMessage, "Object Error",JOptionPane.YES_NO_OPTION);
					if(yDelete == 0){
						int index = nameList.getSelectedIndex();
					imagePanel.polygonsList.remove(index);
					imagePanel.polygonNames.remove(index);			
					saveFile();
					try {
						window.setupGUI("src/images/" + currentFile);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					window.setSize(800,750);
					window.validate();
					window.repaint();
					return;
					}else{
						return;
					}
					}else{
					imagePanel.finishPolygon(currentPolygon);
					imagePanel.currentPolygon = new ArrayList<Point>();
					window.setSize(800,750);
					window.validate();
					window.repaint();
					}
				}else{
					if(imagePanel.currentPolygon.isEmpty()){
						JOptionPane.showMessageDialog(null, "No points to save.");
						return;
					}
			    	try {
						addNewPolygon();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		saveButton.setToolTipText("Click to save object");
		
		//add a button to load a picture
		JButton loadButton = new JButton("Load a New Picture");
		loadButton.setMnemonic(KeyEvent.VK_N);
		loadButton.setSize(50, 20);
		loadButton.setEnabled(true);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load(new File("../HCI/src/images"));
			}
		});
		loadButton.setToolTipText("click to load objects");
		
		//add a button to load a picture
		JButton editButton = new JButton("Edit object");
		editButton.setMnemonic(KeyEvent.VK_N);
		editButton.setSize(50, 20);
		editButton.setEnabled(true);
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(nameList.getSelectedValue() == null){
					System.out.println("no selected object.");
				}else{
				indexEdit = nameList.getSelectedIndex();
				String renameMessage = "Do you want to rename selected object?";
		  		int confirmRename = JOptionPane.showConfirmDialog(new JFrame(), renameMessage, "Confirm Exit",JOptionPane.YES_NO_OPTION);
		  		if(confirmRename==0){
		  			String newname = JOptionPane.showInputDialog("Please input the new name: ");
		  			if(newname !=null){
		  			imagePanel.polygonNames.remove(indexEdit);
		  			imagePanel.polygonNames.add(indexEdit,newname);
		  			saveFile();
					try {
						window.setupGUI("src/images/" + currentFile);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					window.setSize(800,750);
					window.validate();
					window.repaint();
		  			}
		  		}
		  		imagePanel.currentFile = currentFile;
		  		imagePanel.currentPolygon = imagePanel.polygonsList.get(indexEdit);
		  		imagePanel.edit = true;
		  		}
			}
		});
		editButton.setToolTipText("click to edit objects");
		
		JButton deleteButton = new JButton("Delete object");
		deleteButton.setMnemonic(KeyEvent.VK_N);
		deleteButton.setSize(50, 20);
		deleteButton.setEnabled(true);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(imagePanel.polygonsList.isEmpty()){
					int choice = JOptionPane.showConfirmDialog(new JFrame(), "Delete current object?", "Confirm Delete",JOptionPane.YES_NO_OPTION);
					if(choice==0){
						imagePanel.currentPolygon = new ArrayList<Point>();
						window.setSize(800,750);
						window.validate();
						window.repaint();
					}else{
						return;
					}
				}
				else{
				int index = nameList.getSelectedIndex();
				int choice = JOptionPane.showConfirmDialog(new JFrame(), "Delete currently selected object?", "Confirm Delete",JOptionPane.YES_NO_OPTION);
				if(choice==0){
					if(index==-1){
						imagePanel.currentPolygon = new ArrayList<Point>();
					}else{
				imagePanel.polygonsList.remove(index);
				imagePanel.polygonNames.remove(index);
				saveFile();
					}
				try {
					window.setupGUI("src/images/" + currentFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				window.setSize(800,750);
				window.validate();
				window.repaint();
			}
				}
			}
			
		});
		deleteButton.setToolTipText("click to delete objects");
		
		
		displayList(polygonNames);
		
		toolboxPanel.add(saveButton);
		toolboxPanel.add(loadButton);
		toolboxPanel.add(editButton);
		toolboxPanel.add(deleteButton);
		toolboxPanel.add(listScroller);

		//add toolbox to window
		appPanel.add(toolboxPanel);

		//display all the stuff
		this.pack();
        this.setVisible(true);
	}

	int readLines() throws IOException{
		//Just finds out how many lines are in the text file
		FileReader lineCountF = new FileReader("out.txt");
		BufferedReader lineCountB = new BufferedReader(lineCountF);
		String line = null;
		int lineNumber = 0;

		while ((line = lineCountB.readLine()) != null){
			lineNumber++;
		}
		lineCountB.close();
		return lineNumber;
	}


	public void getPolygons() throws IOException{
		//This method reads the defined objects from the file
		
		File f = new File(currentFileOut + ".txt");
		if(!f.exists())
		{
			f.createNewFile();
			System.out.println("New file \" " + currentFileOut + ".txt\" has been created to the current directory");
		}
		
		
		
		FileReader inFile = new FileReader(currentFileOut + ".txt");
		
		BufferedReader reader = new BufferedReader(inFile);
		int numberLines = 0;
		while(reader.readLine()!=null)
		{
			numberLines++;
		}
		
		reader.close();
		int currentLine = 0;
		
		inFile = new FileReader(currentFileOut + ".txt");
		BufferedReader in = new BufferedReader(inFile);
		
		System.out.println("no of lines= " + numberLines);
		
		if(numberLines==0)
		{
			return;
		}
		
		boolean firstPoly = true;
		
		while(true){
		String xcoord = in.readLine();
		System.out.println(xcoord);
		currentLine++;
		//end of object
		if(xcoord.equals("#")){
			System.out.println("Object read.");
			if(!firstPoly){
				polygonsList.add(currentPolygon);
			}
			currentPolygon = new ArrayList<Point>();
			//end of file
			
			if(currentLine == numberLines){
				in.close();
				return;
			}
			
			xcoord = in.readLine();
			System.out.println("object name : " + xcoord);
			polygonNames.add(xcoord);
			currentLine++;
			firstPoly = false;
			
			
			
		//y coord comes next
		}else{
			String ycoord = in.readLine();
			currentLine++;
			Point tempPoint = new Point();
			tempPoint.setX(Integer.parseInt(xcoord));
			tempPoint.setY(Integer.parseInt(ycoord));
			//add these points to the current object
			currentPolygon.add(tempPoint);
		}
		
		}
	}

	/**
	 * Runs the program
	 * @param argv path to an image
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String argv[]) throws IOException{
		try {
			//create a window and display the image
			window.getPolygons();
			//Let the user confirm the app closing.
			window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			window.setTitle("Image Labeler");
			window.setupGUI("src/images/U1003_0000.jpg");
			hello = false;
			window.setSize(800,750);
		} catch (Exception e) {
			System.err.println("Image: " + "src/images/U1003_0000.jpg");
			e.printStackTrace();
		}
	}
	
}











