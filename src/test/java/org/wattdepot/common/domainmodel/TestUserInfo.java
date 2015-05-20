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


public class TestUserInfo {

  @Test
  public void testEquals() throws Exception {
    UserInfo info = InstanceFactory.getUserInfo();
    assertTrue(info.equals(info));
    assertFalse(info.equals(null));
    assertFalse(info.equals(""));
    UserInfo info2 = new UserInfo();
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info2.setUid("foo");
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info2.setUid(info.getUid());
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info2.setFirstName("foo");
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info2.setFirstName(info.getFirstName());
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info2.setLastName("foo");
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info2.setLastName(info.getLastName());
    assertFalse(info2.hashCode() == info.hashCode());
    assertFalse(info2.equals(info));
    info.addProperty(new Property("key", "value"));
    UserInfo info3 = InstanceFactory.getUserInfo2();
    assertFalse(info.equals(info3));
  }

  @Test
  public void testGetProperty() throws Exception {
    UserInfo info = InstanceFactory.getUserInfo();
    Property p = new Property("key", "value");
    info.addProperty(p);
    assertTrue(p.equals(info.getProperty("key")));
  }

  @Test
  public void testHashCode() throws Exception {
    UserInfo info = InstanceFactory.getUserInfo();
    int hash = info.hashCode();
    info.addProperty(new Property("key", "value"));
    assertFalse(hash == info.hashCode());
  }
}