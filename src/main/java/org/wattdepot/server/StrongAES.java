/**
 * StrongAES.java This file is part of WattDepot.
 * <p/>
 * Copyright (C) 2014  Cam Moore
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.server;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * StrongAES a singleton used for encrypting and decrypting user passwords.
 *
 * @author Cam Moore
 *
 */
public class StrongAES {

  /** The singleton instance. */
  private static StrongAES instance;

  private SecretKeySpec key;
  private Cipher cipher;
  private String salt;

  private static String stSalt;

  static {
    String val = System.getenv().get(ServerProperties.WATTDEPOT_SALT_ENV);
    if (val != null) {
      stSalt = val;
    }
    else {
      stSalt = "Watt2014Depot808";
    }
  }

  /**
   * @throws NoSuchPaddingException if there is a problem.
   * @throws NoSuchAlgorithmException if there is a problem.
   * @throws NoSuchProviderException if there is a problem.
   *
   */
  private StrongAES() throws NoSuchAlgorithmException, NoSuchPaddingException,
      NoSuchProviderException {
    String val = System.getenv().get(ServerProperties.WATTDEPOT_SALT_ENV);
    if (val != null) {
      this.salt = val;
      stSalt = val;
    }
    else {
      this.salt = "Watt2014Depot808";
      stSalt = this.salt;
    }
    this.key = new SecretKeySpec(salt.getBytes(), "AES");
    this.cipher = Cipher.getInstance("AES");
  }

  /**
   * @return The singleton StrongAES instance.
   */
  public static StrongAES getInstance() {
    if (instance == null) {
      try {
        instance = new StrongAES();
      }
      catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      catch (NoSuchPaddingException e) {
        e.printStackTrace();
      }
      catch (NoSuchProviderException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return instance;
  }

  /**
   * @param plainText the plain text to encrypt.
   * @return the encrypted text.
   */
  public String encrypt(String plainText) {
    String cipherText = null;
    try {
      this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
      cipherText = Base64.encodeBase64String(cipher.doFinal(plainText.getBytes()));
//      cipherText = String.valueOf(Base64.encodeBase64(cipher.doFinal(plainText.getBytes())));
    }
    catch (InvalidKeyException e) { // NOPMD
      // not sure what to do.
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return cipherText;
  }

  /**
   * @param cipherText the encrypted string.
   * @return the decrypted string.
   * @throws java.security.InvalidKeyException if there is a problem.
   * @throws javax.crypto.BadPaddingException if there is a problem.
   * @throws javax.crypto.IllegalBlockSizeException if there is a problem.
   */
  public String decrypt(String cipherText) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    String plainText = null;
    this.cipher.init(Cipher.DECRYPT_MODE, this.key);
    byte[] encryptText = Base64.decodeBase64(cipherText);
    plainText = new String(cipher.doFinal(encryptText));
//    plainText = new String(cipher.doFinal(Base64.decodeBase64(cipherText.getBytes())));
    return plainText;
  }

  /**
   * Static method for encrypting text.
   * @param text The text to encrypt.
   * @return The encrypted string.
   */
  public static String symmetricEncrypt(String text) {
    byte[] raw;
    String encryptedString;
    SecretKeySpec skeySpec;
    byte[] encryptText = text.getBytes();
    Cipher cipher;
    try {
      raw = stSalt.getBytes("UTF-8");
      System.out.println(raw.length);
      skeySpec = new SecretKeySpec(raw, "AES");
      cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
      encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
    }
    catch (Exception e) {
      e.printStackTrace();
      return "Error";
    }
    return encryptedString;
  }

  /**
   * Static method for decrypting text.
   * @param text The encrypted text.
   * @return The decrypted string.
   */
  public static String symmetricDecrypt(String text) {
    Cipher cipher;
    String encryptedString;
    byte[] encryptText = null;
    byte[] raw;
    SecretKeySpec skeySpec;
    try {
      raw = stSalt.getBytes("UTF-8");
      skeySpec = new SecretKeySpec(raw, "AES");
      encryptText = Base64.decodeBase64(text);
      cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec);
      encryptedString = new String(cipher.doFinal(encryptText));
    }
    catch (Exception e) {
      e.printStackTrace();
      return "Error";
    }
    return encryptedString;
  }

}
