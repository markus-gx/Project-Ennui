import { GeneralFunctions } from './../GeneralFunctions';
import { Controller } from '../lib/Controller';
import { GameDTO } from '../DTOs/GameDTO';
import { UserDTO } from '../DTOs/UserDTO';

declare function post(url: String, data: any, success: Function,specified:Function,token:String): void;
declare function getJSON(addresse: string, func: Function,token: String): void;

export class AdminStartController extends Controller {
    static selector: string = '#userMenuAdminPanel';

    constructor(element: HTMLElement) {
        super(element);
    }
    static initialize() {
        initializeAdminPage();
    }
}

function initializeAdminPage(){
    GeneralFunctions.deleteAllContentAndCreateNewMain("adminPageMain");
    GeneralFunctions.generateMainContentPage("admin",userLoggedInCallback,true,function(){ alert("nope"); },function(){},function(){},generateExtraAdminContent);
    FB.getLoginStatus(userLoggedInCallback);
    getJSON(GeneralFunctions.backendServer + "/games/notactivated", generateItems,GeneralFunctions.usrAccessToken);
    getJSON(GeneralFunctions.backendServer + "/events/notActivated",generateItems,GeneralFunctions.usrAccessToken);
}

function userLoggedInCallback(response: any) {
    if (response.status == "connected") {
        document.getElementById("fbLoginBtn").style.display = "none";
        post(GeneralFunctions.backendServer + "/users/login", { fbToken: response.authResponse.accessToken }, GeneralFunctions.userFinallyLoggedIn, pageSpecifiedLogin,GeneralFunctions.usrAccessToken);

        document.getElementById("fbLoginBtn").style.display = "none";
        document.getElementById("userProfile").style.display = "inline-block";
        GeneralFunctions.usrAccessToken = response.authResponse.accessToken;
    }
    else {
        document.getElementById("fbLoginBtn").style.display = "inline-block";
        document.getElementById("userProfile").style.display = "none";
    }
}

function pageSpecifiedLogin(){

}

function generateExtraAdminContent(){
    document.getElementById("adminSearchDiv").remove();
}

function generateItems(data:any){
    console.log(data);
    let content = document.getElementById("adminPageContent");
    let popUp = document.createElement("div");
    popUp.className = "popup";
    popUp.addEventListener('click',function(){
        popUp.style.display = "none";
    });
    content.appendChild(popUp);
    let h3 = document.createElement("h3");
    let dataDTOName ="";
    if(data.success != undefined && data.success == true && data.message == "events"){
        h3.innerHTML = "Events waiting for activation (" + data.result.length + "): ";
        data = data.result;
        dataDTOName = "event";
    }
    else if(data.success != undefined && data.success == true && data.message == "games"){
        h3.innerHTML = "Games waiting for activation (" + data.result.length + "): ";   
        data = data.result;
        dataDTOName = "game";
    }
    content.appendChild(h3);

    for(let i = 0; i < data.length; i++){
        let gdiv = document.createElement("div");
        gdiv.className = "itemDiv";
        let h1 = document.createElement("h1");
        h1.innerHTML = data[i].name;
        h1.addEventListener('click',function(){
            let arr = JSON.stringify(data[i]).split(",");
            popUp.innerHTML = "";
            for(let b = 0; b < arr.length; b++){
                popUp.innerHTML = popUp.innerHTML + "<br/>" + arr[b];
            }
            closePopUps();
            popUp.style.display = "block";
        });
        gdiv.appendChild(h1);
        let accept = document.createElement("div");
        accept.innerHTML = "Accept";
        accept.className = "accept";
        accept.setAttribute("id",data[i].id);
        accept.addEventListener('click',function(){
            if(dataDTOName == "game"){
                post(GeneralFunctions.backendServer + "/games/activate/" + accept.getAttribute("id"),{},function(response:any)
                {
                    gdiv.remove();
                },null,GeneralFunctions.usrAccessToken);
                gdiv.remove();
            }
            else if(dataDTOName == "event"){
                post(GeneralFunctions.backendServer + "/events/activate/"+ accept.getAttribute("id"),{},function(response:any){ gdiv.remove();},null,GeneralFunctions.usrAccessToken);
                gdiv.remove();
            }
        });
        gdiv.appendChild(accept);
        let deleteDiv = document.createElement("div");
        deleteDiv.innerHTML = "Delete";
        deleteDiv.className = "delete";
        deleteDiv.setAttribute("id",data[i].id);
        deleteDiv.addEventListener('click',function(){
            if(dataDTOName == "game"){
                post(GeneralFunctions.backendServer + "/games/delete/" + accept.getAttribute("id"),{},function(response:any){ gdiv.remove();},null, GeneralFunctions.usrAccessToken);
                gdiv.remove();
            }
            else if(dataDTOName == "event"){
                post(GeneralFunctions.backendServer + "/events/delete/" + accept.getAttribute("id"),{},function(response:any){ gdiv.remove();},null, GeneralFunctions.usrAccessToken);
                gdiv.remove();
            }
        });
        gdiv.appendChild(deleteDiv);

        content.appendChild(gdiv);
    }
}

function closePopUps(){
    for(let b = 0; b < document.getElementsByClassName("popup").length; b++){
        (<HTMLDivElement>document.getElementsByClassName("popup")[b]).style.display = "none";
    }
}
