import java.util.ArrayList;

class Student {
    String name;
    int age;

    Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String toString() {
        return name + "(" + age + ")";
    }
}

class Course {
    String courseName;
    ArrayList<Student> students; // ArrayList inside object

    Course(String courseName) {
        this.courseName = courseName;
        this.students = new ArrayList<>();
    }

    public String toString() {
        return courseName + " " + students;
    }
}

class School {
    String schoolName;
    ArrayList<Course> courses; // Another ArrayList inside object

    School(String schoolName) {
        this.schoolName = schoolName;
        this.courses = new ArrayList<>();
    }

    public String toString() {
        return schoolName + " " + courses;
    }

}

public class Q {
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        Q q = new Q();
        // Create students
        Student s1 = new Student("Alice", 12);
        Student s2 = new Student("Bob", 13);
        Student s3 = new Student("Charlie", 11);

        // Create courses and add students
        Course math = new Course("Math");
        math.students.add(s1);
        math.students.add(s2);

        Course science = new Course("Science");
        science.students.add(s3);

        // Create school and add courses
        School school = new School("Greenwood High");
        school.courses.add(math);
        school.courses.add(science);

        // Create top-level ArrayList of schools
        ArrayList<School> schools = new ArrayList<>();
        schools.add(school);
        System.out.println(q.file(schools));
        // Print structure
        System.out.println(schools);

    }

    String file(ArrayList<?> arr) throws IllegalArgumentException, IllegalAccessException {
        java.lang.reflect.Field[] fields = arr.get(0).getClass().getDeclaredFields();
        StringBuilder strng = new StringBuilder();
        for (Object obj : arr) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(obj);
                if (value instanceof ArrayList<?>) {
                    ArrayList<?> innerList = (ArrayList<?>) value;
                    if (!innerList.isEmpty()) {
                        str.append("[");
                        str.append(file(innerList));
                        str.append("]");
                    } else {
                        str.append("[]");
                    }
                } else {
                    str.append(value != null ? value.toString() : "null");
                }

                if (i < fields.length - 1)
                    str.append("|");
            }

            strng.append(str);

        }
        return strng.toString();
    }
}
