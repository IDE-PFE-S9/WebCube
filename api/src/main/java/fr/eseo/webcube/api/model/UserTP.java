package fr.eseo.webcube.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_tp")
public class UserTP implements Serializable {

    @EmbeddedId
    UserTpKey id;

    @ManyToOne
    @MapsId("uniqueName")
    @JoinColumn(name = "uniqueName")
    User user;

    @ManyToOne
    @MapsId("tpId")
    @JoinColumn(name = "tp_id")
    TP tp;

    @Column(name = "completion")
    private Integer completion;
}