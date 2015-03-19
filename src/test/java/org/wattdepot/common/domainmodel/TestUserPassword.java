/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.common.domainmodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestUserPassword {

  @Test
  public void testEquals() {
    UserPassword p1 = InstanceFactory.getUserPassword();
    assertTrue(p1.equals(p1));
    assertFalse(p1.equals(null));
    assertFalse(p1.equals(new Object()));
    UserPassword p2 = new UserPassword();
    assertFalse(p2.hashCode() == p1.hashCode());
    assertFalse(p2.equals(p1));
    p2.setEncryptedPassword("foo");
    assertFalse(p2.hashCode() == p1.hashCode());
    assertFalse(p2.equals(p1));
    p2.setEncryptedPassword(p1.getEncryptedPassword());
    assertFalse(p2.hashCode() == p1.hashCode());
    assertFalse(p2.equals(p1));
    p2.setUid("foo");
    assertFalse(p2.hashCode() == p1.hashCode());
    assertFalse(p2.equals(p1));
    p2.setUid(p1.getUid());
    assertFalse(p2.hashCode() == p1.hashCode());
    assertFalse(p2.equals(p1));
    p2.setOrganizationId("foo");
    assertFalse(p2.hashCode() == p1.hashCode());
    assertFalse(p2.equals(p1));
    p2.setOrganizationId(p1.getOrganizationId());
    assertTrue(p2.hashCode() == p1.hashCode());
    assertTrue(p2.equals(p1));
  }
}