/**
 * TestProperty.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
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
package org.wattdepot.common.domainmodel;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the Property class.
 *
 * @author Cam Moore
 *
 */
public class TestProperty {

  /**
   * Test method for {@link org.wattdepot.common.domainmodel.Property#Property(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testPropertyStringString() {
    Property prop = new Property("key", "value");
    assertNotNull(prop);
    assertTrue("key".equals(prop.getKey()));
    assertTrue("value".equals(prop.getValue()));
  }

  /**
   * Test method for {@link org.wattdepot.common.domainmodel.Property#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Property p1 = new Property();
    Property p2 = new Property("key", "value");
    int hashCode1 = p1.hashCode();
    int hashCode2 = p2.hashCode();
    assertFalse(hashCode1 == hashCode2);
    assertFalse(p1.equals(p2));
    assertFalse(p2.equals(p1));
    assertFalse(p2.equals(null));
    assertFalse(p1.equals(new Object()));
    p1.setKey(p2.getKey());
    p1.setValue(p2.getValue());
    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));
    assertTrue(p1.hashCode() == p2.hashCode());
  }

}
