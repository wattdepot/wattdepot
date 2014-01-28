/**
 * StrongAES.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.server;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * StrongAES a singleton used for encrypting and decrypting user passwords.
 * 
 * @author Cam Moore
 * 
 */
public class StrongAES {

  /** The singleton instance. */
  private static StrongAES instance;

  private Key key;
  private Cipher cipher;
  private String salt;

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
    }
    else {
      this.salt = "Watt2014Depot808";
    }
    this.key = new SecretKeySpec(salt.getBytes(), "AES");
    this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
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
   */
  public String decrypt(String cipherText) {
    String plainText = null;
    try {
      this.cipher.init(Cipher.DECRYPT_MODE, this.key);
      plainText = new String(cipher.doFinal(Base64.decodeBase64(cipherText)));
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
    return plainText;

  }
}
