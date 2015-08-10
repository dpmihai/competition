$(document).ready(function() {
    $(".tooltip").tipTip({maxWidth: "auto"});
});

$(function(){
    $("ul#ticker").liScroll({travelocity: 0.03});
}); 

jQuery.fn.slide = function() {	
	$(".searchPanel").slideToggle("fast");	
	$(".trigger").toggleClass("active");
	$(".searchName").focus();
	return false;
}

$(document).ready(function(){
	$(".trigger").click(function(){	       
	       $(this).slide();
	       return false;
	});
});
