/**
 * GarbageCollectionDefinitionList.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import java.util.ArrayList;

/**
 * GarbageCollectionDefinitionList - Encapsulates an ArrayList of
 * GarbageCollectionDefinitions so Restlet can convert it to a JSON object.
 * 
 * @author Cam Moore
 * 
 */
public class GarbageCollectionDefinitionList {
  private ArrayList<GarbageCollectionDefinition> definitions;

  /**
   * Default constructor.
   */
  public GarbageCollectionDefinitionList() {
    definitions = new ArrayList<GarbageCollectionDefinition>();
  }

  /**
   * @return the definitions
   */
  public ArrayList<GarbageCollectionDefinition> getDefinitions() {
    return definitions;
  }

  /**
   * @param definitions the definitions to set
   */
  public void setDefinitions(ArrayList<GarbageCollectionDefinition> definitions) {
    this.definitions = definitions;
  }

}
