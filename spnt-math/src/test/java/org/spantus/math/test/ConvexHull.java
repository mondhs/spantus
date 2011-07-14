package org.spantus.math.test;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConvexHull {
	// This program runs the two convex hull algorithms on 2D input from 
	// text file. The text input file format is a simple numbers 
	// separated by white space, x coordinate first. 
	// Both Brute-force and quickhull are run. It is your job to finish 
	// implementing quickhull. 
	    public static void main (String args[]) {
	        // insert code here...
			Point2D.Double[] inputpoints, outputpolygon;
			String inputfilename;
			
			// if you enter a command line argument, assume it's the filename of the
			// text file containing the input points; otherwise ask the user for the input filename. 
			if (args.length >0) {
			  inputfilename = args[0];
			}
			else {
			  System.out.print("Enter file name for input points: ");
			  Scanner sc = new Scanner(System.in);
			  inputfilename = sc.next();
			}
			
			System.out.println("Reading input points from file " + inputfilename);
			inputpoints = readPoints(inputfilename);
	 
	// debugging print statements 		
	/*
			for(int i = 0; i < inputpoints.length; i++)
			  System.out.println(i + ") " + inputpoints[i]);
	*/	
			// call the convex-hull algorithms & print results.
	        outputpolygon = bruteForceConvexHull(inputpoints); 

			System.out.println("Polygon result from brute-force: ");
			for(int i = 0; i < outputpolygon.length; i++) {
			  if (outputpolygon[i] == null) break;
			  System.out.println(i + ") " + outputpolygon[i]);
			}
	    
	        outputpolygon = QuickHull(inputpoints); 
			System.out.println("Polygon result from QuickHull: ");
			for(int i = 0; i < outputpolygon.length; i++) {
			  if (outputpolygon[i] == null) break;
			  System.out.println(i + ") " + outputpolygon[i]);
			}		
	     }
		 
		 
		// Creates array of Point2D.Double read from the input filename. 
		// returns array of points. 
	   public static Point2D.Double [] readPoints(String filename) {
		  Point2D.Double[] outputpoints;
		  Scanner sc; 
		  try {
		     sc = new Scanner(new File (filename));
		  } catch (IOException e) {
		    System.out.println("Error reading file " + filename);
			return null;
		  }
		  
		  // first create an ArrayList of points, so we don't have to 
		  // worry about how many there are when reading the file.
	    
		  ArrayList<Point2D.Double> temppoints = new ArrayList<Point2D.Double>(); 

	      //loop for reading one point at a time. 
		  while (sc.hasNextDouble()) {
		     double xval, yval;
			 xval = sc.nextDouble();		 
		     if (sc.hasNextDouble() == false)  {
			   System.out.println("Warning: last point has no y-coordinate; assigning y-coord of zero");
			   yval =0.0;
			 }
			 else yval = sc.nextDouble();
			 temppoints.add(new Point2D.Double(xval,yval)); 
		  }

//	    Former debugging print statement.	  
//		  System.out.println(temppoints);

		  //convert the arraylist into an array of the right type, and return it. 
		  // the input parameter to toArray makes sure the types are correct. 
		  // let me know if you know a better way to do this..
		  outputpoints =  temppoints.toArray(new Point2D.Double[1]);
		  return outputpoints;
		}

	// executes the brute-force algorithm for finding the convex hull of its input points.
	// The output polygon is an array of vertices in order. The array size of the polygon 
	// may be larger than the number of vertices; if this the case, the index after the 
	// last vertex stores a null Object. 
	   public static Point2D.Double [] bruteForceConvexHull(Point2D.Double [] inputpointset) {
	     Point2D.Double[] result = new Point2D.Double[inputpointset.length];
		 
		 // first search for the point with minimum x value and start there. 
		 Point2D.Double startpoint, p1, p2;
		 startpoint = inputpointset[0];
		 int i;
		 for (i=1; i < inputpointset.length; i++) {
		   if (startpoint.x > inputpointset[i].x) 
		      startpoint = inputpointset[i];
		 }
		 result[0] = startpoint;

	     // now start a loop, searching for the next vertex after the first one. 
		 // in this code, we always search in the clockwise direction.
	     int currentvertexindex = 0;
		 Point2D.Double currentvertex = result[currentvertexindex];
		 Point2D.Double pointtocheck = null;
		 boolean done;
		 if (inputpointset.length==1) done = true;
		 else done = false;
		 while (!done) {
		   // loop through each point to see if it is the next vertex
		   for (i=0; i < inputpointset.length; i++) { 
			  pointtocheck = inputpointset[i];
			  // can't test equal points because they don't form a line.
			  if (!pointtocheck.equals(currentvertex)) {
			      // check to see if all points are on right side of line 
				  //   from currentvertex to pointtocheck.
			      if (allPointsOnRight(currentvertex, pointtocheck, inputpointset)) {
					  // pointtocheck is a vertex of polygon; break out. 
					  break;
				  }
			   }
		    }
			// at this point, we should have found a vertex. 
			// if i== inputpointset.length at this point, we didn't find a vertex, but that
			// should never happen!
			
			// if the found vertex is the same as the first vertex, we have finished. 
			// otherwise, add in the vertex and continue the loop.
			if (pointtocheck.equals(result[0])) done = true;
			else {
			// add a new vertex, get ready for next loop iteration.
				currentvertexindex++;
				result[currentvertexindex] = pointtocheck;
				currentvertex = pointtocheck;
			}
		 }
			 
	     return result;
	   }

	   // Takes input points p1 and p2, array of points A. 
	   // consider the line from p1 to p2. 
	   // if all points in A are on the line from p1 to p2 or to the right 
	   // of the line (The "right" side of the line is the one where
	   // you are assumed to be at p1 looking towards p2)   
	   public static boolean allPointsOnRight(Point2D.Double p1, Point2D.Double p2, Point2D.Double[] A) {
		  // check all points to see if they are all on the right. 
		  // use the properties of the determinant. 
		  for (int i=0; i < A.length; i++) {
		     if (determinantformula(p1, p2, A[i]) > 0)  
			    return false;
		  } 
		  
		  return true;
	   }
	   
	   // for input points p1 = (x1,y1), p2 = (x2,y2), p3 = (x3,y3), computes the determinant 
	   //  | x1 y1 1 |
	   //  | x2 y2 1 |
	   //  | x3 y3 1 |
	   // This determinant is positive when point p3 is to the left of the line
	   //  from p1 to p2, and negative when point p3 is to the right of the line
	   // Furthermore, the magnitude of this determinant is the twice the area 
	   //  of the enclosed triangle.
	   public static double determinantformula(Point2D.Double p1,Point2D.Double p2, Point2D.Double p3) {
	      return (p1.x * p2.y + p3.x * p1.y + p2.x * p3.y 
				- p3.x * p2.y - p2.x * p1.y - p1.x * p3.y);
	   } 

	// executes the QuickHull algorithm for finding the convex hull of its input points. 
	// The output polygon is an array of vertices in order. The array size of the polygon 
	// may be larger than the number of vertices; if this the case, the index after the 
	// last vertex stores a null Object. 
	   public static Point2D.Double [] QuickHull(Point2D.Double [] inputpointset) {
	      System.out.println("QuickHull is not yet implemented and currently returns its input");
	      return inputpointset;
	   }
}
