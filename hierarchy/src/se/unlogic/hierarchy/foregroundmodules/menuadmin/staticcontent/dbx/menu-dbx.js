// DBX2.05 :: Docking Boxes (dbx)
// *****************************************************
// DOM scripting by brothercake -- http://www.brothercake.com/
// GNU Lesser General Public License -- http://www.gnu.org/licenses/lgpl.html
//******************************************************


//global dbx manager reference
var dbx;

//docking boxes manager
function dbxManager(sid)
{
	//global reference to this
	dbx = this;

	//alert and don't continue if the session ID isn't valid
	if(!/^[-_a-z0-9]+$/i.test(sid)) { alert('Error from dbxManager:\n"' + sid + '" is an invalid session ID'); return; }

	//whether a browser is supported
	//we'll do object tests here to set the flag and then re-use it in the dbxGroup constructor
	//so this browser isn't supported if ...
	//the '*' collection isn't supported, or if this is an older konqueror build (< 3.2),
	//we need the '*' collection so the dbx-box element can be anything
	//this affects win/ie5, and older safari builds
	//we need to cut out older safari builds anyway, because
	//they don't support the absolute-in-relative contextual positioning
	//that we need to make the clone positioning work
	//we need to cut out older konqueror builds
	//because the dragging and animation is unstable in these older builds
	this.supported = !(document.getElementsByTagName('*').length == 0 || (navigator.vendor == 'KDE' && typeof window.sidebar == 'undefined'));

	//don't continue if unsupported
	if(!this.supported) { return; }


	//identify supported method of adding encapsulated event listeners
	this.etype = typeof document.addEventListener != 'undefined' ? 'addEventListener' : typeof document.attachEvent != 'undefined' ? 'attachEvent' : 'none';

	//set event name prefix
	this.eprefix = (this.etype == 'attachEvent' ? 'on' : '');

	//if this is opera, and a build earlier than 7.5
	if(typeof window.opera != 'undefined' && parseFloat(navigator.userAgent.toLowerCase().split(/opera[\/ ]/)[1].split(' ')[0], 10) < 7.5)
	{
		//set the event listening value to 'none'
		//so that these builds don't continue
		//because the drag scripting is unstable
		this.etype = 'none';
	}

	//if encapsulated event listening is not supported, set flag to unsupported and don't continue
	//this will filter-out mac/ie5, plus the old opera builds we just injected into this group
	if(this.etype == 'none') { this.supported = false; return; }


	//count the number of running timers
	//so that we can limit it
	//the limit will be set the same as the number of boxes in the group
	//so that it's possible for all of them to be animated simultaneously
	//but not for multiple sets to be going at the same time
	this.running = 0;



	//the session id is used as part of the cookie name
	//so that one cookie can store multiple groups
	//but single or multiple groups on different pages
	//are discreet, so don't wipe out each other's cookies
	this.sid = sid;


	//create an object for storing save data
	//(order and open-state of boxes, to save to cookie)
	this.savedata = {};

	//look for existing cookie state data
	this.cookiestate = this.getCookieState();
};


//set state to cookie and output to receiver method
dbxManager.prototype.setCookieState = function()
{
	//format expiry date
	var now = new Date();
	now.setTime(now.getTime() + (365*24*60*60*1000));

	//compile the save object data into a string
	var str = '';
	for(j in this.savedata)
	{
		//if this member is not a function
		//(an external prototype we can't control)
		if(typeof this.savedata[j]!='function')
		{
			//add to string
			str += j + '=' + this.savedata[j] + '&'
		}
	}

	//trim off the last stray ampersand
	//and save the string to this.state
	//so that the data is available from onstatechange
	this.state = str.replace(/^(.+)&$/, '$1');

	//convert the format to netscape compatible cookie string,
	//(http://wp.netscape.com/newsref/std/cookie_spec.html
	// which says that the data can't contain commas, even though the RFC allows it,
	// I'm making it fully compatible to address issues like this:
	// http://trac.wordpress.org/ticket/2660)
	//at the same time I've changed = for : to avoid ambiguity there as well
	//but we're only doing this at the last minute, and not overwriting this.state
	//so that the internal format isn't affected
	//and so that it remains backwardly compatible with earlier versions
	this.cookiestring = this.state.replace(/,/g, '|');
	this.cookiestring = this.cookiestring.replace(/=/g, ':');

	//call the onstatechange method
	//continue to save the cookie, if
	//	the method doesn't exist; or
	//	the method exists and returns true
	if(typeof this.onstatechange == 'undefined' || this.onstatechange())
	{
		//create the cookie
		document.cookie = 'dbx-' + this.sid + '='
			+ this.cookiestring
			+ '; expires=' + now.toGMTString()
			+ '; path=/';

		//*** dev output
		//this.output.value = str;
	}

};


//get state from cookie
dbxManager.prototype.getCookieState = function()
{
	//set null reference so we always have something to return
	this.cookiestate = null;

	//if a cookie exists
	if(document.cookie)
	{
		//if it's our cookie
		if(document.cookie.indexOf('aaaadbx-' + this.sid)!=-1)
		{
			//extract order data
			this.cookie = document.cookie.split('dbx-' + this.sid + '=')[1].split(';')[0].split('&');

			//interate through resulting data
			for(var i in this.cookie)
			{
				//if this member is not a function
				//(an external prototype we can't control)
				if(typeof this.cookie[i]!='function')
				{
					//convert the format back to internal format
					this.cookie[i] = this.cookie[i].replace(/\|/g, ',');
					this.cookie[i]= this.cookie[i].replace(/:/g, '=');

					//split into key and value
					this.cookie[i] = this.cookie[i].split('=');

					//split value (which is comma-delimited) into an array
					this.cookie[i][1] = this.cookie[i][1].split(',');
				}
			}

			//copy the cookie data into a state object
			//so that when a dbx group is instantiated
			//we can test whether there's existing cookie data for it
			//using typeof this.cookiestate['container-id']
			this.cookiestate = {};
			for(i in this.cookie)
			{
				//if this member is not a function
				//(an external prototype we can't control)
				if(typeof this.cookie[i]!='function')
				{
					//create member
					this.cookiestate[this.cookie[i][0]] = this.cookie[i][1];
				}
			}
		}
	}

	return this.cookiestate;
};


//add a member to the manager's save data object
dbxManager.prototype.addDataMember = function(gid, order)
{
	this.savedata[gid] = order;
};


//create an HTML element
dbxManager.prototype.createElement = function(tag)
{
	//create element using supported method and return it
	return typeof document.createElementNS != 'undefined' ? document.createElementNS('http://www.w3.org/1999/xhtml', tag) : document.createElement(tag);
};


//get an element identified by classname
//by upwards iteration from event target
dbxManager.prototype.getTarget = function(e, pattern, node)
{
	//if we have an explicit node reference, use that
	if(typeof node != 'undefined')
	{
		var target = node;
	}
	//otherwise store a reference to the target node
	else
	{
		target = typeof e.target != 'undefined' ? e.target : e.srcElement;
	}

	//iterate upwards from the target
	//until we find a reference to the specified element
	var regex = new RegExp(pattern, '');
	while(!regex.test(target.className))
	{
		target = target.parentNode;
	}

	//return the element
	return target;
};














//create new docking boxes group
function dbxGroup(gid, dir, thresh, fix, ani, togs, def, open, close, move, toggle, kmove, ktoggle, syntax)
{
	//alert and don't continue if the container ID isn't valid
	if(!/^[-_a-z0-9]+$/i.test(gid)) { alert('Error from dbxGroup:\n"' + gid + '" is an invalid container ID'); return; }

	//group container
	this.container = document.getElementById(gid);

	//don't continue if the container doesn't exist,
	//or the script is unsupported
	if(this.container == null || !dbx.supported) { return; }

	//reference to this
	var self = this;

	//group id
	this.gid = gid;

	//drag ok flag
	this.dragok = false;

	//initially null reference to target box
	this.box = null;

	//container orientation - expressed as a boolen for whether it's vertical
	this.vertical = dir == 'vertical';

	//drag threshold - how far the cursor
	//must move before the drag registers
	this.threshold = parseInt(thresh, 10);

	//restrict drag movement to container axis
	this.restrict = fix == 'yes';

	//animation resolution - 0 means no effect
	this.resolution = parseInt(ani, 10);



	//include open/close toggles
	this.toggles = togs == 'yes';

	//is the box open by default
	this.defopen = def != 'closed';


	//vocabulary
	this.vocab = {
		'open' : open,
		'close' : close,
		'move' : move,
		'toggle' : toggle,
		'kmove' : kmove,
		'ktoggle' : ktoggle,
		'syntax' : syntax
		};



	//re-inforce relative positioning and block display on group container
	//the container must have positioning,
	//because many other calculations depend on it
	//I originally supported static positioning as well
	//but this solves some browser clone positioning quirks
	//which I otherwise couldn't fix cleanly
	this.container.style.position = 'relative';
	this.container.style.display = 'block';

	//if this is opera 7 or 8, add display:run-in, which (for some reason...)
	//fixes a clone positioning discrepancy equal to the container's offset
	//with respect to its ancestor positioned element
	if(typeof window.opera != 'undefined')
	{
		this.container.style.display = 'run-in';
	}


	//array of box objects
	this.boxes = [];

	//array of buttons, so we can save them on creation
	//and then have a reference to initialise them from cookie data
	this.buttons = [];


	//box order array
	this.order = [];


	//get all elements within this container
	this.eles = this.container.getElementsByTagName('*');

	//for each element - iterating by length because it will be changing
	for(var i=0; i<this.eles.length; i++)
	{
		//if it's a docking box and not a dummy
		if(/dbx\-box/i.test(this.eles[i].className) && !/dbx\-dummy/i.test(this.eles[i].className))
		{
			//re-inforce relative positioning and block display
			this.eles[i].style.position = 'relative';
			this.eles[i].style.display = 'block';

			//add to array of docking boxes
			this.boxes.push(this.eles[i]);

			//add its default open state as an additional classname
			this.eles[i].className += ' dbx-box-open';

			//add an "id" to its classname, so we can identify it whatever its position
			//using the current length of the order array as counter
			this.eles[i].className += ' dbxid' + this.order.length;

			//add its order in the array to the order array
			//plus its open state ("+" or "-" for open or close), which is open by default
			this.order.push(this.order.length.toString() + '+');

			//bind mousedown handler
			this.eles[i][dbx.etype](dbx.eprefix + 'mousedown', function(e)
			{
				//convert event argument
				if(!e) { e = window.event; }

				//get box element from target
				//then pass event reference and box to mousedown handler
				self.mousedown(e, dbx.getTarget(e, 'dbx\-box'));

			}, false);

		}

		//if it's a handle
		if(/dbx\-handle/i.test(this.eles[i].className))
		{
			//re-inforce relative positioning and block display
			this.eles[i].style.position = 'relative';
			this.eles[i].style.display = 'block';

			//add cursor classname
			this.eles[i].className += ' dbx-handle-cursor';

			//if the handle doesn't already have a title, create one
			//if it does, append the title using pattern match syntax
			this.eles[i].setAttribute('title', this.eles[i].getAttribute('title') == null || this.eles[i].title == '' ? this.vocab.move : this.vocab.syntax.replace('%mytitle%', this.eles[i].title).replace('%dbxtitle%', this.vocab.move));

			//if we're adding toggle buttons
			if(this.toggles)
			{
				//add toggle behavior, returning the button object for the cookie function to use
				this.buttons.push(this.addToggleBehavior(this.eles[i]));
			}

			//otherwise attempt to bind a keyboard handlers to the handle itself
			//which will work if the handle is a focussable element
			else
			{
				//we need a key handler for the button
				//we also want to be able to suppress page scrolling when appropriate
				//but browsers have different ideas about which event that comes from, and what will work
				//in ie and safari we need onkeydown, in moz we need onkeypress,
				//in opera default action suppression doesn't occur either way
				this.eles[i][dbx.etype](dbx.eprefix + 'key' + (typeof document.uniqueID != 'undefined' || navigator.vendor == 'Apple Computer, Inc.' ? 'down' : 'press'), function(e)
				{
					//convert event argument
					if(!e) { e = window.event; }

					//get handle element from target
					//then pass event and handle to keypress handler
					//return value determines whether native action happens
					return self.keypress(e, dbx.getTarget(e, 'dbx\-handle'));

				}, false);

				//bind focus handler
				this.eles[i][dbx.etype](dbx.eprefix + 'focus', function(e)
				{
					//convert event argument
					if(!e) { e = window.event; }

					//get handle element from target
					//the padding null open state and handle
					//to create keyboard navigation tooltip
					self.createTooltip(null, dbx.getTarget(e, 'dbx\-handle'));

				}, false);

				//bind blur handler
				this.eles[i][dbx.etype](dbx.eprefix + 'blur', function()
				{
					//remove any tooltip that's there
					self.removeTooltip();

				}, false);

			}
		}
	}



	//add this group to the dbx manager save data object
	//along with a string representation of its order
	dbx.addDataMember(this.gid, this.order.join(','));




	//add a dummy docking box to the end of the container
	//which we'll need as an insertBefore reference for boxes moved to the end
	var dummy = this.container.appendChild(dbx.createElement('span'));
	dummy.className = 'dbx-box dbx-dummy';

	//re-inforce important styles
	dummy.style.display = 'block';
	dummy.style.width = '0';
	dummy.style.height = '0';
	dummy.style.overflow = 'hidden';

	//apply offleft positioning rule for a column only
	//opera has rendering problem without it
	//but if it's applied to a row then you can't move an object to the end
	if(this.vertical) { dummy.className += ' dbx-offdummy'; }

	//add it to boxes array
	this.boxes.push(dummy);




	//if there's cookie state data that relates to this group
	if(dbx.cookiestate != null && typeof dbx.cookiestate[this.gid] != 'undefined')
	{
		//number of values
		var num = dbx.cookiestate[this.gid].length;

		//if the number of values matches the number of boxes
		//(minus one, because the dummy state is not stored)
		if(num == this.boxes.length - 1)
		{
			//dbx.output.value += this.gid + '=' + dbx.cookiestate[this.gid] + '&';

			//iterate through values
			for(i=0; i<num; i++)
			{
				//the index of this box
				var index = parseInt(dbx.cookiestate[this.gid][i], 10);

				//move this box before the last one
				this.container.insertBefore(this.boxes[index], dummy);

				//if we're using toggle buttons, and
				//if the box (in its corresponding place in boxes array) should be closed
				//### Thanks to Ward Vandewege for the patch that allows for more than 10 boxes ###
				if(this.toggles && /\-$/.test(dbx.cookiestate[this.gid][i]))
				{
					//send this box button to toggle box state method, but don't save
					this.toggleBoxState(this.buttons[index], false);
				}
			}

			//regenerate the box order array (from which, save back to cookie)
			this.getBoxOrder();
		}
	}

	//else if there is no cookie,
	//and the box is set to be not-open by default
	//and we're using toggle buttons
	else if(!this.defopen && this.toggles)
	{
		//iterate through box buttons
		var len = this.buttons.length;
		for(i=0; i<len; i++)
		{
			//send this box button to toggle box state method and save
			this.toggleBoxState(this.buttons[i], true);
		}
	}





	//add a document mouseout handler
	document[dbx.etype](dbx.eprefix + 'mouseout', function(e)
	{
		//convert event argument and identify related target
		//we have to test it this way, because
		//"e" is defined here, but it's not the event object
		if(typeof e.target == 'undefined') { e = window.event; e.relatedTarget = e.toElement; }

		//if the related target is null, fire the mouseup handler
		//this catches mouse movement outside the window while holding a clone
		//which could cause "sticky mouse", as onmouseup doesn't fire outside the window
		if(e.relatedTarget == null)
		{
			//pass event to mouseup handler
			self.mouseup(e);
		}

	}, false);




	//add document mousemove handler
	//for when we're moving the clone
	document[dbx.etype](dbx.eprefix + 'mousemove', function(e)
	{
		//pass event to mousemove handler
		self.mousemove(e);

		//IE needs this so that the drag action works properly for links
		//otherwise it "mis-fires", as it were, and generates that black "no action" symbol
		//leading to a series of bugs that results in the clone being sticky to the mouse
		//and resetting at whatever arbitrary position you let go of it
		//drag 'n' drop scripting always requires this return false on the mousemove
		//so I feel pretty silly for not having noticed it before, even though
		//I've never really understood why it's necessary in the first place
		//### Thanks to Thomas Karl for reporting this bug ###
		//however if it returns false all the time then
		//text-range selection is broken completely in IE
		//so we have to return by the inverse of the dragok flag, for whether
		//a drag action is occuring (so return false), or this is unrelated mousemovement (so return true)
		return !self.dragok;

	}, false);


	//add document mouseup handler
	//for when we let go of the clone
	document[dbx.etype](dbx.eprefix + 'mouseup', function(e)
	{
		//pass event to mouseup handler
		self.mouseup(e);

	}, false);



	//add document key handlers to toggle a keydown flag
	//which we can use to tell key-initiated focus events from mouse-initiated focus events
	this.keydown = false;
	document[dbx.etype](dbx.eprefix + 'keydown', function()
	{
		self.keydown = true;

	}, false);
	document[dbx.etype](dbx.eprefix + 'keyup', function()
	{
		self.keydown = false;

	}, false);


};




//add docking box toggle (open and close) behavior
dbxGroup.prototype.addToggleBehavior = function()
{
	//copy a reference to this
	var self = this;

	//insert toggle button
	var button = arguments[0].appendChild(dbx.createElement('a'));

	//we need to add something inside the link, or it may not be key accessible to all
	//so I'm going to write in a non-breaking space
	//using a unicode reference, because we can't use an entity within createTextNode
	//(there is a createEntityReference method, but afaik no browser implements it)
	button.appendChild(document.createTextNode('\u00a0'));

	//set pointer cursor, else it will inherit move cursor from parent
	button.style.cursor = 'pointer';

	//give it an href so it can accept the focus
	button.href = 'javascript:void(null)';

	//set classname and title to be initially open
	button.className = 'dbx-toggle dbx-toggle-open';
	button.setAttribute('title', this.vocab.toggle.replace('%toggle%', this.vocab.close));


	//create a hasfocus flag to determine if the button is focussed
	//which we'll use to differentiate click events on the button
	//and prevent them from working if the button isn't focussed
	//this will prevent browser-based screenreaders from being able to undisplay the contents
	//but that will fail in opera and safari, so we need to exclude them specifically
	//fortunately there aren't any readers based on opera or safari
	//(opera 8 has voice, but that's something else)
	//we're using a tri-state flag here, so avoid conflict with
	//browsers doing automatic type conversion
	//tests against this value are going to use strict [in]equality
	button.hasfocus = typeof window.opera != 'undefined' || navigator.vendor == 'Apple Computer, Inc.' ? null : false;


	//keyboard navigation tooltip object
	this.tooltip = null;


	//bind click handler to button
	button.onclick = function()
	{
		//if the has focus flag is strictly true or null
		if(this.hasfocus === true || this.hasfocus === null)
		{
			//remove any tooltip that's there
			self.removeTooltip();

			//toggle box state and save
			self.toggleBoxState(this, true);
		}
	};

	//we need a key handler for the button
	//but we also want to be able to suppress page scrolling when appropriate
	//but browsers have different ideas about which event that comes from, and what will work
	//in ie and safari we need onkeydown, in moz we need onkeypress,
	//in opera default action suppression doesn't occur either way
	button['onkey' + (typeof document.uniqueID != 'undefined' || navigator.vendor == 'Apple Computer, Inc.' ? 'down' : 'press')] = function(e)
	{
		//dbx.output.value = '\n- keyup\n' + dbx.output.value;

		//convert event argument
		if(!e) { e = window.event; }

		//pass event and button object to keypress handler
		//return value determines whether native action happens
		return self.keypress(e, this);
	};

	//bind focus handler to button
	button.onfocus = function()
	{
		//iterate through all buttons
		var len = self.buttons.length;
		for(var i=0; i<len; i++)
		{
			//remove hilite classname
			self.buttons[i].className = self.buttons[i].className.replace(/[ ](dbx\-toggle\-hilite\-)(open|closed)/, '');
		}

		//get open state from button classname
		var isopen = (/dbx\-toggle\-open/.test(this.className));

		//add the hilite classname
		this.className += ' dbx-toggle-hilite-' + (isopen ? 'open' : 'closed');


		//create keyboard navigation tooltip
		//passing the open state, and the button itself
		self.createTooltip(isopen, this);



		//set the isactive flag for focus setter in animation
		//we need the flag to prevent setting focus on
		//the animated elements that we only cloned and didn't move
		//and to prevent setting highlight on pressed buttons from cookie initialisation
		this.isactive = true;

		//set the has focus flag if it's not strictly null
		if(this.hasfocus !== null) { this.hasfocus = true; }
	};

	//bind blur handler to button
	button.onblur = function()
	{
		//remove the hilite classname
		this.className = this.className.replace(/[ ](dbx\-toggle\-hilite\-)(open|closed)/, '');

		//remove any tooltip that's there
		self.removeTooltip();

		//clear the has focus flag if it's not strictly null
		if(this.hasfocus !== null) { this.hasfocus = false; }
	};

	//return the button object
	return button;
};


//toggle the state of box
dbxGroup.prototype.toggleBoxState = function(button, regen)
{
	//get open state from button classname
	var isopen = (/dbx\-toggle\-open/.test(button.className));

	//iteratively find a reference to the button's parent box
	var parent = dbx.getTarget(null, 'dbx\-box', button);

	//store values to dbx properties for external methods
	dbx.box = parent;
	dbx.toggle = button;
	//but the container might be undefined
	//if this is called from the cookie function
	if(typeof dbx.container == 'undefined')
	{
		//so in that case, retrieve it iteratively
		dbx.group = dbx.getTarget(null, 'dbx\-group', parent);
	}
	//otherwise just copy it from container
	else { dbx.group = dbx.container; }

	//if the box is currently closed, and onopen doesn't exist or returns true; or
	//if the box is currently open, and onclose doesn't exist or returns true
	if
	(
		(!isopen && (typeof dbx.onboxopen == 'undefined' || dbx.onboxopen()))
		||
		(isopen && (typeof dbx.onboxclose == 'undefined' || dbx.onboxclose()))
	)
	{
		//change the classname and title
		button.className = 'dbx-toggle dbx-toggle-' + (isopen ? 'closed' : 'open');
		button.title = this.vocab.toggle.replace('%toggle%', isopen ? this.vocab.open : this.vocab.close);

		//add hilite classname if necessary
		if(typeof button.isactive != 'undefined')
		{
			button.className += ' dbx-toggle-hilite-' + (isopen ? 'closed' : 'open')
		}

		//change the parent box open state classname
		//which is both a stored value for us to read its state
		//and used in a descendent selector to hide the inner content
		parent.className = parent.className.replace(/[ ](dbx-box-)(open|closed)/, ' $1' + (isopen ? 'closed' : 'open'));

		//if the regenerate flag is set,
		//regenerate the box order array and save to cookie
		if(regen) { this.getBoxOrder(); }
	}
};


//shift the position of box
dbxGroup.prototype.shiftBoxPosition = function(e, anchor, positive)
{
	//iteratively find a reference to the anchor's parent box
	var parent = dbx.getTarget(null, 'dbx\-box', anchor);

	//store values to dbx properties for external methods
	dbx.group = this.container;
	dbx.box = parent;
	dbx.event = e;

	//if onboxdrag doesn't exist, or returns true
	if(typeof dbx.onboxdrag == 'undefined' || dbx.onboxdrag())
	{

		//create an array for storing the offset position and id of each box
		//which we can then sort numerically, to find the visual order
		var positions = [];

		//for each box in the array
		var len = this.boxes.length;
		for(var i=0; i<len; i++)
		{
			//store i and the offset position of box i
			positions[i] = [i, this.boxes[i][this.vertical ? 'offsetTop' : 'offsetLeft']];

			//if it's this one, store the number
			if(parent == this.boxes[i]) { this.idref = i; }
		}

		//sort the positions array by second member (offset position)
		positions.sort(this.compare);

		//iterate through the (now numerically sorted) positions array
		for(i=0; i<len; i++)
		{
			//if the index of this box is the index of our anchor parent box
			if(positions[i][0] == this.idref)
			{
				//if movement is positive and this is not the penultimate box
				//(checking penultimate instead of last, because the last one is a dummy)
				//or movement is negative and this is not the first box
				if((positive && i < len - 2) || (!positive && i > 0))
				{
					//get a reference to the sibling box
					//next sibling for positive or previous sibling for negative
					var sibling = this.boxes[positions[i + (positive ? 1 : -1)][0]];

					//if we're using the box animation effect (if the resolution is > 0)
					if(this.resolution > 0)
					{
						//get both before positions of the parent and sibling boxes
						var visipos = { 'x' : parent.offsetLeft, 'y' : parent.offsetTop };
						var siblingpos = { 'x' : sibling.offsetLeft, 'y' : sibling.offsetTop };
					}

					//move the boxes as appropriate
					//which is sibling before parent for positive
					//or parent before sibling for negative
					var obj = { 'insert' : (positive ? sibling : parent), 'before' : (positive ? parent : sibling) };
					this.container.insertBefore(obj.insert, obj.before);

					//if we're using the box animation effect
					if(this.resolution > 0)
					{
						//create new box animators for the sibling box then parent box
						//do the sibling first, because the parent is the one we're actually moving
						//so we want that to be on top
						var animators =
						{
							'sibling' : new dbxAnimator(this, sibling, siblingpos, this.resolution, true, anchor),
							'parent' : new dbxAnimator(this, parent, visipos, this.resolution, true, anchor)
						};
					}

					//or we're not using the animation
					//we have to check this, because we're setting focus on the anchor
					//but if the animation is in use, the anchor will be hidden
					//and you can't [necessarily] set focus on a non-visible element
					//if we are using the animation, this code is repeated at the necessary point there
					else
					{
						//send focus back to the anchor
						//this is necessary for opera 8 and internet explorer,
						//otherwise it loses the focus when moving in a negative direction
						//** possibly because it gets transferred to the clone ... don't really know
						anchor.focus();
					}

					//stop now - we're done
					break;
				}
			}
		}

		//regenerate the box order array and save to cookie
		this.getBoxOrder();
	}
};


//sort matrix numerically by second member
dbxGroup.prototype.compare = function(a, b)
{
	return a[1] - b[1];
};



//create a tooltip for keyboard navigation instructions
dbxGroup.prototype.createTooltip = function(isopen, anchor)
{
	//if the keydown flag is set
	if(this.keydown)
	{
		//create the tooltip inside the group container
		//it's here so that it comes out above all the boxes
		this.tooltip = this.container.appendChild(dbx.createElement('span'));
		this.tooltip.style.visibility = 'hidden';
		this.tooltip.className = 'dbx-tooltip';

		//if the open state is not null we have a box with toggles
		if(isopen != null)
		{
			//so create the full tooltip
			this.tooltip.appendChild(document.createTextNode(this.vocab.kmove + this.vocab.ktoggle.replace('%toggle%', isopen ? this.vocab.close : this.vocab.open)));
		}

		//if it is null we have a anchor-handle
		else
		{
			//so create the tooltip with only move instructions
			this.tooltip.appendChild(document.createTextNode(this.vocab.kmove));
		}

		//iteratively find a reference to the anchor's parent box
		var parent = dbx.getTarget(null, 'dbx\-box', anchor);

		//set tooltip position to box origin
		//so developers can move it from there, eg, with margin
		this.tooltip.style.left = parent.offsetLeft + 'px';
		this.tooltip.style.top = parent.offsetTop + 'px';

		//show the tooltip on a timer so it's not in your face
		//we could do this by conditionalising the whole process
		//and only creating tooltips after event-discriminated timeouts
		//but this is a great deal simpler, and nobody will notice the difference :)
		var tooltip = this.tooltip;
		window.setTimeout(function()
		{
			if(tooltip != null) { tooltip.style.visibility = 'visible'; }
		}, 500);
	}
};

//remove such a tooltip, if it's there
dbxGroup.prototype.removeTooltip = function()
{
	//if there's a tooltip
	if(this.tooltip != null)
	{
		//remove it and nullify the reference
		this.tooltip.parentNode.removeChild(this.tooltip);
		this.tooltip = null;
	}
};




//docking box mousedown handler
dbxGroup.prototype.mousedown = function(e, box)
{
	//store the target node, converting event argument as we go
	var node = typeof e.target != 'undefined' ? e.target : e.srcElement;

	//if it's a text node, convert refence to its parent
	//this is for safari, in which events can come from text nodes
	if(node.nodeName == '#text') { node = node.parentNode; }

	//if the target is not a toggle, box or group
	if(!/dbx\-(toggle|box|group)/i.test(node.className))
	{
		//while target doesn't contain docking box handle classname
		//set reference upwards until we find it
		//this is so that the handle can contain inner elements
		//but stop if we get to a box or group
		//to filter out any remaining events that started from higher than the handle
		while(!/dbx\-(handle|box|group)/i.test(node.className))
		{
			node = node.parentNode;
		}
	}

	//if target is a handle
	if(/dbx\-handle/i.test(node.className))
	{
		//remove any tooltip that's there
		this.removeTooltip();

		//set the "released" flag, initially to false
		//which is used to detect whether a box has already moved once
		//or this is the first time it's been released
		//we'll need this as part of sticky box / drag threshold evaluations
		this.released = false;


		//store initial mouse coords
		this.initial = { 'x' : e.clientX, 'y' : e.clientY };

		//reset the current mouse coords object
		this.current = { 'x' : 0, 'y' : 0 };

		//create a moveable shadow of this physical box
		this.createCloneBox(box);

		//prevent default action to try to stop text range selection while dragging
		if(typeof e.preventDefault != 'undefined' ) { e.preventDefault(); }

		//prevent textrange selection in IE
		//by temporarily suppressing it on the whole document
		if(typeof document.onselectstart != 'undefined')
		{
			document.onselectstart = function() { return false; }
		}
	}
};


//document mousemove handler
dbxGroup.prototype.mousemove = function(e)
{
	//if dragging is okay and we have a box reference
	if(this.dragok && this.box != null)
	{
		//whether the current direction of mouse movement is positive
		//("positive" for down or right or "negative" for up or left)
		//using the mouse coords stored from last time
		this.positive = this.vertical ? (e.clientY > this.current.y ? true : false) : (e.clientX > this.current.x ? true : false);

		//store the current mouse coords
		this.current = { 'x' : e.clientX, 'y' : e.clientY };


		//store the total difference from the initial coordinates
		var overall = { 'x' : this.current.x - this.initial.x, 'y' : this.current.y - this.initial.y };



		//if the differences are both less than or equal to the drag threshold
		//even out to zero, which creates a "stickiness" around the origin
		if
		(
			((overall.x >= 0 && overall.x <= this.threshold) || (overall.x <= 0 && overall.x >= 0 - this.threshold))
			&&
			((overall.y >= 0 && overall.y <= this.threshold) || (overall.y <= 0 && overall.y >= 0 - this.threshold))
		)
		{
			this.current.x -= overall.x;
			this.current.y -= overall.y;
		}


		//if this box has already been released, or one of the differences has changed past the drag threshold
		//(having a drag threshold is so that handles can also be links or other actuators without conflict
		// because no-one holds the mouse perfectly still when they click a link)
		if(this.released || overall.x > this.threshold || overall.x < (0 - this.threshold) || overall.y > this.threshold || overall.y < (0 - this.threshold))
		{
			//store values to dbx properties for external methods
			dbx.group = this.container;
			dbx.box = this.box;
			dbx.event = e;

			//if onboxdrag doesn't exist or returns true
			if(typeof dbx.onboxdrag == 'undefined' || dbx.onboxdrag())
			{
				//set the released flag, to say this can always happen from now on
				//otherwise, after moving the box away once,
				//the subsequent tests would make it sticky
				//to the threshold points instead of the origin
				this.released = true;

				//move the clone to mouse coords minus mouse/position difference
				//if we're restricting the axis of movement, only change the applicable position value
				if(!this.restrict || !this.vertical) { this.boxclone.style.left = (this.current.x - this.difference.x) + 'px'; }
				if(!this.restrict || this.vertical) { this.boxclone.style.top = (this.current.y - this.difference.y) + 'px'; }

				//move the original box to new position
				this.moveOriginalToPosition(this.current.x, this.current.y);

				//prevent default action to try to stop text range selection while dragging
				if(typeof e.preventDefault != 'undefined' ) { e.preventDefault(); }
			}
		}
	}

	return true;
};


//document mouseup handler
dbxGroup.prototype.mouseup = function(e)
{
	//if we have a box reference
	if(this.box != null)
	{
		//move the original box to new position
		//this is not strictly necessary, because it happens
		//in response to actions that have already occured by now
		//but this covers us for safety, just in case of .. something ..
		this.moveOriginalToPosition(e.clientX, e.clientY);

		//remove the clone box
		this.removeCloneBox();

		//regenerate the box order array and save to cookie
		this.getBoxOrder();

		//release textrange selection in IE
		if(typeof document.onselectstart != 'undefined')
		{
			document.onselectstart = function() { return true; }
		}
	}

	//reset drag ok flag
	this.dragok = false;
};


//toggle or handle keypress handlers
dbxGroup.prototype.keypress = function(e, anchor)
{
	//if the keyCode is one of the arrow keys
	if(/^(3[7-9])|(40)$/.test(e.keyCode))
	{
		//remove any tooltip that's there
		this.removeTooltip();

		//if this is up/down in a column, or left/right in a row
		if((this.vertical && /^(38|40)$/.test(e.keyCode)) || (!this.vertical && /^(37|39)$/.test(e.keyCode)))
		{
			//pass anchor and direction to shift box position method
			//where 37 (left) and 38 (up) are negative directions
			//and 39 (right) and 40 (down) are positive directions
			this.shiftBoxPosition(e, anchor, /^[3][78]$/.test(e.keyCode) ? false : true);

			//prevent default action if that's supported
			//otherwise return false (resulting in the same effect in ie)
			if(typeof e.preventDefault != 'undefined') { e.preventDefault(); }
			else { return false; }



			//stop event bubbling, because in safari events can come from text nodes
			//and without this bubble control each keyup would call the function twice
			//but since we're doing this, we should do it for everyone for the sake of consistency
			typeof e.stopPropagation != 'undefined' ? e.stopPropagation() : e.cancelBubble = true;

			//and since we're doing that, we also need to clear the keydown flag manually
			//because the event won't reach the document keyup handler which normally does that
			this.keydown = false;

		}
	}

	return true;
};







//regenerate box order array, save to cookie and output to receiver method
dbxGroup.prototype.getBoxOrder = function()
{
	//rebuild the order array
	this.order = [];

	//re-iterate through the elements in this column
	var len = this.eles.length;
	for(var j=0; j<len; j++)
	{
		//if it's a docking box, and not a clone or a dummy
		if(/dbx\-box/i.test(this.eles[j].className) && !/dbx\-(clone|dummy)/i.test(this.eles[j].className))
		{
			//add its index (extracted from dbxid classname)
			//plus its open state (extracted from dbx-box-(open|closed) classname )
			this.order.push(this.eles[j].className.split('dbxid')[1] + (/dbx\-box\-open/i.test(this.eles[j].className) ? '+' : '-'));
		}
	}

	//save the order to this member of the dbx manager's save data object
	dbx.savedata[this.gid] = this.order.join(',');

	//set a cookie and output to receiver method
	dbx.setCookieState();
};


//create a clone
dbxGroup.prototype.createClone = function()
{
	//create a clone and append it to the group container
	//it has to be appended to group container, not body
	//so that it inherits CSS just the same as the original box
	var clone = this.container.appendChild(arguments[0].cloneNode(true));

	//add clone classname
	clone.className += ' dbx-clone';

	//re-inforce important styles
	clone.style.position = 'absolute';
	clone.style.visibility = 'hidden';

	//set z-index
	clone.style.zIndex = arguments[1];

	//move clone to superimpose original
	clone.style.left = arguments[2].x + 'px';
	clone.style.top = arguments[2].y + 'px';

	//set width and height same as original
	clone.style.width = arguments[0].offsetWidth + 'px';
	clone.style.height = arguments[0].offsetHeight + 'px';

	return clone;
};


//create a moveable clone of the original box
dbxGroup.prototype.createCloneBox = function(box)
{
	//original box object
	this.box = box;

	//get original box position
	this.position = { 'x' : this.box.offsetLeft, 'y' : this.box.offsetTop };
	//document.title = 'x=' + this.position.x + ' y=' + this.position.y;

	//calculate mouse/position difference
	this.difference = { 'x' : (this.initial.x - this.position.x), 'y' : (this.initial.y - this.position.y) };

	//create a clone of the original box
	//set the index at the top of the stack
	this.boxclone = this.createClone(this.box, 30000, this.position);

	//set move cursor
	this.boxclone.style.cursor = 'move';

	//dont hide the original / show the clone just yet
	//wait until it's confirmed to be moving
	//so that links will still work before the drag threshold

	//set drag ok flag
	this.dragok = true;
};


//remove a clone box
dbxGroup.prototype.removeCloneBox = function()
{
	//remove the clone
	this.container.removeChild(this.boxclone);

	//show the original
	this.box.style.visibility = 'visible';

	//nullify the reference
	this.box = null;
};


//move original box to new position
dbxGroup.prototype.moveOriginalToPosition = function(clientX, clientY)
{
	//get position and dimensions of the clone
	//xy is y for a vertical column and x for a horizontal row
	//wh is h for a vertical column and w for a horizontal row
	var cloneprops = {
		'xy' : this.vertical ? clientY - this.difference.y : clientX - this.difference.x,
		'wh' : this.vertical ? this.boxclone.offsetHeight : this.boxclone.offsetWidth
		};

	//hide the original
	this.box.style.visibility = 'hidden';

	//show the clone
	this.boxclone.style.visibility = 'visible';


	//dbx.output.value = 'clone: xy=' + cloneprops.xy + '; wh=' + cloneprops.wh + '\n\n';


	//for each box in the array
	var len = this.boxes.length;
	for(var i=0; i<len; i++)
	{
		//get position and dimensions of the original box
		var boxprops = {
			'xy' : this.vertical ? this.boxes[i].offsetTop : this.boxes[i].offsetLeft,
			'wh' : this.vertical ? this.boxes[i].offsetHeight : this.boxes[i].offsetWidth
			};

		//if - the direction of movement is positive; and
		//	clone left/top plus clone width/height is greater than box left/top; and
		//	clone left/top is less than box left/top
		//or - the direction of movement is negative; and
		//	clone left/top is less than box left/top; and
		//	clone left/top plus clone width/height is greater than box left/top
		if
		(
			(this.positive && cloneprops.xy + cloneprops.wh > boxprops.xy && cloneprops.xy < boxprops.xy)
			||
			(!this.positive && cloneprops.xy < boxprops.xy && cloneprops.xy + cloneprops.wh > boxprops.xy)
		)
		{
			//we've found the box before which to insert our original
			//but if the boxes we're comparing are the same don't continue
			if(this.boxes[i] == this.box) { return; }



			//look for the next sibling of this box
			var sibling = this.box.nextSibling;
			while(sibling.className == null || !/dbx\-box/.test(sibling.className))
			{
				sibling = sibling.nextSibling;
			}

			//and don't continue if that sibling is the box we're inserting before
			//(so that we're not doing an action that would result in no change)
			if(this.boxes[i] == sibling) { return; }



			//if we're using the box animation effect (if the resolution is > 0)
			if(this.resolution > 0)
			{
				//the animation is not on the invisible object we're going to move
				//but the visible object that will get shifted as a result
				//because that's the only one that visibly appears to have moved

				//so .. with the position of the original box before shifting it
				//(which may have moved since last time we calculated it)
				//get the index of the boxes array that equates to the visibly-moved box
				//if we're moving down (ie, the original box is lower than the box we inserted before)
				//then the visibly-moved box is the one BEFORE the box we're inserting it before
				//otherwise the visibly-moved box IS the box we inserted it before
				//var visindex = this.box[this.vertical ? 'offsetTop' : 'offsetLeft'] < boxprops.xy ? i - 2 : i;

				//if the value is -1, it should be the last visible box
				//which is length - 2 because the very last one is a dummy
				//if(visindex < 0) { visindex = len - 2; }
				//if(visindex < 0) { visindex = 0 - visindex; }

				//get the actual object
				//var visibox = this.boxes[visindex];


				//if we're moving down (ie, the original box is lower than the box we inserted before)
				//then the visibly-moved box is the previous sibling of the box we're inserting it before
				if(this.box[this.vertical ? 'offsetTop' : 'offsetLeft'] < boxprops.xy)
				{
					var visibox = this.boxes[i].previousSibling;
					while(visibox.className == null || !/dbx\-box/.test(visibox.className))
					{
						visibox = visibox.previousSibling;
					}
				}
				//otherwise the visibly-moved box IS the box we inserted it before
				else
				{
					visibox = this.boxes[i];
				}

				//get both before positions of the visibly-moved box
				var visipos = { 'x' : visibox.offsetLeft, 'y' : visibox.offsetTop };
			}



			//get the pre-position of the original box
			var prepos = { 'x' : this.box.offsetLeft, 'y' : this.box.offsetTop };



			//move the original box before this box
			//dbx.output.value = '[' + (this.boxes[i] == sibling) + ']  INSERT ' + this.box.className.split('dbxid')[1] + ' BEFORE ' + this.boxes[i].className.split('dbxid')[1] + '; visibox=' + visibox.className.split('dbxid')[1] + '      --- ' + Math.random() + '\n' + dbx.output.value;
			this.container.insertBefore(this.box, this.boxes[i]);


			//update initial mouse co-ordinates values with the
			//difference between these positions and the pre-positions
			//so that the sticky region follows the static box
			this.initial.x += (this.box.offsetLeft - prepos.x);
			this.initial.y += (this.box.offsetTop - prepos.y);



			//if we're using the box animation effect
			//and the box to animate is not the same as the original box we moved
			if(this.resolution > 0 && visibox != this.box)
			{
				//dbx.output.value = 'ACCEPTED: this.boxes[i]=' + this.boxes[i].className.split('dbxid')[1] + '; box=' + this.box.className.split('dbxid')[1] + '; visibox=' + visibox.className.split('dbxid')[1] + '      --- ' + Math.random() + '\n' + dbx.output.value;


				//dbx.output.value += 'box ' + i + ': xy=' + boxprops.xy + '; wh=' + boxprops.wh + '\n';


				//create a new box animator
				//if we're moving down [the original box is lower than the box we inserted before]
				//then the visibly-moved box is the one BEFORE the box we're inserting it before
				//otherwise the visibly-moved box IS the box we inserted it before
				var animator = new dbxAnimator(this, visibox, visipos, this.resolution, false, null);
			}



			else
			{
				//dbx.output.value = 'DECLINED: this.boxes[i]=' + this.boxes[i].className.split('dbxid')[1] + '; box=' + this.box.className.split('dbxid')[1] + '; visibox=' + visibox.className.split('dbxid')[1] + '      --- ' + Math.random() + '\n' + dbx.output.value;
			}



			//and stop
			break;
		}
	}
};



















//animation object
function dbxAnimator(caller, box, pos, res, kbd, anchor)
{
	//dbx.output.value = 'ANIMATE: ' + box.className.split('dbxid')[1] + '      --- ' + Math.random() + '\n' + dbx.output.value;


	//calling object
	this.caller = caller;

	//the box we're going to animate
	this.box = box;

	//timer object, initially null
	//so we can test its non-existence against null
	this.timer = null;

	//its position before moving
	var before = pos[this.caller.vertical ? 'y' : 'x'];

	//its new position
	var after = this.box[this.caller.vertical ? 'offsetTop' : 'offsetLeft'];

	//if the values are not the same
	if(before != after)
	{
		//don't continue if the number of running timers
		//is greater than the number of boxes in this group
		//(minus one, so as not to count the dummy)
		if(dbx.running > this.caller.boxes.length - 1) { return; }

		//create a clone of the box
		//set the index just below the original box clone
		var clone = this.caller.createClone(this.box, 29999, arguments[2]);

		//make the clone visible
		clone.style.visibility = 'visible';

		//make the box invisible
		this.box.style.visibility = 'hidden';
		//this.box.style.MozOpacity = '0.2';

		//calculate the change between the before and after positions
		//so it comes out as a negative number for movement upwards/leftwards
		//then animate the clone from its zero position
		//for the amount specified by the direction specified at the set resolution
		this.animateClone(
			clone,
			before,
			after > before ? after - before : 0 - (before - after),
			this.caller.vertical ? 'top' : 'left',
			res,
			kbd,
			anchor
			);
	}
};


//animate a clone
dbxAnimator.prototype.animateClone = function(clone, current, change, dir, res, kbd, anchor)
{
	//reference to this
	var self = this;

	//timer counter so we know when it's finished
	var count = 0;

	//add to the number of running timers
	dbx.running ++;

	//start a perpetual timer
	this.timer = window.setInterval(function()
	{
		//dbx.output.value = 'TIMER: ' + self.timer + '      --- ' + Math.random() + '\n' + dbx.output.value;

		//increase counter
		count ++;

		//change current position by change divided by resolution
		current += change / res;

		//re-apply the clone position
		clone.style[dir] = current + 'px';

		//if the counter has reached resolution
		if(count == res)
		{
			//abandon this timer and nullify the reference
			window.clearTimeout(self.timer);
			self.timer = null;

			//deduct from the number of running timers
			dbx.running --;

			//remove the clone
			self.caller.container.removeChild(clone);

			//reshow the original box
			self.box.style.visibility = 'visible';
			//self.box.style.MozOpacity = '1';

			//if this animation was keyboard initiated
			if(kbd)
			{
				//send focus to the anchor, if it's not null and if its parent isn't hidden
				//we need to test the latter to prevent sending focus to a hidden anchor
				//which can happen if multiple animations overlap quickly,
				//such as from keydown events repeating
				if(anchor != null && anchor.parentNode.style.visibility != 'hidden')
				{
					anchor.focus();
				}

				//else if it is null but we're using toggles
				else if(self.caller.toggles)
				{
					//get a reference to the button
					var button = self.caller.buttons[parseInt(self.box.className.split('dbxid')[1],10)];

					//if there is one and it has an "isactive" flag
					if(button != null && typeof button.isactive != 'undefined')
					{
						//send focus back to the button
						//otherwise the focus may get transferred to the clone
						//and then lost when the clone is destroyed
						button.focus();
					}
				}
			}
		}

	}, 20);
};










//DOM cleaner for IE
if(typeof window.attachEvent != 'undefined')
{
	window.attachEvent('onunload', function()
	{
		//relevant events
		var ev = ['mousedown', 'mousemove', 'mouseup', 'mouseout', 'click', 'keydown', 'keyup', 'focus', 'blur', 'selectstart', 'statechange', 'boxdrag', 'boxopen', 'boxclose'];
		var el = ev.length;

		//for each item in the document.all collection
		var dl = document.all.length;
		for(var i=0; i<dl; i++)
		{
			//for each relevant event
			for(var j=0; j<el; j++)
			{
				//if(typeof document.all[i][ev[j]] != 'undefined' && document.all[i][ev[j]] != null)
				//{
				//	alert( document.all[i][ev[j]] );
				//}

				//set it to null so it's garbage collected
				document.all[i]['on' + ev[j]] = null;
			}
		}
	});
}
