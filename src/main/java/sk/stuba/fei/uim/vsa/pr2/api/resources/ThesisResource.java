package sk.stuba.fei.uim.vsa.pr2.api.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Assignment;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;
import sk.stuba.fei.uim.vsa.pr1.entities.Typ;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.AssignmentDTO;
import sk.stuba.fei.uim.vsa.pr2.dto.CreateAssignmentDTO;
import sk.stuba.fei.uim.vsa.pr2.dto.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("/theses")
public class ThesisResource {

    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public ThesisResource(){
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @Secured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createThesis(String body, @Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            if (!(securityContext.getUserPrincipal() instanceof Teacher)){
                return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, "You dont have permission for this request", null))).build();
            }
            CreateAssignmentDTO request = this.jsonMapper.readValue(body, CreateAssignmentDTO.class);
            request.setType(request.getType().toUpperCase());
            if (request.getRegistrationNumber() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Registration number cannot be null", null))).build();
            }
            if (request.getTitle() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Title cannot be null", null))).build();
            }
            if (request.getType() == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Type cannot be null", null))).build();
            }
            if (!isTypeValid(request.getType())){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, "Type is not valid", null))).build();
            }
            try {
                Assignment assignment = thesisService.makeThesisAssignment(((Teacher)securityContext.getUserPrincipal()).getId(), request.getRegistrationNumber(),request.getTitle(), request.getType(), request.getDescription());
                if (assignment == null){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Thesis was not created", null))).build();
                }
                return Response.status(Response.Status.CREATED).entity(this.jsonMapper.writeValueAsString(new AssignmentDTO(assignment))).build();
            }catch (IllegalArgumentException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), null))).build();
            }
        }catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), null))).build();
        }
    }

    @Secured
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTheses(@Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            List<Assignment> assignments = this.thesisService.getTheses();
            List<AssignmentDTO> assignmentsDTO = assignments.stream()
                    .map(a -> {
                        return new AssignmentDTO(a);
                    }).collect(Collectors.toList());
            return Response.ok(this.jsonMapper.writeValueAsString(assignmentsDTO)).build();
        }catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), null))).build();
        }
    }

    @Secured
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThesis(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            try {
                Assignment assignment = thesisService.getThesis(id);
                if (assignment == null){
                    return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
                }
                return Response.ok(this.jsonMapper.writeValueAsString(new AssignmentDTO(assignment))).build();
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
    public Response deleteThesis(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        Assignment assignment = thesisService.getThesis(id);
        try {
            if (assignment == null){
                return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
            }
            if (securityContext.getUserPrincipal() instanceof Teacher){
                Long loggedTeacherId = ((Teacher)securityContext.getUserPrincipal()).getId();
                if (assignment.getSupervisor().getId().equals(loggedTeacherId)){
                    Assignment delAssignment = thesisService.deleteThesis(assignment.getId());
                    if (delAssignment == null){
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Assignment was not deleted.", null))).build();
                    }
                    return Response.ok(this.jsonMapper.writeValueAsString(new AssignmentDTO(delAssignment))).build();

                }
            }
            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, "You dont have permission for this request", null))).build();

        }catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }

    @Secured
    @POST
    @Path("/{id}/assign")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignThesisToStudent(@PathParam("id") Long thesisId, String body, @Context SecurityContext securityContext) throws JsonProcessingException {
        Assignment assignment = thesisService.getThesis(thesisId);
        try {
            if (assignment == null){
                return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
            }
            try {
                if (securityContext.getUserPrincipal() instanceof Student){
                    Long loggedStudentId = ((Student)securityContext.getUserPrincipal()).getId();
                    Assignment assignedThesis = thesisService.assignThesis(thesisId, loggedStudentId);
                    if (assignedThesis == null){
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Thesis was not assigned.", null))).build();
                    }
                    return Response.ok(this.jsonMapper.writeValueAsString(new AssignmentDTO(assignedThesis))).build();

                }else {
                    JsonNode jsonNode = this.jsonMapper.readTree(body);
                    Long studentId = jsonNode.get("studentId").asLong();
                    Student student = thesisService.getStudent(studentId);
                    if (student == null){
                        return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested student was not found.", null))).build();
                    }
                    Assignment assignedThesis = thesisService.assignThesis(thesisId, studentId);
                    if (assignedThesis == null){
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Thesis was not assigned.", null))).build();
                    }
                    return Response.ok(this.jsonMapper.writeValueAsString(new AssignmentDTO(assignedThesis))).build();
                }
            }catch (IllegalStateException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(409, e.getMessage(), null))).build();
            }catch (JsonProcessingException e1){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
            }
        }catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }

    @Secured
    @POST
    @Path("/{id}/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response SubmitThesis(@PathParam("id") Long thesisId, String body, @Context SecurityContext securityContext) throws JsonProcessingException {
        Assignment assignment = thesisService.getThesis(thesisId);
        try {
            if (assignment == null){
                return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested resource was not found.", null))).build();
            }
            try {
                if (securityContext.getUserPrincipal() instanceof Student){
                    if (!checkStudentAssignedToThesis(assignment, (Student)securityContext.getUserPrincipal())){
                        return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, "You dont have permission for this request", null))).build();
                    }
                    Assignment submittedThesis = thesisService.submitThesis(assignment.getId());
                    if (submittedThesis == null){
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Thesis was not assigned.", null))).build();
                    }
                    return Response.ok(this.jsonMapper.writeValueAsString(new AssignmentDTO(submittedThesis))).build();
                }else {
                    JsonNode jsonNode = this.jsonMapper.readTree(body);
                    Long studentId = jsonNode.get("studentId").asLong();
                    Student student = thesisService.getStudent(studentId);
                    if (student == null){
                        return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, "Requested student was not found.", null))).build();
                    }
                    if (!checkStudentAssignedToThesis(assignment, student)){
                        return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, "You dont have permission for this request", null))).build();
                    }
                    Assignment submittedThesis = thesisService.submitThesis(assignment.getId());
                    if (submittedThesis == null){
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, "Thesis was not assigned.", null))).build();
                    }
                    return Response.ok(this.jsonMapper.writeValueAsString(new AssignmentDTO(submittedThesis))).build();
                }
            }catch (IllegalStateException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(409, e.getMessage(), null))).build();
            }catch (JsonProcessingException e1){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
            }
        }catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }

    private boolean checkStudentAssignedToThesis(Assignment assignment, Student student) {
        Assignment studentThesis = student.getAssignment();
        if ((studentThesis == null) || !(studentThesis.getId().equals(assignment.getId()))) {
            return false;
        }
        return true;
    }


    private boolean isTypeValid(String type){
        for (Typ typ : Typ.values()){
            if (typ.name().equals(type)){
                return true;
            }
        }
        return false;
    }
}
