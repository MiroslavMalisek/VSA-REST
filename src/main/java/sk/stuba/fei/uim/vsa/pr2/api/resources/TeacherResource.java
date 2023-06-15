package sk.stuba.fei.uim.vsa.pr2.api.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("/teachers")
public class TeacherResource {

    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public TeacherResource(){
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @Secured
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeachers(@Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            List<Teacher> teachers = this.thesisService.getTeachers();
            List<TeacherDTO> teachersDTO = teachers.stream()
                    .map(t -> {
                        return new TeacherDTO(t);
                    }).collect(Collectors.toList());
            return Response.ok(this.jsonMapper.writeValueAsString(teachersDTO)).build();
        }catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), null))).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTeacher(String body) throws JsonProcessingException {
        try {
            CreateTeacherDTO request = this.jsonMapper.readValue(body, CreateTeacherDTO.class);
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

                if (thesisService.getTeacherByAisId(request.getAisId()) != null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Teacher with this ID already exists", null))).build();
                }
                if (thesisService.getTeacherByEmail(request.getEmail()) != null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Teacher with this email already exists", null))).build();
                }
                Teacher t = thesisService.createTeacher(request.getAisId(), request.getName(), request.getEmail(), request.getPassword(), request.getInstitute(), request.getDepartment());
                return Response.status(Response.Status.CREATED).entity(this.jsonMapper.writeValueAsString(new TeacherDTO(t))).build();


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
    public Response getTeacher(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            try {
                Teacher teacher = thesisService.getTeacher(id);
                if (teacher == null){
                    return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
                }
                return Response.ok(this.jsonMapper.writeValueAsString(new TeacherDTO(teacher))).build();
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
    public Response deleteTeacher(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        Teacher teacher = thesisService.getTeacher(id);

        try {
            if (teacher == null){
                return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
            }
            if (securityContext.getUserPrincipal() instanceof Teacher){
                Long loggedTeacherId = ((Teacher)securityContext.getUserPrincipal()).getId();
                if (id.equals(loggedTeacherId)){
                    Teacher delTeacher = thesisService.deleteTeacher(id);
                    if (delTeacher == null){
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Teacher was not deleted.", null))).build();
                    }
                    return Response.ok(this.jsonMapper.writeValueAsString(new TeacherDTO(delTeacher))).build();

                }
            }
            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, "You dont have permission for this request", null))).build();

        }catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }
}
