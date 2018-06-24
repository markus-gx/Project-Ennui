import { Holder } from './../DTOs/Holder';
import { GeneralFunctions } from './../GeneralFunctions';
import { OfferDTO } from './../DTOs/OfferDTO';
import { Controller } from '../lib/Controller';
import { UserDTO } from '../DTOs/UserDTO';
import { TimePeriod } from '../DTOs/TimePeriod';
import { Review } from '../DTOs/Review';

declare function post(url:String,data:any,success:Function,pageSpecifiedLogin:Function,token:String): void;
declare function getJSON(addresse:string,func:Function,token: String): void;
let weekdays = { 
    de: [ "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag" ],
    en: [ "Sunday", "Monfay", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ]
};
let offerList: Array<OfferDTO> = [];
export class LeisureStartController extends Controller {
    static selector: string = '#header_leisure';

    constructor(element: HTMLElement) {
        super(element);
        
        element.addEventListener('click',function(){
            initializeLeisurePage();    
        });
        let leisureHeadline = document.getElementById("leisure_headline");
        leisureHeadline.addEventListener('click',function(){
            initializeLeisurePage();    
        });
    }
    static initialize(){
        initializeLeisurePage();
    }
}

function initializeLeisurePage(){
    GeneralFunctions.deleteAllContentAndCreateNewMain("leisurePageMain");
    GeneralFunctions.generateMainContentPage("leisure",userLoggedInCallback,true,showFavoredOffers,function(){},function(){},generateExtraLeisureContent);
    FB.getLoginStatus(userLoggedInCallback);
}

function generateExtraLeisureContent(){
    let searchBox = document.createElement("input");
    searchBox.className = "searchBox";
    searchBox.setAttribute("type", "text");
    searchBox.setAttribute("placeholder", "Search for Offers...");
    searchBox.addEventListener('keyup', function () {
        applySearchBarFilter(searchBox.value);
        
    });
    document.getElementById("leisureSearchDiv").appendChild(searchBox);
    let long = GeneralFunctions.usrLong; 
    let lat = GeneralFunctions.usrLat; 

    let selection = document.createElement("div");
    selection.innerHTML = "<h1>Worauf hätten Sie Lust?</h1>";
    selection.id = "offerSelection";
    let content = document.getElementById("leisurePageContent");
    content.appendChild(selection);
    let backButton = document.createElement("span");
    backButton.className = "icon-back hidden";
    backButton.id = "offerBackButton";
    backButton.addEventListener('click',function(){
        initializeLeisurePage();
    });
    content.appendChild(backButton);
    GeneralFunctions.showLoadingBar();
    getJSON(GeneralFunctions.backendServer + "/offers/categories",generateCategories,"");
}

function generateCategories(response: Array<string>){
    let actItems = document.createElement("div");
    actItems.id = "actItems";
    response.forEach(element => {
        let activity = document.createElement("div");
        activity.className = "activity";
        activity.innerHTML = convertCategoryName(element);
        //activity.setAttribute("value",element);
        activity.addEventListener('click',function(){
            document.getElementById("offerSelection").style.display = 'none';
            document.getElementById("offerBackButton").style.display = 'block';
            GeneralFunctions.showLoadingBar();
            getJSON(GeneralFunctions.backendServer + "/offers?category=" + element + "&latitude=" + GeneralFunctions.usrLat + "&longitude=" + GeneralFunctions.usrLong,generateOffers,"");
        });
        actItems.appendChild(activity);
    });
    document.getElementById("offerSelection").appendChild(actItems);
    GeneralFunctions.hideLoadingBar();
}

function convertCategoryName(cat: string){
    switch(cat){
            case "CLIMBING": return "Climbing";
            case "BOWLING": return "Bowling";
            case "CINEMA": return "Cinema";
            case "CART": return "Cart";
            case "BILLIARD": return "Billiard";
            case "LASERTAG": return "Lasertag";
            case "SEGWAY": return "Segway";
            case "MUSEUM": return "Museum";
            case "PAINTBALL": return "Paintball";
            case "TRAMPOLIN": return "Trampolin";
            case "CASINO": return "Casino";
            case "OPEN_AIR_POOL": return "Openair-Pool";
            case "INDOOR_POOL": return "Indoor-Pool";
            case "THERMAL_BATH": return "Thermal-Bath";
            case "HIGH_ROPE_COURSE": return "High-Rope-Course";
            case "ICE_SKATE": return "Iceskaeting";
            case "ESCAPE_THE_ROOM": return "Escape the Room";
            case "SKIING": return "Skiing";
            case "WATER_SKIING": return "Water Skiing";
            case "SWIMMING": return "Swimming";
            default: return cat
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

function pageSpecifiedLogin(userData:any){
   
}

function showFavoredOffers(){

}

function generateOffers(response: Holder<OfferDTO>){
    let content = document.getElementById("leisurePageContent");
    let offers = document.createElement("div");
    offers.id = "offers";
    offerList = [];
    response.result.forEach(e => {//for (var i = 0; i < response.length; i++) {
        offerList.push(e);
        offers.appendChild(generateOfferItem(e));
    });
    content.appendChild(offers);
    GeneralFunctions.hideLoadingBar();
}
function generateOfferItem(e:OfferDTO) {
    let offer = document.createElement("div");
    offer.className = "offerItem";
    let head = document.createElement("h2");
    head.innerHTML = e.name + "";
    let openState = document.createElement("p");
    openState.innerHTML = e.open_now ? "Open!" : "Closed!";
    openState.className = e.open_now ? "green": "red";
    let vicinity = document.createElement("p");
    vicinity.innerHTML = e.locationDetails.vicinity +"";
    vicinity.className = "vicinity";
    offer.appendChild(head);
    offer.appendChild(openState);
    offer.appendChild(vicinity);
    offer.addEventListener("click", function() {
        let content = document.getElementsByClassName("contentContainer")[0];
        createOfferItemPage(e);
        content.className = content.className + " fadeOut";
        setTimeout(function() {
            content.className = "contentContainer hidden";
        }, 200);
    });
    return offer;
}
function createOfferItemPage(item: OfferDTO) {
    getJSON(GeneralFunctions.backendServer + "/offers/" + item.reference ,fillDetailsPage,GeneralFunctions.usrAccessToken);
    if(document.getElementsByClassName("leisureItemPageContainer").length == 0){
        console.log("1 new");
        let page = document.getElementById("leisurePageMain");
        let leisureContainer = document.createElement("div");
        leisureContainer.className = "leisureItemPageContainer";

        let header = document.createElement("div");
        header.className = "detailsHeader";

        let backButton = document.createElement("div");
        backButton.className="icon-back";
        backButton.addEventListener('click',function(){ 
            let content = document.getElementsByClassName("contentContainer")[0];
            content.className = "contentContainer";
            leisureContainer.className = leisureContainer.className + " fadeOutt";
            setTimeout(function() {
                leisureContainer.className = "leisureItemPageContainer hidden";
            }, 200);
        });
        header.appendChild(backButton);

        let headerContent = document.createElement("div");
        headerContent.className = "detailsHeaderContent";

        let offerName = document.createElement("div");
        offerName.className = "detailsName";
        offerName.innerHTML = item.name + "";

        let openStatus = document.createElement("div");
        openStatus.className = "detailsOpenStatus closed";
        openStatus.innerHTML = item.open_now?"open":"closed";
        if(item.open_now) {
            openStatus.className = "detailsOpenStatus open"
        }

        let rating = document.createElement("div");
        rating.className = "detailsRating starContainer";
        rating.innerHTML = generateRating(item.rating) + "";

        let location = document.createElement("div");
        location.className = "detailsLocation";
        location.innerHTML = item.locationDetails.vicinity + "";

        let tel = document.createElement("div");
        tel.className = "detailsTelNr";
        tel.innerHTML = "tel: ...";


        headerContent.appendChild(offerName);
        headerContent.appendChild(openStatus);
        headerContent.appendChild(rating);
        headerContent.appendChild(location);
        headerContent.appendChild(tel);

        header.appendChild(headerContent);
        leisureContainer.appendChild(header);

        let openHours = document.createElement("div");
        openHours.className = "detailsOpenHours";
        openHours.innerHTML = "loading...";
        leisureContainer.appendChild(openHours);

        let reviews = document.createElement("div");
        reviews.className = "detailsReviews";
        reviews.innerHTML = "loading...";
        leisureContainer.appendChild(reviews);

        page.appendChild(leisureContainer);
    }
    else {
        document.getElementsByClassName("leisureItemPageContainer")[0].className = "leisureItemPageContainer";
        let name = document.getElementsByClassName("detailsName")[0];
        name.innerHTML = item.name + "";
        
        let openStatus = document.getElementsByClassName("detailsOpenStatus")[0];
        openStatus.className = "detailsOpenStatus closed";
        openStatus.innerHTML = "closed";
        if(item.open_now){
            openStatus.className = "detailsOpenStatus open";
            openStatus.innerHTML = "open";
        }

        let rating = document.getElementsByClassName("detailsRating")[0];
        rating.innerHTML = generateRating(item.rating) + "";

      
        let location = document.getElementsByClassName("detailsLocation")[0];
        location.innerHTML = item.locationDetails.vicinity + "";
     

    }
}
function fillDetailsPage(response: any) {
    var offer: OfferDTO = response.result[0];

    let openHours: any = document.getElementsByClassName("detailsOpenHours")[0];
    let reviews: any = document.getElementsByClassName("detailsReviews")[0];
    console.log(document.getElementsByClassName("detailsOpenHours"));
    console.log(document.getElementsByClassName("detailsReviews"));
    let tel: any = document.getElementsByClassName("detailsTelNr")[0];
    tel.innerHTML = "tel: " + '<a href="tel:' + response.result[0].international_phone_number + '">' +  response.result[0].international_phone_number + "</a>";
    //openHours
    openHours.innerHTML = "";
    let openHoursArray:TimePeriod[] = new Array();
    for(var i = 0; i < offer.periods.length; i++) {
        openHoursArray[offer.periods[i].open.day] = offer.periods[i];
    }


    for(var i = 0; i < 7; i++) {
        var index = i + new Date().getDay() - 1;
        if(index > 6) {
            index -= 7;
        }
        if(index < 0) {
            index += 7;
        }
        openHours.appendChild(generateOpenHourTag(openHoursArray[index], index))
    }

    //reviews
    reviews.innerHTML = "";
    for(let review of offer.reviews) {
        let reviewItem = document.createElement("div");
        reviewItem.className = "reviewItem";


        let reviewImg = document.createElement("img");
        reviewImg.className = "reviewImg";
        reviewImg.src = review.profile_photo_url + "";
        reviewItem.appendChild(reviewImg);
        	
        let reviewRating = document.createElement("div");
        reviewRating.className = "reviewRating starContainer";
        reviewRating.innerHTML = generateRating(review.rating) + "";
        reviewItem.appendChild(reviewRating);
        

        let reviewText = document.createElement("div");
        reviewText.className = "reviewText";
        reviewText.innerHTML = review.text + "";
        reviewItem.appendChild(reviewText);

        reviews.appendChild(reviewItem);
    }
    
}
function generateOpenHourTag(timePeriod: TimePeriod, day: number) {
    let openHourTag = document.createElement("div");
    openHourTag.className = "openHourTag";

    let tagDay = document.createElement("div");
    tagDay.className = "tagDay";
    tagDay.innerHTML = weekdays.de[day];
    openHourTag.appendChild(tagDay);

    let tagHours = document.createElement("div");
    tagHours.className = "tagHours";

    if (timePeriod == undefined) {
        openHourTag.className+=" closed";
        tagHours.innerHTML = "closed";
    }
    else {
        tagHours.innerHTML =  timePeriod.open.time.substr(0,2) + ":" + timePeriod.open.time.substr(2,3) + " - " + timePeriod.close.time.substr(0,2) + ":" + timePeriod.close.time.substr(2,3);
    }

    openHourTag.appendChild(tagHours);
    return openHourTag;
}
function generateRating(rating: number) {
    var stars = "";
    for(var i = 0; i < Math.round(rating); i++) {
        stars += '<div>★</div>';
    }
    return stars;
}
function applySearchBarFilter(text:string) {
    deleteAllOffers();
    let content = document.getElementById("offers");
    offerList.forEach(e => {
        if(e.name.toUpperCase().indexOf(text.toUpperCase()) !== -1 || text == "") {
            content.appendChild(generateOfferItem(e));
        }
    });
}
function deleteAllOffers() {
    let content = document.getElementById("offers");
    while(content.firstChild){
        content.removeChild(content.firstChild);
    }
   
}