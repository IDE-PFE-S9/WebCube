package fr.eseo.webcube.api.Response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.eseo.webcube.api.Details.TpDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TpResponse {

    private String uniqueName;

    private String firstname;

    private String surname;

    private String completion;

    private List<TpDetails> tpDetails;


    public TpResponse(String uniqueName, List<TpDetails> tpDetails) {
        this.uniqueName = uniqueName;
        this.tpDetails = tpDetails;
    }

    public TpResponse(String uniqueName, String firstname, String surname, String completion) {
        this.uniqueName = uniqueName;
        this.firstname = firstname;
        this.surname = surname;
        this.completion = completion;
    }
}
