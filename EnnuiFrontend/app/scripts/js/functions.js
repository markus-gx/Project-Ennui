(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "https://connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));

window.fbAsyncInit = function() {
    FB.init({
        appId      : '967640606679290',
        cookie     : true,  // enable cookies to allow the server to access 
                            // the session
        xfbml      : true,  // parse social plugins on this page
        version    : 'v2.8' // use graph api version 2.8
    });
}

function getJSON(addresse,func,token){
    if(token != "" && token != undefined){
        $.ajax({
            headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'bearer ' + token
            },
            'type': 'GET',
            'url': addresse,
            'dataType': 'json',
            'success': func
            });
    }
    else{
         $.getJSON(addresse,func);
    }
}

function initMap(){
    var defaultBounds = new google.maps.LatLngBounds(new google.maps.LatLng(48.210033, 16.363449), new google.maps.LatLng(48.210033, 16.363449));

    var options = {
        bounds: defaultBounds,
        types: ['geocode']
    };
}

function activateSearchBoxForEvents(element,lat,lng,setter){
    var defaultBounds = new google.maps.LatLngBounds(new google.maps.LatLng(lat, lng), new google.maps.LatLng(lat, lng));
    var options = {
        bounds: defaultBounds,
        types: ['geocode']
    };

    var autocomplete = new google.maps.places.Autocomplete(element, options);

    google.maps.event.addListener(autocomplete, 'place_changed', function () {
        var place = autocomplete.getPlace();
        if (typeof place == "undefined") {
            window.alert("Nicht gefunden!");
        }
		else{
            console.log("palce: ");
            console.log(place);
			setter.setPlace(place);
		}
    });
}

function createMapOnObject(element,lati,lngi,markerName,editable,updateFunction){
    var map = new google.maps.Map(element, {
        center: {lat: lati, lng: lngi},
        zoom: 15,
    });
    if(editable == false){
        map = new google.maps.Map(element, {
            center: {lat: lati, lng: lngi},
            zoom: 15,
            disableDefaultUI: true,
            draggable: false,
            maxZoom: 15,
            minZoom: 15,
            scrollwheel: false

        });
    }
    var myLatlng = new google.maps.LatLng(parseFloat(lati),parseFloat(lngi));
    var marker = new google.maps.Marker({
          position: myLatlng,
          map: map,
          title: markerName
    });
    if(editable){
        google.maps.event.addListener(map, 'click', function(event) {
            var latlng = new google.maps.LatLng(parseFloat(event.latLng.lat()),parseFloat(event.latLng.lng()));
            marker.setMap(null);
            marker = new google.maps.Marker({
                position: latlng, 
                map: map,
                title: document.getElementById("eventCreationName").value
            });
            getJSON("https://maps.googleapis.com/maps/api/geocode/json?latlng="+event.latLng.lat() + ","+event.latLng.lng(),function(response){updateFunction(response); console.log(response);});
        });
        var input = document.createElement('input');
        input.id = "pac-input";
        input.type ="text";
        input.className ="controls";
        input.setAttribute("placeholder","Search for Place");
        document.getElementsByClassName("eventCreationContainer")[0].appendChild(input);
        var searchBox = new google.maps.places.SearchBox(input);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

        // Bias the SearchBox results towards current map's viewport.
        map.addListener('bounds_changed', function() {
          searchBox.setBounds(map.getBounds());
        });

        searchBox.addListener('places_changed', function() {
          var places = searchBox.getPlaces();

          if (places.length == 0) {
            return;
          }
          // For each place, get the icon, name and location.
          var bounds = new google.maps.LatLngBounds();
          places.forEach(function(place) {
            if (!place.geometry) {
              console.log("Returned place contains no geometry");
              return;
            }
            if (place.geometry.viewport) {
              // Only geocodes have viewport.
              bounds.union(place.geometry.viewport);
            } else {
              bounds.extend(place.geometry.location);
            }
          });
          map.fitBounds(bounds);
        });
    }
}

function post(url,data,success,specified,token){
    $.ajax({
    headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'bearer ' + token
    },
    'type': 'POST',
    'url': url,
    'data': JSON.stringify(data),
    'dataType': 'json',
    'success': function(response){
        success(response,specified);
    }
    });
}

function generateProgressBar(element,text,textValue,maxValue){
    var bar = new ProgressBar.Circle(element, {
    color: '#aaa',
    // This has to be the same size as the maximum width to
    // prevent clipping
    strokeWidth: 4,
    trailWidth: 1,
    easing: 'easeInOut',
    duration: 1400,
    text: {
        autoStyleContainer: false
    },
    from: { color: '#aaa', width: 1 },
    to: { color: '#333', width: 5 },
    // Set default step function for all animate calls
    step: function(state, circle) {
        circle.path.setAttribute('stroke', state.color);
        circle.path.setAttribute('stroke-width', state.width);
        var percent = round(((100 / maxValue) * textValue),0);
        var value = circle.value() * percent;
        if (value === 0) {
        circle.setText('');
        } else {
        circle.setText(text + "<br/>" + percent +"%");
        }

    }
    });
    bar.text.style.fontSize = '1.69rem';
    bar.animate(((100 / maxValue) * textValue)/100);  // Number from 0.0 to 1.0
}

function round(Zahl, n){
  var faktor = Math.pow(10, n);
  return Math.round(Zahl*faktor)/faktor;
};

function sendNotification(title,msg,ico) {
    // Check if browser supports
    if (!("Notification" in window)) {
        alert("This browser does not support desktop notification");
    }
    else if (Notification.permission === "granted") {
        var notification = new Notification(title,{
            body: msg,
            icon: ico
        });
        setTimeout(function() {
            notification.close();
        }, 5000);
    }
    else if (Notification.permission !== 'denied') {
        Notification.requestPermission(function (permission) {
        if (permission === "granted") {
            var notification = new Notification(title,{
                body: msg,
                icon: ico
            });
            setTimeout(function() {
                notification.close();
            }, 5000);
        }
        });
    }
}
function getItemWhenDocumentReady(id) {
    $(document).ready(function() {
        console.log( document.getElementsByClassName(id) );
        return document.getElementsByClassName(id);
            
    });
}
