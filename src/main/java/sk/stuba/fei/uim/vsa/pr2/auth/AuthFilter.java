package sk.stuba.fei.uim.vsa.pr2.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.dto.Message;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter  implements ContainerRequestFilter {

    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public AuthFilter(){
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        System.out.println(this.jsonMapper);
        String authHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Basic")) {
            //System.out.println("not auth");
            abort(request);
            return;
        }
        //System.out.println("auth");
        String emailLogin = this.decodeHeader(authHeader)[0];
        String passwordLogin = this.decodeHeader(authHeader)[1];
        //System.out.println("meno: " + emailLogin + " Heslo: " + passwordLogin);
        if (emailLogin.isEmpty() || passwordLogin.isEmpty()){
            abort(request);
            return;
        }

        Student student = thesisService.getStudentByEmail(emailLogin);
        Teacher teacher = thesisService.getTeacherByEmail(emailLogin);
        if (student == null && teacher == null){
            abort(request);
            return;
        }
        if (student != null){
            if (!BCryptService.verify(passwordLogin, student.getPassword())){
                abort(request);
                return;
            }else {
                final SecurityContext securityContext = request.getSecurityContext();
                MySecurityContext context = new MySecurityContext(student);
                context.setSecure(securityContext.isSecure());
                request.setSecurityContext(context);
                return;
            }
        }
        if (teacher != null){
            if(!BCryptService.verify(passwordLogin, teacher.getPassword())){
                abort(request);
                return;
            }else {
                final SecurityContext securityContext = request.getSecurityContext();
                MySecurityContext context = new MySecurityContext(teacher);
                context.setSecure(securityContext.isSecure());
                request.setSecurityContext(context);
                return;
            }
        }
    }

    private String[] decodeHeader(String authHeader){

        byte[] decodedBytes = Base64.getDecoder().decode(authHeader.split(" ")[1]);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] usernameAndPassword = credentials.split(":");
        /*String username = usernameAndPassword[0];
        String password = usernameAndPassword[1];*/
        return usernameAndPassword;
    }

    private void abort(ContainerRequestContext request) throws JsonProcessingException {
        request.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic")
                .entity(this.jsonMapper.writeValueAsString(new Message(401, "Unauthorized request.", null)))
                .build());
    }
}
