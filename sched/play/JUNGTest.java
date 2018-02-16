package play;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class JUNGTest {

	public static void main(String[] args) {
		// Graph<V, E> where V is the type of the vertices
		// and E is the type of the edges
		// NUMBER OF PEOPLE TAKING CLASSES
		int numPeople = 9;
		int numColors = 5;
		//MAKE THE COLOR ARRAY
		Color[] colorsArray = new Color[numColors];
		for(int i = 0; i < numColors; ++i){
			Color newColor = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
			for(int j = 0; j < i; ++j){
				if (newColor.getRed() == colorsArray[j].getRed() && newColor.getGreen() == colorsArray[j].getGreen() && newColor.getBlue() == colorsArray[j].getBlue()){
					newColor = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
					System.out.println("made it?");
					j = -1;
				}
			}
			colorsArray[i] = newColor;
		}
		
		// CREATED A HASHMAP WITH NAME OF PERSON AND STRING ARRAY OF CLASSES
		Map<String, Set<String>> classes = new HashMap<String, Set<String>>();
		classes.put("a", new HashSet<String>(Arrays.asList(new String[] { "131" })));
		classes.put("b", new HashSet<String>(Arrays.asList(new String[] { "131", "132", "247", "101", "201" })));
		classes.put("c", new HashSet<String>(Arrays.asList(new String[] { "131", "155", "101" })));
		classes.put("d", new HashSet<String>(Arrays.asList(new String[] { "131", "102", "201" })));
		classes.put("e", new HashSet<String>(Arrays.asList(new String[] { "131", "201", "132" })));
		classes.put("f", new HashSet<String>(Arrays.asList(new String[] { "1", "131", "215", "208" })));
		classes.put("g", new HashSet<String>(Arrays.asList(new String[] { "2" })));
		classes.put("h", new HashSet<String>(Arrays.asList(new String[] { "3" })));
		classes.put("i", new HashSet<String>(Arrays.asList(new String[] { "4" })));
		//make a real object for courses for vertices
		//read in vertices, edges, do graph coloring and report back result
		//course has name, 
		// CREATE MY GRAPH
		Graph<String, String> graph = new SparseMultigraph<String, String>();
		// CREATED A SET OF STRINGS FOR ALL CLASSES
		Set<String> allclasses = new HashSet<String>();
		// ADDED ALL CLASSES TO SET OF ALLCLASSES
		for (String s : classes.keySet()) {
			allclasses.addAll(classes.get(s));
		}
		// ADDED VERTICES FOR ALL CLASSES
		for (String c : allclasses) {
			graph.addVertex(c);
		}
		// ADDED EDGE CONFLICT IF TWO CLASSES CONFLICTED
		for (String c : allclasses) {
			for (String d : allclasses) {
				if (!c.equals(d)) {
					for (String person : classes.keySet()) {
						if ((classes.get(person).contains(c) && classes.get(person).contains(d))
								&& !graph.containsEdge("conflict" + " " + c + " " + d)
								&& !graph.containsEdge("conflict" + " " + d + " " + c)) {
							graph.addEdge("conflict" + " " + c + " " + d, c, d);
						}
					}
				}
			}
		}
		//failed sorting by degrees
//		int[] degrees = new int[allclasses.size()];
//		int count = 0;
//		for(String s : allclasses){
//			degrees[count] = graph.degree(s);
//			++count;
//		}
		
		// TOSTRING FOR THE GRAPH
		System.out.println("The graph g = " + graph.toString());
		// ALL THE CODE FOR VISUALIZING THE GRAPH
		Layout<String, String> layout = new CircleLayout(graph);
		// Layout<String, String> layout = new FRLayout<String, String>(graph);
		layout.setSize(new Dimension(1430, 780));
		BasicVisualizationServer<String, String> vv = new BasicVisualizationServer<String, String>(layout);
		vv.setPreferredSize(new Dimension(1550, 850)); // Sets the viewing area
		// ALL PAINTING AND LABELING DONE HERE
		// PAINTING
		// MAKE THIS A HASHMAP OF VERTICES, NOT STRINGS
		// map courses to colors
		Map<String, Color> colorHash = new HashMap<String, Color>();
		for(String s: allclasses){
			colorHash.put(s, null);
		}
		Deque<Vertex> colorV = new ArrayDeque<Vertex>();
		Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {
			public Paint transform(String i) {
//				System.out.println(i);
//				return(colorHash.get(i));
//				int degree = graph.degree(i);
				String[] f = (String[]) graph.getNeighbors(i).toArray(new String[0]);
				return findDiffColor(i, f, 0, colorsArray, colorHash);
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		// LABELS ALL VERTICES
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		// LABELS ALL EDGES
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		// CENTERS LABELS
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		// DONE PAINTING AND LABELLING

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static Color findDiffColor(String i, String[] f, int count, Color[] colorsArray, Map<String, Color> colorHash){
		boolean sameColor = false;
		Color colorCheck = colorsArray[count];
		for(String s: f){
			if(colorHash.get(s) != null && colorCheck.getRed() == colorHash.get(s).getRed() && colorCheck.getGreen() == colorHash.get(s).getGreen() && colorCheck.getBlue() == colorHash.get(s).getBlue()){
				sameColor = true;
			}
		}
		if (sameColor == true){
			return findDiffColor(i, f, count+1 ,colorsArray, colorHash);
		}
		else{
			colorHash.put(i, colorCheck);
			return colorCheck;
		}
	}
}
