import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CipherAES {

    public static final String TRANSFORMATION = "AES/ECB/PKCS5PADDING";

    private File encrypted = new File("encrypted.txt");
    private File input = new File("input.txt");

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException, InvalidKeySpecException {
        CipherAES aes = new CipherAES();
        BufferedReader br = new BufferedReader(new FileReader(aes.input));
        String line;
        List list = new ArrayList();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            list.add(Arrays.asList(values));
        }
        list.add(Arrays.asList(new String[]{"Łukasz Telefon","783783783"}));

        aes.writeData("dupa", list);
        System.out.println(aes.readData("dupa"));
    }

    public SecretKey getKeyFromPassword(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public void writeData(String password, List list) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init( Cipher.ENCRYPT_MODE, getKeyFromPassword(password));
        CipherOutputStream cipherOutputStream = new CipherOutputStream( new BufferedOutputStream( new FileOutputStream( encrypted ) ), cipher );
        ObjectOutputStream outputStream = new ObjectOutputStream( cipherOutputStream );
        outputStream.writeObject(list);
        outputStream.close();
    }

    public List readData(String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, ClassNotFoundException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init( Cipher.DECRYPT_MODE, getKeyFromPassword(password));
        CipherInputStream cipherInputStream = new CipherInputStream( new BufferedInputStream( new FileInputStream( encrypted ) ), cipher );
        ObjectInputStream inputStream = new ObjectInputStream( cipherInputStream );
        return (List) inputStream.readObject();
    }
}