package fr.eseo.webcube.api.Details;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TpDetails {
    private String completion;
    private Integer id;
    private String nom;
    private String type;
    private String gitLink;

    // Constructeurs, getters, setters, etc.

    // Assurez-vous d'implémenter equals() et hashCode() si vous souhaitez comparer des instances de cette classe.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TpDetails tpDetails = (TpDetails) o;
        return Objects.equals(completion, tpDetails.completion) &&
                Objects.equals(id, tpDetails.id) &&
                Objects.equals(nom, tpDetails.nom) &&
                Objects.equals(type, tpDetails.type) &&
                Objects.equals(gitLink, tpDetails.gitLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(completion, id, nom, type, gitLink);
    }
}
