package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {

    private Integer code;
    private String message;
    private Error error;

    public Message(Integer code, String message, Error error){
        this.code = code;
        this.message = message;
        if (error != null){
            this.error = error;
        }
    }
}
