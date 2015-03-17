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

public class TestOrganization {

  @Test
  public void testEquals() {
    Organization org1 = InstanceFactory.getOrganization();
    assertTrue(org1.equals(org1));
    assertFalse(org1.equals(null));
    assertFalse(org1.equals(""));
    Organization org2 = new Organization();
    assertFalse(org2.hashCode() == org1.hashCode());
    assertFalse(org2.equals(org1));
    org2.setId("id");
    assertFalse(org2.hashCode() == org1.hashCode());
    assertFalse(org2.equals(org1));
    org2.setId(org1.getId());
    assertFalse(org2.hashCode() == org1.hashCode());
    assertFalse(org2.equals(org1));
    org2.setName("foo");
    assertFalse(org2.hashCode() == org1.hashCode());
    assertFalse(org2.equals(org1));
    org2.setName(org1.getName());
    assertFalse(org2.hashCode() == org1.hashCode());
    assertFalse(org2.equals(org1));
    org2.add(InstanceFactory.getUserInfo().getUid());
    assertTrue(org2.hashCode() == org1.hashCode());
    assertTrue(org2.equals(org1));
  }

  @Test
  public void testContains() {
    Organization org1 = InstanceFactory.getOrganization();
    assertTrue(org1.contains(InstanceFactory.getUserInfo().getUid()));
    Organization org2 = new Organization();
    assertFalse(org2.contains(InstanceFactory.getUserInfo().getUid()));
    org2.add(InstanceFactory.getUserInfo().getUid());
    org2.remove(InstanceFactory.getUserInfo().getUid());
    assertFalse(org2.contains(InstanceFactory.getUserInfo().getUid()));
  }

}