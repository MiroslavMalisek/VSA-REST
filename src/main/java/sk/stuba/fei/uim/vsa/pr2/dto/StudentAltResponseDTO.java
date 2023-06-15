package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;

@Data
@NoArgsConstructor
public class StudentAltResponseDTO {

    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private Integer year;
    private Integer term;
    private String programme;
    private Long thesis;

    public StudentAltResponseDTO(Student student) {
        this.id = student.getId();
        this.aisId = student.getAisId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.year = student.getStudyYear();
        this.term = student.getSemester();
        this.programme = student.getStudyProgram();
        if (student.getAssignment() != null){
            this.thesis = student.getAssignment().getId();
        }
    }
}
