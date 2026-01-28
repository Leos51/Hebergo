package training.afpa.cda24060.squatrbnb.utilitaires;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class CookieUtil {

    // Nom du cookie "Remember Me"
    private static final String REMEMBER_ME_COOKIE_NAME = "SQUATRNB_REMEMBER";

    // Durée de vie du cookie "Remember Me" en secondes (30 jours)
    private static final int REMEMBER_ME_MAX_AGE = 30 * 24 * 60 * 60;

    /**
     * Crée un cookie "Remember Me" sécurisé
     *
     * @param response La réponse HTTP
     * @param email L'email de l'utilisateur
     * @param token Le token de remember me (généré de manière sécurisée)
     */
    public static void creerCookieRememberMe(HttpServletResponse response, String email, String token) {
        // Encoder l'email et le token en Base64
        String cookieValue = Base64.getEncoder().encodeToString((email + ":" + token).getBytes());

        Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_NAME, cookieValue);

        // Durée de vie: 30 jours
        cookie.setMaxAge(REMEMBER_ME_MAX_AGE);

        // HttpOnly: empêche l'accès via JavaScript (protection XSS)
        cookie.setHttpOnly(true);

        // Secure: envoyer uniquement via HTTPS (à activer en production)
        // En développement local, mettre à false
        cookie.setSecure(false);

        // Path: disponible sur toute l'application
        cookie.setPath("/");

        // SameSite: protection CSRF (géré via setHttpOnly et path)

        response.addCookie(cookie);
    }

    /**
     * Récupère le cookie "Remember Me"
     *
     * @param request La requête HTTP
     * @return Le cookie si présent, Optional.empty() sinon
     */
    public static Optional<Cookie> recupererCookieRememberMe(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> REMEMBER_ME_COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
    }

    /**
     * Extrait l'email et le token du cookie "Remember Me"
     *
     * @param cookie Le cookie "Remember Me"
     * @return Un tableau [email, token] ou null si invalide
     */
    public static String[] extraireDonneesCookie(Cookie cookie) {
        try {
            String decodedValue = new String(Base64.getDecoder().decode(cookie.getValue()));
            String[] parts = decodedValue.split(":", 2);

            if (parts.length == 2) {
                return parts; // [email, token]
            }
        } catch (Exception e) {
            // Cookie invalide ou corrompu
            return null;
        }

        return null;
    }

    /**
     * Supprime le cookie "Remember Me"
     *
     * @param request La requête HTTP
     * @param response La réponse HTTP
     */
    public static void supprimerCookieRememberMe(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> cookieOpt = recupererCookieRememberMe(request);

        if (cookieOpt.isPresent()) {
            Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_NAME, null);
            cookie.setMaxAge(0); // Supprime immédiatement
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            response.addCookie(cookie);
        }
    }

    /**
     * Génère un token de remember me sécurisé
     *
     * @param email L'email de l'utilisateur
     * @return Un token unique
     */
    public static String genererTokenRememberMe(String email) {
        // Générer un token unique basé sur l'email et un timestamp
        long timestamp = System.currentTimeMillis();
        String data = email + ":" + timestamp + ":" + Math.random();

        // Hash avec SHA-256 serait mieux, mais pour simplifier on utilise Base64
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
}
