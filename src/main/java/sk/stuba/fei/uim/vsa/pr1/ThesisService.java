package sk.stuba.fei.uim.vsa.pr1;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.stuba.fei.uim.vsa.pr1.entities.*;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class ThesisService extends AbstractThesisService<Student, Teacher, Assignment>{

    public ThesisService(){
        super();
    }

    @Override
    public Student createStudent(Long aisId, String name, String email, String password, Integer studyYear, Integer semester, String studyProgram) {
        if (aisId == null){
            return null;
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s where s.aisId = :aisId", Student.class);
            query.setParameter("aisId", aisId);
            query.getSingleResult();
            em.close();
            return null;
        }catch (NoResultException e){
            em.getTransaction().begin();
            try {
                Student s = new Student();
                s.setAisId(aisId);
                s.setName(name);
                s.setEmail(email);
                s.setPassword(password);
                s.setStudyYear(studyYear);
                s.setSemester(semester);
                s.setStudyProgram(studyProgram);
                em.persist(s);
                em.getTransaction().commit();
                em.close();
                return s;
            }catch (Exception e1){
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
                return null;
            }
        }
    }

    @Override
    public Student getStudent(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Invalid ID");
        }
        EntityManager em = emf.createEntityManager();
        Student s = em.find(Student.class, id);
        em.close();
        return s;
    }

    @Override
    public Student getStudentByAisId(Long aisId) {
        if (aisId == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s where s.aisId = :aisId", Student.class);
            query.setParameter("aisId", aisId);
            Student s = query.getSingleResult();
            em.close();
            return s;
        }catch (NoResultException e){
            em.close();
            return null;
        }
    }

    @Override
    public Student getStudentByEmail(String email) {
        if (email == null){
            throw new IllegalArgumentException("Email na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s where s.email = :email", Student.class);
            query.setParameter("email", email);
            Student s = query.getSingleResult();
            em.close();
            return s;
        }catch (NoResultException e){
            em.close();
            return null;
        }
    }

    @Override
    public Student updateStudent(Student student) {
        if (student == null){
            throw new IllegalArgumentException("Inštancia je NULL");
        }
        if (student.getId() == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Student s1 = em.find(Student.class, student.getId());
        if (s1 == null){
            em.close();
            return null;
        }
        em.getTransaction().begin();
        try {
            //AisId, name, email nesmu byt null
            if (student.getAisId() != null){
                s1.setAisId(student.getAisId());
            }
            if (student.getName() != null){
                s1.setName(student.getName());
            }
            if (student.getEmail() != null){
                s1.setEmail(student.getEmail());
            }

            s1.setStudyYear(student.getStudyYear());
            s1.setSemester(student.getSemester());
            s1.setStudyProgram(student.getStudyProgram());
            em.merge(s1);
            em.getTransaction().commit();
            em.close();
            return s1;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public List<Student> getStudents() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s", Student.class);
        List<Student> students = query.getResultList();
        em.close();
        return students;
    }

    @Override
    public Student deleteStudent(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Student s = em.find(Student.class, id);
        if (s == null) {
            em.close();
            return null;
        }
        em.getTransaction().begin();
        try {
            if (s.getAssignment() == null){
                em.remove(s);
                em.getTransaction().commit();
                em.close();
                return s;
            }
            TypedQuery<Assignment> q = em.createQuery("SELECT a FROM Assignment a WHERE a.student = :student", Assignment.class);
            q.setParameter("student", s);
            Assignment a = q.getSingleResult();
            a.setStudent(null);
            a.setStatus(Status.FREE_TO_TAKE);
            s.setAssignment(null);
            em.remove(s);
            em.getTransaction().commit();
            em.close();
            return s;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public Teacher createTeacher(Long aisId, String name, String email, String password, String institute, String department) {
        if (aisId == null){
            return null;
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Teacher> query = em.createQuery("SELECT t FROM Teacher t where t.aisId = :aisId", Teacher.class);
            query.setParameter("aisId", aisId);
            query.getSingleResult();
            em.close();
            return null;
        }catch (NoResultException e){
            em.getTransaction().begin();
            try {
                Teacher t = new Teacher();
                t.setAisId(aisId);
                t.setName(name);
                t.setEmail(email);
                t.setPassword(password);
                t.setInstitute(institute);
                t.setDepartment(department);
                em.persist(t);
                em.getTransaction().commit();
                em.close();
                return t;
            }catch (Exception e1){
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
                return null;
            }
        }
    }

    @Override
    public Teacher getTeacher(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Invalid ID");
        }
        EntityManager em = emf.createEntityManager();
        Teacher t = em.find(Teacher.class, id);
        em.close();
        return t;
    }

    @Override
    public Teacher getTeacherByAisId(Long aisId) {
        if (aisId == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Teacher> query = em.createQuery("SELECT t FROM Teacher t where t.aisId = :aisId", Teacher.class);
            query.setParameter("aisId", aisId);
            Teacher t = query.getSingleResult();
            em.close();
            return t;
        }catch (NoResultException e){
            em.close();
            return null;
        }
    }

    @Override
    public Teacher getTeacherByEmail(String email) {
        if (email == null){
            throw new IllegalArgumentException("Email na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Teacher> query = em.createQuery("SELECT t FROM Teacher t where t.email = :email", Teacher.class);
            query.setParameter("email", email);
            Teacher t = query.getSingleResult();
            em.close();
            return t;
        }catch (NoResultException e){
            em.close();
            return null;
        }
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        if (teacher == null){
            throw new IllegalArgumentException("Inštancia je NULL");
        }
        if (teacher.getId() == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Teacher t1 = em.find(Teacher.class, teacher.getId());
        if (t1 == null){
            em.close();
            return null;
        }
        em.getTransaction().begin();
        try {
            //AisId, name, email, department a institute nesmu byt null
            if (teacher.getAisId() != null){
                t1.setAisId(teacher.getAisId());
            }
            if (teacher.getName() != null){
                t1.setName(teacher.getName());
            }
            if (teacher.getEmail() != null){
                t1.setEmail(teacher.getEmail());
            }
            if (teacher.getInstitute() != null){
                t1.setInstitute(teacher.getInstitute());
                t1.setDepartment(teacher.getInstitute());
            }
            if (teacher.getDepartment() != null){
                t1.setDepartment(teacher.getDepartment());
            }
            List<Assignment> assignments = t1.getAssignments();
            if (!assignments.isEmpty()){
                for (Assignment a : assignments){
                    a.setInstitute(t1.getInstitute());
                    em.merge(a);
                }
            }
            em.merge(t1);
            em.getTransaction().commit();
            em.close();
            return t1;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public List<Teacher> getTeachers() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Teacher> query = em.createQuery("SELECT t FROM Teacher t", Teacher.class);
        List<Teacher> teachers = query.getResultList();
        em.close();
        return teachers;
    }

    @Override
    public Teacher deleteTeacher(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Teacher t = em.find(Teacher.class, id);
        if (t == null) {
            em.close();
            return null;
        }
        em.getTransaction().begin();
        try {
            if (t.getAssignments().isEmpty()){
                em.remove(t);
                em.getTransaction().commit();
                em.close();
                return t;
            }
            TypedQuery<Assignment> q = em.createQuery("SELECT a FROM Assignment a WHERE a.supervisor = :teacher", Assignment.class);
            q.setParameter("teacher", t);
            List<Assignment> list = q.getResultList();
            for (Assignment a : list){
                Student s = a.getStudent();
                if (s != null){
                    s.setAssignment(null);
                    em.remove(a);
                }
            }
            em.remove(t);
            em.getTransaction().commit();
            em.close();
            return t;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public Assignment makeThesisAssignment(Long supervisor, String registrationNumber, String title, String type, String description) {
        if (supervisor == null){
            throw new IllegalArgumentException("Supervisor ID je NULL");
        }
        Assignment a;
        try {
            a = getThesis(registrationNumber);
            if (a != null){
                throw new IllegalArgumentException("Thesis with this registration number already exists");
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
        //Find teacher
        Teacher t;
        try {
            t = getTeacher(supervisor);
            if (t == null){
                throw new IllegalArgumentException("Teacher with this ID doesnt exists");
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Supervisor ID je NULL");
        }

        //ziskali sme platneho ucitela a thesis je unikatna
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Assignment assignment = new Assignment();
            assignment.setRegistrationNumber(registrationNumber);
            assignment.setTyp(Typ.valueOf(type));
            assignment.setSupervisor(t);
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setInstitute(t.getInstitute());
            em.persist(assignment);
            t.addAssignment(assignment);
            em.merge(t);
            em.getTransaction().commit();
            em.close();
            return assignment;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public Assignment assignThesis(Long thesisId, Long studentId) {
        if (thesisId == null){
            throw new IllegalArgumentException("Thesis ID je NULL");
        }
        if (studentId == null){
            throw new IllegalArgumentException("Student ID je NULL");
        }
        Assignment a;
        try {
            a = getThesis(thesisId);
            if (a == null){
                return null;
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Thesis ID je NULL");
        }
        Student s;
        try {
            s = getStudent(studentId);
            if (s == null){
                return null;
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Student ID je NULL");
        }
        if (s.getAssignment() != null){
            throw new IllegalStateException("Student is already assigned for another thesis");
        }
        //mame ziskanu Assignment aj studenta
        if ((a.getStatus() != Status.FREE_TO_TAKE) || (a.getStudent() != null)){
            throw new IllegalStateException("Práca je už zabraná");
        }
        Date date = new Date();
        if (a.getDeadline().compareTo(date) < 0){
            throw new IllegalStateException("Práca je po deadline");
        }

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            a.setStudent(s);
            a.setStatus(Status.IN_PROGRESS);
            s.setAssignment(a);
            em.merge(s);
            em.merge(a);
            em.getTransaction().commit();
            em.close();
            return a;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public Assignment submitThesis(Long thesisId) {
        if (thesisId == null){
            throw new IllegalArgumentException("Thesis ID je NULL");
        }
        Assignment a;
        try {
            a = getThesis(thesisId);
            if (a == null){
                return null;
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Thesis ID je NULL");
        }
        //mame thesis
        if (a.getStatus() != Status.IN_PROGRESS){
            throw new IllegalStateException("Práca nie je v správnom stave");
        }
        Date date = new Date();
        if (a.getDeadline().compareTo(date) < 0){
            throw new IllegalStateException("Práca je po deadline");
        }
        if (a.getStudent() == null){
            throw new IllegalStateException("Práca nemá priradeného študenta");
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            a.setStatus(Status.SUBMITTED);
            em.merge(a);
            em.getTransaction().commit();
            em.close();
            return a;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public Assignment deleteThesis(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Assignment a = em.find(Assignment.class, id);
        if (a == null) {
            em.close();
            return null;
        }
        em.getTransaction().begin();
        try {
            Student s = a.getStudent();
            if (s != null){
                s.setAssignment(null);
                a.setStudent(null);
            }
            Teacher t = a.getSupervisor();
            if (t != null){
                t.getAssignments().remove(a);
                a.setSupervisor(null);
            }
            em.remove(a);
            em.getTransaction().commit();
            em.close();
            return a;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }

    @Override
    public List<Assignment> getTheses() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Assignment> query = em.createQuery("SELECT a FROM Assignment a", Assignment.class);
        List<Assignment> assignments = query.getResultList();
        em.close();
        return assignments;
    }

    @Override
    public List<Assignment> getThesesByTeacher(Long teacherId) {
        if (teacherId == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Teacher t = em.find(Teacher.class, teacherId);
        if (t == null) {
            em.close();
            return new ArrayList<>();
        }
        try {
            TypedQuery<Assignment> q = em.createQuery("SELECT a FROM Assignment a WHERE a.supervisor = :teacher", Assignment.class);
            q.setParameter("teacher", t);
            List<Assignment> res = q.getResultList();
            em.close();
            return res;
        } catch (Exception e){
            em.close();
            return new ArrayList<>();
        }

    }

    @Override
    public Assignment getThesisByStudent(Long studentId) {
        if (studentId == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Student s = em.find(Student.class, studentId);
        if (s == null) {
            em.close();
            return null;
        }
        try {
            TypedQuery<Assignment> q = em.createQuery("SELECT a FROM Assignment a WHERE a.student = :student", Assignment.class);
            q.setParameter("student", s);
            Assignment a = q.getSingleResult();
            em.close();
            return a;
        } catch (Exception e){
            em.close();
            return null;
        }

    }

    @Override
    public Assignment getThesis(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Vstup id je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Assignment a = em.find(Assignment.class, id);
        em.close();
        return a;
    }

    @Override
    public Assignment getThesis(String regNumber) {
        if (regNumber == null){
            throw new IllegalArgumentException("Registration number is NULL");
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Assignment> q = em.createQuery("SELECT a FROM Assignment a WHERE a.registrationNumber = :regNumber", Assignment.class);
            q.setParameter("regNumber", regNumber);
            Assignment a = q.getSingleResult();
            em.close();
            return a;
        } catch (Exception e){
            em.close();
            return null;
        }
    }

    @Override
    public Assignment updateThesis(Assignment thesis) {
        if (thesis == null){
            throw new IllegalArgumentException("Inštancia je NULL");
        }
        if (thesis.getId() == null){
            throw new IllegalArgumentException("Id na vstupe je NULL");
        }
        EntityManager em = emf.createEntityManager();
        Assignment a1 = getThesis(thesis.getId());
        em.getTransaction().begin();
        try {
            //Vsetky atributy okrem institute nesmu byt null
            //datum vytvorenia, a status nema zmysel updateovat
            if (thesis.getRegistrationNumber() != null){
                a1.setRegistrationNumber(thesis.getRegistrationNumber());
            }
            if (thesis.getTitle() != null){
                a1.setTitle(thesis.getTitle());
            }
            if (thesis.getDescription() != null) {
                a1.setDescription(thesis.getDescription());
            }
            if (thesis.getDeadline() != null){
                a1.setDeadline(thesis.getDeadline());
            }
            if (thesis.getTyp() != null){
                a1.setTyp(thesis.getTyp());
            }
            if ((thesis.getSupervisor() != null) && (!a1.getSupervisor().getId().equals(thesis.getSupervisor().getId()))){
                a1.getSupervisor().getAssignments().remove(a1);
                a1.setSupervisor(thesis.getSupervisor());
                thesis.getSupervisor().addAssignment(a1);
            }
            a1.setInstitute(thesis.getInstitute());
            em.merge(a1);
            em.getTransaction().commit();
            em.close();
            return a1;
        }catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            return null;
        }
    }
}
