import { EventStartController } from '../scripts/controllers/EventStartController';
import { GameStartController } from '../scripts/controllers/GameStartController';
import { LeisureStartController } from '../scripts/controllers/LeisureStartController';
import { UserDTO } from '../scripts/DTOs/UserDTO';
import { AdminStartController } from "./controllers/AdminStartController";

declare function post(url:String,data:any,success:Function,pageSpecifiedLogin:Function,token: String): void;
declare function getJSON(addresse:string,func:Function): void;
declare function sendNotification(title:string,msg:string,ico:string): void;

export class GeneralFunctions{

    static usrLat:number = undefined;
    static usrLong:number = undefined;
    static usrCity:String = undefined;
    static usrCountryCode:String = undefined;
    static usrAccessToken:String = undefined;
    static backendServer:String = "https://ennui.htl-leonding.ac.at:8443"; //http://vm82.htl-leonding.ac.at:8080";
    static userDto:UserDTO = null;
    static creationPlace:any = null;

    static getUserCoordinatesSetup(geolocationSuccess:any){ 
        navigator.geolocation.getCurrentPosition(function(pos:Position){
            GeneralFunctions.usrLat = pos.coords.latitude;
            GeneralFunctions.usrLong = pos.coords.longitude;
            getJSON("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + pos.coords.latitude + "," + pos.coords.longitude,function(place:any){
                for (let i = 0; i < place.results[0].address_components.length; i++) {
                    if( place.results[0].address_components[i].types[0] == 'country'){
                        GeneralFunctions.usrCountryCode =  place.results[0].address_components[i].short_name;       
                    }
                    if( place.results[0].address_components[i].types[0] == 'locality'){
                        GeneralFunctions.usrCity =  place.results[0].address_components[i].long_name;
                    }
                }
                geolocationSuccess();
            });
        },function(error:PositionError){
            console.log(error);
            GeneralFunctions.getUserIPServiceData(geolocationSuccess);
        });
    }

    static getUserIPServiceData(success:any){
        getJSON("https://freegeoip.net/json/",function(data:any){
            GeneralFunctions.usrLat = data.latitude;
            GeneralFunctions.usrLong = data.longitude;
            GeneralFunctions.usrCountryCode = data.country_code;
            GeneralFunctions.usrCity = data.city;
            success();
        });
    }

    static geolocationErrorFunction(error:PositionError){
        console.log(error);
    }

    static sendNotificationToUser(title:string,msg:string,ico:string){
        sendNotification(title,msg,ico);
    }

    static compareNumber(a:any, b:any){
        if(a > b){
            return 1;
        }
        else if(a < b){
            return -1;
        }
        else return 0;
    }
    static compareNumberAndSecondNumber(a:any, b:any,a2:any,b2:any){
        if(a > b){
            return 1;
        }
        else if(a < b){
            return -1;
        }
        else {
            if(a2 > b2){
                return 1;
            }
            else if(a2 < b2){
                return -1;
            }
            else return 0;
        }
    }

    static getCountryFromPlace(place: any){
        for (let i = 0; i < place.address_components.length; i++) {
            if(place.address_components[i].types[0] == 'country'){
                return place.address_components[i].short_name;      
            }
        }
    }

    static getCityFromPlace(place: any){
        for (let i = 0; i < place.address_components.length; i++) {
            if(place.address_components[i].types[0] == 'locality'){
                return place.address_components[i].long_name;      
            }
        }
    }
    
    static  deleteAllContentAndCreateNewMain(mainId:string){
        let body = document.getElementsByTagName("body")[0];
        let fbroot = document.getElementById("fb-root");
        while(body.firstChild){
            body.removeChild(body.firstChild);
        }
        let main = document.createElement("main");
        main.id = mainId;
        body.appendChild(main);
        body.appendChild(fbroot);
    }

    static activateUserProfile(img:string){
        let pb = document.getElementById("userProfilePic");
        pb.style.background = "url(https://graph.facebook.com/" + this.userDto.fbId + "/picture?type=normal) no-repeat";
        pb.style.backgroundSize = "cover";
    }

    static activateIcons(){
        let stars = document.getElementsByClassName("icon-heart");
        for(let i = 0; i < stars.length; i++){
            stars[i].className = "icon-heart";
        }
        let clocks = document.getElementsByClassName("icon-clock");
        for(let i = 0; i < clocks.length; i++){
            clocks[i].className = "icon-clock";
        } 
        let rating = document.getElementsByClassName("rating");
       for(let i = 0; i < rating.length; i++){
            rating[i].className = "rating";
           /* if(this.userDto.ratedGames[rating[i].parentElement.getAttribute("value")] != undefined){
                for(let c = 0; c < rating[i].childNodes.length; c++){
                    (<HTMLSpanElement>rating[i].childNodes[c]).className = "";
                    if(+(<HTMLSpanElement>rating[i].childNodes[c]).getAttribute("value") <= this.userDto.ratedGames[rating[i].parentElement.getAttribute("value")]){
                        (<HTMLSpanElement>rating[i].childNodes[c]).className = "selected";
                    }
                }
            }*/
        } 
    }

    static disableIcons(){
        let deleteButtons = document.getElementsByClassName("deleteGameBtn");
        for(let i = 0; i < deleteButtons.length; i++) {
            deleteButtons[i].className = "deleteGameBtn hidden";
        }
        let stars = document.getElementsByClassName("icon-heart");
        for(let i = 0; i < stars.length; i++){
            stars[i].className = "icon-heart hidden";
        }
        let clocks = document.getElementsByClassName("icon-clock");
        for(let i = 0; i < clocks.length; i++){
            clocks[i].className = "icon-clock hidden";
        }
        let rating = document.getElementsByClassName("rating");
        for(let i = 0; i < rating.length; i++){
            rating[i].className = "rating hidden";
        }  
    }

    static isItemFavored(values:any,iId:any){
        if(values != null){
            for(let i =0 ; i < values.length; i++){
                if(values[i].id == iId){
                    return true;
                }
            }
        }
        return false;
    }

    static generateMainContentPage(page:string, userLoggedInCallback:any, showLoginButton:boolean, showFavoredCallback:any, showSubmissionsCallback: any, showCalendarCallback:any, extraAppend:any){
        let contentContainer = document.createElement("div");
        contentContainer.className = "contentContainer";
        document.getElementById(page + "PageMain").appendChild(contentContainer);

        //SideBar
        let sideBar = document.createElement("div");
        sideBar.className = "sideBar closed";
        sideBar.id = page + "PageSideBar";

        let ennui = document.createElement("div");
        ennui.className = "logoContainer";
        let logo = document.createElement("p");
        logo.innerHTML = "Ennui";
        logo.addEventListener('click', function () {
            location.reload();
        });
        ennui.appendChild(logo);

        let searchDiv = document.createElement("div");
        searchDiv.id = page + "SearchDiv";
        searchDiv.className = "instaSearchDiv";
        let icon = document.createElement("span");
        icon.className = "icon-search";
        searchDiv.appendChild(icon);

        ennui.appendChild(searchDiv);
        sideBar.appendChild(ennui);
        

        let absswipe = document.createElement("div");
        absswipe.className = "swipeContainer";
        absswipe.id = "filterMenuSwiper";
        absswipe.addEventListener('touchmove',function(e){
            let currentX = e.touches[0].clientX;
            if((currentX-300) < 0 && (currentX-300) > -280){
                sideBar.style.left = (currentX-300)+ "px";
            }
        });
        absswipe.addEventListener('touchstart',function(){
            sideBar.style.overflow = "hidden";
            sideBar.style.opacity = "1";
        });
        absswipe.addEventListener('touchend',function(){
            sideBar.style.overflow = "auto";
            if(sideBar.className == "sideBar closed"){
                if(parseInt(sideBar.style.left,10) > -180){
                    sideBar.className = "sideBar";
                    sideBar.style.left = "0px";
                    sideBar.style.opacity = "1";
                }
                else{
                    sideBar.style.left = "-280px";
                    sideBar.style.opacity = "0";
                }
            }
            else{
                if(parseInt(sideBar.style.left,10) < -80){
                    sideBar.className = "sideBar closed";
                    sideBar.style.left = "-280px";
                    sideBar.style.opacity = "0";
                }
                else{
                    sideBar.style.left="0px";
                    sideBar.style.opacity = "1";
                }
            }
        });
        sideBar.appendChild(absswipe);

        //mainContentContainer
        let mainContentContainer = document.createElement("div");
        mainContentContainer.className = "mainContentContainer";
        //loadingbar
        let loadingBar = document.createElement("div");
        loadingBar.className = "loadingBar hidden";
        mainContentContainer.appendChild(loadingBar);

        //HeaderBar
        let headerBar = document.createElement("div");
        headerBar.className = "headerBar";
        //FB Login Button
        let fbLogin = document.createElement("div");
        let fbiconspan = document.createElement("div");
        fbiconspan.className = "icon-facebook-square";
        let fbLoginText = document.createElement("div");
        fbLoginText.innerHTML = "Login ";
        if(window.matchMedia("(min-width: 780px)").matches){
            fbLoginText.innerHTML = "Login with ";
        }
        fbLogin.appendChild(fbLoginText);
        fbLogin.appendChild(fbiconspan);
        fbLogin.className = "headerBtn";
        fbLogin.id = "fbLoginBtn";
        fbLogin.addEventListener('click', function () {
            FB.login(userLoggedInCallback, { return_scopes: true, scope: 'public_profile,email,user_events,user_friends,user_likes' });
        });
        headerBar.appendChild(fbLogin);
        if (showLoginButton != true) {
            fbLogin.style.display = "none";
        }
        //UserProfile
        let userProfileMenu = document.createElement("div");
        userProfileMenu.className = "userMenu";
        userProfileMenu.id = "userMenu";
        let userMenuList = document.createElement("ul");
        let userMenuListItem1 = document.createElement("li");
        userMenuListItem1.textContent = "Calendar";
        userMenuListItem1.id = "userMenuCalendar";
        userMenuListItem1.addEventListener('click', showCalendarCallback);
        if(page != "event") {
            userMenuListItem1.className+="hidden";
        }
        let userMenuListItem2 = document.createElement("li");
        userMenuListItem2.textContent = "Favourites";
        userMenuListItem2.id = "userMenuFavourites";
        userMenuListItem2.addEventListener('click', showFavoredCallback);
        let userMenuListItem3 = document.createElement("li");
        userMenuListItem3.textContent = "Logout";
        userMenuListItem3.id = "userMenuLogout";
        userMenuListItem3.addEventListener('click', function () {
            FB.logout(function (response) { });
            GeneralFunctions.usrAccessToken = undefined;
            document.getElementById("fbLoginBtn").style.display = "inline-block";
            document.getElementById("userProfile").style.display = "none";
            document.getElementById("userMenu").style.maxHeight = "0px";
            document.getElementById("userMenuAdminPanel").className = "hidden";
            if(document.getElementById("eventAddButton") != undefined){
                document.getElementById("eventAddButton").className = "roundButton hidden";
            }
            GeneralFunctions.userDto = null;
            GeneralFunctions.disableIcons();
        });
        let userMenuListItem4 = document.createElement("li");
        userMenuListItem4.textContent = "Admin-Panel";
        userMenuListItem4.id = "userMenuAdminPanel";
        userMenuListItem4.className = "hidden";
        userMenuListItem4.addEventListener('click',function(){
            AdminStartController.initialize();
        });
        let userMenuListItem5 = document.createElement("li");
        userMenuListItem5.textContent = "Improve Ennui";
        userMenuListItem5.id = "userExtendBtn";
        userMenuListItem5.addEventListener('click',function(){
                if(confirm("You want to extend our event range? Press okay, and we will save your Page-Likes from Facebook!") == true){
                    post(GeneralFunctions.backendServer + "/pages/add",{},GeneralFunctions.crawledPageSuccess,null,GeneralFunctions.usrAccessToken);
                }
        });
        let userMenuListItem6 = document.createElement("li");
        userMenuListItem6.id="userSubmissionPanel";     
        if(page == "event"){
            userMenuListItem6.textContent = "Event Submissions";
        }
        else if(page == "game"){    
            userMenuListItem6.textContent = "Game Submissions";
        }
        userMenuListItem6.addEventListener('click',showSubmissionsCallback);


        userMenuList.appendChild(userMenuListItem1); 
        if(page == "game" || page == "event"){
            userMenuList.appendChild(userMenuListItem2);
            userMenuList.appendChild(userMenuListItem6);
        }   
        userMenuList.appendChild(userMenuListItem3);
        userMenuList.appendChild(userMenuListItem4);
        userMenuList.appendChild(userMenuListItem5);
        userProfileMenu.appendChild(userMenuList);
        headerBar.appendChild(userProfileMenu);

        let userProfileContainer = document.createElement("div");
        userProfileContainer.className = "headerBtn";
        userProfileContainer.id = "userProfile";

        let userProfileImg = document.createElement("div");
        userProfileImg.className = "userImg";
        userProfileImg.id = "userProfilePic";
        userProfileContainer.appendChild(userProfileImg);

        let userProfileBtn = document.createElement("div");
        userProfileBtn.className = "userBtn";
        userProfileBtn.id = "userBtn";
        let showUserMenu = false;
        let isHoveringButton = false;
        let isHoveringMenu = false;
        userProfileMenu.addEventListener('mouseenter', function () { isHoveringMenu = true; });
        userProfileMenu.addEventListener('mouseleave', function () { isHoveringMenu = false; });
        userProfileBtn.addEventListener('mouseenter', function () { isHoveringButton = true; });
        userProfileBtn.addEventListener('mouseleave', function () { isHoveringButton = false; });
        userProfileBtn.addEventListener("click", function (e) {
            if (!showUserMenu || userProfileMenu.style.maxHeight == "0px") {
                userProfileMenu.style.top = e.clientY + "px";
                let left = e.clientX - 200;
                if(window.matchMedia("(min-width: 780px)").matches){
                    left = left - 300;
                }

                userProfileMenu.style.left = left + "px";
                userProfileMenu.style.maxHeight = "200px";
                showUserMenu = true;
            }
            else{
                userProfileMenu.style.maxHeight = "0px";
                showUserMenu = false;
            }
        });
        document.addEventListener("click", function () {
            if (showUserMenu && (!isHoveringButton && !isHoveringMenu)) {
                userProfileMenu.style.maxHeight = "0px";
                showUserMenu = false;
            }
        });
        userProfileContainer.appendChild(userProfileBtn);
        headerBar.appendChild(userProfileContainer);

        //CategoryButtons
        let menuBtn = document.createElement("span");
        menuBtn.className = "icon-menu";
        menuBtn.addEventListener('click',function(){
            if(sideBar.className == "sideBar viaBtn" || sideBar.className == "sideBar"){
                sideBar.className = "sideBar closed";
                sideBar.style.left = "-280px";
                sideBar.style.opacity = "0";
            }
            else{
                sideBar.className = "sideBar viaBtn";
                sideBar.style.left = "0px";
                sideBar.style.opacity = "1";
            }
        });
        let eventBtn1 = document.createElement("div");
        eventBtn1.className = "headerBtn mleft"
        eventBtn1.innerHTML = "Offers ";
        eventBtn1.addEventListener('click',function(){
            LeisureStartController.initialize();
        });
        let eventBtn2 = document.createElement("div");
        eventBtn2.className = "headerBtn"
        eventBtn2.innerHTML = "Events ";
        eventBtn2.addEventListener('click', function () {
            EventStartController.initialize();
        });
        let eventBtn3 = document.createElement("div");
        eventBtn3.className = "headerBtn"
        eventBtn3.innerHTML = "Games ";
        eventBtn3.addEventListener('click', function () {
             GameStartController.initialize();
        });

        headerBar.appendChild(menuBtn);
        headerBar.appendChild(eventBtn1);
        headerBar.appendChild(eventBtn2);
        headerBar.appendChild(eventBtn3);
        //MainContent
        let mainContent = document.createElement("div");
        mainContent.className = "mainContent";
        mainContent.id = page + "PageContent";

        contentContainer.appendChild(sideBar);
        mainContentContainer.appendChild(headerBar);
        mainContentContainer.appendChild(mainContent);
        contentContainer.appendChild(mainContentContainer);
        document.getElementsByTagName("body")[0].style.overflowY = "hidden";     
        if(GeneralFunctions.userDto != null){
            GeneralFunctions.activateUserProfile(GeneralFunctions.userDto.profilePicture);
        }
        extraAppend();
    }
    static userFinallyLoggedIn(userData:any,pageSpecified:any){
        GeneralFunctions.userDto = new UserDTO(userData.id,userData.profileImage,userData.favouriteEvents,userData.favouriteGames,userData.name,userData.fbId);
        GeneralFunctions.activateUserProfile(userData.profileImage);
        if (userData.admin == true) {
            document.getElementById("userMenuAdminPanel").className = "";
           
        }
        GeneralFunctions.activateIcons();
       // GeneralFunctions.sendUserActivatedEventNames(userData.activatedEvents);
        pageSpecified(userData);
    }

    static crawledPageSuccess(response: any){
        if(response.success){
            alert("We've found " + response.userPagesCount + " Pages and saved " + response.userPagesSavedCount + " from them!");
        }
        else{
            alert(response.msg);
        }
    }

    
    static sendUserActivatedEventNames(activatedEvents:any){
        if(activatedEvents != undefined){
            if(activatedEvents.length > 0){
                for(var i = 0; i < activatedEvents.length; i++){
                    GeneralFunctions.sendNotificationToUser("Event-Hinzugefügt",activatedEvents[i],"images/logo.png");
                }
            }
        }
    }

    static testImage(url:any,imageFound:any,imageNotFound:any) {
        let tester=new Image();
        tester.onload=imageFound;
        tester.onerror=imageNotFound;
        tester.src = url;
    }

    static showLoadingBar(){
        document.getElementsByClassName("loadingBar")[0].className = "loadingBar";
    }
    static hideLoadingBar(){
        document.getElementsByClassName("loadingBar")[0].className = "loadingBar hidden";
    }
}