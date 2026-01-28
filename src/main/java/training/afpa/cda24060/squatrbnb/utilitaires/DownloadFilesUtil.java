package training.afpa.cda24060.squatrbnb.utilitaires;

import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class DownloadFilesUtil {
    private static final long TAILLE_MAX_FICHIER = 5 * 1024 * 1024;
    private static final String[] EXTENSIONS_AUTORISEES = {".jpg", ".jpeg", ".png", ".gif"};

    public static String sauvegarderFichier(Part part, String cheminTelechargement) throws IOException {
        if (part == null || part.getSize() == 0) {
            throw new IOException("Aucun fichier n'a été uploadé");
        }

        if (part.getSize() > TAILLE_MAX_FICHIER) {
            throw new IOException("Le fichier est trop volumineux. Taille maximale : 5 MB");
        }

        String nomFichierOriginal = obtenirNomFichier(part);

        if (!estExtensionAutorisee(nomFichierOriginal)) {
            throw new IOException("Type de fichier non autorisé. Extensions autorisées : jpg, jpeg, png, gif");
        }

        String extensionFichier = obtenirExtensionFichier(nomFichierOriginal);
        String nomFichierUnique = UUID.randomUUID().toString() + extensionFichier;

        File dossierTelechargement = new File(cheminTelechargement);
        if (!dossierTelechargement.exists()) {
            dossierTelechargement.mkdirs();
        }

        Path cheminFichier = Paths.get(cheminTelechargement, nomFichierUnique);
        Files.copy(part.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

        return nomFichierUnique;
    }

    public static boolean supprimerFichier(String nomFichier, String cheminTelechargement) {
        if (nomFichier == null || nomFichier.isEmpty()) {
            return false;
        }

        try {
            Path cheminFichier = Paths.get(cheminTelechargement, nomFichier);
            Files.deleteIfExists(cheminFichier);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String obtenirNomFichier(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");

        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                String nomFichier = element.substring(element.indexOf('=') + 1).trim();
                return nomFichier.replace("\"", "");
            }
        }
        return "unknown";
    }

    private static String obtenirExtensionFichier(String nomFichier) {
        if (nomFichier == null || nomFichier.isEmpty()) {
            return "";
        }

        int dernierPointIndex = nomFichier.lastIndexOf('.');
        if (dernierPointIndex == -1) {
            return "";
        }

        return nomFichier.substring(dernierPointIndex).toLowerCase();
    }

    private static boolean estExtensionAutorisee(String nomFichier) {
        String extension = obtenirExtensionFichier(nomFichier);

        for (String extensionAutorisee : EXTENSIONS_AUTORISEES) {
            if (extension.equals(extensionAutorisee)) {
                return true;
            }
        }

        return false;
    }

    public static long obtenirTailleMaxFichier() {
        return TAILLE_MAX_FICHIER;
    }

    public static String[] obtenirExtensionsAutorisees() {
        return EXTENSIONS_AUTORISEES.clone();
    }
}
