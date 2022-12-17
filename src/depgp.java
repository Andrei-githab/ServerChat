import java.security.PublicKey;
import java.util.Base64;

public interface depgp {
    public String encrypt(String message, PublicKey pkay) throws Exception;
    public String decrypt(String encryptedMessage) throws Exception;

}
