package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Error {

    private String type;
    private String trace;

    public Error(String type, String trace){
        this.type = type;
        this.trace = trace;
    }
}
