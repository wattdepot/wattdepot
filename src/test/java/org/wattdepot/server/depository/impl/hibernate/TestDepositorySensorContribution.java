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

package org.wattdepot.server.depository.impl.hibernate;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestDepositorySensorContribution {

  @Test
  public void testEquals() throws Exception {
    DepositorySensorContribution dsc1 = new DepositorySensorContribution();
    dsc1.setDepository(InstanceFactory.getDepository());
    dsc1.setSensor(InstanceFactory.getSensor());
    assertTrue(dsc1.equals(dsc1));
    assertFalse(dsc1.equals(null));
    assertFalse(dsc1.equals(new Object()));
    assertFalse(dsc1.equals("foo"));
    DepositorySensorContribution dsc2 = new DepositorySensorContribution();
    assertFalse(dsc2.hashCode() == dsc1.hashCode());
    assertFalse(dsc2.equals(dsc1));
    dsc2.setDepository(new DepositoryImpl());
    assertFalse(dsc2.hashCode() == dsc1.hashCode());
    assertFalse(dsc2.equals(dsc1));
    dsc2.setDepository(dsc1.getDepository());
    assertFalse(dsc2.hashCode() == dsc1.hashCode());
    assertFalse(dsc2.equals(dsc1));
    dsc2.setSensor(new SensorImpl());
    assertFalse(dsc2.hashCode() == dsc1.hashCode());
    assertFalse(dsc2.equals(dsc1));
    dsc2.setSensor(dsc1.getSensor());
    assertTrue(dsc2.hashCode() == dsc1.hashCode());
    assertTrue(dsc2.equals(dsc1));
    assertTrue(dsc2.toString().equals(dsc1.toString()));
  }
}