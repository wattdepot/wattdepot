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

function getKnownGCD(id) {
  return GCDS[id];
}

// ****************** Depositories **************************
function putNewDepository() {
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
function putNewSensor() {
    var id = $("input[name='sensor_id']").val();
    var name = $("input[name='sensor_name']").val();
    var uri = $("input[name='sensor_uri']").val();
    var selected_model = $("select[name='sensor_model']").val();
    var sensor = {
        "id" : id,
        "name" : name,
        "uri" : uri,
        "modelId" : selected_model,
        "organizationId" : ORGID
    };
    setSelectedTab('sensors');
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

function putNewInlineSensorProperty() {
    var id = $("input[name='sensor_id']").val();
    var old_sensor = getKnownSensor(id);
    var key = $("input[name='inline_sensor_key']").val();
    var value = $("input[name='inline_sensor_value']").val();
    var property = new Object();
    property.key = key;
    property.value = value;
    old_sensor.properties.push(property);
    var loc = buildLocation(old_sensor.locationId);
    var model = buildSensorModel(old_sensor.modelId);
    var owner = getKnownUserGroup(ORGID);    
    var sensor = {
        "id" : old_sensor.id,
        "name" : old_sensor.name,
        "uri" : old_sensor.uri,
        "sensorLocation" : loc,
        "model" : model,
        "organizationId" : ORGID,
        "properties" : old_sensor.properties
    };
    setSelectedTab('sensors');
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
    for (var i = 0; i < properties.length; i++) {
        prop_str +=  properties[i].key + " : " + properties[i].value;
    }
    $('#sensor_properties').text(prop_str);

    modalElement.modal('show');
};

function updateSensor() {
  var id = $("input[name='edit_sensor_id']").val();
  var name = $("input[name='edit_sensor_name']").val();
  var uri = $("input[name='edit_sensor_uri']").val();
  var selected_model = $("select[name='sensor_model']").val();
  var sensor = {
      "id" : id,
      "name" : name,
      "uri" : uri,
      "modelId" : selected_model,
      "organizationId" : ORGID
  };
  setSelectedTab('sensors');
  $.ajax({
      url : '/wattdepot/' + ORGID + '/sensor/' + id,
      type : 'POST',
      contentType : 'application/json',
      data : JSON.stringify(sensor),
      success : function() {
          location.reload();
      },
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

//****************** Collector Process Definitions **************************
function putNewCPD() {
    var id = $("input[name='cpd_id']").val();
    var name = $("input[name='cpd_name']").val();
    var selected_sensor = $("select[name='cpd_sensor']").val();
    var interval = $("input[name='cpd_polling']").val();
    var selected_depository = $("select[name='cpd_depository']").val();
    var process = {
        "id" : id,
        "name" : name,
        "sensorId" : selected_sensor,
        "pollingInterval" : interval,
        "depositoryId" : selected_depository,
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
    $("input[name='edit_cpd_name']").val(process['id']);
    var lid = process.sensorId;
    $('select[name="edit_cpd_sensor"] option[value="' + lid + '"]').prop(
            "selected", "selected");
    $("input[name='edit_cpd_polling']").val(process['pollingInterval']);
    var mid = process.depositoryId;
    $('select[name="edit_cpd_depository"] option[value="' + mid + '"]').prop(
            "selected", "selected");
    var properties = process.properties;
    var prop_str = "";
    for (var i = 0; i < properties.length; i++) {
        prop_str +=  properties[i].key + " : " + properties[i].value;
    }
    $('#metadata_properties').text(prop_str);
    modalElement.modal('show');
};

function updateCPD() {
  var id = $("input[name='edit_cpd_id']").val();
  var name = $("input[name='edit_cpd_name']").val();
  var selected_sensor = $("select[name='edit_cpd_sensor']").val();
  var interval = $("input[name='edit_cpd_polling']").val();
  var selected_depository = $("select[name='edit_cpd_depository']").val();
  var process = {
      "id" : id,
      "name" : name,
      "sensorId" : selected_sensor,
      "pollingInterval" : interval,
      "depositoryId" : selected_depository,
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

//****************** Garbage Collection Definitions **************************
function putNewGCD() {
    var id = $("input[name='gcd_id']").val();
    var name = $("input[name='gcd_name']").val();
    var selected_depository = $("select[name='cpd_depository']").val();
    var selected_sensor = $("select[name='gcd_sensor']").val();
    var ignore = $("input[name=gcd_ignore").val();
    var collect = $("input[name='gcd_collect']").val();
    var gap = $("input[name='gcd_gap']").val();
    var gcd = {
        "id" : id,
        "name" : name,
        "depositoryId" : selected_depository,
        "sensorId" : selected_sensor,
        "ignoreWindowDays" : ignore,
        "collectWindowDays" : collect,
        "minGapSeconds" : gap,
        "organizationId" : ORGID
    };
    setSelectedTab('garbagecollection');
    $.ajax({
        url : '/wattdepot/' + ORGID + '/garbage-collection-definition/',
        type : 'PUT',
        contentType : 'application/json',
        data : JSON.stringify(gcd),
        success : function() {
            location.reload();
        },
    });
};


function edit_gcd_dialog(event, id) {
    setSelectedTab('garbagecollection');
    var modalElement = $('#editGCDModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });
    var gcd = getKnownGCD(id);
    $("input[name='edit_gcd_id']").val(id);
    $("input[name='edit_gcd_name']").val(gcd['id']);
    var mid = gcd.depositoryId;
    $('select[name="edit_cpd_depository"] option[value="' + mid + '"]').prop(
        "selected", "selected");
    var lid = gcd.sensorId;
    $('select[name="edit_gcd_sensor"] option[value="' + lid + '"]').prop(
            "selected", "selected");
    $("input[name='edit_gcd_ignore']").val(gcd['ignoreWindowDays']);
    $("input[name='edit_gcd_collect']").val(gcd['collectWindowDays']);
    $("input[name='edit_gcd_gap']").val(gcd['minGapSeconds']);
    modalElement.modal('show');
};

function updateGCD() {
  var id = $("input[name='edit_gcd_id']").val();
  var name = $("input[name='edit_gcd_name']").val();
  var selected_depository = $("select[name='edit_gcd_depository']").val();
  var selected_sensor = $("select[name='edit_gcd_sensor']").val();
  var ignore = $("input[name='edit_gcd_ignore']").val();
  var collect = $("input[name='edit_gcd_collect']").val();
  var gap = $("input[name='edit_gcd_gap']").val();
  var gcd = {
      "id" : id,
      "name" : name,
      "depositoryId" : selected_depository,
      "sensorId" : selected_sensor,
      "ignoreWindowDays" : ignore,
      "collectWindowDays" : collect,
      "minGapSeconds" : gap,
      "organizationId" : ORGID
  };
  setSelectedTab('garbagecollection');
  $.ajax({
      url : '/wattdepot/' + ORGID + '/garbage-collection-definition/' + id,
      type : 'POST',
      contentType : 'application/json',
      data : JSON.stringify(gcd),
      success : function() {
          location.reload();
      },
  });
};

function delete_gcd_dialog(event, id) {
    var modalElement = $('#deleteGCDModal');

    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });
    modalElement.find('#del_gcd_id').html(id);
    modalElement.modal('show');
};

function deleteGCD() {
    var id = $('#del_gcd_id').html();
    setSelectedTab('garbagecollection');
    $.ajax({
        url : '/wattdepot/' + ORGID + '/garbage-collection-definition/' + id,
        type : 'DELETE',
        contentType : 'application/json',
        success : function() {
            location.reload();
        },
    });
};

