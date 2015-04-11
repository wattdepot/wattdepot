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

/**
 * Created by carletonmoore on 4/10/15.
 */

var wdClient = null;
var loaded = false; // If the google visualization API has been loaded.


/**
 * Loads the page either using the permalink or starting fresh.
 */
function loadPage() {
  wdClient = org.WattDepot.Client(server);
}

function setUpTimeSeries() {
  var sel = $("#foobar");
  for (var key in POWER_SENSORS) {
    sel.append("<option value=\"" + key + "\">" + POWER_SENSORS[key]['name'] + "</option>");
  }

}
