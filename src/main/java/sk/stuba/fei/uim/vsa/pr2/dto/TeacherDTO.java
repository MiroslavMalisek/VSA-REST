package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Assignment;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TeacherDTO {

    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private String institute;
    private String department;
    private List<AssignmentDTO> theses;

    public TeacherDTO(Teacher teacher) {
        this.id = teacher.getId();
        this.aisId = teacher.getAisId();
        this.name = teacher.getName();
        this.email = teacher.getEmail();
        this.institute = teacher.getInstitute();
        this.department = teacher.getDepartment();
        this.theses = new ArrayList<>();
        if (teacher.getAssignments() != null){
            for (Assignment assignment: teacher.getAssignments()){
                this.theses.add(new AssignmentDTO(assignment));
            }
        }
    }
}
