/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Krinal
 */
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/*
 * Public class for RSA KeysGeneration 
 */
public class Trial_key {
	
	//variable for key generation algorithm
	private static String keyAlgorithm = "RSA";
	
	//Main function, generates two public, private Key pairs
	//One for Sender and another for Destination
	public static void main(String[] args)
			throws NoSuchAlgorithmException, 
				   InvalidKeySpecException, 
				   IOException, 
				   NoSuchProviderException {
		
		//Generate sender and destination (private, public) keys
		generateRSAKeys("sender_" );
		generateRSAKeys("destination_");		
	}

	//Generates RSA random keys
	private static void generateRSAKeys(String node) 
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
		
		//Generate KeyPair Object for the given algorithm (here we are using "RSA")
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
		
		//random object for randomness of the keys
		SecureRandom randomKeys = SecureRandom.getInstance("SHA1PRNG");
		
		//generate key of size 2048 bits
		keyGen.initialize(2048, randomKeys);	
		KeyPair RSAKeys = keyGen.genKeyPair();
		
		//Get private and public keys from keyPair generated above
		Key privateKey = RSAKeys.getPrivate();
		Key publicKey = RSAKeys.getPublic();
		
		//Initialize keyFactory for writing the keys to file for given algorithm
		KeyFactory fact = KeyFactory.getInstance(keyAlgorithm);
		
		//Get Corresponding key specs for private and public keys 
		RSAPublicKeySpec pub = fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		
		//save the generated keys to file
		saveKeysToFile(node + "PublicKey.key", pub.getModulus(), pub.getPublicExponent());
		saveKeysToFile(node + "PrivateKey.key", priv.getModulus(), priv.getPrivateExponent());
		
		System.out.println("Key Files Generated: ");
		System.out.println(node + "PublicKey.key");
		System.out.println(node + "PrivateKey.key\n");		
	}
	
	//save the modulus and exponent of the keys to the file
	private static void saveKeysToFile(String fileName, BigInteger mod,
			BigInteger exp) throws IOException {
		
		ObjectOutputStream outStream = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(fileName)));
		
		outStream.writeObject(mod);
		outStream.writeObject(exp);			
		
		outStream.close();
		}
	}


