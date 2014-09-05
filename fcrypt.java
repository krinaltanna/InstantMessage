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
import java.math.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class fcrypt {
       public static void main(String args[])             
    { 
        
       if (args.length != 5){
            System.out.println(args.length);
            System.out.println("Incorrect number of inputs given");
        }
    
        
       try
        {
// System.out.println("In main");
        if (args[0].equals("-e")){
            
            String destinationPublicKey = args[1];
            String senderPrivateKey = args[2];
            String inputFileName = args[3];
            String encryptedFileName = args[4];
            encryptUsingAES(destinationPublicKey,senderPrivateKey,inputFileName,encryptedFileName);
            
        }
        else if (args[0].equals("-d"))
            {
                String destinatioPrivatekey = args[1];
                String senderPublicKey = args[2];
                String encryptedFileName = args[3];
                String decryptedFileName = args[4];
                decryptAndSaveFile(destinatioPrivatekey,senderPublicKey,encryptedFileName,decryptedFileName);
                
                }
         else
                {
                    System.out.println(args[0]);
                        System.out.println("Invalid operation");
                        
                        }       
    }
        catch (Exception e)
    {
       String message = e.getMessage();
       System.out.println(message);
    }
    }
    
    public static void encryptUsingAES(String destinatioPublickey,String senderPrivateKey, String inputFileName, String encryptedFileName) {
        try
        {
     
    byte[] input = readFileData(new File(inputFileName));   
   //Grenerating KeyGenerator for AES
     KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(128);
    byte[] key = keyGenerator.generateKey().getEncoded();
    SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
   
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    //Reading data from file
     FileOutputStream fileStream = new FileOutputStream(encryptedFileName);
    //Encrypting the data using AES keys
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
    byte[] encrypted = cipher.doFinal(input);
    //encrypting AES key with RSA key
    byte[] RSAEncryptedAESKey = encryptAESKeywithRSA(secretKeySpec,destinatioPublickey);
    byte[] signatureByte = encryptedWithDigitalSig(senderPrivateKey, encrypted);
        //Copyiing the encrypted data and encrypted key to file
        BufferedOutputStream bufferStream = new BufferedOutputStream(fileStream);
        ObjectOutputStream outputStream = new ObjectOutputStream(bufferStream);
           outputStream.writeObject(RSAEncryptedAESKey);
           outputStream.writeObject(cipher.getIV());
          
           outputStream.writeObject(encrypted);
           outputStream.writeObject(signatureByte);
           outputStream.close();
        }
        catch(Exception e)
        {
            String message = e.getMessage();
            System.out.println(message);
        
        }
       }
    public static void decryptAndSaveFile(String destinatioPrivatekey,String senderPublicKey,String encryptedFileName,String decryptedFileName){
        try
        {
      //Reading the data from file 
    InputStream in = new FileInputStream(encryptedFileName);
    BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
    ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
    //Getting keys and text as objects
    byte[] encryptedAESKey = (byte[]) objectInputStream.readObject();
    //getting the AES key
    SecretKeySpec returnedSecretKeySpec = decryptAESWithRSA(encryptedAESKey,destinatioPrivatekey );
    byte[] returnedIvSpecByte = (byte[]) objectInputStream.readObject();
    IvParameterSpec returnedIvSpec = new IvParameterSpec(returnedIvSpecByte);
    byte[] encrypted = (byte[]) objectInputStream.readObject();
    //decrypting the text using 
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
    //SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    byte[] sign = (byte[]) objectInputStream.readObject();
    
    boolean ifVerified = verifySignature(sign, senderPublicKey, encrypted); 
    if (!ifVerified){
        System.out.println("Authentication failed. Data may have been tempered with");
        System.exit(0);
    }
    //System.out.println("encrypted: " + new String(encrypted));
    cipher.init(Cipher.DECRYPT_MODE, returnedSecretKeySpec,returnedIvSpec);
    byte[] decrypted = cipher.doFinal(encrypted);
    
    //System.out.println(ifVerified);
    FileOutputStream fileWriter = new FileOutputStream(decryptedFileName);
    //BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); 
    fileWriter.write(decrypted);
    fileWriter.close();
    //System.out.println("Succesfully Encrypted");
    //System.out.println("decrypted: " + new String(decrypted));t
        }
        catch(Exception e){
            String message = e.getMessage();
            System.out.println(message);
        }
}
    private static byte[] readFileData(File file) throws IOException{
        //getting the size of file
        long fileLength = file.length();
        //Convert File into byte data
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] dataBytes;
        //Creating a byte array to store the data of file
        try (DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {
            //Creating a byte array to store the data of file
            dataBytes = new byte[(int)fileLength];
            dataInputStream.readFully(dataBytes);
        }
        return dataBytes;       
    }
    private static Object readKeyFromFile(String keyFile, String fileType) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException{
        //Reading and storing dat of file
        InputStream inputStream = new FileInputStream(keyFile);
        BufferedInputStream buffered = new BufferedInputStream(inputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(buffered);
        try{
//Extracting the modulus and exponent from Key object
        BigInteger modulus = (BigInteger) objectInputStream.readObject();
        BigInteger exponent = (BigInteger) objectInputStream.readObject();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        //Genarating private or public file
        if(fileType.equals("public")){
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            return keyFactory.generatePublic(publicKeySpec);
        }
        else if (fileType.equals("private")){
            RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            return keyFactory.generatePrivate(privateKeySpec);
            
        }
        else
            return null;
        }
        finally{
                objectInputStream.close();
                }        
       }
    public static byte[] encryptAESKeywithRSA(SecretKey AESKey, String destinationPublicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IOException, FileNotFoundException, InvalidKeySpecException, ClassNotFoundException{
        PublicKey publicKey = (PublicKey) readKeyFromFile(destinationPublicKey, "public");
        Cipher RSACipher = Cipher.getInstance("RSA"); 
        //Using WRAP_MODE to avoid complications of encoding.
        RSACipher.init(Cipher.WRAP_MODE, publicKey);
        //Encrypting AES Key with RSA key
        byte[] RSAEncryptedAESKey = RSACipher.wrap(AESKey);
        return RSAEncryptedAESKey;      
    } 
    public static SecretKeySpec decryptAESWithRSA(byte[] EncrypteAESKey,String destinationPrivateKey) throws IOException, NoSuchAlgorithmException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException{
       //Reading private key
        PrivateKey privateKey = (PrivateKey) readKeyFromFile(destinationPrivateKey, "private");
        //Creating RSA Cipher instance
       Cipher RSACipher = Cipher.getInstance("RSA");
       //Intializing RSA private key
       RSACipher.init(Cipher.UNWRAP_MODE, privateKey);
       //decrypting to get AES key from RSA key
       SecretKeySpec AESKey = (SecretKeySpec) RSACipher.unwrap(EncrypteAESKey,"AES", Cipher.SECRET_KEY);
       return AESKey;
    }
    public static byte[] encryptedWithDigitalSig(String fileName, byte data[] ) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException{
        //Reading Private key
        PrivateKey privateKey = (PrivateKey) readKeyFromFile(fileName,"private");
        //getting signature Instance
        Signature signature = Signature.getInstance("SHA1withRSA");
        //Intializing
        signature.initSign(privateKey);
        //Creating signature and signing it
        signature.update(data);
        return signature.sign();
         
    }
    public static boolean verifySignature(byte[] sign, String fileName, byte[] data) throws IOException, ClassNotFoundException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException{
        //Reading Public key
        PublicKey publicKey = (PublicKey) readKeyFromFile(fileName, "public");
        //Creating instance of Signature class
        Signature signature = Signature.getInstance("SHA1withRSA");
        //Initializing verification with Public Key
        signature.initVerify(publicKey);
        //Checkinh if Signature matches 
        signature.update(data);
        return signature.verify(sign);
        
        
        
        
    }
    
}
