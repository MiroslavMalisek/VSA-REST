package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;

@Data
@NoArgsConstructor
public class CreateStudentDTO {

    private Long aisId;
    private String name;
    private String email;
    private String password;
    private Integer year;
    private Integer term;
    private String programme;


    public CreateStudentDTO(Student student) {
        this.aisId = student.getId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.password = BCryptService.hash(student.getPassword());
        this.year = student.getStudyYear();
        this.term = student.getSemester();
        this.programme = student.getStudyProgram();
    }
}
