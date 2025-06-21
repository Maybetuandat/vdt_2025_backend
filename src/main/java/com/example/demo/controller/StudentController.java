package com.example.demo.controller;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private MetricsController metricsController;

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();   // tăng số lượng  request tổng thể 
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } finally {
          
            metricsController.stopTimer(sample, "getAllStudents");
        }
    }
   
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();
            Optional<Student> student = studentService.getStudentById(id);
            if (student.isPresent()) {
                return ResponseEntity.ok(student.get());
            }
            return ResponseEntity.notFound().build();
        } finally {
            metricsController.stopTimer(sample, "getStudentById");
        }
    }
    
 
    @GetMapping("/search/name")
    public ResponseEntity<List<Student>> searchByName(@RequestParam String name) {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();
            List<Student> students = studentService.searchByName(name);
            return ResponseEntity.ok(students);
        } finally {
            metricsController.stopTimer(sample, "searchByName");
        }
    }
    
    // Tìm kiếm theo trường
    @GetMapping("/search/school")
    public ResponseEntity<List<Student>> searchBySchool(@RequestParam String school) {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();
            List<Student> students = studentService.searchBySchool(school);
            return ResponseEntity.ok(students);
        } finally {
            metricsController.stopTimer(sample, "searchBySchool");
        }
    }
    
    // Thêm học viên mới
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();
            Student savedStudent = studentService.saveStudent(student);
            return ResponseEntity.ok(savedStudent);
        } finally {
            metricsController.stopTimer(sample, "createStudent");
        }
    }
    
    // Cập nhật học viên
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();
            Student updatedStudent = studentService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } finally {
            metricsController.stopTimer(sample, "updateStudent");
        }
    }
    
    // Xóa học viên
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        Timer.Sample sample = metricsController.startTimer();
        try {
            metricsController.incrementStudentRequest();
            if (studentService.deleteStudent(id)) {
                return ResponseEntity.ok("Xóa học viên thành công");
            }
            return ResponseEntity.notFound().build();
        } finally {
            metricsController.stopTimer(sample, "deleteStudent");
        }
    }
}