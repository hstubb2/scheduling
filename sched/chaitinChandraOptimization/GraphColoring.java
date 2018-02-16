package chaitinChandraOptimization;

import java.awt.Color;
import java.util.Map;

public interface GraphColoring<V,E> extends Runnable {
	
	public Map<V, Color> getColoring();

}
