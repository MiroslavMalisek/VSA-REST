package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Assignment;

import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
public class AssignmentDTO {

    private Long id;
    private String registrationNumber;
    private  String title;
    private String description;
    private String department;
    private TeacherAltResponseDTO supervisor;
    private StudentAltResponseDTO author;
    private String publishedOn;
    private String deadline;
    private String type;
    private String status;

    public AssignmentDTO(Assignment assignment) {
        this.id = assignment.getId();
        this.registrationNumber = assignment.getRegistrationNumber();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.department = assignment.getInstitute();
        if (assignment.getSupervisor() != null){
            this.supervisor = new TeacherAltResponseDTO(assignment.getSupervisor());
        }
        if (assignment.getStudent() != null){
            this.author = new StudentAltResponseDTO(assignment.getStudent());
        }
        this.publishedOn = new SimpleDateFormat("yyyy-MM-dd").format(assignment.getCreatedAt());
        this.deadline = new SimpleDateFormat("yyyy-MM-dd").format(assignment.getDeadline());
        this.type = assignment.getTyp().name();
        this.status = assignment.getStatus().name();
    }
}
