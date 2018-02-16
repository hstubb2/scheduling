package chaitinChandraOptimization;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cse131.ArgsProcessor;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import heaps.Decreaser;
import heaps.MinHeap;
import timing.Ticker;

public class ChaitinChandra<V,E> implements GraphColoring<V,E> {
	
	private final Graph<V,E> graph;
	private final Map<V, Color> ans;
	private final Set<Course> classes;
	private final V[] peopleArray;
	
	public ChaitinChandra(Graph<V,E> g, Set<Course> classes, V[] peopleArray) {
		this.classes = classes;
		this.graph = g;
		this.ans = new HashMap<V,Color>();
		this.peopleArray = peopleArray;
	}

	@Override
	public void run() {
		Stack<Course> colorStack = new Stack<Course>();
		int numColors = 1;
		Color[] colorsArray = new Color[numColors];
		Ticker ticker = new Ticker();
		MinHeap<CourseAndDegree> heap = new MinHeap<CourseAndDegree>(classes.size(), ticker);
		Map<Course, Decreaser<CourseAndDegree>> decreaserMap = new HashMap<Course, Decreaser<CourseAndDegree>>();
		Graph<Course, String> newGraph = new SparseMultigraph<Course, String>();

		// ATTEMPTS AT COMPLETING GRAPH COLORING
		for (V v : graph.getVertices()) {
			ans.put(v, null);
		}

		for (V v : graph.getVertices()) {
			Decreaser<CourseAndDegree> handle = heap.insert(new CourseAndDegree((Course) v, graph.degree(v)));
			decreaserMap.put((Course) v, handle);
		}
		System.out.println(heap.toString());
		while (!heap.isEmpty()) {
			pushColorStack(heap, (Graph<Course, String>) graph, decreaserMap, colorStack);
		}

		//int[] boolTable = new int[colorsArray.length];
		boolean colored = false;
		//---------------------------------------------------------------------------------------------------------
		colorsArray = new Color[1];
		while (!colorStack.isEmpty() && !colored) {
			//++numColors;
		/*	Color[] tempArray = new Color[numColors];
			for (int i = 0; i < colorsArray.length; i++)
			{
				tempArray[i] = colorsArray[i];
			}
			colorsArray = tempArray;
			*/
			int[] boolTable = new int[colorsArray.length];
			if (colorsArray[0] == null)
			{
				fillBiggerColorArray(colorsArray, colorsArray.length);
			}
			
			
			
			for (int i = 0; i < boolTable.length; ++i) {
				boolTable[i] = 0;
			}
			Course thisVert = colorStack.pop();
			newGraph.addVertex(thisVert);
			checkNewConflict(newGraph, (Person[]) peopleArray, thisVert);
			fillBoolTable(thisVert, newGraph, colorsArray, boolTable, (Map<Course, Color>) ans);
			
			boolean openSpot = false;
			for (int i = 0; i < boolTable.length; i++)
			{
				if (boolTable[i] == 0)
				{
					openSpot = true;
				}
			}
			
			if (!openSpot)
			{
				++numColors;
				Color[] tempArray = new Color[numColors];
				for (int i = 0; i < colorsArray.length; i++)
				{
					tempArray[i] = colorsArray[i];
				}
				colorsArray = tempArray;
				fillBiggerColorArray(colorsArray, colorsArray.length);
				boolTable = new int[colorsArray.length];
				fillBoolTable(thisVert, newGraph, colorsArray, boolTable, (Map<Course, Color>) ans);
			}
			
			colored = checkColored((Map<Course, Color>) ans);
		}
		//---------------------------------------------------------------------------------------------------------
		
	/*	while (!colorStack.isEmpty() && !colored) {
			++numColors;
			colorsArray = new Color[numColors];
			fillColorArray(colorsArray, numColors);
			for (int i = 0; i < boolTable.length; ++i) {
				boolTable[i] = 0;
			}
			Course thisVert = colorStack.pop();
			newGraph.addVertex(thisVert);
			checkNewConflict(newGraph, (Person[]) peopleArray, thisVert);
			fillBoolTable(thisVert, newGraph, colorsArray, boolTable, (Map<Course, Color>) ans);
			colored = checkColored((Map<Course, Color>) ans);
		}*/
		
		
		//-------------------------------------------------------------------------------------------------
		int colorCount = 0;
		Set<Color> cols = new HashSet<Color>();
		for (Color c : ans.values())
		{
			if (!cols.contains(c))
			{
				colorCount++;
				cols.add(c);
			}
		}
		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------
		boolean correct = true;
		for (Course c : newGraph.getVertices())
		{
			for (Course n : newGraph.getNeighbors(c))
			{
				if (ans.get(c).equals(ans.get(n)))
				{
					correct = false;
				}
			}
		}
		//-------------------------------------------------------------------------------------------------
		System.out.println("Number of courses: " + classes.size());
		System.out.println("Colors needed: " + colorCount);//(numColors));
		System.out.println("Correct: " + correct);
	}

	@Override
	public Map<V, Color> getColoring() {
		return ans;
	}
	
	

	public static void fillColorArray(Color[] colorsArray, int numColors) {
		for (int i = 0; i < numColors; ++i) {
			Color newColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
					(int) (Math.random() * 255));
			for (int j = 0; j < i; ++j) {
				if (newColor.getRed() == colorsArray[j].getRed() && newColor.getGreen() == colorsArray[j].getGreen()
						&& newColor.getBlue() == colorsArray[j].getBlue()) {
					newColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
							(int) (Math.random() * 255));
					j = -1;
				}
			}
			colorsArray[i] = newColor;
		}
	}
	
	public static void fillBiggerColorArray(Color[] colorsArray, int numColors) {
		for (int i = 0; i < numColors; ++i) {
			if (colorsArray[i] != null)
			{
				continue;
			}
			Color newColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
					(int) (Math.random() * 255));
			for (int j = 0; j < i; ++j) {
				if (newColor.getRed() == colorsArray[j].getRed() && newColor.getGreen() == colorsArray[j].getGreen()
						&& newColor.getBlue() == colorsArray[j].getBlue()) {
					newColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
							(int) (Math.random() * 255));
					j = -1;
				}
			}
			colorsArray[i] = newColor;
		}
	}
	
	public static void pushColorStack(MinHeap<CourseAndDegree> heap, Graph<Course, String> graph,
			Map<Course, Decreaser<CourseAndDegree>> decreaserMap, Stack<Course> colorStack) {
		final CourseAndDegree min = heap.extractMin();
		System.out.println(min + " vertex with min degree");
		System.out.println(heap.toString());
		// FIXME
		// for loop may not hit all courses. Ex: A course without conflict (no edges)
		for (Course c : graph.getNeighbors(min.course)) {
			Decreaser<CourseAndDegree> cd = decreaserMap.get(c);
			cd.decrease(cd.getValue().sameCourseLowerDegree());
			// System.out.println(" Lowered " + cd);
		}
		colorStack.push(min.course);
	}
	
	public static void checkNewConflict(Graph<Course, String> newGraph, Person[] peopleArray, Course thisVert) {
		for (Course c : newGraph.getVertices()) {
			for (Person p : peopleArray) {
				if (!c.equals(thisVert) && p.getCourses().contains(c) && p.getCourses().contains(thisVert)
						&& !newGraph.containsEdge("conflict" + " " + c + " " + thisVert)
						&& !newGraph.containsEdge("conflict" + " " + thisVert + " " + c)) {
					newGraph.addEdge("conflict" + " " + c + " " + thisVert, c, thisVert);
				}
			}
		}
	}
	
	public static void fillBoolTable(Course thisVert, Graph <Course, String> newGraph, Color[] colorsArray, int[] boolTable, Map<Course, Color> colorHash){
		for (Course c : newGraph.getNeighbors(thisVert)) {
			for (int i = 0; i < colorsArray.length; ++i) {
				if (colorsArray[i].equals(colorHash.get(c))) {
					boolTable[i] = 1;
				}
			}
		}
		for (int i = 0; i < boolTable.length; ++i) {
			if (boolTable[i] == 0) {
				colorHash.replace(thisVert, null, colorsArray[i]);
				break;
			}
		}
	}
	
	public static boolean checkColored(Map<Course, Color> colorHash){
		// FIXME
		// colorHash.get() should take in a Course as its
		boolean colored = true;
		for (int i = 0; i < colorHash.size(); ++i) {
			if (colorHash.get(i) == null) {
				colored = false;
			}
		}
		return colored;
	}
	
}
