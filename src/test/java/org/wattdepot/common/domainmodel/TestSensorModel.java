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

public class TestSensorModel {

  @Test
  public void testEquals() throws Exception {
    SensorModel model1 = InstanceFactory.getSensorModel();
    assertFalse(model1.equals(null));
    assertFalse(model1.equals(""));
    SensorModel model2 = new SensorModel();
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setId("foo");
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setId(model1.getId());
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setName("foo");
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setName(model1.getName());
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setProtocol("foo");
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setProtocol(model1.getProtocol());
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setType("foo");
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setType(model1.getType());
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setVersion("foo");
    assertFalse(model2.hashCode() == model1.hashCode());
    assertFalse(model2.equals(model1));
    model2.setVersion(model1.getVersion());
    assertTrue(model2.hashCode() == model1.hashCode());
    assertTrue(model2.equals(model1));
  }
}