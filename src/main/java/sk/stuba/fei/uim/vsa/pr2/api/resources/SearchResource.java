package sk.stuba.fei.uim.vsa.pr2.api.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Assignment;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.AssignmentDTO;
import sk.stuba.fei.uim.vsa.pr2.dto.Message;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/search")
public class SearchResource {

    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public SearchResource() {
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @Secured
    @POST
    @Path("/theses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchTheses(String body, @Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            JsonNode jsonNode = this.jsonMapper.readTree(body);
            if (jsonNode.has("studentId")){
                Assignment assignment = thesisService.getThesisByStudent(jsonNode.get("studentId").asLong());
                List<AssignmentDTO> assignmentsDTO = new ArrayList<>();
                if (assignment == null){
                    return Response.ok(this.jsonMapper.writeValueAsString(assignmentsDTO)).build();
                }else {
                    assignmentsDTO.add(new AssignmentDTO(assignment));
                    return Response.ok(this.jsonMapper.writeValueAsString(assignmentsDTO)).build();
                }
            }else {
                List<Assignment> assignments = this.thesisService.getThesesByTeacher(jsonNode.get("teacherId").asLong());
                List<AssignmentDTO> assigmentsDTO = assignments.stream()
                        .map(a -> {
                            return new AssignmentDTO(a);
                        }).collect(Collectors.toList());
                return Response.ok(this.jsonMapper.writeValueAsString(assigmentsDTO)).build();
            }
        } catch (JsonProcessingException e1){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e1.getMessage(), null))).build();
        }
    }

}
