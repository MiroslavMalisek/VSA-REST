package sk.stuba.fei.uim.vsa.pr2.api.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.CreateStudentDTO;
import sk.stuba.fei.uim.vsa.pr2.dto.Message;
import sk.stuba.fei.uim.vsa.pr2.dto.StudentDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("/students")
public class StudentResource {

    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public StudentResource(){
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @Secured
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudents(@Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            List<Student> students = this.thesisService.getStudents();
            List<StudentDTO> studentsDTO = students.stream()
                    .map(s -> {
                        return new StudentDTO(s);
                    }).collect(Collectors.toList());
            return Response.ok(this.jsonMapper.writeValueAsString(studentsDTO)).build();
        }catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), null))).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStudent(String body) throws JsonProcessingException {
        try {
            CreateStudentDTO request = this.jsonMapper.readValue(body, CreateStudentDTO.class);
            if (request.getAisId() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Ais id cannot be null", null))).build();
            }
            if (request.getName() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Name cannot be null", null))).build();
            }
            if (request.getEmail() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Email cannot be null", null))).build();
            }
            if (request.getPassword() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Password cannot be null", null))).build();
            }

            request.setPassword(BCryptService.hash(request.getPassword()));
            try {

                if (thesisService.getStudentByAisId(request.getAisId()) != null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Student with this ID already exists", null))).build();
                }
                if (thesisService.getStudentByEmail(request.getEmail()) != null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Student with this email already exists", null))).build();
                }
                Student s = thesisService.createStudent(request.getAisId(), request.getName(), request.getEmail(), request.getPassword(), request.getYear(), request.getTerm(), request.getProgramme());
                return Response.status(Response.Status.CREATED).entity(this.jsonMapper.writeValueAsString(new StudentDTO(s))).build();


            }catch (IllegalArgumentException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Ais id cannot be null", null))).build();
            }
        }catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), null))).build();
        }
    }

    @Secured
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudent(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            try {
                Student student = thesisService.getStudent(id);
                if (student == null){
                    return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
                }
                return Response.ok(this.jsonMapper.writeValueAsString(new StudentDTO(student))).build();
            }catch (IllegalArgumentException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), null))).build();
            }
        }catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }

    @Secured
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteStudent(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        Student student = thesisService.getStudent(id);

        try {
            if (student == null){
                return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
            }
            if (securityContext.getUserPrincipal() instanceof Teacher){
                Student delStudent = thesisService.deleteStudent(id);
                if (delStudent == null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Student was not deleted.", null))).build();
                }
                return Response.ok(this.jsonMapper.writeValueAsString(new StudentDTO(delStudent))).build();
            }

            Long loggedStudentId = ((Student)securityContext.getUserPrincipal()).getId();
            if (id.equals(loggedStudentId)){
                Student delStudent = thesisService.deleteStudent(id);
                if (delStudent == null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Student was not deleted.", null))).build();
                }
                return Response.ok(this.jsonMapper.writeValueAsString(new StudentDTO(delStudent))).build();
            }

            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, "You dont have permission for this request", null))).build();

        }catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }
}
