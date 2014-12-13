// Utility functions for get/set/delete cookies
function setCookie(name, value, days) {
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    var expires = "; expires=" + date.toGMTString();
  } else
    var expires = "";
  document.cookie = name + "=" + value + expires + "; path=/";
}

function getCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ')
      c = c.substring(1, c.length);
    if (c.indexOf(nameEQ) == 0)
      return c.substring(nameEQ.length, c.length);
  }
  return null;
}

function setSelectedTab(tabName) {
  setCookie("selected-tab", tabName);
}

function getKnownDepository(id) {
  return DEPOSITORIES[id];
};

function getKnownMeasurementType(id) {
  return MEASUREMENTTYPES[id];
};

function getKnownSensorModel(id) {
  return MODELS[id];
};

function getKnownSensor(id) {
  return SENSORS[id];
};

function getKnownSensorGroup(id) {
  return SENSORGROUPS[id];
}

function getKnownCPD(id) {
  return CPDS[id];
}

function getKnownMPD(id) {
  return MPDS[id];
}

function isValidSlug(slug) {
  var patt = /[A-Z\s]+/
  var result = patt.test(slug);
  return !result;
}

// ****************** Depositories **************************
function putNewDepository() {
  if (false === $('#add-depository').parsley().validate()) {
    return;
  }
  var id = $("input[name='depository_id']").val();
  var name = $("input[name='depository_name']").val();
  var selected_type = $("select[name='depository_type']").val();
  var measType = getKnownMeasurementType(selected_type);
  var depo = {
    "id" : id,
    "name" : name,
    "measurementType" : measType,
    "organizationId" : ORGID
  };
  setSelectedTab('depositories');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/depository/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(depo),
    success : function() {
      location.reload();
    },
  });
};

function delete_depository_dialog(event, id) {
  var modalElement = $('#deleteDepositoryModal');

  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  modalElement.find('#del_depository_id').html(id);
  modalElement.modal('show');
};

function deleteDepository() {
  var id = $('#del_depository_id').html();
  setSelectedTab('depositories');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/depository/' + id,
    type : 'DELETE',
    contentType : 'application/json',
    success : function() {
      location.reload();
    },
  });
};

// ****************** Sensors **************************
var numSensorProps = 1;

function addSensorProp() {
  // check empty last row
  var lastProp = $("input[name='sensor_prop" + numSensorProps + "']").val();
  var lastVal = $("input[name='sensor_val" + numSensorProps + "']").val();
  if (lastProp != "" && lastVal != "") {
    numSensorProps++;
    var body = $("#add_sensor_props");
    body.append('<tr>');
    body.append('<td><input type="text" name="sensor_prop' + numSensorProps
        + '" class="form-control"></td>');
    body.append('<td><input type="text" name="sensor_val' + numSensorProps
        + '" class="form-control" onchange="addSensorProp()"></td>');
    body.append('</tr>');
  }
};

function putNewSensor() {
  if (false === $('#add-sensor').parsley().validate()) {
    return;
  }
  var id = $("input[name='sensor_id']").val();
  var name = $("input[name='sensor_name']").val();
  var uri = $("input[name='sensor_uri']").val();
  var selected_model = $("select[name='sensor_model']").val();
  var properties = [];
  for (var i = 1; i < numSensorProps; i++) {
    var property = {};
    var prop = $("input[name='sensor_prop" + i + "']").val();
    var val = $("input[name='sensor_val" + i + "']").val();
    if (prop != "" || val != "") {
      property["key"] = prop;
      property["value"] = val;
      properties.push(property);
    }
  }
  var sensor = {
    "id" : id,
    "name" : name,
    "uri" : uri,
    "modelId" : selected_model,
    "properties" : properties,
    "organizationId" : ORGID
  };
  setSelectedTab('sensors');
  numSensorProps = 1;
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(sensor),
    success : function() {
      location.reload();
    },
  });
};

var numEditSensorProps = 1;

function editSensorProp() {
  // check empty last row
  var lastProp = $("input[name='edit_sensor_prop" + numEditSensorProps + "']").val();
  var lastVal = $("input[name='edit_sensor_val" + numEditSensorProps + "']").val();
  if (lastProp != "" && lastVal != "") {
    numEditSensorProps++;
    var body = $("#edit_sensor_props");
    body.append('<tr>');
    body.append('<td><input type="text" name="edit_sensor_prop' + numEditSensorProps
        + '" class="form-control"></td>');
    body.append('<td><input type="text" name="edit_sensor_val' + numEditSensorProps
        + '" class="form-control" onchange="addSensorProp()"></td>');
    body.append('</tr>');
  }
};


function edit_sensor_dialog(event, id) {
  var modalElement = $('#editSensorModal');
  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });

  var sensor = getKnownSensor(id);
  $("input[name='edit_sensor_id']").val(id);
  $("input[name='edit_sensor_name']").val(sensor['name']);
  $("input[name='edit_sensor_uri']").val(sensor['uri']);
  var mid = sensor.modelId;
  $('select[name="edit_sensor_model"] option[value="' + mid + '"]').prop(
      "selected", "selected");
  var properties = sensor.properties;
  var prop_str = "";
  var body = $("#edit_sensor_props");
  body.empty();
  var i = 0;
  for (i = 0; i < properties.length; i++) {
    body.append('<tr>');
    body.append('<td><input type="text" name="edit_sensor_prop' + i
        + '" value="' + properties[i].key + '" class="form-control"></td>');
    body.append('<td><input type="text" name="edit_sensor_val' + i
        + '" value="' + properties[i].value + '" class="form-control"></td>');
    body.append('</tr>');
  }
  body.append('<tr>');
  body.append('<td><input type="text" name="edit_sensor_prop' + i
      + '" class="form-control"></td>');
  body.append('<td><input type="text" name="edit_sensor_val' + i
      + '" class="form-control" onchange="editSensorProp()"></td>');
  body.append('</tr>');

  modalElement.modal('show');
};

function updateSensor() {
  if(false === $('#edit-sensor').parsley().validate()) {
    return;
  }
  var id = $("input[name='edit_sensor_id']").val();
  var name = $("input[name='edit_sensor_name']").val();
  var uri = $("input[name='edit_sensor_uri']").val();
  var selected_model = $("select[name='edit_sensor_model']").val();
  var properties = [];
  for (var i = 0; i < numEditSensorProps; i++) {
    var property = {};
    var prop = $("input[name='edit_sensor_prop" + i + "']").val();
    var val = $("input[name='edit_sensor_val" + i + "']").val();
    if (prop != "" || val != "") {
      property["key"] = prop;
      property["value"] = val;
      properties.push(property);
    }
  }
  var sensor = {
    "id" : id,
    "name" : name,
    "uri" : uri,
    "modelId" : selected_model,
    "properties" : properties,
    "organizationId" : ORGID
  };
  setSelectedTab('sensors');
  numEditSensorProps = 1;
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor/' + id,
    type : 'POST',
    contentType : 'application/json',
    data : JSON.stringify(sensor),
    success : function() {
      location.reload();
    },
    error : function( jqXHR, textStatus, errorThrown) {
      console.log(textStatus + ": " + errorThrown);
      alert("There was a problem with the request " + textStatus + ": " + errorThrown);
      return false;
    }
  });
};

function delete_sensor_dialog(event, id) {
  var modalElement = $('#deleteSensorModal');

  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  modalElement.find('#del_sensor_id').html(id);
  modalElement.modal('show');
};

function deleteSensor() {
  var id = $('#del_sensor_id').html();
  setSelectedTab('sensors');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor/' + id,
    type : 'DELETE',
    contentType : 'application/json',
    success : function() {
      location.reload();
    },
  });
};

// ****************** Sensor Groups **************************
function putNewSensorGroup() {
  if(false === $('#add-sensor-group').parsley().validate()) {
    return;
  }
  var id = $("input[name='sensorgroup_id']").val();
  var name = $("input[name='sensorgroup_name']").val();
  var selected_ids = $("select[name='groupsensors']").val() || [];
  var selected_sensors = new Array();
  for (var i = 0; i < selected_ids.length; i++) {
    selected_sensors.push(selected_ids[i]);
  }

  setSelectedTab('sensorgroups');
  var grp = {
    "id" : id,
    "name" : name,
    "sensors" : selected_sensors,
    "organizationId" : ORGID
  };
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor-group/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(grp),
    success : function() {
      location.reload();
    },
  });
};

function edit_sensorgroup_dialog(event, id) {
  var modalElement = $('#editSensorGroupModal');
  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });

  var group = getKnownSensorGroup(id);
  $("input[name='edit_sensorgroup_id']").val(group['id']);
  $("input[name='edit_sensorgroup_name']").val(group['name']);
  for (var i = 0; i < group.sensors.length; i++) {
    var uid = group.sensors[i].id;
    $('select[name="edit_groupsensors"] option[value="' + uid + '"]').prop(
        "selected", "selected");
  }
  modalElement.modal('show');
};

function updateSensorGroup() {
  if(false === $('#edit-sensor-group').parsley().validate()) {
    return;
  }
  var id = $("input[name='edit_sensorgroup_id']").val();
  var name = $("input[name='edit_sensorgroup_name']").val();
  var selected_ids = $("select[name='edit_groupsensors']").val() || [];
  var selected_sensors = new Array();
  for (var i = 0; i < selected_ids.length; i++) {
    selected_sensors.push(selected_ids[i]);
  }

  setSelectedTab('sensorgroups');
  var grp = {
    "id" : id,
    "name" : name,
    "sensors" : selected_sensors,
    "organizationId" : ORGID
  };
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor-group/' + id,
    type : 'POST',
    contentType : 'application/json',
    data : JSON.stringify(grp),
    success : function() {
      location.reload();
    },
  });
};

function delete_sensorgroup_dialog(event, id) {
  var modalElement = $('#deleteSensorGroupModal');

  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  modalElement.find('#del_sensorgroup_id').html(id);
  modalElement.modal('show');
};

function deleteSensorGroup() {
  var id = $('#del_sensorgroup_id').html();
  setSelectedTab('sensorgroups');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor-group/' + id,
    type : 'DELETE',
    contentType : 'application/json',
    success : function() {
      location.reload();
    },
  });
};

// ****************** Sensor Models **************************
function putNewModel() {
  if (false === $('#add-sensor-model').parsley().validate()) {
    return;
  }
  var id = $("input[name='model_id']").val();
  var name = $("input[name='model_name']").val();
  var protocol = $("input[name='model_protocol']").val();
  var type = $("input[name='model_type']").val();
  var version = $("input[name='model_version']").val();
  var model = {
    "id" : id,
    "name" : name,
    "protocol" : protocol,
    "type" : type,
    "version" : version
  };
  setSelectedTab('sensors');
  $.ajax({
    url : '/wattdepot/public/sensor-model/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(model),
    success : function() {
      location.reload();
    },
  });
};

function putNewInlineModel() {
  var name = $("input[name='inline_model_id']").val();
  var protocol = $("input[name='inline_model_protocol']").val();
  var type = $("input[name='inline_model_type']").val();
  var version = $("input[name='inline_model_version']").val();
  var model = {
    "name" : id,
    "protocol" : protocol,
    "type" : type,
    "version" : version
  };
  setSelectedTab('sensors');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor-model/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(model),
    success : function() {
      location.reload();
    },
  });
};

function edit_model_dialog(event, id) {
  var modalElement = $('#editModelModal');
  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });

  var model = getKnownSensorModel(id);
  $("input[name='edit_model_id']").val(model['id']);
  $("input[name='edit_model_name']").val(model['name']);
  $("input[name='edit_model_protocol']").val(model['protocol']);
  $("input[name='edit_model_type']").val(model['type']);
  $("input[name='edit_model_version']").val(model['version']);
  modalElement.modal('show');
};

function updateModel() {
  if (false === $('#edit-sensor-model').parsley().validate()) {
    return;
  }
  var id = $("input[name='edit_model_id']").val();
  var name = $("input[name='edit_model_name']").val();
  var protocol = $("input[name='edit_model_protocol']").val();
  var type = $("input[name='edit_model_type']").val();
  var version = $("input[name='edit_model_version']").val();
  var model = {
    "id" : id,
    "name" : name,
    "protocol" : protocol,
    "type" : type,
    "version" : version
  };
  setSelectedTab('sensors');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor-model/' + id,
    type : 'POST',
    contentType : 'application/json',
    data : JSON.stringify(model),
    success : function() {
      location.reload();
    },
  });
};

function delete_model_dialog(event, id) {
  var modalElement = $('#deleteModelModal');

  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  modalElement.find('#del_model_id').html(id);
  modalElement.modal('show');
};

function deleteModel() {
  var id = $('#del_model_id').html();
  setSelectedTab('sensormodels');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/sensor-model/' + id,
    type : 'DELETE',
    contentType : 'application/json',
    success : function() {
      location.reload();
    },
  });
};

// ****************** Collector Process Definitions **************************
var numCPDProps = 1;

function addCPDProp() {
  // check empty last row
  var lastProp = $("input[name='cpd_prop" + numSensorProps + "']").val();
  var lastVal = $("input[name='cpd_val" + numSensorProps + "']").val();
  if (lastProp != "" && lastVal != "") {
    numSensorProps++;
    var body = $("#add_cpd_props");
    body.append('<tr>');
    body.append('<td><input type="text" name="cpd_prop' + numSensorProps
        + '" class="form-control"></td>');
    body.append('<td><input type="text" name="cpd_val' + numSensorProps
        + '" class="form-control" onchange="addCPDProp()"></td>');
    body.append('</tr>');
  }
};


function putNewCPD() {
  if(false === $('#add-cpd').parsley().validate()) {
    return;
  }
  var id = $("input[name='cpd_id']").val();
  var name = $("input[name='cpd_name']").val();
  var selected_sensor = $("select[name='cpd_sensor']").val();
  var interval = $("input[name='cpd_polling']").val();
  var selected_depository = $("select[name='cpd_depository']").val();
  var properties = [];
  for (var i = 1; i < numSensorProps; i++) {
    var property = {};
    var prop = $("input[name='cpd_prop" + i + "']").val();
    var val = $("input[name='cpd_val" + i + "']").val();
    if (prop != "" || val != "") {
      property["key"] = prop;
      property["value"] = val;
      properties.push(property);
    }
  }
  var process = {
    "id" : id,
    "name" : name,
    "sensorId" : selected_sensor,
    "pollingInterval" : interval,
    "depositoryId" : selected_depository,
    "properties" : properties,
    "organizationId" : ORGID
  };
  setSelectedTab('sensorprocesses');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/collector-process-definition/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(process),
    success : function() {
      location.reload();
    },
  });
};

var numEditCPDProps = 1;

function editCPDProp() {
  // check empty last row
  var lastProp = $("input[name='edit_cpd_prop" + numEditCPDProps + "']").val();
  var lastVal = $("input[name='edit_cpd_val" + numEditCPDProps + "']").val();
  if (lastProp != "" && lastVal != "") {
    numEditSensorProps++;
    var body = $("#edit_cpd_props");
    body.append('<tr>');
    body.append('<td><input type="text" name="edit_cpd_prop' + numEditCPDProps
        + '" class="form-control"></td>');
    body.append('<td><input type="text" name="edit_CPD_val' + numEditCPDProps
        + '" class="form-control" onchange="editCPDProp()"></td>');
    body.append('</tr>');
  }
};


function edit_cpd_dialog(event, id) {
  setSelectedTab('sensorprocesses');
  var modalElement = $('#editCPDModal');
  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  var process = getKnownCPD(id);
  $("input[name='edit_cpd_id']").val(id);
  $("input[name='edit_cpd_name']").val(process['name']);
  var lid = process.sensorId;
  $('select[name="edit_cpd_sensor"] option[value="' + lid + '"]').prop(
      "selected", "selected");
  $("input[name='edit_cpd_polling']").val(process['pollingInterval']);
  var mid = process.depositoryId;
  $('select[name="edit_cpd_depository"] option[value="' + mid + '"]').prop(
      "selected", "selected");
  var properties = process.properties;
  var prop_str = "";
  var body = $("#edit_cpd_props");
  body.empty();
  var i = 0;
  for (i = 0; i < properties.length; i++) {
    body.append('<tr>');
    body.append('<td><input type="text" name="edit_cpd_prop' + i
        + '" value="' + properties[i].key + '" class="form-control"></td>');
    body.append('<td><input type="text" name="edit_cpd_val' + i
        + '" value="' + properties[i].value + '" class="form-control"></td>');
    body.append('</tr>');
  }
  body.append('<tr>');
  body.append('<td><input type="text" name="edit_cpd_prop' + i
      + '" class="form-control"></td>');
  body.append('<td><input type="text" name="edit_cpd_val' + i
      + '" class="form-control" onchange="editCPDProp()"></td>');
  body.append('</tr>');
  modalElement.modal('show');
};

function updateCPD() {
  if(false === $('#edit-cpd').parsley().validate()) {
    return;
  }
  var id = $("input[name='edit_cpd_id']").val();
  var name = $("input[name='edit_cpd_name']").val();
  var selected_sensor = $("select[name='edit_cpd_sensor']").val();
  var interval = $("input[name='edit_cpd_polling']").val();
  var selected_depository = $("select[name='edit_cpd_depository']").val();
  var properties = [];
  for (var i = 0; i < numEditSensorProps; i++) {
    var property = {};
    var prop = $("input[name='edit_cpd_prop" + i + "']").val();
    var val = $("input[name='edit_cpd_val" + i + "']").val();
    if (prop != "" || val != "") {
      property["key"] = prop;
      property["value"] = val;
      properties.push(property);
    }
  }
  var process = {
    "id" : id,
    "name" : name,
    "sensorId" : selected_sensor,
    "pollingInterval" : interval,
    "depositoryId" : selected_depository,
    "properties" : properties,
    "organizationId" : ORGID
  };
  setSelectedTab('sensorprocesses');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/collector-process-definition/' + id,
    type : 'POST',
    contentType : 'application/json',
    data : JSON.stringify(process),
    success : function() {
      location.reload();
    },
  });
};

function delete_cpd_dialog(event, id) {
  var modalElement = $('#deleteSensorProcessModal');

  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  modalElement.find('#del_sensorprocess_id').html(id);
  modalElement.modal('show');
};

function deleteCPD() {
  var id = $('#del_sensorprocess_id').html();
  setSelectedTab('sensorprocesses');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/collector-process-definition/' + id,
    type : 'DELETE',
    contentType : 'application/json',
    success : function() {
      location.reload();
    },
  });
};

// ****************** Garbage Collection Definitions **************************
function putNewMPD() {
  if (false === $('#add-mpd').parsley().validate()) {
    return;
  }
  var id = $("input[name='MPD_id']").val();
  var name = $("input[name='MPD_name']").val();
  var selected_depository = $("select[name='cpd_depository']").val();
  var selected_sensor = $("select[name='MPD_sensor']").val();
  var ignore = $("input[name=MPD_ignore").val();
  var collect = $("input[name='MPD_collect']").val();
  var gap = $("input[name='MPD_gap']").val();
  var MPD = {
    "id" : id,
    "name" : name,
    "depositoryId" : selected_depository,
    "sensorId" : selected_sensor,
    "ignoreWindowDays" : ignore,
    "collectWindowDays" : collect,
    "minGapSeconds" : gap,
    "organizationId" : ORGID
  };
  setSelectedTab('measurementpruning');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/measurement-pruning-definition/',
    type : 'PUT',
    contentType : 'application/json',
    data : JSON.stringify(MPD),
    success : function() {
      location.reload();
    },
  });
};

function edit_MPD_dialog(event, id) {
  setSelectedTab('measurementpruning');
  var modalElement = $('#editMPDModal');
  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  var MPD = getKnownMPD(id);
  $("input[name='edit_MPD_id']").val(id);
  $("input[name='edit_MPD_name']").val(MPD['id']);
  var mid = MPD.depositoryId;
  $('select[name="edit_cpd_depository"] option[value="' + mid + '"]').prop(
      "selected", "selected");
  var lid = MPD.sensorId;
  $('select[name="edit_MPD_sensor"] option[value="' + lid + '"]').prop(
      "selected", "selected");
  $("input[name='edit_MPD_ignore']").val(MPD['ignoreWindowDays']);
  $("input[name='edit_MPD_collect']").val(MPD['collectWindowDays']);
  $("input[name='edit_MPD_gap']").val(MPD['minGapSeconds']);
  modalElement.modal('show');
};

function updateMPD() {
  if (false === $('#edit-mpd').parsley().validate()) {
    return;
  }
  var id = $("input[name='edit_MPD_id']").val();
  var name = $("input[name='edit_MPD_name']").val();
  var selected_depository = $("select[name='edit_MPD_depository']").val();
  var selected_sensor = $("select[name='edit_MPD_sensor']").val();
  var ignore = $("input[name='edit_MPD_ignore']").val();
  var collect = $("input[name='edit_MPD_collect']").val();
  var gap = $("input[name='edit_MPD_gap']").val();
  var MPD = {
    "id" : id,
    "name" : name,
    "depositoryId" : selected_depository,
    "sensorId" : selected_sensor,
    "ignoreWindowDays" : ignore,
    "collectWindowDays" : collect,
    "minGapSeconds" : gap,
    "organizationId" : ORGID
  };
  setSelectedTab('measurementpruning');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/measurement-pruning-definition/' + id,
    type : 'POST',
    contentType : 'application/json',
    data : JSON.stringify(MPD),
    success : function() {
      location.reload();
    },
  });
};

function delete_MPD_dialog(event, id) {
  var modalElement = $('#deleteMPDModal');

  modalElement.modal({
    backdrop : true,
    keyboard : true,
    show : false
  });
  modalElement.find('#del_MPD_id').html(id);
  modalElement.modal('show');
};

function deleteMPD() {
  var id = $('#del_MPD_id').html();
  setSelectedTab('measurementpruning');
  $.ajax({
    url : '/wattdepot/' + ORGID + '/measurement-pruning-definition/' + id,
    type : 'DELETE',
    contentType : 'application/json',
    success : function() {
      location.reload();
    },
  });
};

