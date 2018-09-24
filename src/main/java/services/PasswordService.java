package services;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasswordService {
    final static Logger logger = LoggerFactory.getLogger(PasswordService.class);

    public static String hashPassword(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plaintext, String hashed) {

        return BCrypt.checkpw(plaintext, hashed);
    }
}
