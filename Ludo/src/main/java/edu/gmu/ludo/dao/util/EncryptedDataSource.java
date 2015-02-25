package edu.gmu.ludo.dao.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptedDataSource extends DriverManagerDataSource {

	//SWE_681 AES algorithm is used to encrypt the database password
	private final static String ALGORITHM = "AES";
	//swe_681_fall2013
	private final static byte[] KEEP_IT_SIMPLE = {0x73, 0x77, 0x65, 0x5f, 0x36, 0x38, 0x31, 0x5f, 0x66, 0x61, 0x6c, 0x6c, 0x32, 0x30, 0x31, 0x33 }; 

	@Override
	public String getPassword() {
		try {
			return decrypt(super.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = new BASE64Encoder().encode(encVal);
		return encryptedValue;
	}

	public static String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(KEEP_IT_SIMPLE, ALGORITHM);
		return key;
	}

	public static void main(String[] args) throws Exception {

		String data = "XXXXX";
		String dataEnc = encrypt(data);
		String dataDec = decrypt(dataEnc);

		System.out.println("Plain Text : " + data);
		System.out.println("Encrypted Text : " + dataEnc);
		System.out.println("Decrypted Text : " + dataDec);
	}
}
