/**

Cookies currently in usage:
shc is used as a prefix. server history cookie.
	shcNum		-	contains the number of server history entries. Necessary for dynamic naming system.
	shcEntry#	-	# will be a number corresponding to the entry.

Other:







*/




/**
 * Get number of entries. Add them to the drop down.
 *
 */
function seedServerHistory(){

	$('#shList').empty();
	var entryAmt = parseInt(getCookie('shcNum'));
	
	//alert('contents of shcEntry1: ' + getCookie('shcEntry1'));
	
	if(entryAmt != null && entryAmt > 0){
		var addrName = '';
		var entryName = '';
	
		//alert('before loop searching entries');
		for(var i = 1; i <= entryAmt; i++){
			entryName = 'shcEntry' + i;
			//alert('in seed loop. entryName:' + entryName + '.');
			addrName = getCookie(entryName);
			//alert('in seed loop. addrName:' + addrName + '.');
			if( addrName != null){
				$('#shList').append('<li><a onclick="addrClick(\'' + addrName + '\')">' + addrName + '</a></li>');
			}
		}//end for
	
	}
}


/**
 * This will add the address input to the server history.
 * update: modified to use cookies to track.
 *
 */
function shAdd(){
	//on add check to see if it doesn't already exist in server.
	var entryAmt = parseInt(getCookie('shcNum'));
	//alert('value of entryAmt:' + entryAmt);
	var location = $('#serverAddress').val();
	if(entryAmt != null && entryAmt > 0){
		var found = false;
		var addrName = '';
		var entryName = '';
		//alert('before loop searching entries');
		for(var i = 1; i <= entryAmt; i++){
			entryName = 'shcEntry' + i;
			//alert('in loop. entryName:' + entryName + '.');
			addrName = getCookie(entryName);
			//alert('in loop. addrName:' + addrName + '.');
			if( addrName == location){
				found = true;
			}
			//alert('after if location == addrname');
		}//end for
		
		//alert('after loop. found value: ' + found);
		if(!found){
			entryAmt += 1;
			makeCookie('shcNum', entryAmt, 31);
			entryName = 'shcEntry' + entryAmt;
			makeCookie(entryName, location, 31);
			$('#shList').append('<li><a onclick="addrClick(\'' + location + '\')">' + location + '</a></li>');
		}
			
	
	}
	else{
		makeCookie('shcNum', 1, 31);
		var entryname = 'shcEntry' + 1;
		makeCookie(entryname, location, 31);
		$('#shList').append('<li><a onclick="addrClick(\'' + location + '\')">' + location + '</a></li>');
	}
	//old code works.
	/**
	alert('need to implement add to cookie, check cookies, add to drop down hist\nSite:' + $('#serverAddress').val() );
	var location = $('#serverAddress').val();
	$('#shList').append('<li><a onclick="addrClick(\'' + location + '\')">' + location + '</a></li>');
	*/
}
/**
 * Remove all addresses from list and clear out the cookie history
 *
 */
function clearAddressHistory(){
	var entryAmt = getCookie('shcNum');
	if(entryAmt != null && entryAmt > 0){
		var entryName = '';
		for(var i = 1; i <= entryAmt; i++){
			entryName = 'shcEntry' + i;
			deleteCookie(entryName);
		}
		
		$('#shList').empty();
		makeCookie('shcNum', 0, 31); // overwrite data to 0 entries last for 1 month.
		alert('entries removed');
	}
	else{
		alert('nothing to remove');
	}
}

/**
*Debug purposes only
*Shows cookie contents.
*
*/
function showServerHistoryCookies(){
	var info = document.cookie;
	alert('History cookies:\n' + info);
}

/**
 * When an address in history is clicked set the serveraddress input area to that value.
 * All addresses are modified to be onclick call this function and pass the site recorded.
 *
 */
function addrClick(site){
	//alert('clicked wattdepot addr');
	if(!site){
		alert('no site given'); //technically this shouldn't ever happen.. this is more a sanity check while i build the functions.
	}
	else{
		$('#serverAddress').val(site);
	}
}


/**
 * Previously used to check work. Discontinued.
 *
 *
$('#serverHist').click(function () {
	
});

*/



/**
 * Creates a cookie with given 'name' Which will contain 'data' and expires an amount of 'time' from now.
 * Time is done in terms of days.
 * Note: this also works for overwriting a cookie with new information.
 *
 */
function makeCookie(name, data, time){
	if(time){
		var date = new Date();
		date.setTime(date.getTime() + (time * 1000 * 60 * 60 * 24)); //sets date to time in days from now.
		var expire = "; expires=" + date.toGMTString();
	}
	else{
		var expire = "";
	}
	
	document.cookie = name + "=" + data + expire + "; path=/";
}

/**
 * This returns the data portion of a cookie given a name.
 * If name doesn't match, returns null.
 *
 */
function getCookie(name){
	var cName = name + "=";
	var info = document.cookie.split(';');
	for( var i = 0; i < info.length; i++){
		var c = info[i];
		while(c.charAt(0) == ' '){
			c = c.substring(1,c.length);
		}
		if(c.indexOf(cName) == 0){
			return c.substring(cName.length, c.length);
		}
		
	}
	
	return null;
}

/**
 * This function deletes a cookie of the given name by setting expiration time to negative(removes on next update)
 * Note: this makes it possible to remove a cookie that doesn't exist prior to function call
 *
 */
function deleteCookie(name){
	makeCookie(name, "", -1);
}

