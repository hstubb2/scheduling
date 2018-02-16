package chaitinChandraOptimization;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Person {
	private String name;
	private int numCourses;
	private Collection<Course> courses;
	
	public Person(String name){
		this.name = name;
		this.courses = new HashSet<Course>(Arrays.asList(new Course[] {}));
		this.numCourses = 0;
	}
	public Person(String name, Collection<Course> courses){
		this.name = name;
		this.courses = courses;
		this.numCourses = courses.size();
	}
	
	public Collection<Course> getCourses(){
		return this.courses;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void addCourse(Course c){
		this.courses.add(c);
		++numCourses;
	}

}
