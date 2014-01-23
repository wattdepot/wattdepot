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
    return SENSORPROCESSES[id];
}

// ****************** Depositories **************************
function putNewDepository() {
    var id = $("input[name='depository_id']").val();
    var name = $("input[name='depository_name']").val();
    var selected_type = $("select[name='depository_type']").val();
    var measType = getKnownMeasurementType(selected_type);
    var owner = getKnownUserGroup(ORGID);
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
    var selected_loc = $("select[name='sensor_location']").val();
    var loc = buildLocation(selected_loc);
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
    var modalElement = $('#addSensorModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });

    var sensor = getKnownSensor(id);
    $("input[name='sensor_id']").val(id);    
    $("input[name='sensor_name']").val(sensor['name']);
    $("input[name='sensor_uri']").val(sensor['uri']);
    var lid = sensor.locationId;
    $('select[name="sensor_location"] option[value="' + lid + '"]').prop(
            "selected", "selected");
    var mid = sensor.modelId;
    $('select[name="sensor_model"] option[value="' + mid + '"]').prop(
            "selected", "selected");
    var properties = sensor.properties;
    var prop_str = "";
    for (var i = 0; i < properties.length; i++) {
        prop_str +=  properties[i].key + " : " + properties[i].value;
    }
    $('#sensor_properties').text(prop_str);

    modalElement.modal('show');
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
    var name = $("input[name='sensorgroup_name']").val();
    var selected_ids = $("select[name='groupsensors']").val() || [];
    var selected_sensors = new Array();
    for (var i = 0; i < selected_ids.length; i++) {
        selected_sensors.push(buildSensor(selected_ids[i]));
    }
    var owner = getKnownUserGroup(ORGID);    
    
    setSelectedTab('sensorgroups');
    var grp = {
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
    var modalElement = $('#addSensorGroupModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });

    var group = getKnownSensorGroup(id);
    $("input[name='sensorgroup_name']").val(id);
    for (var i = 0; i < group.sensors.length; i++) {
        var uid = group.sensors[i].id;
        $('select[name="groupsensors"] option[value="' + uid + '"]').prop(
                "selected", "selected");
    }
    modalElement.modal('show');
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
    var name = $("input[name='model_id']").val();
    var protocol = $("input[name='model_protocol']").val();
    var type = $("input[name='model_type']").val();
    var version = $("input[name='model_version']").val();
    var model = {
        "name" : name,
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
    var modalElement = $('#addModelModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });

    var model = getKnownSensorModel(id);
    $("input[name='model_id']").val(model['name']);
    $("input[name='model_protocol']").val(model['protocol']);
    $("input[name='model_type']").val(model['type']);
    $("input[name='model_version']").val(model['version']);
    modalElement.modal('show');
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

//****************** Sensor Processes **************************
function putNewProcess() {
    var name = $("input[name='sensorprocess_name']").val();
    var selected_sensor = $("select[name='process_sensor']").val();
    var sensor = buildSensor(selected_sensor);
    var interval = $("input[name='sensorprocess_polling']").val();
    var selected_depository = $("select[name='process_depository']").val();
    var depo = buildDepository(selected_depository);
    var owner = getKnownUserGroup(ORGID);    
    var process = {
        "name" : name,
        "sensor" : sensor,
        "pollingInterval" : interval,
        "depositoryId" : selected_depository,
        "organizationId" : ORGID
    };
    setSelectedTab('sensorprocesses');
    $.ajax({
        url : '/wattdepot/' + ORGID + '/collector-metadata/',
        type : 'PUT',
        contentType : 'application/json',
        data : JSON.stringify(process),
        success : function() {
            location.reload();
        },
    });
};

function putNewInlineMetaProperty() {
    var id = $("input[name='meta_id']").val();
    var metadata = getKnownCPD(id);
    var key = $("input[name='inline_meta_key']").val();
    var value = $("input[name='inline_meta_value']").val();
    var property = new Object();
    var sensor = buildSensor(metadata.sensorId);
    var owner = getKnownUserGroup(ORGID);    

    property.key = key;
    property.value = value;
    metadata.properties.push(property);
    var collector = {
        "id" : id,
        "name" : metadata.name,
        "sensor" : sensor,
        "pollingInterval" : metadata.pollingInterval,
        "depositoryId" : metadata.depositoryId,
        "organizationId" : ORGID,
        "properties" : metadata.properties
        
    };
    setSelectedTab('sensorprocesses');
    $.ajax({
        url : '/wattdepot/' + ORGID + '/collector-metadata/',
        type : 'PUT',
        contentType : 'application/json',
        data : JSON.stringify(collector),
        success : function() {
            location.reload();
        },
    });
    
}

function edit_process_dialog(event, id) {
    setSelectedTab('sensorprocesses');
    var modalElement = $('#addProcessModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });

    var process = getKnownCPD(id);
    $("input[name='meta_id']").val(id);
    $("input[name='sensorprocess_name']").val(process['id']);
    var lid = process.sensorId;
    $('select[name="process_sensor"] option[value="' + lid + '"]').prop(
            "selected", "selected");
    $("input[name='sensorprocess_polling']").val(process['pollingInterval']);
    var mid = process.depositoryId;
    $('select[name="process_depository"] option[value="' + mid + '"]').prop(
            "selected", "selected");
    var properties = process.properties;
    var prop_str = "";
    for (var i = 0; i < properties.length; i++) {
        prop_str +=  properties[i].key + " : " + properties[i].value;
    }
    $('#metadata_properties').text(prop_str);
    modalElement.modal('show');
};

function delete_process_dialog(event, id) {
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
        url : '/wattdepot/' + ORGID + '/collector-metadata/' + id,
        type : 'DELETE',
        contentType : 'application/json',
        success : function() {
            location.reload();
        },
    });
};

