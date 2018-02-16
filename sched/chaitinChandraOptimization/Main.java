package chaitinChandraOptimization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cse131.ArgsProcessor;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import heaps.Decreaser;
import heaps.MinHeap;
import timing.Ticker;

public class Main {
	//make an object that deals with conflict
	//infterf
	//be as non specific to courses and conflict as possible when implementing a class 
	
	public static void readData(HashSet<Person> peopleSet, HashSet<Course> courseSet) throws IOException {
		int countCols = 0;
		// 
		// 45 courses
		// start thinking about making only 2 exams for each person a day
		// start with number of courses, then find the lowest number of colors
		// needed to fully color the graph.. maybe using binary search?
		File myFile = new File("sched/chaitinChandraOptimization/Cytron1.xlsx");
		FileInputStream fis = new FileInputStream(myFile);
		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next(); // For each row, iterate through each
											// columns
			Iterator<Cell> cellIterator = row.cellIterator();
			Person p = new Person("SAMPLE");
			Course c = new Course("SAMPLE");
			String a = "";
			String b = "";
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (countCols == 0) {
					a = cell.toString();
				}
				if (countCols == 1) {
					b = cell.toString();
				}
				if (countCols == 2) {
					c = new Course(a + b);
					courseSet.add(c);
				}
				if (countCols == 7) {
					String s = cell.toString();
					// c = new Course(s);
					// courseSet.add(c);
					boolean contained = false;
					for (Person peep : peopleSet) {
						if (peep.getName() == s) {
							p = peep;
							contained = true;
							break;
						}
					}
					if (!contained) {
						p = new Person(s);
					}
					peopleSet.add(p);
					// System.out.println(s);
					p.addCourse(c);
				}

				++countCols;
				if (countCols == 8) {
					countCols = 0;
				}
			}
		}
	}
	
	public static void checkConflict(Set<Course> classes, Person[] peopleArray, Graph<Course, String> graph) {
		for (Course c : classes) {
			for (Course d : classes) {
				if (!c.equals(d)) {
					for (Person p : peopleArray) {
						if (p.getCourses().contains(c) && p.getCourses().contains(d)
								&& !graph.containsEdge("conflict" + " " + c + " " + d)
								&& !graph.containsEdge("conflict" + " " + d + " " + c)) {
							graph.addEdge("conflict" + " " + c + " " + d, c, d);
						}
					}
				}
			}
		}
	}

	public static void fillArrays(Iterator coursesIterator, Course[] courseArray, Iterator peopleIterator,
			Person[] peopleArray) {
		for (int i = 0; i < peopleArray.length; ++i) {
			peopleArray[i] = (Person) peopleIterator.next();
		}

		for (int i = 0; i < courseArray.length; ++i) {
			courseArray[i] = (Course) coursesIterator.next();
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("---1---");
		HashSet<Person> peopleSet = new HashSet();
		HashSet<Course> courseSet = new HashSet();
		try {
			readData(peopleSet, courseSet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---2---");
		Course[] courseArray = new Course[courseSet.size()];
		Person[] peopleArray = new Person[peopleSet.size()];
		int numColors = 0;
		Set<Course> classes = new HashSet<Course>();
		Ticker ticker = new Ticker();
		Graph<Course, String> graph = new SparseMultigraph<Course, String>();
		Graph<Course, String> newGraph = new SparseMultigraph<Course, String>();
		Iterator coursesIterator = courseSet.iterator();
		Iterator peopleIterator = peopleSet.iterator();

		System.out.println("---3---");
		fillArrays(coursesIterator, courseArray, peopleIterator, peopleArray);

		for (Person p : peopleArray) {
			classes.addAll(p.getCourses());
		}

		for (Course c : classes) {
			graph.addVertex(c);
		}
		
		System.out.println("---4---");
		checkConflict(classes, peopleArray, graph);

		// establish heap here for size
		

		System.out.println("---5---");
		ChaitinChandra chaitin = new ChaitinChandra(graph, classes, peopleArray);
		
		System.out.println("---RUNNING CHAITIN---");
		chaitin.run();
		System.out.println("---GETTING COLORING---");
		Map<Course,Color> chaitinColorHash = chaitin.getColoring();
		
		// NEW STUFF IN TESTING --------------------------------------------------
		DayScheduler<Course> DS = new DayScheduler<Course>(chaitinColorHash, peopleArray);
		for (Set<Color> cols : DS.calcDays(500)) {
			System.out.println("Group");
			for (Color col : cols) {
				System.out.println(col.toString());
			}
		}
		// -----------------------------------------------------------------------

		Transformer<Course, Paint> vertexPaint = new Transformer<Course, Paint>() {
			public Paint transform(Course i) {
				return (Color) chaitinColorHash.get(i);
			}
		};

		Layout<Course, String> layout = new CircleLayout(graph);
		layout.setSize(new Dimension(1430, 780));
		BasicVisualizationServer<Course, String> vv = new BasicVisualizationServer<Course, String>(layout);
		vv.setPreferredSize(new Dimension(1550, 850));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
}
