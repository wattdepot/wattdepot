/**
 * UnitsHelper.java This file is part of WattDepot.
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
package org.wattdepot.common.util;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

/**
 * UnitsHelper - Utility class that helps build measurement types.
 * 
 * @author Cam Moore
 * 
 */
public class UnitsHelper {
  /** Holds the defined units. */
  public static final Map<String, Unit<?>> quantities = new HashMap<String, Unit<?>>();

  static {
    UnitFormat format = UnitFormat.getInstance();
    format.alias(SI.MICRO(SI.GRAM.divide(NonSI.LITER)), "ppm");
    format.label(SI.MICRO(SI.GRAM.divide(NonSI.LITER)), "ppm");
    quantities.put(buildName("Power", SI.WATT), SI.WATT);
    quantities.put(buildName("Energy", SI.WATT.times(NonSI.HOUR)), SI.WATT.times(NonSI.HOUR));
    quantities.put(buildName("Frequency", SI.HERTZ), SI.HERTZ);
    quantities.put(buildName("Temperature", NonSI.FAHRENHEIT), NonSI.FAHRENHEIT);
    quantities.put(buildName("Temperature", SI.CELSIUS), SI.CELSIUS);
    quantities.put(buildName("Volume", NonSI.GALLON_LIQUID_US), NonSI.GALLON_LIQUID_US);
    quantities.put(buildName("Volume", NonSI.LITER), NonSI.LITER);
    quantities.put(buildName("Flow Rate", NonSI.GALLON_LIQUID_US.divide(SI.SECOND)),
        NonSI.GALLON_LIQUID_US.divide(SI.SECOND));
    quantities.put(buildName("Flow Rate", NonSI.LITER.divide(SI.SECOND)),
        NonSI.LITER.divide(SI.SECOND));
    quantities.put(buildName("Mass", SI.KILOGRAM), SI.KILOGRAM);
    quantities.put(buildName("Mass", NonSI.POUND), NonSI.POUND);
    quantities.put(buildName("Humidity", NonSI.PERCENT), NonSI.PERCENT);
    quantities.put(buildName("Concentration", SI.MICRO(SI.GRAM.divide(NonSI.LITER))), SI.MICRO(SI.GRAM.divide(NonSI.LITER)));
    quantities.put(buildName("Cloud Coverage", NonSI.PERCENT), NonSI.PERCENT);

  }

  /**
   * @param type
   *          The type of the unit, Energy, Power, Mass, etc.
   * @param unit
   *          The Unit<?>.
   * @return The name.
   */
  public static String buildName(String type, Unit<?> unit) {
    String s = unit.toString();
    String s1 = Normalizer.normalize(s, Normalizer.Form.NFKD);
    String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    try {
      String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
      s2 = s2.replace("?", "");
      return type + " (" + s2 + ")";
      
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return type + " (" + s1 + ")";
  }


}
