package training.afpa.cda24060.squatrbnb.model;

/**
 * Type de logement (Appartement, Maison, Studio, Villa, etc.)
 */
public class TypeLogement {

    private Long id;
    private String libelle;
    private String icone;
    private String description;
    private Boolean actif ;

    // Constructeurs
    public TypeLogement() {}

    public TypeLogement(Long id, String nom) {
        this.id = id;
        this.libelle = nom;
        this.actif = true;
    }

    public TypeLogement(Long id, String libelle, String icone) {
        this.id = id;
        this.libelle = libelle;
        this.icone = icone;
        this.actif = true;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;

    }

    @Override
    public String toString() {
        return "TypeLogement{id=" + this.id + ", libelle='" + this.libelle + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeLogement that = (TypeLogement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}