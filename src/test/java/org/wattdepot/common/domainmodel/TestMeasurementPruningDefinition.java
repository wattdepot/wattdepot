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

public class TestMeasurementPruningDefinition {

  @Test
  public void testEquals() {
    MeasurementPruningDefinition mpd = InstanceFactory.getMeasurementPruningDefinition();
    assertTrue(mpd.equals(mpd));
    assertFalse(mpd.equals(null));
    assertFalse(mpd.equals("foo"));
    MeasurementPruningDefinition mpd2 = new MeasurementPruningDefinition();
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setCollectWindowDays(-100);
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setCollectWindowDays(mpd.getCollectWindowDays());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setDepositoryId("foo");
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setDepositoryId(mpd.getDepositoryId());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setId("foo");
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setId(mpd.getId());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setIgnoreWindowDays(-100);
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setIgnoreWindowDays(mpd.getIgnoreWindowDays());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setMinGapSeconds(-100);
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setMinGapSeconds(mpd.getMinGapSeconds());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setName("foo");
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setName(mpd.getName());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setSensorId("foo");
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setSensorId(mpd.getSensorId());
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setOrganizationId("foo");
    assertFalse(mpd2.hashCode() == mpd.hashCode());
    assertFalse(mpd2.equals(mpd));
    mpd2.setOrganizationId(mpd.getOrganizationId());
    assertTrue(mpd2.hashCode() == mpd.hashCode());
    assertTrue(mpd2.equals(mpd));
  }
}