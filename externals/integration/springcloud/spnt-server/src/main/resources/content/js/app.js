/////////////////////////////////////////////////////////////////
//// Based on  WamiApp API                                      ////
/////////////////////////////////////////////////////////////////

//  Create a new Wami.App object. Note, you should never create more than one
//  Spantus.App object on your page.
//  
//  @param elem
//             The parent element under which we insert the audio applet, most
//             likely a div
//
//  @param wamiResponseHandlers
//             A mapping of event type to handlers, valid options are:
//
//   { "onReady" : func,             // function() called when WAMI is ready -- treat this like onload 
//     "onRecognitionResult" : func, // function(recResult) called when rec result available 
//     "onTimeout" : func,           // function() called when wami times out 
//     "onError" : func,             // function(errorType, errorMessage) called when an error occurs 
//     "onMessage" : func              // function(xmlNode) called when any other type of response is received from the server. 
//                                   // This will only occur for wami-developers who are also writing server-side code, 
//                                   // this will be xml no matter what value you set wamiResponseFormat to 
//   }
//
//  @param wamiResponseFormat
//             Must a string, either "xml" or "json", indicating the type of
//             object passed into onRecognitionResult() (and any future event
//             handlers which take arguments)
//  @param audioOptions
//            audio options, see below (defaults are shown) 
//
//   { "width" : int,              // width in pixels
//     "height" : int,             // height in pixels
//     "pollForAudio" : bool,      // enables audio playback (note: default is false, 
//                                 //and you MUST enable this to turn playback synthesized speech or other audio
//     "allowStopPlaying" : bool,  // allows the user to stop audio playback 
//     "playRecordTone" : bool,    // plays a tone when recording starts and stops
//     "hideButton" : bool,        // if true, hides the clickable button (but still shows the level meter); 
//                                 // you must use javascript to start and stop recording
//     "useSpeechDetector" : bool, // if false: "hold to talk" mode where recording is started and
//                                 // stopped manually, if true: "click to talk" mode where beginning
//                                 // and end of speech is detected after recording is started 
//   }
//  
//  @param configOptions
//             You may set these here, or call configure() later 
//  
//  {
//      "sendIncrementalResults" : true,  // true if you want incremental recognition results
//      "sendAggregates" : true,          // true if you want to use the incremental aggregator 
//      "splitTag" : "command"            // tag used to split aggregates 
//  }
// 
//  @param grammar
//             You may set this here, or call configure() or setGrammar later 
//  
//  {
//             "language" : "en-us",             // "en-us" or "zh" 
//             "grammar" : "MUST BE SPECIFIED", //you must specify a grammar 
//             "type" : "jsgf"                  // currently ignored: this is the only supported type 
//  }
 
Spantus.App = function(elem, spntResponseHandlers, spntResponseFormat, audioOptions,
		configOptions, grammar) {
	if (!elem) {
		return;
	}
	
	// store the response handler -- global
	if (_gSpantusAppSingleton) {
		alert("Error: You should only create one Spantus.App per page");
		return;
	}

	_gSpantusAppSingleton = this;
	this._spntResponseHandlers = spntResponseHandlers;
	this._spntResponseFormat = spntResponseFormat;

	if (typeof (configOptions) != "undefined"
			|| typeof (grammar) != "undefined") {
		this.configure(configOptions, grammar);
	}

	// attach the audio applet
	this.attachAudio(elem, audioOptions);

	return true;
}

/**
 * Configuration options and grammar options shown at constructor
 */
Spantus.App.prototype.configure = function(options, grammar) {
	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "configure");
	root.setAttribute("wsessionid", _spntParams.wsessionid);
	
	if (typeof (options) != "undefined") {
		this.appendParam(xmlDoc, root, "sendIncrementalResults",
				options.sendIncrementalResults);
		this
				.appendParam(xmlDoc, root, "sendAggregates",
						options.sendAggregates);
		this.appendParam(xmlDoc, root, "splitTag", options.splitTag);
		this.appendParam(xmlDoc, root, "locationProtocol", location.protocol);
		this.appendParam(xmlDoc, root, "locationDomain", document.domain);
		this.appendParam(xmlDoc, root, "locationPort", location.port);
		this.appendParam(xmlDoc, root, "locationPath", location.pathname);

		if (options.recordOnly) {
			this.appendParam(xmlDoc, root, "recordOnly", options.recordOnly);
		}

		if (options.devKey) {
			this.appendParam(xmlDoc, root, "jsapiDevKey", options.devKey);
		}
	}

	if (typeof (grammar) != "undefined") {
		this.appendGrammar(xmlDoc, root, grammar);
	}

	if (options.environment) {
		this._spntEnvironment = options.environment;
		this.setupEnvironment(options.environment);
	} else {
		this._spntEnvironment = null;
	}

	setTimeout(function() { _gSpantusAppSingleton.spntPoll() }, 1);
	this.postXML(xmlDoc);
}

// see constructor for grammar specification format
Spantus.App.prototype.setGrammar = function(grammar) {
	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "configure");
	this.appendGrammar(xmlDoc, root, grammar);
	this.postXML(xmlDoc);
}

// speak via TTS
// text: the string to speak
// options: { "language" : "zh" }
Spantus.App.prototype.speak = function(text, options) {
	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "speak");
	var params = xmlDoc.createElement("params");
	for ( var name in options) {
		var param = xmlDoc.createElement("param");
		param.setAttribute("name", name);
		param.setAttribute("value", options[name]);
		params.appendChild(param);
	}
	var elem = xmlDoc.createElement("synth_string");
	elem.appendChild(xmlDoc.createTextNode(text));
	root.appendChild(params);
	root.appendChild(elem);
	this.postXML(xmlDoc);
}

Spantus.App.prototype.playRecording = function(wsessionid, utt_id) {
	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "playrecording");
	root.setAttribute("wsessionid", wsessionid);
	root.setAttribute("uttid", utt_id);
	this.postXML(xmlDoc);
}

Spantus.App.prototype.playWavFromURL = function(url) {
	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "playurl");
	root.setAttribute("url", url);
	this.postXML(xmlDoc);
}

Spantus.App.prototype.replayLastRecording = function() {
	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "replay");
	this.postXML(xmlDoc);
}

/** post an xml message to the server (using an xml node) */
Spantus.App.prototype.postXML = function(xmlNode) {
	var xmlStr = Spantus.utils.xml.toString(xmlNode);
	this.postXMLString(xmlStr);
}

Spantus.App.prototype.logEvents = function(logs) {
	var xdoc = Spantus.utils.xml.newDocument('update');
	var root = xdoc.firstChild;
	root.setAttribute('type', 'logevents');

	for ( var i = 0; i < logs.length; i++) {
		var node = xdoc.createElement('event');
		for (attr in logs[i]) {
			node.setAttribute(attr, logs[i][attr]);
		}

		root.appendChild(node);

	}
	this.postXML(xdoc);
}

/** post a json message to the server */
Spantus.App.prototype.postJSON = function(json) {
	alert("postJSON not yet implemented");
}

Spantus.App.prototype.startRecording = function() {
	try {
		this.getAudioApplet().startListening();
		applet.startListening();
	} catch (e) {
		// ignore
	}
}

Spantus.App.prototype.stopRecording = function() {
	try {
		this.getAudioApplet().stopRecording();
	} catch (e) {
		// ignore
	}
}

Spantus.App.prototype.stopPlaying = function() {
	try {
		this.getAudioApplet().stopPlaying();
	} catch (e) {
		// ignore
	}
}

// ///////////////////////////////////////////////////////////////
// // "PRIVATE" functionality - subject to change at any time ////
// //////////////////////////////////////////////////////////////
// truly global: the singleton spnt app
var _gSpantusAppSingleton;

// "private" member variables
Spantus.App.prototype._spntResponseFormat;
Spantus.App.prototype._spntResponseHandlers;
Spantus.App.prototype._audioApplet;
Spantus.App.prototype._messageElements = null;
Spantus.App.prototype._spntEnvironment = null;
Spantus.App.prototype._spntShouldPoll = true;


Spantus.App.prototype.repoll = function(timeoutBeforeRepoll) {
	var timeout = 200;
	if (!timeout) {
		timeout = timeoutBeforeRepoll;
	}

	var xmlDoc = Spantus.utils.xml.newDocument("update");
	var root = xmlDoc.firstChild;
	root.setAttribute("type", "repoll");
	root.setAttribute("timeout", timeout);
	this.postXML(xmlDoc);
}

Spantus.App.prototype.stopPolling = function() {
	this._spntShouldPoll = false;
	this.repoll();
}

Spantus.App.prototype.startPolling = function() {
	if (!this._spntShouldPoll) {
		this._spntShouldPoll = true;
		setTimeout(this.spntPoll, 1);
	}
}

Spantus.App.prototype._messageQueue = new Array();
Spantus.App.prototype._currentMessage = null;

Spantus.App.prototype.postXMLString = function(xmlStr) {
	this._messageQueue.push(xmlStr);
	this.trySendingMessage();
}

Spantus.App.prototype.trySendingMessage = function() {
	if (this._currentMessage != null || this._messageQueue.length == 0) {
		return;
	}
	
	this._currentMessage = this._messageQueue.shift();
	
	var body = document.getElementsByTagName("body")[0];

	if (this._messageElements == null) {
		this._messageElements = this.createMessageElements();
		body.appendChild(this._messageElements.messageFormDiv);
		body.appendChild(this._messageElements.messageFrame);
	}

	var postID = Math.floor(Math.random() * 100000000);

	this._messageElements.messageForm.id = "spntForm" + postID;
	
	var params = "jsxss=post&postID=" + postID;
	var posturl = this.appendParamsToURL(_spntParams.controlUrl, params)
	this._messageElements.messageForm.setAttribute("accept-charset", "UTF-8");
	this._messageElements.messageForm.setAttribute("action", posturl);

	this._messageElements.messageField.value = this._currentMessage;
	this._messageElements.messageForm.submit();
}

Spantus.App.prototype.removeFromParent = function(elem) {
	var parent = elem.parentNode;
	parent.removeChild(elem);
}

Spantus.App.prototype.createMessageElements = function() {
	var rand = Math.floor(Math.random() * 100000000);
	var frameName = "spntHiddenFrame" + rand;
	var divName = "spntFormDiv" + rand;

	var messageFormDiv = document.createElement("div");
	messageFormDiv.style.visibility = "hidden";
	messageFormDiv.id = divName;

	var messageForm = document.createElement("form");
	var t = new Date().getTime(); // force IE not to cache
	messageForm.setAttribute("method", "POST");
	messageForm.setAttribute("target", frameName);

	var messageField = document.createElement("input");
	messageField.id = "spntMessage";
	messageField.setAttribute("name", "spntMessage");
	messageField.setAttribute("type", "hidden");
	messageForm.appendChild(messageField);

	messageFormDiv.appendChild(messageForm);

	var messageFrame;
	try { // IE hack to prevent form submission from causing a popup
		messageFrame = document
				.createElement('<iframe name="' + frameName + '">');
	} catch (ex) {
		messageFrame = document.createElement('iframe');
	}

	messageFrame.name = frameName;
	messageFrame.id = frameName;
	messageFrame.src = "about:blank";
	messageFrame.style.display = "none";

	return {
		"messageFormDiv" :messageFormDiv,
		"messageField" :messageField,
		"messageForm" :messageForm,
		"messageFrame" :messageFrame
	};
}

Spantus.App.prototype.getAppletParam = function(params, name) {
	for ( var i = 0; i < params.length; i++) {
		var param = params[i];
		if (param.name == name) {
			return param;
		}
	}

	return null;
}

Spantus.App.prototype.overrideAppletParam = function(params, options, name) {
	var param = this.getAppletParam(params, name);

	if (param) {
		param.value = this.getParam(options, name, param.value);
	} else {
		alert("Applet Param Not Found: " + name);
	}
}

Spantus.App.prototype.attachSpantusPlugin = function(elem, options, params){
	var enabled = false;
	var mimetype;
	var i;
	for(i=0; i<navigator.plugins.length; i++){
		var plugin = navigator.plugins[i];
		var j;
		if (plugin.name.indexOf("Spantus") >= 0){
			enabled = true;
			mimetype = plugin[0];
		} else {
			for(j=0; j<plugin.length; j++){
		   	  	mimetype = plugin[j];
		   	  	// The enabledPlugin test doesn't work on OS X
		   	  	if (mimetype.type=="application/x-spnt-plugin"){
		   	  		enabled = true;
		   	  		break;
		   	  	}
			}
		}
		if (enabled){
			break;
		}
	}
	if (!enabled)
		return false;

	var applet = document.createElement("object");
	applet.setAttribute("TYPE", "application/x-spnt-plugin");
	applet.style.visibility = "hidden";
	applet.style.width = "0px";
	applet.style.height = "0px";
	applet.style.margin = "0px";
	applet.style.padding = "0px";
	applet.style.border_style = "none";
	applet.style.border_width = "0px";
	applet.style.max_width = "0px";
	applet.style.max_height = "0px";
	var skip = {"CODE" : true, 
				"ARCHIVE" : true,
				"type" : true,
				"scriptable" : true};
	var hideButton = "false";
	for (var i = 0; i < params.length; i++){
		var param = params[i];
		var name = param.name;
		var value = param.value;
		if (name == "hideButton"){
			hideButton = value;
		} else if (!skip[name]){
			var e = document.createElement("param");
			e.setAttribute("name", name);
			e.setAttribute("value", value);
			applet.appendChild(e);
		}
	}
	if (hideButton == "false"){
		var div = document.createElement("div");
		div.appendChild(applet)
		var form = document.createElement("form");
		var button = document.createElement("input");
		var interval = 0;
		button.setAttribute("TYPE", "button");
		button.setAttribute("VALUE", "Record");
		button.style.width = "100%";
		button.style.height = "100%";
		var pollrec = function(){
			if (applet.recorder_state == 0){
				button.value = "Record";
			} else {
				button.value = "Stop";
				setTimeout(pollrec, 100);
			}
		};
		button.onclick = function(){
			if (applet.recorder_state == 0){
				// Idle
				applet.startListening();
				setTimeout(pollrec, 100);
			} else {
				applet.stopRecording();
			}
		};
		form.appendChild(button);
		div.appendChild(form);
		return div;
	} else {
		return applet;
	}
}

Spantus.App.prototype.attachJavaPlugin = function(elem, option, params){
	var applet = document.createElement("applet");
	applet.setAttribute("CODE", _spntParams.applet.code);
	applet.setAttribute("ARCHIVE", _spntParams.applet.archive);

	for ( var i = 0; i < params.length; i++) {
		var param = params[i];
		var e = document.createElement("param");
		var name = param.name;
		var value = param.value;

		e.setAttribute("name", name);
		e.setAttribute("value", value);
		applet.appendChild(e);
	}
	return applet;
}

Spantus.App.prototype.attachAudio = function(elem, options) {
	_spntParams.applet.width = this.getParam(options, "width",
			_spntParams.applet.width);
	_spntParams.applet.height = this.getParam(options, "height",
			_spntParams.applet.height);

	var params = _spntParams.applet.params;
	var localeCode = this.getParam(options, "localeCode", "lt_LT");
	
	var pollForAudio = this.getParam(options, "pollForAudio", "true");

	if (pollForAudio != "true") {
		options.playUrl = '';
		this.overrideAppletParam(params, options, "playUrl");
	}

	this.overrideAppletParam(params, options, "allowStopPlaying");
	this.overrideAppletParam(params, options, "playRecordTone");
	this.overrideAppletParam(params, options, "hideButton");
	this.overrideAppletParam(params, options, "useSpeechDetector");

	
	var applet = this.attachSpantusPlugin(elem, options, params);
	if (!applet) {
		applet = this.attachJavaPlugin(elem, options, params);
		applet.width = _spntParams.applet.width;
		applet.height = _spntParams.applet.height;
	}
	else {
		applet.style.width = _spntParams.applet.width;
		applet.style.height = _spntParams.applet.height;
	}
	
	applet.setAttribute("NAME", _spntParams.applet.name);
	applet.setAttribute("localeCode", "lt_LT");
	
	elem.appendChild(applet);
	this._audioApplet = applet;

	// alert('Done Attaching Audio Applet');
}

Spantus.App.prototype.appendParamsToURL = function(url, params) {
	if (url.indexOf("?") == -1) {
		return url + "?" + params;
	}
	else {
		return url + "&" + params;
	}
}

// Poll by requesting javascript from the Spantus server.
Spantus.App.prototype.spntPoll = function() {
	var callback = "_gSpantusAppSingleton.responseHandler";

	if (this._spntEnvironment) {
		callback = "parent._gSpantusAppSingleton.responseHandler"
	}

	var time = new Date().getTime(); // force IE not to cache
	var params = "jsxss=get&polling=true&t=" + time + "&callback=" + callback;
	var scriptURL = this.appendParamsToURL(_spntParams.controlUrl, params);

	var prev = document.getElementById("spntPollScriptTag");
	if (prev) {
		prev.parentNode.removeChild(prev);
	}

	if (this._spntEnvironment == null) {
		var head = document.getElementsByTagName("head")[0];
		var e = document.createElement("script");
		e.src = scriptURL;
		e.id = "spntPollScriptTag";
		head.appendChild(e);
	} else {
		var spntframe = document.getElementById('spntScriptLoader');

		if (spntframe.isLoaded) {
			if (spntframe.contentWindow.buildScript) {
				spntframe.contentWindow.buildScript(scriptURL);
			}
		} else {
			spntframe.scriptqueue.push(scriptURL);
		}
	}
}

Spantus.App.prototype.setupEnvironment = function(src) {
	// prep the transport
	// i.e. build the iframe so that it can create scripts
	var spntframe = document.createElement('iframe');
	spntframe.src = src;
	spntframe.id = 'spntScriptLoader';
	spntframe.isLoaded = false;
	spntframe.style.width = '1px';
	spntframe.style.position = 'absolute';
	spntframe.style.left = '-999px';
	spntframe.scriptqueue = [];

	var iframeLoadFunction = function() {
		var wdw = spntframe.contentWindow, q = spntframe.scriptqueue, i, m;

		for (i = 0, m = q.length; i < m; i++) {
			wdw.buildScript(q[i]);
		}

		spntframe.isLoaded = true;
	}
	
	if (spntframe.attachEvent) {
		spntframe.attachEvent("onload", iframeLoadFunction); // ie
	} else if (spntframe.addEventListener) {
		spntframe.addEventListener("load", iframeLoadFunction, true); // Safari, Firefox
	} else {
		spntframe.onload = iframeLoadFunction;
	}

	document.body.appendChild(spntframe);
}

Spantus.App.prototype.responseHandler = function(xmlStr) {
	// Internet Explorer
	var xmlDoc = Spantus.utils.xml.fromString(xmlStr);
	var replies = xmlDoc.getElementsByTagName("reply");

	var repollTimeout = 1; // 1ms between polls

	for ( var i = 0; i < replies.length; i++) {
		var reply = replies[i];
		var type = reply.getAttribute("type");
		if (type == "recresult") {
			this.handleRecResult(reply);
		} else if (type == "timeout") {
			this.handleCallback("onTimeout");
			return; // on timeout, we do not continue polling, and exit
			// immediately
		} else if (type == "error") {
			this._spntShouldPoll = false;
			this.handleCallback("onError", reply.getAttribute("error_type"),
					reply.getAttribute("message"), reply.getAttribute("details"));
		} else if (type == "spnt_ready") {
			this.handleCallback("onReady", _spntParams.wsessionid);
		} else if (type == "stop_polling") {
			this._spntShouldPoll = false;
		} else if (type == "repoll") {
			repollTimeout = reply.getAttribute("timeout");
		} else if (type == "finishedplayingaudio") {
			this.handleCallback("onFinishedPlayingAudio");
		} else if (type == "invalid_key") {
			alert("The developer key for this domain does not match the Spantus database.  This WAMI app will not work properly!");
		} else if (type == "update_processed") {
			//var message = 'Processed: ' + this._currentMessage + '\nMessage Queue: ' + this._messageQueue.length;
			//alert(message);
			this._currentMessage = null;
			
			var postID = reply.getAttribute("postID");
			if (this._messageElements.messageForm.id == "spntForm" + postID) {
				this._messageElements.messageFrame.src = "about:blank";
				// alert('reset from src: ' + postID);
			}
			// alert('processed: ' + postID + ',' +
			// this._messageElements.messageForm.id);
			
			setTimeout(function() { _gSpantusAppSingleton.trySendingMessage() }, 10);
			
		} else {
			this.handleCallback("onMessage", reply);
		}
	}

	if (this._spntShouldPoll) {
		setTimeout(function() { _gSpantusAppSingleton.spntPoll() }, repollTimeout);
	}
}

Spantus.App.prototype.appendParam = function(xmlDoc, elem, name, value) {
	if (typeof (value) != "undefined" && value != null) {
		var param = xmlDoc.createElement("param");
		param.setAttribute("name", name);
		param.setAttribute("value",
				(typeof (value) == "boolean") ? (value ? "true" : "false")
						: value);
		elem.appendChild(param);
	}
}

Spantus.App.prototype.appendGrammar = function(xmlDoc, elem, grammar) {
	var grammarLanguage = this.getParam(grammar, "language", "en-us");
	var grammar = this.getParam(grammar, "grammar", "");
	if (grammar == "") {
		alert("You must specify a grammar!");
		return;
	}

	var node = xmlDoc.createElement("jsgfgrammar");
	node.setAttribute("language", grammarLanguage);
	var text = xmlDoc.createTextNode(grammar);
	node.appendChild(text);
	elem.appendChild(node);
}

Spantus.App.prototype.getParam = function(options, name, defaultValue) {
	var value = (typeof (options) != "undefined" && options != null && typeof (options[name]) != "undefined") ? options[name]
			: defaultValue;

	return (typeof (value) == "boolean") ? (value ? "true" : "false") : value
}

Spantus.App.prototype.getAudioApplet = function() {
	return this._audioApplet;
}

Spantus.App.prototype.convertXmlRecResultToJson = function(reply) {
	var incremental = reply.getAttribute("incremental") == "true";
	var utt_id = parseInt(reply.getAttribute("utt_id"));
	var incremental_index = parseInt(reply.getAttribute("incremental_index"));

	var hyps = new Array();
	var xmlHyps = reply.getElementsByTagName("hyp");
	if (xmlHyps && xmlHyps.length > 0) {
		for ( var i = 0; i < xmlHyps.length; i++) {
			hyps[i] = this.convertXmlHypToJson(xmlHyps[i]);
		}
	}

	return {
		"incremental" :incremental,
		"utt_id" :utt_id,
		"incremental_index" :incremental_index,
		"hyps" :hyps
	};
}

Spantus.App.prototype.convertXmlHypToJson = function(xmlHyp) {
	var index = parseInt(xmlHyp.getAttribute("index"));
	var textNode = xmlHyp.getElementsByTagName("text")[0].firstChild;
	var text = textNode ? textNode.data : "";

	var jsonHyp = {
		"index" :index,
		"text" :text
	};
	var xmlAggs = xmlHyp.getElementsByTagName("aggregate");
	if (xmlAggs && xmlAggs.length > 0) {
		jsonHyp["aggregate"] = this.convertXmlAggregateToJson(xmlAggs[0]);
	}
	return jsonHyp;
}

Spantus.App.prototype.convertXmlAggregateToJson = function(xmlAgg) {
	var index = parseInt(xmlAgg.getAttribute("index"));
	var partial = xmlAgg.getAttribute("partial") == "true";

	var kvsHash = {};
	var kvs = xmlAgg.getElementsByTagName("kv");
	if (kvs) {
		for ( var i = 0; i < kvs.length; i++) {
			kvsHash[kvs[i].getAttribute("key")] = kvs[i].getAttribute("value");
		}
	}

	return {
		"index" :index,
		"partial" :partial,
		"kvs" :kvsHash
	};
}

Spantus.App.prototype.handleRecResult = function(reply) {
	var recresult = (this._spntResponseFormat == "json") ? this
			.convertXmlRecResultToJson(reply) : reply;
	this.handleCallback("onRecognitionResult", recresult);
}

Spantus.App.prototype.handleCallback = function(name, arg1, arg2, arg3, arg4) {
	if (typeof (this._spntResponseHandlers[name]) != "undefined") {
		this._spntResponseHandlers[name](arg1, arg2, arg3, arg4);
	}
}

// for iPhone
var _playURL = _spntParams.playUrl;
var _recordURL = _spntParams.recordUrl;

// for Android
try {
	window.spnt.setRecordUrl(_recordURL);
	window.spnt.setPlayUrl(_playURL);
} catch (e) {
	// ignore, just means it's not android
}

// Backwards compatibility before namespace
var SpantusApp = Spantus.App;
