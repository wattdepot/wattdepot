/**
 * TestStrongAES.java This file is part of WattDepot.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

/**
 * TestStrongAES Test cases for the StrongAES class.
 * 
 * @author Cam Moore
 * 
 */
public class TestStrongAES {

  /**
   * Sets up the tests.
   */
  @Before
  public void setUp() {
  }

  /**
   * Cleans up after tests.
   */
  @After
  public void tearDown() {
  }

  /**
   * Test method for
   * {@link org.wattdepot.server.StrongAES#encrypt(java.lang.String)}.
   */
  @Test
  public void testRoundTrip() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
    StrongAES aes = StrongAES.getInstance();
    String plain = "This is a test with lots of data";
    String result = aes.encrypt(plain);
    assertFalse(result.equals(plain));
    String decrypt = aes.decrypt(result);
    assertTrue(decrypt.equals(plain));
  }

  /**
   * Tests the static methods.
   */
  @Test
  public void testStatic() {
    String plain = "This is a test with lots of data";
    String result = StrongAES.symmetricEncrypt(plain);
    assertNotNull(result);
    String decrypt = StrongAES.symmetricDecrypt(result);
    assertEquals(plain, decrypt);

  }
}
