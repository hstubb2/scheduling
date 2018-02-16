package chaitinChandraOptimization;

import java.awt.Color;
import java.util.LinkedList;

public class ColorTuple {
	Color first;
	Color second;
	Color third;
	Color fourth;

	int numberOfIntersect;

	public ColorTuple(Color f, Color s, Color t, Color ft, int nof) {
		numberOfIntersect = nof;
		first = f;
		second = s;
		third = t;
		fourth = ft;
	}

	public boolean compare(LinkedList<ColorTuple> lCT) {
		for (ColorTuple CT : lCT) {
			if (first.equals(CT.first) || first.equals(CT.second) || first.equals(CT.third)
					|| first.equals(CT.fourth)) {
				if (second.equals(CT.first) || second.equals(CT.second) || second.equals(CT.third)
						|| second.equals(CT.fourth)) {
					if (third.equals(CT.first) || third.equals(CT.second) || third.equals(CT.third)
							|| third.equals(CT.fourth)) {
						if (fourth.equals(CT.first) || fourth.equals(CT.second) || fourth.equals(CT.third)
								|| fourth.equals(CT.fourth)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
