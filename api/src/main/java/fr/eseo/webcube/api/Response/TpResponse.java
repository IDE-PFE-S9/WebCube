package fr.eseo.webcube.api.Response;

import java.util.List;
import java.util.Set;

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

    private int completion;

    private List<TpDetails> tpDetails;

    private Set<String> roles;


    public TpResponse(String uniqueName, List<TpDetails> tpDetails) {
        this.uniqueName = uniqueName;
        this.tpDetails = tpDetails;
    }

    public TpResponse(String uniqueName, List<TpDetails> tpDetails, Set<String> roles) {
        this.uniqueName = uniqueName;
        this.tpDetails = tpDetails;
        this.roles = roles;
    }

    public TpResponse(String uniqueName, String firstname, String surname, Integer completion) {
        this.uniqueName = uniqueName;
        this.firstname = firstname;
        this.surname = surname;
        this.completion = completion;
    }

    public TpResponse(String uniqueName, String firstname, String surname, Integer completion, Set<String> roles) {
        this.uniqueName = uniqueName;
        this.firstname = firstname;
        this.surname = surname;
        this.completion = completion;
        this.roles = roles;
    }
}
