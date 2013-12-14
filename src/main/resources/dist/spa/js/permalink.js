/* 
 *     Wattdepot-apps Single Page Application Permalink system
 *
 *     Kendyll Doi, Christopher Foo, Dylan Kobayashi 2012
 *     
 */

var sourceNumber;
var server;
var source = new Array();
var datatype = new Array();
var sdate = new Array();
var edate = new Array();
var stime = new Array();
var etime = new Array();
var timeInterval = new Array();
var active = new Array();
var nowTime = new Array();
var numFormsFinished = 0;

//allows selecting of URL on select
$(document).ready(function(){
    $('#linkSpace').click(function(){
         this.select(); 
    });
});
/* 
 *     Wattdepot-apps getUrlVariable
 *     Gets variables placed in url of window. Based on code provided from
 *     http://css-tricks.com/snippets/javascript/get-url-variables/
 *
 *     @param search id for variable in url.
 */
function getUrlVariable(search) {
    var query = window.location.search.substring(1);
    var variables = query.split("&");
    for (var i = 0; i < variables.length; i++) {
        var items = variables[i].split("=");
        if(items[0] == search){
            //alert(items[1]);
            var temp = items[1].split(";");
            return(temp);
        }
    }
    return(null);
}

/* 
 *     Gets url from the window.
 *
 */
function getUrl(){
   return window.location.protocol + "//" + window.location.hostname + window.location.pathname;
}

/* 
 *     Checks URL for variables used in the permalink system
 *     placing found variables into script variables for recall.
 *
 */
function permalinkCheck(){
    if(window.location.search.length == 0){
        return false
    }
    else{
        server = getUrlVariable('ser');
        sourceNumber = getUrlVariable('no');
        source = getUrlVariable('s');
        datatype = getUrlVariable('dt');
        sdate = getUrlVariable('sd');
        stime = getUrlVariable('st');
        edate = getUrlVariable('ed');
        etime = getUrlVariable('et');
        timeInterval = getUrlVariable('int');
        active = getUrlVariable('act');
        nowTime = getUrlVariable('now');
       // wdClient = org.WattDepot.Client(server);
        // Get all sources
       // getSourcesWithPermalink();
       return true
    }
}

/* 
 *     gathers together the declared values to make a permalink.
 *
 */
function gatherVariables(){
    var temp = '?';
    if($('#serverAddress').val() != null){
        temp += 'ser='+$('#serverAddress').val()+'&' ;
    }
    temp += 'no=' + activeIndex.length;
    var sourceline = 's=';
    var dataline = 'dt=';
    var stline = 'st=';
    var sdline = 'sd=';
    var etline = 'et=';
    var edline = 'ed=';
    var nowline = 'now=';
    var intline = 'int=';
    var actline = 'act=';
    
    
    for (i = 0; i < formIndex; i++){
        if(findActiveIndex(i)+1){
            sourceline += $('#sourceSelect' + i).val() + ';';
            dataline += $('#dataType' + i).val() + ';';
            stline += $('#startTimePicker' + i).val() + ';';
            sdline += document.getElementById('startDatepicker'+i).value + ';';
            etline += $('#endTimePicker' + i).val() + ';';
            edline += document.getElementById('endDatepicker'+i).value + ';';
            if($('#endTimeNow'+i).is(':checked')){
                nowline += 'y;';
            }
            else{
                nowline += 'n;';
            }
            intline += $('#interval' + i).val() + ';';
            if($('#show'+i).is(':checked')){
                actline += 'y;';
            }
            else{
                actline += 'n;';
            }
        }
    }
    return temp + '&' + sourceline + '&' + dataline + '&' + stline + '&' + 
            sdline + '&' + etline + '&' + edline + '&' + nowline + '&' +
            intline + '&' + actline;
}

/* 
 *    Inserts the server address if it was inserted into the URL. from a
 *    permalink.
 *
 *     @param original the default URL for the SPA.
 */
function permalinkServer(original){
    if(server != null){
        return server;
    }
    return original;
}

/* 
 *     Uses permalink values to recreated page based on all provided variables.
 */
function fillPage(){
    numFormsFinished = 0;
    for (i = 0; i < sourceNumber; i++){
        var actTemp = false;
        if (active[i] == 'y'){
            actTemp = true;
        }
        var nowTemp = false;
        if (nowTime[i] == 'y'){
            nowTemp = true;
        }
        if (typeof edate[i] == "undefined"){
            edate[i] = edate[i-1];
        }
        createFilledVisualizerForm(actTemp, source[i], datatype[i], stime[i], sdate[i], etime[i], edate[i], nowTemp, timeInterval[i], function(data, formIndex, dataType){
            putAndSelectDataTypes(data, formIndex, dataType);
            numFormsFinished++;
            if(numFormsFinished == sourceNumber) {
                    visualize();
            }
        });
    }
}