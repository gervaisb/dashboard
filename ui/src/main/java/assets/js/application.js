function load($loadable) {
	$loadable.load($loadable.attr('data-source'), function( response, status, xhr ) {
		  if ( status == "error" ) {
			    $loadable.html('<div class="alert alert-error">'+
			    		'<h4>Something went wrong</h4>'+
			    		'<p>We failed to serve the requested fragment.<p>'+
			    		'<p>Cause : '+$loadable.attr('data-source')+'; '+xhr.status+' '+xhr.statusText+'</p>'+
			    		'</div>');
		  }});
}
    
function refreshLater($refreshable) {
	var minimalRefreshDelay = 3000; // millis
   	var delay = Math.floor((Math.random()*10000)+minimalRefreshDelay);
   	setTimeout(function(){
   		refreshAndReplan($refreshable);
   	}, delay);
}
    
function refreshAndReplan($refreshable) {
	load($refreshable);
	refreshLater($refreshable);
}

function initialize($context) {
	$('[data-source]', $context).each(function(){
		load($(this));		
	}).filter('[data-refresh="true"]').each(function(){
		refreshLater($(this));
	});
};

$(function(){
	initialize($(document));
});

/**
 * Equal Heights Plugin
 * Equalize the heights of elements. Great for columns or any elements
 * that need to be the same size (floats, etc).
 * 
 * Version 1.0
 * Updated 12/10/2008
 *
 * Copyright (c) 2008 Rob Glazebrook (cssnewbie.com) 
 *
 * Usage: $(object).equalHeights([minHeight], [maxHeight]);
 * 
 * Example 1: $(".cols").equalHeights(); Sets all columns to the same height.
 * Example 2: $(".cols").equalHeights(400); Sets all cols to at least 400px tall.
 * Example 3: $(".cols").equalHeights(100,300); Cols are at least 100 but no more
 * than 300 pixels tall. Elements with too much content will gain a scrollbar.
 * 
 */

(function($) {
	$.fn.equalHeights = function(minHeight, maxHeight) {
		tallest = (minHeight) ? minHeight : 0;
		this.each(function() {
			if($(this).height() > tallest) {
				tallest = $(this).height();
			}
		});
		if((maxHeight) && tallest > maxHeight) tallest = maxHeight;
		return this.each(function() {
			$(this).height(tallest).css("overflow","auto");
		});
	}
})(jQuery);