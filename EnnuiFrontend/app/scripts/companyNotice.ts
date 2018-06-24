let wind: any = window;
if (typeof wind.console !== 'undefined' && typeof wind.console.log !== 'undefined') {
	wind.console.log('Crafted and created by Markus Geilehner, Simon Gutenbrunner and Martin Singer. Visit: geilehner.at/markus and simon.gutenbrunner.at');
} else {
	wind.console = {};
	wind.console.log = wind.console.error = function() {};
}
