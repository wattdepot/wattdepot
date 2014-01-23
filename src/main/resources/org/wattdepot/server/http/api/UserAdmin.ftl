<!DOCTYPE html>
<html>
<head>
<title>WattDepot User and Organization Administration</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap-theme.min.css">
<link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
<script src="/webroot/dist/js/less-1.3.0.min.js"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://code.jquery.com/jquery.js"></script>
<script src="/webroot/dist/js/bootstrap.min.js"></script>
<script src="/webroot/dist/js/wattdepot-user-admin.js"></script>
<script> 
</script> 

</head>
<body>
<nav class="navbar navbar-default" role="navigation">
  <!-- Brand and toggle get grouped for better mobile display -->
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="#"><img src="/webroot/dist/wattdepot-logo.png"> WattDepot</a>
  </div>

  <!-- Collect the nav links, forms, and other content for toggling -->
  <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <ul class="nav navbar-nav">
      <li class="active"><a href="#">Definitions</a></li>
      <li><a href="/wattdepot/admin/summary/">Logs/Information/Summary</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
    </ul>
  </div><!-- /.navbar-collapse -->
</nav>

  <div id="modal-dialogs"></div>
 
  <div class="container">
  <div class="panel-group" id="help">
    <div class="panel panel-default">
      <div class="panel-heading">
        <a class="panel-title" data-toggle="collapse" data-parent="#help" href="#collapseHelp">Help <img src="/webroot/dist/icon-help-sm.png"></a>
      </div>
      <div id="collapseHelp" class="panel-collapse collapse">
        <div class="panel-body">
        Use the following steps to create your Organizations and Users.
        <ol>
            <li>Create an Organization without any users in it. This is important since the persistence requires users to be members of an organization and the organization must exist before users are created.</li>
            <li>Create one or more users (one at a time). When creating the user choose the organization they are members of.</li>
        </ol>
        <p>Deleting an organization deletes all the items in the organization. This is a very <em>dangerous</em> opperation.</p>
        </div>
      </div>
    </div>
  </div>
  <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li><a id="organization_tab_link" href="#orgs" data-toggle="tab">Organizations</a></li>
        <li><a id="users_tab_link" href="#users" data-toggle="tab">Users</a></li>
    </ul>
    <!-- Tab panes -->
    <div class="tab-content">
        <div class="tab-pane active" id="orgs">
            <div class="well">
                <table class="table">
                    <thead>
                        <tr>
                            <th colspan="2"><h3>Organizations</h3></th>      
                        </tr>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Members</th>                      
                            <th style="width: 7px;"></th>
                            <th style="width: 7px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list orgs as g>
                        <#if g.id != adminId>
                        <tr><td>${g.id}</td><td>${g.name}</td><td><#list g.users as u>${u} </#list></td>
                            <td>
                                <#if g.id != "admin"><span class="glyphicon glyphicon-pencil" onclick="edit_organization_dialog(event, '${g.id}');"></span></#if>
                            </td>
                            <td>
                                <#if g.id != "admin"><span class="glyphicon glyphicon-remove" onclick="delete_organization_dialog(event, '${g.id}');"></span></#if>
                            </td>
                        </tr>
                        </#if>
                    </#list>
                    </tbody>
                </table>
                <button data-toggle="modal" data-target="#addOrganizationModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add Organization</button>
            </div>        
        </div>
        <div class="tab-pane" id="users">
            <div class="well">
                <table class="table">
                    <thead>
                      <tr><th colspan="5"><h3>Users</h3></th></tr>
                        <tr>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Org</th>
                            <th style="width: 7px;"></th>
                            <th style="width: 7px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list users as u>
                        <#if u.uid != rootUid>
                        <tr><td>${u.firstName!}</td><td>${u.lastName!}</td><td>${u.uid}</td><td>${u.email!}</td><td>${u.organizationId!}</td>
                            <td>
                                <#if u.uid != rootUid><span class="glyphicon glyphicon-pencil" onclick="edit_user_dialog(event, '${u.uid}');"></span></#if>
                            </td>
                            <td>
                                <#if u.uid != rootUid><span class="glyphicon glyphicon-remove" onclick="delete_user_dialog(event, '${u.uid}');"></span></#if>
                            </td>
                        </tr>
                        </#if>
                    </#list>
                    </tbody>
                </table>
                <button data-toggle="modal" data-target="#addUserModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add User</button>
            </div>
        </div>       
    </div>  
    
    
  <!-- Add Organization -->
  <div class="modal fade" id="addOrganizationModal" tabindex="-1" role="dialog" aria-labelledby="addOrganizationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add an Organization</h4>
        </div>
        <div class="modal-body">
            <div class="container">
                <form>
                <div class="form-group">
                        <label class="col-md-3 control-label" for="organization_name">Organization Id</label>
                        <div class="col-md-9">
                            <input type="text" name="organization_id" class="form-control"/>
                            <p class="help-block">Organization id must be unique and be a slug. Slugs consist of lowercase letter, numbers and '-', no other characters are allowed.</p>
                        </div>
                </div>
                <div class="form-group">
                        <label class="col-md-3 control-label" for="organization_name">Group Name</label>
                        <div class="col-md-9">
                            <input type="text" name="organization_name" class="form-control"/>
                            <p class="help-block">User group names should be unique.</p>
                        </div>
                </div>
<!--
                <div class="form-group">
                    <label class="col-md-3 control-label" for="organization_users">Users</label>
                    <div class="col-md-9">
                        <select class="form-control" name="organization_users" multiple="multiple">
                        <#list users as u>
                            <#if u.uid != rootUid>
                            <option value="${u.uid}">${u.uid}</option>
                            </#if>
                        </#list>
                        </select>
                        <p class="help-block">Choose the members of the group. Users can only be in one group.</p>
                    </div>
                </div>
-->
                <div class="clearfix"></div>
                </form>
            </div>                
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewOrganization();">Save changes</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->

  <!-- Edit Organization -->
  <div class="modal fade" id="editOrganizationModal" tabindex="-1" role="dialog" aria-labelledby="editOrganizationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Edit Organization</h4>
        </div>
        <div class="modal-body">
            <div class="container">
                <form>
                <div class="form-group">
                  <label class="col-md-3 control-label" for="edit_organization_name">Id</label>
                  <div class="col-md-9">
                    <input type="text" name="edit_organization_id" class="form-control" disabled/>
                    <p class="help-block">Organization ids cannot be edited once created.</p>
                  </div>
                </div>
                <div class="form-group">
                        <label class="col-md-3 control-label" for="edit_organization_name">Organization Name</label>
                        <div class="col-md-9">
                            <input type="text" name="edit_organization_name" class="form-control"/>
                            <p class="help-block">Organization names should be unique.</p>
                        </div>
                </div>
                <div class="form-group">
                <div class="form-group">
                    <label class="col-md-3 control-label" for="organization_users">Users</label>
                    <div class="col-md-9">
                        <select class="form-control" name="edit_organization_users" multiple="multiple" disabled>
                        <#list users as u>
                            <#if u.uid != rootUid>
                            <option value="${u.uid}">${u.uid}</option>
                            </#if>
                        </#list>
                        </select>
                       <p class="help-block">To edit the users in the Organization, you must create or delete the users since you cannot change their Organization.</p>
                    </div>
                </div>
                </div>
                <div class="clearfix"></div>
                </form>
            </div>                
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="updateOrganization();">Save changes</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->

<!-- Delete Organization -->
<div class="modal fade" id="deleteOrganizationModal" tabindex="-1"
    role="dialog" aria-labelledby="deleteOrganizationModalLabel"
    aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                    aria-hidden="true">&times;</button>
                <h4 class="modal-title">Delete Organization</h4>
            </div>
            <div class="modal-body">
                <p>
                    <b>Delete Organization </b>
                </p>
                <div id="del_organization_id"></div>
                <p></p>
                <p><b>Warning!</b> This operation will delete all the WattDepot Sensors, Depositories, Measurements, and 
                Collector Process Definitions, etc. associated with this Organization.</p> 
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default"
                    data-dismiss="modal">Close</button>
                <button id="delete_button" type="button"
                    class="btn btn-primary" onclick="deleteOrganization();">Delete
                    Organization</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- / .modal -->

  <!-- *********** Users ************ -->
  <!-- Add User -->
  <div class="modal fade" id="addUserModal" tabindex="-1" role="dialog"
      aria-labelledby="addUserModalLabel" aria-hidden="true">
      <div class="modal-dialog">
          <div class="modal-content">
              <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal"
                      aria-hidden="true">&times;</button>
                  <h4 class="modal-title">Add User</h4>
              </div>
              <div class="modal-body">
                  <div class="container">
                      <form>
                          <div class="form-group">
                              <label class="col-md-3 control-label">User Id</label>
                              <div class="col-md-9">
                                  <input type="text" name="user_id"
                                      class="form-control">
                                  <p class="help-block">Unique user
                                      id.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">First
                                  Name</label>
                              <div class="col-md-9">
                                  <input type="text" name="user_firstname"
                                      class="form-control">
                                  <p class="help-block">First name.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">Last
                                  Name</label>
                              <div class="col-md-9">
                                  <input type="text" name="user_lastname"
                                      class="form-control">
                                  <p class="help-block">Last name.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">Email
                                  Address</label>
                              <div class="col-md-9">
                                  <input type="email" name="user_email"
                                      class="form-control">
                                  <p class="help-block">email address.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">Password</label>
                              <div class="col-md-9">
                                  <input type="password"
                                      name="user_password"
                                      class="form-control">
                                  <p class="help-block">Password.</p>
                              </div>
                          </div>
                          <div class="form-group">
                            <label class="col-md-3 control-label" for="organizations">Organization</label>
                            <div class="col-md-9">
                              <select class="form-control" name="user_organization">
                              <#list orgs as o>
                                <#if o.id != orgId>
                                <option value="${o.id}">${o.name}</option>
                                </#if>
                              </#list>
                        </select>
                        <p class="help-block">Choose the organization for this user. Users can only be in one organization.</p>
                    </div>
                </div>
                      </form>
                  </div>
              </div>
              <div class="modal-footer">
                  <button type="button" class="btn btn-default"
                      data-dismiss="modal">Close</button>
                  <button type="button" class="btn btn-primary"
                      onclick="putNewUser();">Save changes</button>
              </div>
          </div><!-- /.modal-content -->
      </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->

  <!-- Edit User -->
  <div class="modal fade" id="editUserModal" tabindex="-1" role="dialog"
      aria-labelledby="addUserModalLabel" aria-hidden="true">
      <div class="modal-dialog">
          <div class="modal-content">
              <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal"
                      aria-hidden="true">&times;</button>
                  <h4 class="modal-title">Edit User</h4>
              </div>
              <div class="modal-body">
                  <div class="container">
                      <form>
                          <div class="form-group">
                              <label class="col-md-3 control-label">User Id</label>
                              <div class="col-md-9">
                                  <input type="text" name="edit_user_id"
                                      class="form-control" disabled>
                                  <p class="help-block">Cannot change the User's id once set.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">First
                                  Name</label>
                              <div class="col-md-9">
                                  <input type="text" name="edit_user_firstname"
                                      class="form-control">
                                  <p class="help-block">First name.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">Last
                                  Name</label>
                              <div class="col-md-9">
                                  <input type="text" name="edit_user_lastname"
                                      class="form-control">
                                  <p class="help-block">Last name.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">Email
                                  Address</label>
                              <div class="col-md-9">
                                  <input type="email" name="edit_user_email"
                                      class="form-control">
                                  <p class="help-block">email address.</p>
                              </div>
                          </div>
                          <div class="form-group">
                              <label class="col-md-3 control-label">Password</label>
                              <div class="col-md-9">
                                  <input type="password"
                                      name="edit_user_password"
                                      class="form-control">
                                  <p class="help-block">Password.</p>
                              </div>
                          </div>
                          <div class="form-group">
                            <label class="col-md-3 control-label" for="organizations">Organization</label>
                            <div class="col-md-9">
                              <select class="form-control" name="edit_user_organization" disabled>
                              <#list orgs as o>
                                <#if o.id != orgId>
                                <option value="${o.id}">${o.name}</option>
                                </#if>
                              </#list>
                        </select>
                        <p class="help-block">You cannot change a User's organization once set.</p>
                    </div>
                </div>
                      </form>
                  </div>
              </div>
              <div class="modal-footer">
                  <button type="button" class="btn btn-default"
                      data-dismiss="modal">Close</button>
                  <button type="button" class="btn btn-primary"
                      onclick="updateUser();">Save changes</button>
              </div>
          </div><!-- /.modal-content -->
      </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->

<!-- Delete User -->
<div class="modal fade" id="deleteUserModal" tabindex="-1" role="dialog"
    aria-labelledby="deleteUserModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                    aria-hidden="true">&times;</button>
                <h4 class="modal-title">Delete User</h4>
            </div>
            <div class="modal-body">
                <p>
                    <b>Delete User </b>
                </p>
                <div id="del_user_id"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default"
                    data-dismiss="modal">Close</button>
                <button id="delete_button" type="button"
                    class="btn btn-primary" onclick="deleteUser();">Delete
                    User</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- / .modal -->
      
</div>
<script>
$(document).ready(function () {
    var selected_tab = getCookie("selected-tab");
    if (selected_tab != null) {
        $('#' + selected_tab + '_tab_link').tab('show');
    }
    
});

var ORGID = "${orgId}";
var USERS = {};
<#list users as u>
USERS["${u.uid}"] = {"id": "${u.uid}", "firstName" : "${u.firstName!"none"}", "lastName" : "${u.lastName!"none"}", "email" : "${u.email!"none"}", "organizationId" : "${u.organizationId!"none"}", "properties" : [<#assign k = u.properties?size><#list u.properties as p>{"key":"${p.key}", "value":"${p.value}"}<#if k != 1>,</#if><#assign k = k -1></#list>]};
</#list>
var ORGANIZATIONS = {};
<#list orgs as o>
ORGANIZATIONS["${o.id}"] = {"id": "${o.id}", "name": "${o.name}", "users": [
<#assign j = o.users?size>
<#list o.users as u>
{"id": "${u}"}<#if j != 1>,</#if><#assign j = j - 1>
</#list>
]};
</#list>
</script>
</body>
</html>