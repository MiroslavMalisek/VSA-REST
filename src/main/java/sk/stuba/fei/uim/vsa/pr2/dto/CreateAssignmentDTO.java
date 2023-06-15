package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Assignment;

@Data
@NoArgsConstructor
public class CreateAssignmentDTO {

    private String registrationNumber;
    private String title;
    private String description;
    private String type;

    public CreateAssignmentDTO(Assignment assignment) {
        this.registrationNumber = assignment.getRegistrationNumber();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.type = assignment.getTyp().name().toUpperCase();
    }
}
