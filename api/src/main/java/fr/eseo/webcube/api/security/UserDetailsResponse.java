package fr.eseo.webcube.api.security;

public class UserDetailsResponse {

    private int id;

    private String given_name;

    private String typePerson;

    private String family_name;


    public UserDetailsResponse(int id, String typePerson){
        this.id = id;
        this.typePerson = typePerson;
    }

    public UserDetailsResponse(String given_name, String family_name){
        this.given_name = given_name;
        this.family_name = family_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGiven_name() {
        return typePerson;
    }

    public void setGiven_name(String typePerson) {
        this.typePerson = typePerson;
    }

    public String getTypePerson() {
        return typePerson;
    }

    public void setTypePerson(String typePerson) {
        this.typePerson = typePerson;
    }
}
