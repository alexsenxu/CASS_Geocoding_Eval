//
//  This block of code creates the YDN Rack module that supports searching within
//  a set of docs. Previously, the only search functionality would scour the entire
//  site for the terms entered. This module aims to provide contextual search 
//  within documentation so a user can more easily find something related to the 
//  docs currently being presented. We use a bunch of y! technology to do the heavy
//  lifting and that's awesome that we just have to manufacture the glue. 
//
//  Depends on having YUI 3+ seed on the page ahead of it. eg:
//    <script type="text/javascript" src="http://l.yimg.com/a/combo?/yui/3.1.0/build/yui/yui-min.js"></script>
//
//  Assumes it's on a regular YDN page which will have a ydnRack id on it. 
//

YUI.namespace('YDN.docs');

YUI.YDN.docs.searchModule = (function()  {

    // the initial markup for the search box. Since the whole thing is JS,
    // we don't want strange markup artifacts if there's no JS or whatever. 
    var boxInit = '<div id="ydn-search-module" class="fadeBox"><div class="hd"><p class="title">Search This Book</p></div>' +
                  '<div class="bd"><div id="ydn-search-module-inputs"><input id="search-input" accesskey="d">' +
                  '<button id="search-button">Search!</button></div><div id="ydn-search-module-results"></div></div></div>';
    var urlbase = '';
    var urllevel = 0;

    YUI().use('io', 'substitute', 'gallery-yql', 'event-key', function(Y)  {

        //
        // This piece of code takes the search results that come back, sticks them
        // into a JS object, and hammers that out into a markup UL on the page.
        //
        //  YUI 3 modules used: node, substitute
        //
        //  in:  searchResults (object) the object that comes back from the
        //       XDR request to YQL.
        //  out: None. Sets the markup in the search module according to the conditions
        //       of the results set passed in. Nothing is returned.
        //
        var showSearchResults = function(searchResults)  {
            // container for the formatted results of the search
            var resultsOutput = Y.Node.get('#ydn-search-module-results'),
            // this is a template that's used to generate the LIs 
            // for each search result
            liTemplate = "<li><a href=\"{url}\">{title}</a></li>",
            // the string of markup that's stuck back into the rack
            markup = '', i;

            // show the results if we have them
            if(searchResults && (searchResults.count != 0)) {
                // start off the unordered list
		searchResults.count = searchResults.results.BATCHSEARCHRESPONSE.RESULTSET_WEB.NUMRESULTS;
                markup = '<ul>';
		var dispcnt = 0;

                // iterate over all the search results
                // use the LI template from above to fill in the specific values
                // and append it to the markup string
		tu = {};
                for (i = 0; i < searchResults.count; i+=1) {
			if(urllevel == 0 || (urllevel == 1 && searchResults.results.BATCHSEARCHRESPONSE.RESULTSET_WEB.RESULT[i].URL.indexOf(urlbase) == 0))
			{
			    dispcnt++;
			    if(dispcnt > 10)
				break;
			    tu = {"url": searchResults.results.BATCHSEARCHRESPONSE.RESULTSET_WEB.RESULT[i].URL, "title": searchResults.results.BATCHSEARCHRESPONSE.RESULTSET_WEB.RESULT[i]["TITLE.HTML.HL"]};
			    markup += Y.Lang.substitute(liTemplate, tu);
			    tu = {};
			}
                }

                // close off the unordered list so it's ready to append to the DOM
                markup += '</ul>';
            } else {
                // For whatever reason, there were no results. Message to user. 
                markup = "<p>No results returned.</p>";
            }

            // hook the markup on the DOM now that it's complete
            resultsOutput.set("innerHTML", markup);
        },

        //
        // This piece of code is called from the YQL request if there's an error.
        // All it does is ignore the error and all the parameters passed in and
        // sticks an error in the results container. Not much else important to
        // say about these two lines of code.
        //
        //  YUI 3 modules used: node
        //
        //  in:  None.
        //  out: None. Just sticks an error string in the results div. 
        //       We don't really care what the error was - we always have the 
        //       same string.
        // 
        showError = function()  {
            // grab the results div
            var resultsOutput = Y.Node.get('#ydn-search-module-results');
    
            // stick the error in the results div
            resultsOutput.set('innerHTML', '<p>There was an error fetching the search results.</p>');
        },

        //
        // This does the XDR from yql using the yql gallery module. We just tell it 
        // what query to make and what to do with the results and call it a day. 
        //
        //  YUI 3 modules used: node, Lang.substitute, io
        //
        //  in:  None. We just grab the data from the page. 
        //  out: None. Makes the XDR call and lets the callbacks handle
        //       things from there. 
        //
        fetchData = function(e, truesearch)  {
            // this stops the bubbling and such. not necessarily useful here
            // except that the shift+enter event will also fire the enter event
            // without this.
            e.halt(true);

            // these four vars pretty much just generate path. They strip off the 
            // prefix that we use on alpha (/ydndrafts) and strip off and filename 
            // that might be in the URL. The result is the prod path. 
            var regex = /^\/ydndrafts/i, pathend = location.pathname.lastIndexOf('/'), path = location.pathname.slice(0,pathend +1).replace(regex,''),

            // support for crippling search results. This was a requested feature 
            // and was **NOT** my idea. We test for the value passed in and the e.shiftKey
            // property just to be thorough. Hitting return will give back the 
            // incomplete results. The shift-return easter egg will give developers 
            // complete results.
            excluded = (truesearch || e.shiftKey)?'':' filetype:html',

            // this gets the user input for the search
            input = Y.get('#search-input'),

            // make the YQL call. Use the path var from above and what the user has typed 
            // into the input box
            yqlQuery = new Y.yql('select * from xml where url=\'http://developer.yahoo.com/homepage/docsearch.html?query=' + input.get("value") + '&path=' + encodeURIComponent(path) + '\'');

            // the function to be called when the query event (results returned) is fired
            yqlQuery.on('query', showSearchResults);
            // the function to be called if there's an error making the YQL call
            yqlQuery.on('error', showError);
        };

        //
        // This is called once the DOM has settled. It adds the initial module with 
        // the title and search box/button. 
        //
        //  YUI 3 modules used: node
        //
        //  in:  None.
        //  out: None. Adds the search box to the top of the YDN rack then attaches 
        //       the search functionality onto the search button. 
        //
        function init()  {
            // create the module with the search button and input field in the ydnRack
            var rackGrid = Y.Node.get('#ydnRack');
            rackGrid.prepend(boxInit);

            // hook the functionality onto the button's click
            Y.on('click', fetchData, "#search-button");

            // handle shift-return for truesearch
            Y.on('key', fetchData, '#search-input', 'down:13+shift', Y, true);

            // handle return on the search box
            Y.on('key', fetchData, '#search-input', 'down:13', Y, false);
        }

        // wait until the DOM stops wobbling before we start messing with it.
        var ulen = document.location.href.split('/');
        if(ulen.length >= 6)
	{
		urllevel = 1;
		var ucnt = 0;
		for(var i in ulen)
		{
			ucnt++;
			if(ucnt > 6)
				break;
			if(ucnt <=2 || ulen[i] != "")
			urlbase += ulen[i] + '/';
		}
	}
	Y.on('domready', init);

    });

})();
