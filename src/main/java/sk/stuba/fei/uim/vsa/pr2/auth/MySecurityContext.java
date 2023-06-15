package sk.stuba.fei.uim.vsa.pr2.auth;

import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class MySecurityContext implements SecurityContext {


    private Student student;
    private Teacher teacher;

    private final Long id;
    private boolean secure;

    public MySecurityContext(Student student) {
        this.student = student;
        this.id = student.getId();
    }

    public MySecurityContext(Teacher teacher) {
        this.teacher = teacher;
        this.id = teacher.getId();
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        if (this.student != null){
            return this.student;
        }
        return this.teacher;
    }

    @Override
    public boolean isUserInRole(String s) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Basic";
    }

}
