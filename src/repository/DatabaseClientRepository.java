package repository;

public interface DatabaseClientRepository {
    void findAllStudents();
    void findStudentById(int id);
    void addStudent(String name);
    void createFile();
    void deleteStudent(int id);
}
