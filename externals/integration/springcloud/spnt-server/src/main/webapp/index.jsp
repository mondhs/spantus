<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%
	//session.invalidate(); 
	java.util.Locale newLocale = null;
	String localeLanguage = request.getParameter("locale");
	java.util.Locale sessionLocale = (java.util.Locale) session
			.getAttribute("locale");
	if (localeLanguage == null && sessionLocale != null) {
		localeLanguage = sessionLocale.getCountry();
	}
	if ("lt".equals(localeLanguage)) {
		newLocale = new java.util.Locale("lt", "LT");
	} else {
		newLocale = new java.util.Locale("en", "US");
	}
	session.setAttribute("locale", newLocale);
%>
<fmt:setLocale value="${locale}" scope="session" />
<fmt:setBundle basename="messages" />

<html>
<head>
<title><fmt:message key="TITLE" /></title>
<link rel="stylesheet" href="css/spnt-ui.css">
<script src="api/content/js/spantus.js">
	
</script>

<script>
	var mySapntusApp;
	function onLoad() {
		var audioDiv = document.getElementById('AudioContainer');

		var jsgf = 
			"#JSGF V1.0;\n" +
			"grammar parrot;\n" +
			"public <top> = hello wami | i want a cracker | feed me;\n";

		var grammar = {
			"language" : "en-us",
			"grammar" : jsgf
		};

		var audioOptions = {
			"pollForAudio" : true,
			"localeCode": "${sessionScope.locale}"
		};

		var configOptions = {
			"sendIncrementalResults" : false,
			"sendAggregates" : false
		};

		var handlers = {
			"onReady" : onSapntusReady, //WAMI is loaded and ready
			"onRecognitionResult" : onSapntusRecognitionResult, //Speech recognition result available
			"onError" : onSapntusError, //An error occurred
			"onTimeout" : onSapntusTimeout
		}; 

		mySpantusApp = new Spantus.App(audioDiv, handlers, "json", audioOptions,
				configOptions, grammar);
	}

	function onSapntusReady() {
	}

	function onSapntusRecognitionResult(result) {
		var hyp = result.hyps[0].text; //what we think the user said
		alert("You said: '" + hyp + "'");
		mySapntusApp.speak(hyp); //Speech synthesis of what we heard
		setTimeout("mySapntusApp.replayLastRecording()", 500); //play back audio we recorded
	}

	function onSapntusError(type, message) {
		alert("Spantus error: type  = " + type + ", message = " + message);
	}

	//called when your WAMI session times out due to
	//in activity.
	function onSapntusTimeout() {
		alert("Spantus timed out.  Hit reload to start over");
	}
</script>

</head>
<body>
	<a href="?locale=en">EN</a>
	<a href="?locale=lt">LT</a>
	<div id="agreement">
		<fmt:message key="ANONYMOUSLY.WARNING" />
		<input id="agreementBtn" type="button" onclick="onLoad();document.getElementById('agreement').style.display = 'none'; document.getElementById('AudioContainer').style.display = 'block'; " value="<fmt:message key="BUTTON.START" />"
			 />

	</div>
	<div id="AudioContainer"></div>
</body>
</html>