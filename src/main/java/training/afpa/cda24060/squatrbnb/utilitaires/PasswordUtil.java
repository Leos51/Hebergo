package training.afpa.cda24060.squatrbnb.utilitaires;

//import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class PasswordUtil {
//    public static String hashpw(String plainpassword) {
//        return BCrypt.hashpw(plainpassword, BCrypt.gensalt());
//    };
//
//    public static  boolean checkpw(String plainpassword, String hashpassword) {
//        return BCrypt.checkpw(plainpassword, hashpassword);
//    }

private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

public static String hashpw(String plainPassword) {
    return encoder.encode(plainPassword);
}

public static boolean checkpw(String plainPassword, String hashPassword) {
    return encoder.matches(plainPassword, hashPassword);
}

}
