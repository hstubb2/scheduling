package chaitinChandraOptimization;

public class CourseAndDegree implements Comparable<CourseAndDegree> {
	
	public final Course course;
	public final int    degree;
	
	public CourseAndDegree(Course course, int degree) {
		this.course = course;
		this.degree = degree;
	}

	@Override
	public String toString() {
		return course + " " + degree;
	}
	
	public CourseAndDegree sameCourseLowerDegree() {
		return new CourseAndDegree(course, degree-1);
	}

	@Override
	public int compareTo(CourseAndDegree o) {
		return new Integer(degree).compareTo(new Integer(o.degree));
	}
	

}
