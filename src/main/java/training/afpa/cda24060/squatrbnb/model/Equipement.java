package training.afpa.cda24060.squatrbnb.model;

/**
 * Équipement disponible dans un logement
 */
public class Equipement {

    private Long id;
    private String nom;
    private String icone;
    private String categorie;
    private String description;

    // Constructeurs
    public Equipement() {}

    public Equipement(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Equipement(Long id, String nom, String icone, String categorie) {
        this.id = id;
        this.nom = nom;
        this.icone = icone;
        this.categorie = categorie;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Equipement{id=" + id + ", nom='" + nom + "', categorie='" + categorie + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipement that = (Equipement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}


