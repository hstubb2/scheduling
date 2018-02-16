package chaitinChandraOptimization;

import java.awt.Color;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class DayScheduler<V> {

	// Vertex Color... Each vertex is a course
	Map<V, Color> chaitlinAns;
	Person[] peopleArray;

	Map<Color, Set<Person>> colors;
	Map<Color, Set<Person>> origColors;
	int numberOfCols;

	public DayScheduler(Map<V, Color> ans, Person[] people) {
		chaitlinAns = ans;
		peopleArray = people;
		initialize();
	}

	// TODO: Check for null values before puts and gets and store reusable items
	// in local variables
	private void initialize() {
		colors = new HashMap<Color, Set<Person>>();

		/*for (V course : chaitlinAns.keySet()) {
			Set<Person> tempPSet = new HashSet<Person>();

			if (!colors.containsKey(chaitlinAns.get(course))) {
				// Place all colors in the color Map
				colors.put(chaitlinAns.get(course), tempPSet);
			}
		}*/
		fillColors();

		/*for (Person p : peopleArray) {
			for (Course c : p.getCourses()) {
				Set<Person> prevSet = colors.get(chaitlinAns.get(c));
				prevSet.add(p);
				colors.put(chaitlinAns.get(c), prevSet);
			}
		}*/
		fillPeople();

		numberOfCols = colors.size();
		origColors = new HashMap<Color, Set<Person>>(colors);
	}
	
	private void fillColors() {
		for (V course : chaitlinAns.keySet()) {
			Set<Person> tempPSet = new HashSet<Person>();

			if (!colors.containsKey(chaitlinAns.get(course))) {
				// Place all colors in the color Map
				colors.put(chaitlinAns.get(course), tempPSet);
			}
		}
	}
	
	private void fillPeople() {
		for (Person p : peopleArray) {
			for (Course c : p.getCourses()) {
				Set<Person> prevSet = colors.get(chaitlinAns.get(c));
				prevSet.add(p);
				colors.put(chaitlinAns.get(c), prevSet);
			}
		}
	}
	
	/*public void shuffleColors() {
		Map<Color, Set<Person>> newColors = new HashMap<Color, Set<Person>>();
		fillColors();
		for (Color c : colors.keySet()) {
			Color newCol;
			while (true) {
				newCol = new Color((int) (Math.random() * 255), (int) (Math.random() * 255),(int) (Math.random() * 255));
				if (!newColors.containsKey(newCol)) {
					newColors.put(newCol, colors.get(c));
					for (V cor : chaitlinAns.keySet()) {
						if (chaitlinAns.get(cor).equals(c)) {
							chaitlinAns.put(cor, newCol);
						}
					}
					break;
				}
			}
		}
		colors = newColors;
		fillPeople();
		origColors = new HashMap<Color, Set<Person>>(colors);
	}*/

	private Set<Person> getIntersect(Set<Person> a, Set<Person> b) {
		Set<Person> ret = new HashSet<Person>();
		for (Person aP : a) {
			for (Person bP : b) {
				if (aP.equals(bP)) {
					ret.add(aP);
				}
			}
		}

		return ret;
	}

	private LinkedList<Set<Color>> calcDaysInternal() {
		@SuppressWarnings("unchecked")
		LinkedList<Set<Color>> ret = new LinkedList<Set<Color>>();
		LinkedList<ColorTuple> CT = new LinkedList<ColorTuple>();
		ArrayList<Color> keys = new ArrayList<Color>(colors.keySet());
		Collections.shuffle(keys);
		for (Color col : keys) {
			for (Color col2 : keys) {
				if (col2.equals(col)) {
					continue;
				}
				Set<Person> inter1 = getIntersect(colors.get(col), colors.get(col2));
				for (Color col3 : keys) {
					if (col2.equals(col3) || col.equals(col3)) {
						continue;
					}
					Set<Person> inter2 = getIntersect(inter1, colors.get(col3));
					for (Color col4 : keys) {
						if (col2.equals(col4) || col.equals(col4) || col3.equals(col4)) {
							continue;
						}
						Set<Person> inter3 = getIntersect(inter2, colors.get(col4));
						ColorTuple tempCT = new ColorTuple(col, col2, col3, col4, inter3.size());
						if (!tempCT.compare(CT)) {
							CT.add(tempCT);
						}
					}
				}
			}
		}

		Collections.shuffle(CT);
		for (ColorTuple c : CT) {
			if (c.numberOfIntersect == 0) {
				Set<Color> cSet = new HashSet<Color>();
				cSet.add(c.first);
				cSet.add(c.second);
				cSet.add(c.third);
				cSet.add(c.fourth);
				colors.remove(c.first);
				colors.remove(c.second);
				colors.remove(c.third);
				colors.remove(c.fourth);
				ret.add(cSet);
				for (Set<Color> cSetTemp : calcDaysInternal()) {
					ret.add(cSetTemp);
				}
				break;

				/*
				 * System.out.println(c.first.toString() + " & " +
				 * c.second.toString() + " & " + c.third.toString());
				 * System.out.println(c.numberOfIntersect);
				 * System.out.print("\n\n");
				 */
			}
		}

		return ret;
	}

	public LinkedList<Set<Color>> calcDays() {
		LinkedList<Set<Color>> ret = calcDaysInternal();
		Set<Color> toRemove = new HashSet<Color>();
		ArrayList<Color> keys = new ArrayList<Color>(colors.keySet());
		Collections.shuffle(keys);
		loop: for (Color col : keys) {
			for (Set<Color> colSet : ret) {
				 for (Color c : colSet) {
					for (Color c2 : colSet) {
						if (c.equals(c2)) {
							break;
						}
						Set<Person> intersect = getIntersect(origColors.get(c), origColors.get(c2));

						for (Color c3 : colSet) {
							if (c3.equals(c) || c3.equals(c2)) {
								break;
							}
							Set<Person> intersect2 = getIntersect(intersect, origColors.get(c3));
							if (!getIntersect(intersect2, origColors.get(col)).isEmpty()) {
								continue loop;
							}

						}
					}
				}
					colSet.add(col);
					toRemove.add(col);
			}
		}
		
		for (Color c : toRemove) {
			colors.remove(c);
		}

		System.out.println("LeftOver " + colors.size());
		for (Color col : colors.keySet()) {
			System.out.println(col.toString());
		}
		System.out.print("-----\n\n");

		// Check
		for (Person p : peopleArray) {
			for (Set<Color> setC : ret) {
				int dayCount = 0;
				for (Course c : p.getCourses()) {
					for (Color coll : setC) {
						if (coll.equals(chaitlinAns.get(c))) {
							dayCount++;
						}
					}
				}
				if (dayCount > 3) {
					System.out.println("TOO MANY COURSES ON ONE DAY : " + dayCount);
				}
			}
		}

		return ret;
	}
	
	public LinkedList<Set<Color>> calcDays(int n) {
		LinkedList<Set<Color>> ret = new LinkedList<Set<Color>>();
		
		int leftOverGroups = -1;
		Map<Color, Set<Person>> left = new HashMap<Color, Set<Person>>();
		
		for (int i = 0; i < n; i++) {
			colors = new HashMap<Color, Set<Person>>(origColors);
			LinkedList<Set<Color>> temp = calcDays();
			int leftG = colors.size()/3;
			if (colors.size() % 3 != 0) {
				leftG++;
			}
			if (leftOverGroups == -1 || (leftG + temp.size() < leftOverGroups + ret.size())) {
				ret = temp;
				leftOverGroups = leftG;
				left = colors;
			}
		}
		
		System.out.println("RESULTS AFTER " + n + " TRIALS");
		System.out.println("LeftOver " + colors.size());
		for (Color col : left.keySet()) {
			System.out.println(col.toString());
		}
		System.out.print("-----\n\n");
		
		
		
		return ret;
	}

	public void printIntersects() {
		for (Color col : colors.keySet()) {
			for (Color col2 : colors.keySet()) {
				if (col2.equals(col)) {
					continue;
				}
				for (Color col3 : colors.keySet()) {
					if (col2.equals(col3) || col.equals(col3)) {
						continue;
					}
					System.out.println(col.toString() + " & " + col2.toString() + " & " + col3.toString());
					for (Person p : getIntersect(getIntersect(colors.get(col), colors.get(col2)), colors.get(col3))) {
						System.out.println(p.getName());
					}
				}
				System.out.print("\n\n");
			}
		}
	}

	public void print() {
		for (Color col : colors.keySet()) {
			System.out.println(col.toString());
			for (Person p : colors.get(col)) {
				System.out.println(p.getName());
			}
			colors.get(col).toString();
			System.out.print("\n--------------------\n");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<Course, Color> ansT = new HashMap<Course, Color>();
		Person[] peopleT = new Person[3];
		Course one = new Course("one");
		Course two = new Course("two");
		Course three = new Course("three");
		Course four = new Course("four");

		HashSet<Course> j = new HashSet<Course>();
		HashSet<Course> t = new HashSet<Course>();
		HashSet<Course> r = new HashSet<Course>();

		ansT.put(one, Color.blue);
		ansT.put(two, Color.green);
		ansT.put(three, Color.red);
		ansT.put(four, Color.black);

		j.add(one);
		t.add(one);
		t.add(two);
		r.add(one);
		r.add(two);
		r.add(three);
		j.add(four);

		Person jim = new Person("Jim", j);
		Person tom = new Person("Tom", t);
		Person ryan = new Person("Ryan", r);

		peopleT[0] = jim;
		peopleT[1] = tom;
		peopleT[2] = ryan;

		DayScheduler<Course> DS = new DayScheduler<Course>(ansT, peopleT);

		DS.print();

		DS.printIntersects();
		System.out.println("-----");
		for (Set<Color> cols : DS.calcDays()) {
			for (Color col : cols) {
				System.out.println(col.toString());
			}
		}

	}

}
