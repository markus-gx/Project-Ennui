import { EventDTO } from './../DTOs/EventDTO';
import { Holder } from './../DTOs/Holder';
import { GeneralFunctions } from './../GeneralFunctions';
import { Controller } from '../lib/Controller';
import { Filter } from '../Filter';
import { UserDTO } from '../DTOs/UserDTO';

declare function post(url:String,data:any,success:Function,pageSpecifiedLogin:Function,token: String): void;
declare function getJSON(addresse:string,func:Function, token: String): void;
declare function activateSearchBoxForEvents(element:HTMLInputElement,lat:number,lng:number,setter:Filter): void;
declare function createMapOnObject(element:HTMLElement,lati:number,lngi:number,markerName:string,editable:boolean,placeElement:Function):void;


let eventMarker = 0;
let filterOptions: any = {};

let EventFilter:Filter = new Filter("",25,null,null,"",[]);
let weekday = new Array(7);
weekday[0] =  "Sunday";
weekday[1] = "Monday";
weekday[2] = "Tuesday";
weekday[3] = "Wednesday";
weekday[4] = "Thursday";
weekday[5] = "Friday";
weekday[6] = "Saturday";
let categories = ["Party - Nightlife","Music","Art - Culture","Literatur","Comedy","Food","Games","Health","Shopping","Home - Garden","Sport","Theatre","Sonstiges"];

export class EventStartController extends Controller {
        static selector: string = '#header_events';

        constructor(element: HTMLElement) {
        super(element);
        
        element.addEventListener('click',function(){
            initializeEventPage();    
        });
        let eventHeadline = document.getElementById("events_headline");
        eventHeadline.addEventListener('click',function(){
            initializeEventPage();    
        });
    }
    static initialize(){
        initializeEventPage();
    }
}

function initializeEventPage(){
    //Reset Filter
    EventFilter =  new Filter("",25,null,null,"",[]);
    GeneralFunctions.deleteAllContentAndCreateNewMain("eventPageMain");
    GeneralFunctions.generateMainContentPage("event",userLoggedInCallback,true,showFavoredEvents,showSubmissions,showCalendarView,generateExtraEventContent);
    FB.getLoginStatus(userLoggedInCallback);
    GeneralFunctions.showLoadingBar();
    getNearbyEvents(GeneralFunctions.usrLat,GeneralFunctions.usrLong,GeneralFunctions.usrCountryCode,EventFilter.getRadius(),null,null,[],generateEvents);
}

function getNearbyEvents(latitude:number,longitude:number,country:String,radius:number,start_time:Date,end_time:Date,categories:Array<string>,success:Function){
    let catRequest = "&categories=";
    if(categories != null && categories.length > 0){
        for(let i = 0; i < categories.length; i++){
            if(catRequest == "&categories="){
                catRequest = catRequest + categories[i];
            }
            else{
                catRequest = catRequest + "," + categories[i];
            }
        }
    }
    else{
        catRequest ="&categories="
    }
    filterOptions.lat = latitude;
    filterOptions.lng = longitude;
    filterOptions.country = country;
    filterOptions.radius = radius;
    filterOptions.startTime = start_time;
    filterOptions.endTime = end_time;
    filterOptions.categories = categories;
    filterOptions.catRequest = catRequest;

    getJSON(GeneralFunctions.backendServer + "/events?latitude=" + latitude + "&longitude="+longitude+"&country="+country +"&radius="+radius+
    "&startTime=" + start_time + "&endTime=" + end_time + catRequest,success,"");
    console.log("not logged");
}

function generateExtraEventContent(){


        let searchBox = document.createElement("input");
        searchBox.className = "searchBox";
        searchBox.setAttribute("type","text");
        searchBox.setAttribute("placeholder","Search for Events...");
        searchBox.addEventListener('keyup',function(){
            searchForEvents((<HTMLInputElement>searchBox).value);
        });
        document.getElementById("eventSearchDiv").appendChild(searchBox);

        let sideBar = document.getElementById("eventPageSideBar");
    let ortPara = document.createElement("p");
    ortPara.innerHTML = "- - - - - - - - - - Ort - - - - - - - - - -";
    ortPara.className = "sideBarHeadline";
    sideBar.appendChild(ortPara);

    let placeDiv = document.createElement("div");
    placeDiv.id="searchPlaceDiv";
        let placeSearch = document.createElement("input");
        placeSearch.id = "eventPlaceSearch";
        placeSearch.className = "searchBox";
        placeSearch.setAttribute("type","text");
        placeSearch.setAttribute("placeholder","Enter a place...");
        placeSearch.value = GeneralFunctions.usrCity + ", " + GeneralFunctions.usrCountryCode;
        activateSearchBoxForEvents(placeSearch,GeneralFunctions.usrLat,GeneralFunctions.usrLong,EventFilter);
        let placeIcon = document.createElement("span");
        placeIcon.className ="icon-location";
        placeDiv.appendChild(placeIcon);
        placeDiv.appendChild(placeSearch);

        let radiusRange = document.createElement("input");
        radiusRange.setAttribute("type","range");
        radiusRange.min = "1";
        radiusRange.max = "200";
        radiusRange.defaultValue = EventFilter.getRadius()+"";
        radiusRange.id="eventRadiusRange";
        radiusRange.className = "radiusRange";

        let radiusInput = document.createElement("input");
        radiusInput.setAttribute("type","text");
        radiusInput.value = radiusRange.value;
        radiusInput.disabled = true;
        radiusInput.id = "eventRadiusInput";

        radiusRange.addEventListener('input',function(){
            radiusInput.value = radiusRange.value;
            EventFilter.setRadius(+radiusInput.value);
        });
        placeDiv.appendChild(radiusRange);
        placeDiv.appendChild(radiusInput);
    sideBar.appendChild(placeDiv);

    let datePara = document.createElement("p");
    datePara.innerHTML = "- - - - - - - - - - Datum - - - - - - - - - -";
    datePara.className = "sideBarHeadline";
    sideBar.appendChild(datePara);

    let dateDiv = document.createElement("div");
    dateDiv.id="dateDiv";
        let table = document.createElement("table");
        let tr = document.createElement("tr");
        let fromSpan = document.createElement("td");
        fromSpan.innerHTML = "Vom: ";
        let td = document.createElement("td");
        let calendarFrom = document.createElement("input");
        calendarFrom.setAttribute("type","date");
        calendarFrom.addEventListener('change',function(){
            let date = calendarFrom.value;
            /*let unixTime = 0;
            if(date != "" ){
                unixTime = Date.parse(date) / 1000; 
            }*/
            EventFilter.setFrom(new Date(date));
        });
        tr.appendChild(fromSpan);
        td.appendChild(calendarFrom);
        tr.appendChild(td);

        let tr2 = document.createElement("tr");
        let toSpan = document.createElement("td");
        toSpan.innerHTML = "Bis: ";
        let td2 = document.createElement("td");
        let calendarTo = document.createElement("input");
        calendarTo.setAttribute("type","date");
        calendarTo.addEventListener('change',function(){
            let date = calendarTo.value;
            /*let unixTime = 0;
            if(date != "" ){
                unixTime = Date.parse(date) / 1000; 
            }*/
            EventFilter.setTo(new Date(date));
        });
        tr2.appendChild(toSpan);
        td2.appendChild(calendarTo);
        tr2.appendChild(td2);

        table.appendChild(tr);
        table.appendChild(tr2);

        dateDiv.appendChild(table);
    sideBar.appendChild(dateDiv);

    let catPara = document.createElement("p");
    catPara.innerHTML = "- - - - - - - - - - Kategorien - - - - - - - - - -";
    catPara.className = "sideBarHeadline";
    sideBar.appendChild(catPara);
        let catDiv = document.createElement("div");
        catDiv.className = "categorieContainer";
            for(let i = 0; i < categories.length; i++){
                let cat = document.createElement("div");
                cat.className = "categoryTag";
                cat.id=categories[i].toLowerCase();
                cat.innerHTML = categories[i];
                cat.addEventListener('click',function(){
                    if(cat.className == "categoryTag")
                    {
                        cat.className = "categoryTag selected";
                        EventFilter.addTag(categories[i].toLowerCase());
                    }
                    else{
                        cat.className = "categoryTag";
                        EventFilter.removeTag(categories[i].toLowerCase());
                    }
                });
                catDiv.appendChild(cat);
            }
            sideBar.appendChild(catDiv);

    let apply = document.createElement("div");
    apply.id ="eventApplyButton";
    apply.className = "applyButton";
    let spanapply = document.createElement("span");
    spanapply.innerHTML = "Apply Filter";
    spanapply.addEventListener('click',function(){
        ApplyFilter();
    });
    apply.appendChild(spanapply);
    sideBar.appendChild(apply);

    let mainContent = document.getElementsByClassName("mainContentContainer")[0];
    let addButton = document.createElement("div");
    addButton.className = "roundButton hidden";
    addButton.id = "eventAddButton";
    addButton.addEventListener('click',function(){
        if(GeneralFunctions.userDto != null && GeneralFunctions.userDto.name.length > 0){
            let content = document.getElementsByClassName("contentContainer")[0];
            createEventCreationPage();
            content.className = content.className + " fadeOut";
            setTimeout(function() {
                content.className = "contentContainer hidden";
            }, 200);
        }
    });
    let addspan = document.createElement("span");
    addspan.innerHTML = "+";
    addButton.appendChild(addspan);
    mainContent.appendChild(addButton);
}

function createEventEditPage(dto:EventDTO){
    console.log(dto);
        let page = document.getElementById("eventPageMain");

        let eventContainer  = document.createElement("div");
        eventContainer.className = "eventCreationContainer";
        
        let headerContainer = document.createElement("div");
        headerContainer.className="eventCreationHeaderContainer";
        headerContainer.id ="eventCreationHeaderContainer";
        headerContainer.style.backgroundImage = "url('/images/imgNotFound.png')";
        let backButton = document.createElement("div");
        backButton.className="icon-back";
        backButton.addEventListener('click',function(){ 

            let content = document.getElementsByClassName("contentContainer")[0];
            content.className = "contentContainer";
            eventContainer.className = eventContainer.className + " fadeOutt";
            setTimeout(function() {
                eventContainer.className = "eventCreationContainer hidden";
            }, 200);
        });
        headerContainer.appendChild(backButton);
        let coverUrl = "/images/imgNotFound.png";
        headerContainer.addEventListener('click',function(e:Event){
            if(e.target !== this){
                return;
            }
            let url = prompt("Please enter the cover URL","www.image-example.com/image.jpg");
            if(url != null && url != ""){
                GeneralFunctions.testImage(url,function(){headerContainer.style.backgroundImage = "url('"+url+"')"; coverUrl = url;},function(){alert("Image not found!");});
            }
        });
            let headerContent = document.createElement("div");
            headerContent.className = "headerContent";
            let ename = document.createElement("input");
            
            let startDate = new Date(dto.starttime);
            let startHours = startDate.getHours();
            let startMinutes = "0" + startDate.getMinutes();
            let startSeconds = "0" + startDate.getSeconds();
            let endDate = new Date(dto.endtime);
            let endHours = endDate.getHours();
            let endMinutes = "0" + endDate.getMinutes();
            let endSeconds = "0" + endDate.getSeconds();
            
            ename.placeholder = "Event-Name (Click to edit)";
            ename.type ="text";
            ename.value=dto.name;
            ename.id = "eventCreationName";
            headerContent.appendChild(ename);
            let owner = document.createElement("h3");
            owner.innerHTML = "by " + GeneralFunctions.userDto.name;
            owner.id ="eventCreationOwner";
            headerContent.appendChild(owner);
            let startTime = document.createElement("input");
            startTime.type="date";
            startTime.id = "eventCreationStartTime";
            let startDateInput = String(startDate.getFullYear())+ "-" + String(startDate.getMonth()+1)+"-" +String(startDate.getDate());
            startTime.value= startDateInput;
            console.log(startTime.value);
            let startTimeTime = document.createElement("input");
            startTimeTime.type="time";
            startTimeTime.id ="eventCreationStartTimeTime";
            var timeStart;
            if(startHours < 10){
                timeStart="0"+ String(startHours) +":" +String(startMinutes);
            }
            else{
                timeStart = String(startHours) +":" + String(startMinutes);
            }

            console.log(timeStart);
            startTimeTime.value= timeStart;
            headerContent.appendChild(startTime);
            headerContent.appendChild(startTimeTime);
            let endTime = document.createElement("input");
            endTime.type="date";
            endTime.id = "eventCreationEndTime";
            let endDateInput = String(endDate.getFullYear())+ "-" + String(endDate.getMonth()+1)+"-" +String(endDate.getDate());
            endTime.value= endDateInput;
            let endTimeTime = document.createElement("input");
            endTimeTime.type = "time";
            endTimeTime.id = "eventCreationEndTimeTime";
            if(endHours < 10){
                endTimeTime.value="0"+ String(endHours) +":" +String(endMinutes);
            }
            else{
                 endTimeTime.value= String(endHours) +":" +String(endMinutes);
            }
            headerContent.appendChild(endTime);
            headerContent.appendChild(endTimeTime);
            let website = document.createElement("input");
            website.placeholder = "Enter Webpage Link (#) for none";
            website.type ="text";
            website.id="eventCreationWebsite";
            website.value=dto.getWebsiteURL();
            headerContent.appendChild(website);

            let catDiv = document.createElement("div");
            console.log(categories);
            catDiv.className ="categorieContainer clearfix";
            for(let i = 0; i < categories.length; i++){
                let cat = document.createElement("div");
                if(categories[i].toLowerCase() == convertCategoryReverse([dto.category])){
                    cat.className = "categoryTag selected creation";
                }
                else{
                    cat.className = "categoryTag";
                }
                cat.innerHTML = categories[i];
                cat.id = "creation" + categories[i].toLowerCase();
                cat.addEventListener('click',function(){
                    if(cat.className == "categoryTag")
                    {
                        cat.className = "categoryTag selected creation";
                        EventFilter.addTag(categories[i].toLowerCase());
                    }
                    else{
                        cat.className = "categoryTag";
                        EventFilter.removeTag(categories[i].toLowerCase());
                    }
                });
                catDiv.appendChild(cat);
            }
            headerContent.appendChild(catDiv);
            let finishButton = document.createElement("div");
                finishButton.className = "finishButton";
                let spanHak = document.createElement("span");
                spanHak.innerHTML = "✔";
                finishButton.appendChild(spanHak);
                finishButton.addEventListener('click',function(){
                    if(ename.value.length > 0 && startTime.value.length > 0 && startTimeTime.value.length > 0 && endTime.value.length > 0 && endTimeTime.value.length > 0 && 
                    document.getElementsByClassName("categoryTag selected creation").length > 0 && website.value.length > 0 && descriptionContainer.value.length > 0){
                        let categoriesArr = new Array<String>();
                        for(let j = 0; j < document.getElementsByClassName("categoryTag selected creation").length; j++){
                            categoriesArr[categoriesArr.length] = document.getElementsByClassName("categoryTag selected creation")[j].innerHTML.toLowerCase();
                        }
                        let country;
                        let city;
                        let zip;
                        let street;
                        let streetnr;
                        let locLat;
                        let locLong;
                        let placeName;
                        if(GeneralFunctions.creationPlace != null){
                            for (let j = 0; j < GeneralFunctions.creationPlace.results[0].address_components.length; j++) {
                                if( GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'country'){
                                    country =  GeneralFunctions.creationPlace.results[0].address_components[j].short_name;       
                                }
                                if( GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'locality'){
                                    city =  GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                                }
                                if(GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'postal_code'){
                                    zip = GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                                }
                                if(GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'route'){
                                    street = GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                                }
                                if(GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'street_number'){
                                    streetnr = GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                                }
                            }
                            locLat = GeneralFunctions.creationPlace.results[0].geometry.location.lat;
                            locLong = GeneralFunctions.creationPlace.results[0].geometry.location.lng;
                            placeName =GeneralFunctions.creationPlace.results[0].formatted_address;
                        }
                        else{
                            country = dto.country;
                            city=dto.city;
                            zip=dto.zip;
                            street=dto.street;
                            locLat = dto.latitude;
                            locLong = dto.longitude;
                            placeName = dto.placeName;
                        }
                        let date = new Date(startTime.value);
                        date.setHours(parseInt(startTimeTime.value.split(":")[0]));
                        date.setMinutes(parseInt(startTimeTime.value.split(":")[1]));
                        //let stime = date.getTime()/1000 + (parseInt(startTimeTime.value.split(":")[0])*60*60) + (parseInt(startTimeTime.value.split(":")[1])*60);
                        let edate = new Date(endTime.value);
                        edate.setHours(parseInt(endTimeTime.value.split(":")[0]));
                        edate.setMinutes(parseInt(endTimeTime.value.split(":")[1]));
                        //let etime = edate.getTime()/1000 + (parseInt(endTimeTime.value.split(":")[0])*60*60) + (parseInt(endTimeTime.value.split(":")[1])*60);
                        post(GeneralFunctions.backendServer + "/events/edit",{
                            id: dto.id,
                            name:ename.value,
                            ownerName:GeneralFunctions.userDto.name,
                            description:descriptionContainer.value,
                            category:convertCategory(categoriesArr),
                            ticketUri:website.value,
                            coverUrl:coverUrl,
                            country:country,
                            starttime:date,
                            endtime:edate,
                            city:city,
                            postal:zip,
                            latitude:locLat,
                            longitude:locLong,
                            street:(street+" " +streetnr),place_name:placeName,
                            fbToken:GeneralFunctions.usrAccessToken
                        },function(response:any){
                            if(response.success == true){
                                let content = document.getElementsByClassName("contentContainer")[0];
                                content.className = "contentContainer";
                                eventContainer.className = eventContainer.className + " fadeOutt";
                                setTimeout(function() {
                                    eventContainer.className = "eventCreationContainer hidden";
                                }, 200);
                            }
                            else{
                                alert("something went wrong!");
                            }
                        },null,GeneralFunctions.usrAccessToken);
                    }
                    else{
                        alert("Not all fields are filled!");
                    }
                });
            headerContainer.appendChild(headerContent);
            headerContainer.appendChild(finishButton);
        eventContainer.appendChild(headerContainer);

        let descriptionContainer = document.createElement("textarea");
        descriptionContainer.className = "eventCreationDescription";
        descriptionContainer.id = "eventCreationDescription";
        descriptionContainer.value=dto.description;
        descriptionContainer.placeholder = "Description (click to edit)";
        eventContainer.appendChild(descriptionContainer);

        let rightColumn = document.createElement("div");
        rightColumn.className = "rightColumn";

        let mapWrapper = document.createElement("div");
        mapWrapper.className= "mapWrapper";
        let mapLocation = document.createElement("div");
        mapLocation.innerHTML = "<div class='mapLocationHeader'>Adresse</div>" +
        "<div class='mapLocationAdress'>" + dto.street +  ", " + dto.city + ", " + dto.country + "</div>";
        mapLocation.className = "mapLocation";
        mapLocation.id = "mapCreationLocation";
        mapWrapper.appendChild(mapLocation);
        let mapContainer = document.createElement("div");
        mapContainer.id="eventCreationLocationMap";
        mapContainer.className="eventCreationLocationMap";
        mapWrapper.appendChild(mapContainer);
        rightColumn.appendChild(mapWrapper);
        eventContainer.appendChild(rightColumn);
        

        page.appendChild(eventContainer);
        createMapOnObject(mapContainer,dto.latitude,dto.longitude,"Event-Name",true,function(places:any){
            mapLocation.innerHTML = "<div class='mapLocationHeader'>Adresse</div>" +
            "<div class='mapLocationAdress'>"+ places.results[0].formatted_address+ "</div>"
            GeneralFunctions.creationPlace = places;
        });
}

function createEventCreationPage(){
    if(document.getElementsByClassName("eventCreationContainer").length == 0){
        let page = document.getElementById("eventPageMain");

        let eventContainer  = document.createElement("div");
        eventContainer.className = "eventCreationContainer";
        
        let headerContainer = document.createElement("div");
        headerContainer.className="eventCreationHeaderContainer";
        headerContainer.id ="eventCreationHeaderContainer";
        headerContainer.style.backgroundImage = "url('/images/imgNotFound.png')";
        let backButton = document.createElement("div");
        backButton.className="icon-back";
        backButton.addEventListener('click',function(){ 
            let content = document.getElementsByClassName("contentContainer")[0];
            content.className = "contentContainer";
            eventContainer.className = eventContainer.className + " fadeOutt";
            setTimeout(function() {
                eventContainer.className = "eventCreationContainer hidden";
            }, 200);
        });
        headerContainer.appendChild(backButton);
        let coverUrl = "/images/imgNotFound.png";
        headerContainer.addEventListener('click',function(e:Event){
            if(e.target !== this){
                return;
            }
            let url = prompt("Please enter the cover URL","www.image-example.com/image.jpg");
            if(url != null && url != ""){
                GeneralFunctions.testImage(url,function(){headerContainer.style.backgroundImage = "url('"+url+"')"; coverUrl = url;},function(){alert("Image not found!");});
            }
        });
            let headerContent = document.createElement("div");
            headerContent.className = "headerContent";
            let ename = document.createElement("input");
            
            ename.placeholder = "Event-Name (Click to edit)";
            ename.type ="text";
            ename.id = "eventCreationName";
            headerContent.appendChild(ename);
            let owner = document.createElement("h3");
            owner.innerHTML = "by " + GeneralFunctions.userDto.name;
            owner.id ="eventCreationOwner";
            headerContent.appendChild(owner);
            let startTime = document.createElement("input");
            startTime.type="date";
            startTime.id = "eventCreationStartTime";
            let startTimeTime = document.createElement("input");
            startTimeTime.type="time";
            startTimeTime.id ="eventCreationStartTimeTime";
            headerContent.appendChild(startTime);
            headerContent.appendChild(startTimeTime);
            let endTime = document.createElement("input");
            endTime.type="date";
            endTime.id = "eventCreationEndTime";
            let endTimeTime = document.createElement("input");
            endTimeTime.type = "time";
            endTimeTime.id = "eventCreationEndTimeTime";
            headerContent.appendChild(endTime);
            headerContent.appendChild(endTimeTime);
            let website = document.createElement("input");
            website.placeholder = "Enter Webpage Link (#) for none";
            website.type ="text";
            website.id="eventCreationWebsite";
            headerContent.appendChild(website);

            let catDiv = document.createElement("div");
            catDiv.className ="categorieContainer clearfix";
            for(let i = 0; i < categories.length; i++){
                let cat = document.createElement("div");
                cat.className = "categoryTag";
                cat.innerHTML = categories[i];
                cat.id = "creation" + categories[i].toLowerCase();
                cat.addEventListener('click',function(){
                    if(cat.className == "categoryTag")
                    {
                        cat.className = "categoryTag selected creation";
                        EventFilter.addTag(categories[i].toLowerCase());
                    }
                    else{
                        cat.className = "categoryTag";
                        EventFilter.removeTag(categories[i].toLowerCase());
                    }
                });
                catDiv.appendChild(cat);
            }
            headerContent.appendChild(catDiv);
            let finishButton = document.createElement("div");
                finishButton.className = "finishButton";
                let spanHak = document.createElement("span");
                spanHak.innerHTML = "✔";
                finishButton.appendChild(spanHak);
                finishButton.addEventListener('click',function(){
                    if(ename.value.length > 0 && startTime.value.length > 0 && startTimeTime.value.length > 0 && endTime.value.length > 0 && endTimeTime.value.length > 0 && 
                    document.getElementsByClassName("categoryTag selected creation").length > 0 && website.value.length > 0 && descriptionContainer.value.length > 0 && GeneralFunctions.creationPlace != null && locationNameInput.value.length > 0){
                        let categoriesArr = new Array<String>();
                        for(let j = 0; j < document.getElementsByClassName("categoryTag selected creation").length; j++){
                            categoriesArr[categoriesArr.length] = document.getElementsByClassName("categoryTag selected creation")[j].innerHTML.toLowerCase();
                        }
                        let country;
                        let city;
                        let zip;
                        let street;
                        let streetnr;
                        let placeName;
                        for (let j = 0; j < GeneralFunctions.creationPlace.results[0].address_components.length; j++) {
                            if( GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'country'){
                                country =  GeneralFunctions.creationPlace.results[0].address_components[j].short_name;       
                            }
                            if( GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'locality'){
                                city =  GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                            }
                            if(GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'postal_code'){
                                zip = GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                            }
                            if(GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'route'){
                                street = GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                            }
                            if(GeneralFunctions.creationPlace.results[0].address_components[j].types[0] == 'street_number'){
                                streetnr = GeneralFunctions.creationPlace.results[0].address_components[j].long_name;
                            }
                        }
                        let date = new Date(startTime.value);
                        date.setHours(parseInt(startTimeTime.value.split(":")[0]));
                        date.setMinutes(parseInt(startTimeTime.value.split(":")[1]));
                        //let stime = date.getTime()/1000 + (parseInt(startTimeTime.value.split(":")[0])*60*60) + (parseInt(startTimeTime.value.split(":")[1])*60);
                        let edate = new Date(endTime.value);
                        edate.setHours(parseInt(endTimeTime.value.split(":")[0]));
                        edate.setMinutes(parseInt(endTimeTime.value.split(":")[1]));
                        //let etime = edate.getTime()/1000 + (parseInt(endTimeTime.value.split(":")[0])*60*60) + (parseInt(endTimeTime.value.split(":")[1])*60);
                        placeName = locationNameInput.value;
                        post(GeneralFunctions.backendServer + "/events/add",{
                            name:ename.value,
                            ownerName:GeneralFunctions.userDto.name,
                            description:descriptionContainer.value,
                            category:convertCategory(categoriesArr),
                            ticketUri:website.value,
                            coverUrl:coverUrl,
                            country:country,
                            starttime:date,
                            endtime:edate,
                            city:city,
                            placeName: placeName,
                            postal:zip,
                            latitude:GeneralFunctions.creationPlace.results[0].geometry.location.lat,
                            longitude:GeneralFunctions.creationPlace.results[0].geometry.location.lng,
                            street:(street+" " +streetnr),place_name:GeneralFunctions.creationPlace.results[0].formatted_address,
                            fbToken:GeneralFunctions.usrAccessToken
                        },function(response:any){
                            if(response.success == true){
                                let content = document.getElementsByClassName("contentContainer")[0];
                                content.className = "contentContainer";
                                eventContainer.className = eventContainer.className + " fadeOutt";
                                setTimeout(function() {
                                    eventContainer.className = "eventCreationContainer hidden";
                                }, 200);
                            }
                            else{
                                alert("something went wrong!");
                            }
                        },null,GeneralFunctions.usrAccessToken);
                    }
                    else{
                        alert("Not all fields are filled!");
                    }
                });
            headerContainer.appendChild(headerContent);
            headerContainer.appendChild(finishButton);
        eventContainer.appendChild(headerContainer);

        let descriptionContainer = document.createElement("textarea");
        descriptionContainer.className = "eventCreationDescription";
        descriptionContainer.id = "eventCreationDescription";
        descriptionContainer.placeholder = "Description (click to edit)";
        eventContainer.appendChild(descriptionContainer);

        let rightColumn = document.createElement("div");
        rightColumn.className = "rightColumn";

        let mapWrapper = document.createElement("div");
        mapWrapper.className= "mapWrapper";
        let locationNameInput = document.createElement("input");
        locationNameInput.className="locationNameInput";
        locationNameInput.placeholder = "Location Name (click to edit)";
        locationNameInput.type = "text";
        mapWrapper.appendChild(locationNameInput);
        let mapLocation = document.createElement("div");
        mapLocation.innerHTML = "<div class='mapLocationHeader'>Adresse</div>" +
        "<div class='mapLocationAdress'>" + "Street" + " " + "PLZ" + " " + GeneralFunctions.usrCity + "</div>";
        mapLocation.className = "mapLocation";
        mapLocation.id = "mapCreationLocation";
        mapWrapper.appendChild(mapLocation);
        let mapContainer = document.createElement("div");
        mapContainer.id="eventCreationLocationMap";
        mapContainer.className="eventCreationLocationMap";
        mapWrapper.appendChild(mapContainer);
        rightColumn.appendChild(mapWrapper);
        eventContainer.appendChild(rightColumn);
        

        page.appendChild(eventContainer);
        createMapOnObject(mapContainer,GeneralFunctions.usrLat,GeneralFunctions.usrLong,"Event-Name",true,function(places:any){
            mapLocation.innerHTML = "<div class='mapLocationHeader'>Adresse</div>" +
            "<div class='mapLocationAdress'>"+ places.results[0].formatted_address+ "</div>"
            GeneralFunctions.creationPlace = places;
        });
    }
    else{
        document.getElementsByClassName("eventCreationContainer")[0].className = "eventCreationContainer";
    }
}

//let categories = ["Party - Nightlife","Music","Art - Culture","Literatur","Comedy","Food","Games","Health","Shopping","Home - Garden","Sport","Theatre","Sonstiges"];
function convertCategory(catArray: Array<String>){
   let cat = catArray[0];
   switch(cat){
       case "party - nightlife": return "PARTY";
       case "music": return "MUSIC";
       case "art - culture": return "ART";
       case "literatur": return "LITERATUR";
       case "comedy": return "COMEDY";
       case "food": return "FOOD";
       case "games": return "GAMES";
       case "health": return "HEALTH";
       case "shopping": return "SHOPPING";
       case "home - garden": return "HOME_GARDEN";
       case "sport": return "SPORT";
       case "theatre": return "THEATRE";
       case "sonstiges": return "OTHERS";
       default: return "OTHERS";
   }
}
function convertCategoryReverse(catArray: Array<String>){
   let cat = catArray[0];
   switch(cat){
       case "PARTY": return "party - nightlife";
       case "MUSIC": return "music";
       case "ART": return "art - culture";
       case "LITERATUR": return "literatur";
       case "COMEDY": return "comedy";
       case "FOOD": return "food";
       case "GAMES": return "games";
       case "HEALTH": return "health";
       case "SHOPPING": return "shopping";
       case "HOME_GARDEN": return "home - garden";
       case "SPORT": return "sport";
       case "THEATRE": return "theatre";
       case "OTHERS": return "sonstiges";
       default: return "sonstiges";
   }
}

function userLoggedInCallback(response: any){
    if(response.status == "connected"){
        document.getElementById("fbLoginBtn").style.display = "none";
        post(GeneralFunctions.backendServer + "/users/login",{},GeneralFunctions.userFinallyLoggedIn,pageSpecifiedLogin,response.authResponse.accessToken);

        document.getElementById("fbLoginBtn").style.display = "none";
        document.getElementById("userProfile").style.display = "inline-block";
        GeneralFunctions.usrAccessToken = response.authResponse.accessToken;
    }
    else{
        document.getElementById("fbLoginBtn").style.display = "inline-block";
        document.getElementById("userProfile").style.display = "none";
    }
}

function pageSpecifiedLogin(userData: any){
    markFavoredEvents(GeneralFunctions.userDto.favoredEvents);
    document.getElementById('eventAddButton').className = "roundButton";
    let eventItems = document.getElementsByClassName("eventItem");
    for (var i = 0; i < eventItems.length; i++) {
        let deleteBtn = document.createElement("div");
        deleteBtn.className = "deleteGameBtn hidden";
        deleteBtn.innerHTML = "X";
        eventItems[i].appendChild(deleteBtn);

        if(userData.admin == true) {
            deleteBtn.className="deleteGameBtn";
            let eventItem = eventItems[i];
            let eventid = eventItem.getAttribute("value");
            deleteBtn.addEventListener('click', function() {
                post(GeneralFunctions.backendServer + "/events/delete/" + eventid, { }, function (data: any) { console.log(data); },null,GeneralFunctions.usrAccessToken);
                eventItem.className = "eventItem hidden";
                alert(eventid);
            });
        }
    }
}

function markFavoredEvents(events:any){
    if(events != null){
        let items = document.getElementsByClassName("eventItem");
        for(let i = 0; i < items.length; i++){
            if(GeneralFunctions.isItemFavored(events,items[i].getAttribute("value"))){
                for(let z = 0; z < items[i].childNodes.length; z++){
                    if((<any>items[i].childNodes[z]).className == "icon-heart"){
                        (<any>items[i].childNodes[z]).className = (<any>items[i].childNodes[z]).className + " favored";
                    }
                }
            }
        }
    }
}

function showFavoredEvents(){
    deleteAllEvents();
    let holder = new Holder<EventDTO>();
    holder.result = GeneralFunctions.userDto.favoredEvents;
    generateEvents(holder);
    GeneralFunctions.activateIcons();
    markFavoredEvents(GeneralFunctions.userDto.favoredEvents);
    document.getElementById("eventResultH3").innerHTML = "Your favored events";
    document.getElementById("userMenu").style.maxHeight = "0px";
    document.getElementById("recommendationHeader").style.display = "none";
    document.getElementById("recommendedContainer").style.display = "none";
}
function showCalendarView(){
    deleteAll();
    let mainContent = document.getElementById("eventPageContent");
    let h1 = document.createElement("h1");
    h1.innerHTML = "coming soon..."
    mainContent.appendChild(h1);


}
function removeFavoredEventFromArray(iId:any){
    let newArray = [];
    if(GeneralFunctions.userDto.favoredEvents != null){
        for(let i = 0; i < GeneralFunctions.userDto.favoredEvents.length; i++){
            if(GeneralFunctions.userDto.favoredEvents[i].id != iId){
                newArray[newArray.length] = GeneralFunctions.userDto.favoredEvents[i];
            }
        }
        GeneralFunctions.userDto.favoredEvents = newArray;
    }
}

function generateEventsForHorizontalContainer(data:Holder<EventDTO>){
    let hscroll = document.getElementById("eventPageHorizontalScroll");
    if(hscroll != null || hscroll != undefined){
        for(let i = 0; i < data.result.length; i++){
            if(data.result[i] instanceof EventDTO){
                createEventItem(data.result[i],false,"eventPageHorizontalScroll",false,true);
            }
            else if(data.result[i].name != null){
                let dto:EventDTO = new EventDTO(data.result[i].id,data.result[i].ticketUri,data.result[i].name,data.result[i].ownerName,data.result[i].description,data.result[i].coverUrl,data.result[i].city,data.result[i].placeName, data.result[i].street, data.result[i].longitude, data.result[i].latitude, data.result[i].zip,data.result[i].category,data.result[i].starttime,data.result[i].endtime,data.result[i].country,data.result[i].activated,data.result[i].eventId);
                createEventItem(dto,false,"eventPageHorizontalScroll",false,true);
            }
        }
    }
}

let eventList: Array<EventDTO> = [];
function generateEvents(data: Holder<EventDTO>){
    eventMarker = 0;

    let submission = data.message == "submissions" ? true : false;
    GeneralFunctions.hideLoadingBar();
    if(GeneralFunctions.usrAccessToken != undefined && !submission) {
        let recommendationHeader = document.getElementById("recommendationHeader");
        if(recommendationHeader == null) {
            recommendationHeader = document.createElement("h3");
            recommendationHeader.id = "recommendationHeader";
            recommendationHeader.innerHTML = "Recommended:"
        }
        document.getElementById("eventPageContent").appendChild(recommendationHeader);

        let recommendedContainer = document.getElementById("recommendedContainer");
        if(recommendedContainer == null) {
            recommendedContainer = document.createElement("div");
            recommendedContainer.id="recommendedContainer";
        }

        document.getElementById("eventPageContent").appendChild(recommendedContainer);
        getJSON(GeneralFunctions.backendServer + "/events/logged?latitude=" + filterOptions.lat + "&longitude="+filterOptions.lng+"&country="+filterOptions.country +"&radius="+filterOptions.radius+
        "&startTime=" + filterOptions.startTime + "&endTime=" + filterOptions.endTime + filterOptions.catRequest, appendRecommendations ,GeneralFunctions.usrAccessToken);
    }
    let h3 = document.getElementById("eventResultH3");
    if(h3 == null){
        h3 = document.createElement("h3");
        h3.id = "eventResultH3";
    }
    updateSearchLocationHeader(data.result == null ? null:data.result.length, h3);
    document.getElementById("eventPageContent").appendChild(h3);

    if(data.result != null){
        //data.result = data.result.sort((a:any,b:any)=> GeneralFunctions.compareNumberAndSecondNumber(a.start_time,b.start_time,a.radius,b.radius));
        restrictedEventLoad(data.result, submission);
    }

}
function appendRecommendations(data: any) {
    let recommendations: Array<EventDTO> = data.recommendedResults;

    for(let i = 0; i < recommendations.length; i++) {
        if(recommendations[i] instanceof EventDTO) {
            createEventItem(recommendations[i],false,"recommendedContainer",true,false);
        }
        else {
            createEventItem(new EventDTO(recommendations[i].id,recommendations[i].ticketUri,recommendations[i].name,recommendations[i].ownerName,recommendations[i].description,recommendations[i].coverUrl,recommendations[i].city,recommendations[i].placeName, recommendations[i].street, recommendations[i].longitude, recommendations[i].latitude, recommendations[i].zip,recommendations[i].category,recommendations[i].starttime,recommendations[i].endtime,recommendations[i].country,recommendations[i].activated,recommendations[i].eventId),false,"recommendedContainer",true,false);
        }
    }
}
function restrictedEventLoad(data: Array<EventDTO>, submission: boolean) {
    for(let i = eventMarker; i < eventMarker + 60; i++){
        if(i < data.length) {
            
            if(data[i] instanceof EventDTO){
                eventList.push(data[i]);
                createEventItem(data[i],submission,"eventPageContent",true,false);
            }
            else if(data[i].name != null){
                let dto:EventDTO = new EventDTO(data[i].id,data[i].ticketUri,data[i].name,data[i].ownerName,data[i].description,data[i].coverUrl,data[i].city,data[i].placeName, data[i].street, data[i].longitude, data[i].latitude, data[i].zip,data[i].category,data[i].starttime,data[i].endtime,data[i].country,data[i].activated,data[i].eventId);
                eventList.push(dto);
                createEventItem(dto,submission,"eventPageContent",true,false);
            }
        }
    }
    if(eventMarker < data.length) {
        eventMarker+=60;
    }
    if(eventMarker < data.length) {
        let showMoreText = document.createElement("div");
        showMoreText.className="showMoreText";
        showMoreText.innerHTML="loading more events...";
        document.getElementById("eventPageContent").addEventListener("wheel", function() {
            if(isScrolledIntoView(showMoreText)) {
                document.getElementById("eventPageContent").removeChild(showMoreText);
                restrictedEventLoad(data, submission);
            }
        });
        document.getElementById("eventPageContent").appendChild(showMoreText);
    }
}
function isScrolledIntoView(el: any) {
    var elemTop = el.getBoundingClientRect().top;
    var elemBottom = el.getBoundingClientRect().bottom;
    var isVisible = (elemTop <= window.innerHeight);
    return isVisible;
}
function createEventItem(dto:EventDTO,submissions: boolean,htmlelement: string,marginleft:boolean,small:boolean){
    let content = document.getElementById(htmlelement);

    let item = document.createElement("div");
    item.className = "eventItem";
    if(marginleft == false){
        item.className = "eventItem noleft";
    }
    if(small == true){
        item.className = item.className + " small";
    }
    item.setAttribute("value",""+dto.getId());
    let img = document.createElement("div");
    FB.api(
        '/' + dto.getEventId() + '?access_token=967640606679290|aCgl43Dq0ELePxHtRwxmmX3iAHM&fields=cover',
        'GET',
        {}, 
        function(response:any) {
            if(response != undefined){
                img.style.backgroundImage = "url("+response.cover.source+")"
            }
            else{
                img.style.backgroundImage = "/images/imgNotFound.png"
            }
        }
      );
    img.className = "eventImage";
    let desc = document.createElement("div");
    desc.className = "descContainer";
    desc.innerHTML = dto.getDescription() != null ? dto.getDescription().substr(0,100) +  "..." : "No description...";

    let textContainer = document.createElement("div");
    textContainer.className = "textContainer";
    let h1 = document.createElement("h1");
    h1.innerHTML = dto.getEventName();
    let p = document.createElement("p");
    p.innerHTML = "Wo: " + dto.getPlaceName();
    let p2 = document.createElement("p");
    let date = new Date(dto.getStartTime());
    p2.innerHTML = "Am: " + date.toLocaleString();

    let starSpan:HTMLSpanElement = null;
    let clockSpan:HTMLSpanElement = null;
    if(submissions == false){
        starSpan = document.createElement("span");
            starSpan.className = "icon-heart hidden";
            if(GeneralFunctions.usrAccessToken != undefined){
                starSpan.className = "icon-heart";
                if(GeneralFunctions.userDto != null && GeneralFunctions.isItemFavored(GeneralFunctions.userDto.favoredEvents,dto.getId())){
                    starSpan.className = "icon-heart favored";
                }
            }
            starSpan.addEventListener('click',function(){
                if(GeneralFunctions.usrAccessToken != undefined){
                    if(starSpan.className != "icon-heart favored"){
                        starSpan.className = "icon-heart favored";
                        if(GeneralFunctions.userDto.favoredEvents == null){
                            GeneralFunctions.userDto.favoredEvents = new Array<EventDTO>();
                        }
                        (<Array<EventDTO>>GeneralFunctions.userDto.favoredEvents).push(dto);
                        post(GeneralFunctions.backendServer + "/events/favorize/" + dto.getId(),{},function(data:any){console.log(data);},null,GeneralFunctions.usrAccessToken);
                        
                        
                    }
                    else{
                        starSpan.className = "icon-heart";
                        removeFavoredEventFromArray(item.getAttribute("value"));
                        post(GeneralFunctions.backendServer + "/events/unfavorize/" + dto.getId(),{},function(data:any){console.log(data);},null,GeneralFunctions.usrAccessToken);
                    }
                }
                else{
                    alert("Not logged in!");
                }
            });
            clockSpan = document.createElement("span");
            clockSpan.className = "icon-clock hidden";
            if(GeneralFunctions.usrAccessToken != undefined){
                clockSpan.className = "icon-clock";
                clockSpan.addEventListener('click', function() {
                    alert("not implemented yet");
                })
            }
    }

    img.appendChild(desc);
    item.appendChild(img);
    textContainer.appendChild(h1);
    textContainer.appendChild(p);
    textContainer.appendChild(p2);
    item.appendChild(textContainer);
    if(starSpan != null){
        item.appendChild(starSpan);
    }
    if(clockSpan != null){
        item.appendChild(clockSpan);
    }
    if(submissions == true){
        let activated = document.createElement("span");
        activated.innerHTML = dto.getActivated() ? "Activated!" : "Still pending...";
        activated.className = dto.getActivated() ? "green" : "red";
        item.appendChild(activated);
        var wrenchSpan = document.createElement("span");
        wrenchSpan.className = "icon-wrench";
        wrenchSpan.addEventListener('click', function() {
            let content = document.getElementsByClassName("contentContainer")[0];
            createEventEditPage(dto);
            content.className = content.className + " fadeOut";
            setTimeout(function() {
                content.className = "contentContainer hidden";
            }, 200);
        })
        item.appendChild(wrenchSpan);
    }

    img.addEventListener('click',function(){
        
        post(GeneralFunctions.backendServer + "/events/clicked/" + dto.getId(), {}, function (data: any) { console.log(data); },null,GeneralFunctions.usrAccessToken);
        
        let content = document.getElementsByClassName("contentContainer")[0];
        createEventItemPageFromId(dto.getId());;
        content.className = content.className + " fadeOut";
        setTimeout(function() {
            content.className = "contentContainer hidden";
        }, 200);
    });

    content.appendChild(item);
}

function createEventItemPageFromId(id: number){
    let eventDto:EventDTO = getEventDTOFromId(id);
    if(document.getElementsByClassName("eventItemPageContainer").length == 0){
        let page = document.getElementById("eventPageMain");

        let eventContainer  = document.createElement("div");
        eventContainer.className = "eventItemPageContainer";

        let headerContainer = document.createElement("div");
        headerContainer.className="eventItemPageHeaderContainer";
        headerContainer.id ="eventItemPageHeaderCont";
        headerContainer.style.backgroundImage = "url("+eventDto.getCoverUrl()+")";
            let headerContent = document.createElement("div");
            headerContent.className = "headerContent";
            let ename = document.createElement("h1");
            ename.innerHTML = eventDto.getEventName();
            ename.id = "eventName";
            headerContent.appendChild(ename);
            let owner = document.createElement("h3");
            owner.innerHTML = "by " + eventDto.getEventOwner();
            owner.id ="eventOwner";
            headerContent.appendChild(owner);
            let time = document.createElement("h2");
            let startDate = new Date(eventDto.getStartTime());
            let endDate = new Date(eventDto.getEndTime());
            time.innerHTML = weekday[startDate.getDay()] + ", " + startDate.toLocaleDateString() + " - " + weekday[endDate.getDay()] + ", " + endDate.toLocaleDateString();
            time.id = "eventTime";
            headerContent.appendChild(time);
            let website = document.createElement("a");
            if(eventDto.getWebsiteURL() != null){
                website.innerHTML = "(Go to webpage)";
                website.setAttribute("href",eventDto.getWebsiteURL());
            }
            else{
                website.innerHTML = "No-Webpage";
                website.setAttribute("href","#");
            }
            website.id="eventWebsite";
            headerContent.appendChild(website);
            headerContainer.appendChild(headerContent);
        eventContainer.appendChild(headerContainer);

        let descriptionContainer = document.createElement("div");
        descriptionContainer.className = "eventPageDescription";
        descriptionContainer.id = "eventPageDescription";
        descriptionContainer.innerHTML = eventDto.getDescription();

        let scrollPlaceEvents = document.createElement("div");
        scrollPlaceEvents.className = "scrollHorizontal margintop";
        scrollPlaceEvents.id = "eventPageHorizontalScroll";

        descriptionContainer.appendChild(scrollPlaceEvents);
        eventContainer.appendChild(descriptionContainer);

        let rightColumn = document.createElement("div");
        rightColumn.className = "rightColumn";

        let mapWrapper = document.createElement("div");
        mapWrapper.className= "mapWrapper";
        let mapLocation = document.createElement("div");
        mapLocation.innerHTML = "<div class='mapLocationHeader'>" + eventDto.getPlaceName() + "</div>" +
        "<div class='mapLocationAdress'>" + eventDto.getStreet() + " " + eventDto.getZip() + " " + eventDto.getCity() + "</div>";
        mapLocation.className = "mapLocation";
        mapLocation.id = "mapLocation";
        mapWrapper.appendChild(mapLocation);
        let mapContainer = document.createElement("div");
        mapContainer.id="eventLocationMap";
        mapContainer.className="eventLocationMap";
        mapWrapper.appendChild(mapContainer);
        rightColumn.appendChild(mapWrapper);


        let taxiWrapper = document.createElement("div");
        taxiWrapper.className="taxiWrapper";
        let taxiHeader = document.createElement("div");
        taxiHeader.className = "taxiHeader";
        taxiHeader.innerHTML = "Near Available Taxis";
        taxiWrapper.appendChild(taxiHeader);
        let taxiContainer = document.createElement("div");
        taxiContainer.id = "eventTaxiList";
        taxiContainer.className = "eventTaxiList";
        taxiContainer.innerHTML = "Taxi's loading..";
        taxiWrapper.appendChild(taxiContainer);
        rightColumn.appendChild(taxiWrapper);
        eventContainer.appendChild(rightColumn);
        let backButton = document.createElement("div");
        backButton.className="icon-back";
        backButton.addEventListener('click',function(){ 
            let content = document.getElementsByClassName("contentContainer")[0];
            content.className = "contentContainer";
            eventContainer.className = eventContainer.className + " fadeOutt";
            setTimeout(function() {
                eventContainer.className = "eventItemPageContainer hidden";
            }, 200);
        });
        eventContainer.appendChild(backButton);

        page.appendChild(eventContainer);
        createMapOnObject(mapContainer,eventDto.getLatitude(),eventDto.getLongitude(),eventDto.getEventName(),false,function(){});
        post(GeneralFunctions.backendServer + "/information/taxis",{longitude:GeneralFunctions.usrLong,latitude:GeneralFunctions.usrLat},fillTaxiList,null,GeneralFunctions.usrAccessToken);
    }
    else{
        document.getElementsByClassName("eventItemPageContainer")[0].className = "eventItemPageContainer";
        document.getElementById("eventItemPageHeaderCont").style.backgroundImage = "url("+eventDto.getCoverUrl()+")";
        document.getElementById("eventName").innerHTML = eventDto.getEventName();
        document.getElementById("eventOwner").innerHTML = "by " + eventDto.getEventOwner();
        let startDate = new Date(eventDto.getStartTime());
        let endDate = new Date(eventDto.getEndTime());
        document.getElementById("eventTime").innerHTML = weekday[startDate.getDay()] + ", " + startDate.toLocaleDateString() + " - " + weekday[endDate.getDay()] + ", " + endDate.toLocaleDateString();
        let webpage = document.getElementById("eventWebsite");
        if(eventDto.getWebsiteURL() != null){
            webpage.innerHTML = eventDto.getWebsiteURL();
            webpage.setAttribute("href",eventDto.getWebsiteURL());
        }
        else{
            webpage.innerHTML = "No-Website";
            webpage.setAttribute("href","#");
        }
        document.getElementById("eventPageDescription").innerHTML = eventDto.getDescription();
        let scrollPlaceEvents = document.createElement("div");
        scrollPlaceEvents.className = "scrollHorizontal margintop";
        scrollPlaceEvents.id = "eventPageHorizontalScroll";
        document.getElementById("eventPageDescription").appendChild(scrollPlaceEvents);
        createMapOnObject(document.getElementById("eventLocationMap"),eventDto.getLatitude(),eventDto.getLongitude(),eventDto.getEventName(),false,function(){});
        document.getElementById("mapLocation").innerHTML = "<div class='mapLocationHeader'>" + eventDto.getPlaceName() + "</div>" +
        "<div class='mapLocationAdress'>" + eventDto.getStreet() + " " + eventDto.getZip() + " " + eventDto.getCity() + "</div>";
        post(GeneralFunctions.backendServer + "/information/taxis",{longitude:GeneralFunctions.usrLong,latitude:GeneralFunctions.usrLat},fillTaxiList,null,GeneralFunctions.usrAccessToken);
    }
    getJSON(GeneralFunctions.backendServer + "/events/place?latitude=" + eventDto.getLatitude() + "&longitude=" + eventDto.getLongitude(),generateEventsForHorizontalContainer,"");
}

function fillTaxiList(data:any){
    let taxiContainer = document.getElementById("eventTaxiList");
    if(taxiContainer.firstChild != null){
        while(taxiContainer.firstChild){
            taxiContainer.removeChild(taxiContainer.firstChild);
        }
    }
    let ul = document.createElement("ul");
    for(let i =0; i < data.length; i++){
        let li = document.createElement("li");
        li.innerHTML = "<a href='tel:" + data[i].international_phone_number + "'>" + data[i].name + "</a>" + " " +
        printStars(data[i].rating) + printStarsWhite(5-data[i].rating);
        ul.appendChild(li);
    }
    taxiContainer.appendChild(ul);
}
function printStars(rating:Number){
    let ret = "";
    for(let i = 0; i < rating; i ++){
        ret +="<span class='ratingBlack'></span>";
    }
    return ret;
}
function printStarsWhite(rating:Number){
    let ret = "";
    for(let i = 0; i < rating; i ++){
        ret +="<span class='ratingWhite'>☆</span>";
    }
    return ret;
}

function getEventDTOFromId(id:number): EventDTO{
    for(let i = 0; i < eventList.length; i++){
        if(eventList[i].getId() == id){
            return eventList[i];
        }
    }
    return null;
}

function searchForEvents(inner:string){
    deleteAllEvents();
    let counter = 0;
    eventList.forEach(element => {
        if(inner == ""){
            createEventItem(element,false,"eventPageContent",true,false);
            counter++;
        }
        else if(element.name.toLowerCase().indexOf(inner.toLowerCase()) != -1){
            createEventItem(element,false,"eventPageContent",true,false);
            counter++;
        }
    });
    updateSearchLocationHeader(counter,document.getElementById("eventResultH3"));
}

function deleteAllEvents(){
    let content = document.getElementById("eventPageContent");
    let h3;
    while(content.firstChild){
        if(content.firstChild.nodeName == "H3"){
            h3 = content.firstChild;
        }
        content.removeChild(content.firstChild);
    }
    content.appendChild(h3);
}
function deleteAll() {
    let content = document.getElementById("eventPageContent");
    while(content.firstChild){
        content.removeChild(content.firstChild);
    }
}

function ApplyFilter(){ //To do: Category (tags)
    let place = EventFilter.getPlace();     
    let cc = "";
    deleteAllEvents();
   /* let from = EventFilter.getFrom() == null ? 0 : EventFilter.getFrom() - 7200;
    let to = EventFilter.getTo() == null ? 0 : EventFilter.getTo() + 79200;*/
    GeneralFunctions.showLoadingBar();
    if(place != ""){
        cc = GeneralFunctions.getCountryFromPlace(place);
        getNearbyEvents((<any>place).geometry.location.lat(),(<any>place).geometry.location.lng(),cc,EventFilter.getRadius(),EventFilter.getFrom(),EventFilter.getTo(),EventFilter.getTags(),generateEvents);
    }
    else{
        getNearbyEvents(GeneralFunctions.usrLat,GeneralFunctions.usrLong,GeneralFunctions.usrCountryCode,EventFilter.getRadius(),EventFilter.getFrom(),EventFilter.getTo(),EventFilter.getTags(),generateEvents);
    }
}

function updateSearchLocationHeader(length: any, element:HTMLElement){
    if(length != null){
        if(length != 1){
            if(EventFilter.getPlace() != ""){
                element.innerHTML = "Found: " + length + " Events near " + GeneralFunctions.getCityFromPlace(EventFilter.getPlace()) + ", " + GeneralFunctions.getCountryFromPlace(EventFilter.getPlace());
            }
            else{
                element.innerHTML = "Found: " + length + " Events near " + GeneralFunctions.usrCity + ", " + GeneralFunctions.usrCountryCode;
            }
        }
        else{
            if(EventFilter.getPlace() != ""){
                element.innerHTML = "Found: " + length + " Event near " + GeneralFunctions.getCityFromPlace(EventFilter.getPlace()) + ", " + GeneralFunctions.getCountryFromPlace(EventFilter.getPlace());
            }
            else{
                element.innerHTML = "Found: " + length + " Event near " + GeneralFunctions.usrCity + ", " + GeneralFunctions.usrCountryCode;
            }
        }
    }
    else{
        element.innerHTML = "ERROR";
    }
}

function showSubmissions(){
    deleteAllEvents();
    hideCategoriesAndSearchBar()
    GeneralFunctions.showLoadingBar();
    document.getElementById("userMenu").style.maxHeight = "0px";
    getJSON(GeneralFunctions.backendServer + "/events/mysubmissions",generateEvents,GeneralFunctions.usrAccessToken);
}

function hideCategoriesAndSearchBar(){
    document.getElementById("eventSearchDiv").className = "instaSearchDiv hidden";
    let heads = document.getElementsByClassName("sideBarHeadline");
    for(var i = 0; i < heads.length; i++){
        heads[i].className = "sideBarHeadline hidden";
    }
    document.getElementById("searchPlaceDiv").className = "hidden";
    document.getElementById("dateDiv").className = "hidden";
    document.getElementsByClassName("categorieContainer")[0].className = "categorieContainer hidden";
    document.getElementById("eventApplyButton").className = "applyButton hidden";
}