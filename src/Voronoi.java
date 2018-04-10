/**
 * Randomly generates Voronoi diagrams
 * @author Rebecca Ramnauth
 * Date: 4-8-2018
 */

// image creation libraries
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

// apache poi libraries
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL; // for .xlsx files online

// javax frame libraries
import javax.imageio.ImageIO;
import javax.swing.JFrame;

// java math libraries
import java.lang.Math.*;

public class Voronoi extends JFrame {
    static double p = 3;
    static BufferedImage I;
    static double px[], py[], divisor;      //divisor requires scaling
    static int color[], size = 1001;  //size requires data transform
    static boolean flag;
    
    public Voronoi(int cells, double[][] p) {
    	super("Voronoi Diagram");
    	setBounds(0, 0, size, size);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	int n = 0;
	Random rand = new Random();
	I = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        
        px = p[0];
	py = p[1];
        
        //px = new double[cells];
        //py = new double[cells];
        
	color = new int[cells];
	for (int i = 0; i < cells; i++) {
            //px[i] = i; //rand.nextInt(size);
            //py[i] = i; //rand.nextInt(size);
            color[i] = rand.nextInt(16777215);
 	}
	for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
		n = 0;
		for (byte i = 0; i < cells; i++) {
                    if (distance(px[i], x, py[i], y) < distance(px[n], x, py[n], y)) {
                        n = i;
                    }
		}
		I.setRGB(x, y, color[n]);
            }
	}
 
	Graphics2D g = I.createGraphics();
	g.setColor(Color.BLACK);
	for (int i = 0; i < cells; i++) {
            g.fill(new Ellipse2D .Double(px[i] - 2.5, py[i] - 2.5, 5, 5));
	}
 
	try {
            ImageIO.write(I, "png", new File("voronoi.png"));
	} catch (IOException e) {}
 
    }
 
    public void paint(Graphics g) {
	g.drawImage(I, 0, 0, this);
    }
 
    static double distance(double x1, int x2, double y1, int y2) {
	double d;
        d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); // Euclidian
    //  d = Math.abs(x1 - x2) + Math.abs(y1 - y2); // Manhattan
    //  d = Math.pow(Math.pow(Math.abs(x1 - x2), p) + Math.pow(Math.abs(y1 - y2), p), (1 / p)); // Minkovski
  	return d;
    }
    
    private static void readData(XSSFWorkbook given) throws IOException {
        Sheet sheet1 = given.getSheetAt(0);
        System.out.println("-----------------------------------------");
        System.out.println("        COMPLETED READ OF GIVEN FILE     ");
        System.out.println("-----------------------------------------");
        
        int count = 0;
        for (Row row1 : sheet1){    //consider header cells
            Cell pointerX = row1.getCell(0);
            String valueX = pointerX.toString();
            Cell pointerY = row1.getCell(0);
            String valueY = pointerY.toString();
            count++;
            System.out.println("Row: " + count + "; X: " + valueX + ", Y: " + valueY);
        }
        //calCell(given);
    }
    
    private static int calCell(Sheet data) throws IOException {
        System.out.println("var 'cell' = " + data.getPhysicalNumberOfRows());
        return data.getPhysicalNumberOfRows() - 1;
    }
    
    private static double[][] calData(XSSFWorkbook given) throws IOException {
        System.out.println("-----------------------------------------");
        System.out.println("        CREATING COORDINATE ARRAY        ");
        System.out.println("-----------------------------------------");
        
        Sheet s = given.getSheetAt(0);
        int cell = calCell(s);
        double[][] coords = new double[2][cell];
        
        flag = false;   // false = threshold req met; true = exceeded threshold
        divisor = Double.MIN_VALUE;
        
        for(int i = 0; i < cell; i++){
        //for (Row r : s) {
            Row r = s.getRow(i + 1);
            
            Cell pointerX = r.getCell(0);   // x coordinate
            Cell pointerY = r.getCell(1);   // y coordinate
            
            //convert pointers to int
            double valX = Double.parseDouble(pointerX.toString());
            double valY = Double.parseDouble(pointerY.toString());
            
            //flag greater than threshold
            if ((valX > 1000 && valX > divisor) || (valY > 1000 && valY > divisor)){
                flag = true;
                divisor = Math.max(valX, valY); // caution for: if ((x && y) > 1000)
            }
            
            //add to array
            coords[0][i] = valX;
            coords[1][i] = valY;
            
            //print --test
            //System.out.println("X: " + coords[0][i] + ", Y: " + coords[1][i]);
        }
        
        if (flag) 
            return scaleData(coords);
        return coords;
    }
    
    private static double[][] scaleData(double[][] init_coords){
        double[][] scaled_coords = new double[init_coords.length][init_coords[0].length];
        
        divisor /= 1000;
        System.out.println("Adjusted divisor: " + divisor);
        
        System.out.println("# of coords: " + init_coords[0].length);
        for (int i = 0; i < init_coords[0].length; i++){
            scaled_coords[0][i] = init_coords[0][i]/divisor;
            scaled_coords[1][i] = init_coords[1][i]/divisor;
            
            //print scaled coordinates --i = row# in excel
            /*System.out.println("i = " + (i+2) + "   X-scaled: " + scaled_coords[0][i] 
                           + ", Y-scaled: " + scaled_coords[1][i]);*/
        }
        return scaled_coords;
    }
    
    public static void main(String[] args) throws IOException {
	XSSFWorkbook given = new XSSFWorkbook ("test-data.xlsx");
        //calData(given); //testing
        new Voronoi(calCell(given.getSheetAt(0)), calData(given)).setVisible(true);
    }
}