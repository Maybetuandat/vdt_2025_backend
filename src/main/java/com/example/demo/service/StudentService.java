package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    // Lấy tất cả học viên
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    // Lấy học viên theo ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }
    
    // Tìm kiếm theo tên
    public List<Student> searchByName(String name) {
        return studentRepository.findByFullNameContainingIgnoreCase(name);
    }
    
    // Tìm kiếm theo trường
    public List<Student> searchBySchool(String school) {
        return studentRepository.findBySchoolCategoryContainingIgnoreCase(school);
    }
    
    // Thêm học viên mới
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    // Cập nhật học viên
    public Student updateStudent(Long id, Student studentDetails) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setFullName(studentDetails.getFullName());
            student.setBirthDate(studentDetails.getBirthDate());
            student.setSchoolCategory(studentDetails.getSchoolCategory());
            return studentRepository.save(student);
        }
        throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
    }
    
    // Xóa học viên
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
