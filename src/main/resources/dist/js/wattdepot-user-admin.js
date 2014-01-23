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

function getKnownUser(id) {
    return USERS[id];
}

function getKnownOrganization(id) {
    return ORGANIZATIONS[id];
}

// ****************** Users **************************
function putNewUser() {
    var id = $("input[name='user_id']").val();
    var first = $("input[name='user_firstname']").val();
    var last = $("input[name='user_lastname']").val();
    var pass = $("input[name='user_password']").val();
    var email = $("input[name='user_email']").val();
    var org = $("select[name='user_organization']").val();
    var usr = {
        "uid" : id,
        "firstName" : first,
        "lastName" : last,
        "email" : email,
        "password" : pass,
        "organizationId" : org,
        "properties" : []
    };
    setSelectedTab('users');
    $.ajax({
        url : '/wattdepot/admin/user/',
        type : 'PUT',
        contentType : 'application/json',
        data : JSON.stringify(usr),
        success : function() {
              location.reload();
        },
    });           
};

function edit_user_dialog(event, id) {
    var modalElement = $('#editUserModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });
    var user = getKnownUser(id);
    $("input[name='edit_user_id']").val(user['id']);
    $("input[name='edit_user_firstname']").val(user['firstName']);
    $("input[name='edit_user_lastname']").val(user['lastName']);
    $("input[name='edit_user_email']").val(user['email']);
    $("input[name='edit_user_password']").val(user['password']);
    $("select[name='edit_user_organization'] option[value=" + user.organizationId + "]").prop("selected", "selected");
    modalElement.modal('show');
};

function updateUser() {
    var id = $("input[name='edit_user_id']").val();
    var first = $("input[name='edit_user_firstname']").val();
    var last = $("input[name='edit_user_lastname']").val();
    var pass = $("input[name='edit_user_password']").val();
    var email = $("input[name='edit_user_email']").val();
    var org = $("select[name='edit_user_organization']").val();
    var usr = {
        "uid" : id,
        "firstName" : first,
        "lastName" : last,
        "email" : email,
        "password" : pass,
        "organizationId" : org,
        "properties" : []
    };
    setSelectedTab('users');
    $.ajax({
        url : '/wattdepot/admin/user/' + id,
        type : 'POST',
        contentType : 'application/json',
        data : JSON.stringify(usr),
        success : function() {
              location.reload();
        },
    });           
};



function delete_user_dialog(event, id) {
    var modalElement = $('#deleteUserModal');

    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });
    modalElement.find('#del_user_id').html(id);
    modalElement.modal('show');
};

function deleteUser() {
    var id = $('#del_user_id').html();
    var user = getKnownUser(id);
    setSelectedTab('users');
    $.ajax({
        url : '/wattdepot/' + user.organizationId + '/user/' + id,
        type : 'DELETE',
        contentType : 'application/json',
        success : function() {
            location.reload();
        },
    });
};

// ****************** Organizations **************************
function putNewOrganization() {
    var id = $("input[name='organization_id']").val();
    var name = $("input[name='organization_name']").val();
    var selected_ids = $("select[name='organization_users']").val() || [];
    var selected_users = new Array();
    for (var i = 0; i < selected_ids.length; i++) {
        selected_users.push(getKnownUser(selected_ids[i]));
    }
    setSelectedTab('orgs');
    var org = {
        "id" : id,
        "name" : name,
        "users" : selected_users
    };
    $.ajax({
        url : '/wattdepot/admin/organization/',
        type : 'PUT',
        contentType : 'application/json',
        data : JSON.stringify(org),
        success : function() {
            location.reload();
        },
        error : function( jqXHR, textStatus, errorThrown) {
            console.log(textStatus + ": " + errorThrown);
            return false;
        }
    });
};

function edit_organization_dialog(event, id) {
    var modalElement = $('#editOrganizationModal');
    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });

    var group = getKnownOrganization(id);
    $("input[name='edit_organization_id']").val(id);
    $("input[name='edit_organization_name']").val(group.name);
    for (var i = 0; i < group.users.length; i++) {
        var uid = group.users[i].id;
        $('select[name="edit_organization_users"] option[value="' + uid + '"]').prop(
                "selected", "selected");
    }
    modalElement.modal('show');
};

function updateOrganization() {
    var id = $("input[name='edit_organization_id']").val();
    var name = $("input[name='edit_organization_name']").val();
    var selected_ids = $("select[name='edit_organization_users']").val() || [];
    var selected_users = new Array();
    for (var i = 0; i < selected_ids.length; i++) {
        selected_users.push(selected_ids[i]);
    }
    setSelectedTab('orgs');
    var org = {
        "id" : id,
        "name" : name,
        "users" : selected_users
    };
    $.ajax({
        url : '/wattdepot/admin/organization/' + id,
        type : 'POST',
        contentType : 'application/json',
        data : JSON.stringify(org),
        success : function() {
            location.reload();
        },
        error : function( jqXHR, textStatus, errorThrown) {
            console.log(textStatus + ": " + errorThrown);
            var modalElement = $('#editOrganizationModal');
            modalElement.modal('toggle');
            return true;
        },
    });
};

function delete_organization_dialog(event, id) {
    var modalElement = $('#deleteOrganizationModal');

    modalElement.modal({
        backdrop : true,
        keyboard : true,
        show : false
    });
    modalElement.find('#del_organization_id').html(id);
    modalElement.modal('show');
};

function deleteOrganization() {
    var id = $('#del_organization_id').html();
    setSelectedTab('orgs');
    $.ajax({
        url : '/wattdepot/admin/organization/' + id,
        type : 'DELETE',
        contentType : 'application/json',
        success : function() {
            location.reload();
        },
    });
};
