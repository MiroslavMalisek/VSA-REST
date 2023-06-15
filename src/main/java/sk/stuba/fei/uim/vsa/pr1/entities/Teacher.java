package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Teacher implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private Long aisId;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    //@Column(nullable = false)
    private String institute;
    //@Column(nullable = false)
    private String department;

    @OneToMany(mappedBy = "supervisor", cascade=CascadeType.REMOVE, orphanRemoval=true)
    private List<Assignment> assignments = new ArrayList<>();

    public void removeAssignments() {
        this.assignments.clear();
    }

    public void addAssignment(Assignment assignment){
        this.assignments.add(assignment);
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Teacher)) {
            return false;
        }
        Teacher other = (Teacher) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sk.stuba.fei.uim.vsa.pr1.entities.Teacher[ id=" + id + " ]";
    }

}
