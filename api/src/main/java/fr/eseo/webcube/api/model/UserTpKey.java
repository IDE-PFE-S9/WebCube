package fr.eseo.webcube.api.model;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserTpKey implements Serializable {

    @Column(name = "uniqueName")
    String userId;

    @Column(name = "tp_id")
    Integer tpId;
}
