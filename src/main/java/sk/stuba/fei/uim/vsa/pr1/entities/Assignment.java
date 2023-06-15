package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Assignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String registrationNumber;
    @Column(nullable = false)
    private  String title;
    private String description;

    private String institute;
    @ManyToOne
    private Teacher supervisor;

    @OneToOne
    @JoinColumn(unique = true)
    private Student student;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private java.util.Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private java.util.Date deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Typ typ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    void setRegNumberAndDateAndStatus(){
        /*String fei = "FEI-";
        String padded = String.format("%04d" , this.id);
        this.registrationNumber = fei.concat(padded);*/
        this.createdAt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(this.createdAt);
        c.add(Calendar.MONTH, 3);
        this.deadline = c.getTime();
        this.status = Status.FREE_TO_TAKE;
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
        if (!(object instanceof Assignment)) {
            return false;
        }
        Assignment other = (Assignment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sk.stuba.fei.uim.vsa.pr1.entities.Assignment[ id=" + id + " ]";
    }

}


