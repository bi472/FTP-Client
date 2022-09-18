package repository;

public interface DatabaseClientRepository {
    void findAllStudents();
    void findStudentById(int id);
    void addStudent(String name);
    void deleteStudent(int id);
}
