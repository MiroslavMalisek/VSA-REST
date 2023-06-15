package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;

@Data
@NoArgsConstructor
public class CreateTeacherDTO {

    private Long aisId;
    private String name;
    private String email;
    private String password;
    private String institute;
    private String department;

    public CreateTeacherDTO(Teacher teacher) {
        this.aisId = teacher.getAisId();
        this.name = teacher.getName();
        this.email = teacher.getEmail();
        this.password = BCryptService.hash(teacher.getPassword());
        this.institute = teacher.getInstitute();
        this.department = teacher.getDepartment();
    }
}
