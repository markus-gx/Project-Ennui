import { GameDTO } from './../DTOs/GameDTO';
import { Holder } from './../DTOs/Holder';
import { GeneralFunctions } from './../GeneralFunctions';
import { Controller } from '../lib/Controller';
import { UserDTO } from '../DTOs/UserDTO';

declare function post(url: String, data: any, success: Function,specified:Function,token:String): void;
declare function getJSON(addresse: string, func: Function,token:String): void;
let categories = ["Trinkspiele", "Kartenspiele", "Würfelspiele", "Brettspiele", "Outdoorspiele", "Ballspiele"];
export class GameStartController extends Controller {
    static selector: string = '#header_games';

    constructor(element: HTMLElement) {
        super(element);

        element.addEventListener('click', function () {
            initializeGamePage();
        });
        let gamesHeadline = document.getElementById("games_headline");
        gamesHeadline.addEventListener('click',function(){
            initializeGamePage();    
        });
    }
    static initialize() {
        initializeGamePage();
    }
}

function initializeGamePage() {
    GeneralFunctions.deleteAllContentAndCreateNewMain("gamePageMain");
    GeneralFunctions.generateMainContentPage("game",userLoggedInCallback,true,showFavoredGames,showSubmissions,function(){},generateExtraGameContent);
    FB.getLoginStatus(userLoggedInCallback);
    GeneralFunctions.showLoadingBar();
    if(GeneralFunctions.usrAccessToken != null || GeneralFunctions.usrAccessToken != undefined){
        getJSON(GeneralFunctions.backendServer + "/games/activatedlogged", generateGames,GeneralFunctions.usrAccessToken);   
    }
    else{
        getJSON(GeneralFunctions.backendServer + "/games/activated", generateGames,GeneralFunctions.usrAccessToken);
    }
}

function generateExtraGameContent() {
    let searchBox = document.createElement("input");
    searchBox.className = "searchBox";
    searchBox.setAttribute("type", "text");
    searchBox.setAttribute("placeholder", "Search for Games...");
    searchBox.addEventListener('keyup', function () {
        searchForGames(searchBox.value);
    });
    document.getElementById("gameSearchDiv").appendChild(searchBox); 

    let catPara = document.createElement("p");
    catPara.innerHTML = "- - - - - - - - - - - - Kategorien - - - - - - - - - - - -";
    catPara.className = "sideBarHeadline";
    document.getElementById("gamePageSideBar").appendChild(catPara);
    let catDiv = document.createElement("div");
    catDiv.className = "categorieContainer";
    
    for (let i = 0; i < categories.length; i++) {
        let cat = document.createElement("div");
        cat.className = "categoryTag";
        cat.id = categories[i].toLowerCase();
        cat.innerHTML = categories[i];
        cat.addEventListener('click', function () {
            if (cat.className == "categoryTag") {
                cat.className = "categoryTag selected";
            }
            else {
                cat.className = "categoryTag";
            }
            showSelectedCategories();
        });
        catDiv.appendChild(cat);
    }
    document.getElementById("gamePageSideBar").appendChild(catDiv);

    let mainContent = document.getElementById("gamePageContent");
    let addButton = document.createElement("div");
    addButton.className = "roundButton hidden";
    addButton.id = "addGameButton";
    addButton.addEventListener('click',function(){
        if(GeneralFunctions.userDto != null && GeneralFunctions.userDto.name.length > 0){
            let content = document.getElementsByClassName("contentContainer")[0];
            createGameCreationPage(null);
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

function createGameCreationPage(dto:GameDTO) {
    if(document.getElementsByClassName("gameCreationContainer").length == 0){
        let page = document.getElementById("gamePageMain");

        let gameContainer  = document.createElement("div");
        gameContainer.className = "gameCreationContainer";

        let imgUrl = dto != null ? dto.getCover() : "";

        let header = document.createElement("div");
        header.className = "creationPageHeader";


        let background = dto != null ? dto.getCover() : "url('/images/imgNotFound.png')";
        header.style.backgroundImage = background;
        header.addEventListener("click", function(e) {
            if (e.target == header) {
                imgUrl = prompt("Please enter an image URL!", "imgUrl");
                if(imgUrl != null) {
                    header.style.backgroundImage = "url('" + imgUrl + "')";
                    background = imgUrl;
                }
                
            }
        }); 
        let backButton = document.createElement("div");
        backButton.className="icon-back";
        backButton.addEventListener('click',function(){ 
            let content = document.getElementsByClassName("contentContainer")[0];
            content.className = "contentContainer";
            gameContainer.className = gameContainer.className + " fadeOutt";
            setTimeout(function() {
                gameContainer.className = "gameCreationContainer hidden";
            }, 200);
        });
        header.appendChild(backButton);

        let headerContent = document.createElement("div");
        headerContent.className = "creationPageHeaderContent";

        let nameInput = document.createElement("input");
        nameInput.className = "creationPageNameInput";
        nameInput.type = "text";
        nameInput.placeholder = dto != null ? dto.getName() : "Game Name";
        nameInput.value = dto != null ? dto.getName() : "";
        headerContent.appendChild(nameInput);

        let dateInputContainer = document.createElement("div");
        dateInputContainer.className = "creationPageDateInputContainer";

        let minPlayerInput = document.createElement("input");
        minPlayerInput.className = "creationPageMaxPlayerInput";
        minPlayerInput.type = "text";
        minPlayerInput.placeholder = dto != null ? ""+dto.getMinPlayer() : "min. Player";
        minPlayerInput.value = dto != null ? dto.getMinPlayer()+"" : "";
        dateInputContainer.appendChild(minPlayerInput);

        let seperatorLine = document.createElement("span");
        seperatorLine.innerHTML = " - ";
        dateInputContainer.appendChild(seperatorLine);

        let maxPlayerInput = document.createElement("input");
        maxPlayerInput.className = "creationPageMaxPlayerInput";
        maxPlayerInput.type = "text";
        maxPlayerInput.placeholder = dto != null ? dto.getMaxPlayer()+"" : "max. Player";
        maxPlayerInput.value = dto != null ? dto.getMaxPlayer()+"" : "";
        dateInputContainer.appendChild(maxPlayerInput);

        headerContent.appendChild(dateInputContainer);

        let categoryContainer = document.createElement("div");
        categoryContainer.className = "categorieContainer";
        let categoryList = new Array();

        for(let i = 0; i < categories.length; i++){
            let cat = document.createElement("div");
            cat.className = "categoryTag";
            if(dto != null){
                 cat.className = "categoryTag" + (gameContainsCategory(dto.getCategories(),categories[i]) == true ? " selected" : "");
            }
            cat.innerHTML = categories[i];
            cat.id = "creation" + categories[i].toLowerCase();
            cat.addEventListener('click',function(){
                if(cat.className == "categoryTag")
                {
                    cat.className = "categoryTag selected";
                    categoryList.push(categories[i].toLowerCase());
                }
                else{
                    cat.className = "categoryTag";
                    categoryList.splice(categoryList.indexOf(categories[i].toLowerCase()), 1);
                }
            });
            categoryContainer.appendChild(cat);
        }


        headerContent.appendChild(categoryContainer);

        header.appendChild(headerContent);
        gameContainer.appendChild(header);


        let description = document.createElement("textarea");
        description.placeholder = "Description";
        description.value = dto != null ? dto.getDescription() : "";
        description.className = "creationPageDescription";

        gameContainer.appendChild(description);


        let instruciton = document.createElement("textarea");
        instruciton.placeholder = "Instruction";
        instruciton.className = "creationPageInstruction";
        instruciton.value = dto != null ? dto.getInstruction() : "";


        gameContainer.appendChild(instruciton);

        let addButton = document.createElement("div");
        addButton.className = "finishButton";
        addButton.id = "gameAddButton";
        addButton.innerHTML="<span> &#10003 </span>";

        addButton.addEventListener("click", function(e) {
            
            if(nameInput.value.length > 0 && minPlayerInput.value.length > 0 && maxPlayerInput.value.length > 0 && description.value.length > 0 && instruciton.value.length > 0 && document.getElementsByClassName("categoryTag selected").length > 0) {
                let gameName = nameInput.value;
                let minPlayerLimit = minPlayerInput.value;
                let maxPlayerLimit = maxPlayerInput.value;
                let gameDescription = description.value;
                let gameInstruction = instruciton.value;
                let gameImg = background;
                let gameCategories = new Array();
                for(let i = 0; i < document.getElementsByClassName("categoryTag selected").length; i++) {
                    gameCategories.push(i);
                }

                if(dto != null){
                     post(GeneralFunctions.backendServer + "/games/edit",{
                        id:dto.getId(),
                        name:gameName,
                        description:gameDescription,
                        instruction:gameInstruction,
                        minPlayer:minPlayerLimit,
                        maxPlayer:maxPlayerLimit,
                        categories:gameCategories,
                        cover: background
                    },
                    function(response:any){
                        console.log(response);
                        if(response.success == true){
                            let content = document.getElementsByClassName("contentContainer")[0];
                            content.className = "contentContainer";
                            gameContainer.className = gameContainer.className + " fadeOutt";
                            setTimeout(function() {
                                gameContainer.className = "gameCreationContainer hidden";
                            }, 200);
                        }
                        else{
                            alert("something went wrong!");
                        }
                    },
                    null,
                    GeneralFunctions.usrAccessToken);
                }
                else{
                    post(GeneralFunctions.backendServer + "/games/add",{
                        name:gameName,
                        description:gameDescription,
                        instruction:gameInstruction,
                        minPlayer:minPlayerLimit,
                        maxPlayer:maxPlayerLimit,
                        categories:gameCategories,
                        cover: background
                    },
                    function(response:any){
                        console.log(response);
                        if(response.success == true){
                            let content = document.getElementsByClassName("contentContainer")[0];
                            content.className = "contentContainer";
                            gameContainer.className = gameContainer.className + " fadeOutt";
                            setTimeout(function() {
                                gameContainer.className = "gameCreationContainer hidden";
                            }, 200);
                        }
                        else{
                            alert("something went wrong!");
                        }
                    },
                    null,
                    GeneralFunctions.usrAccessToken);
                }
            }
            else {
                alert("All fields have to be filled!");
            }
        });

        gameContainer.appendChild(addButton);
        page.appendChild(gameContainer);
    }
    else{
        document.getElementsByClassName("gameCreationContainer")[0].className = "gameCreationContainer";
    }
}

function userLoggedInCallback(response: any) {
    if (response.status == "connected") {
        document.getElementById("fbLoginBtn").style.display = "none";
        post(GeneralFunctions.backendServer + "/users/login", {  }, GeneralFunctions.userFinallyLoggedIn, pageSpecifiedLogin,response.authResponse.accessToken);

        document.getElementById("fbLoginBtn").style.display = "none";
        document.getElementById("userProfile").style.display = "inline-block";
        GeneralFunctions.usrAccessToken = response.authResponse.accessToken;
    }
    else {
        document.getElementById("fbLoginBtn").style.display = "inline-block";
        document.getElementById("userProfile").style.display = "none";
    }
}

function pageSpecifiedLogin(userData: any) {
    markFavoredGames(GeneralFunctions.userDto.favoredGames);
    let gameItems = document.getElementsByClassName("gameItem");
    let addbtn = document.getElementById("addGameButton");
    if(addbtn != null || addbtn != undefined){
        addbtn.className = "roundButton";
    }
    for (var i = 0; i < gameItems.length; i++) {
        let deleteBtn = document.createElement("div");
        deleteBtn.className = "deleteGameBtn hidden";
        deleteBtn.innerHTML = "X";
        gameItems[i].appendChild(deleteBtn);
        if(userData.admin == true) {
            deleteBtn.className="deleteGameBtn";
            let gameItem = gameItems[i];
            let gameid = gameItem.getAttribute("value");
            deleteBtn.addEventListener('click', function() {
                post(GeneralFunctions.backendServer + "/games/delete/" + gameid, { }, function (data: any) { console.log(data); },null,GeneralFunctions.usrAccessToken);
                gameItem.className = "gameItem hidden";
            });
        }
    }
    deleteAllGames();
    getJSON(GeneralFunctions.backendServer + "/games/activatedlogged", generateGames,GeneralFunctions.usrAccessToken); 
}

let gameList: Array<GameDTO> = [];
function generateGames(data: Holder<GameDTO>) {
    GeneralFunctions.hideLoadingBar();
    gameList = [];
    deleteAllGames();
    let submission = data.message == "submissions" ? true : false;
    if(submission){
        hideCategoriesAndSearchBar();
    }
    for (let i = 0; i < data.result.length; i++) {
        if(data.result[i] instanceof GameDTO){
            gameList[gameList.length] = data.result[i];
        }
        else{
            gameList[gameList.length] = new GameDTO(data.result[i].id,     
            data.result[i].name, data.result[i].description, data.result[i].instruction, data.result[i].cover, data.result[i].minPlayer,
            data.result[i].maxPlayer, data.result[i].categories, data.result[i].rating,data.result[i].ratedByUser,data.result[i].activated,data.result[i].ownerId);
        }
        createGameItem(gameList[gameList.length - 1],submission);
    }
    if(GeneralFunctions.userDto != null){
        GeneralFunctions.activateIcons();
        markFavoredGames(GeneralFunctions.userDto.favoredGames);
    }
}

function generateGamesFromGameList(data:Array<GameDTO>){
    for(let i = 0; i <data.length; i++){
        createGameItem(data[i], false);
    }
}

function hideCategoriesAndSearchBar(){
    document.getElementsByClassName("categorieContainer")[0].className = "categorieContainer hidden";
    document.getElementById("gameSearchDiv").className = "instaSearchDiv hidden";
    document.getElementsByClassName("sideBarHeadline")[0].className = "sideBarHeadline hidden";
}

function changeCategoryName(string:any) {
    //let categories = ["Trinkspiele", "Kartenspiele", "Würfelspiele", "Brettspiele", "Outdoorspiele", "Ballspiele"];
    //DRINKING_GAMES, CARD_GAMES, DICE_GAMES, BOARD_GAMES, OUTDOOR_GAMES, BALL_GAMES
    switch(string){
        case "DRINKING_GAMES": return "Trinkspiele";
        case "CARD_GAMES": return "Kartenspiele";
        case "DICE_GAMES": return "Würfelspiele";
        case "BOARD_GAMES": return "Brettspiele";
        case "OUTDOOR_GAMES": return "Outdoorspiele";
        case "BALL_GAMES": return "Ballspiele";
        default: return "";
    }
}
function changeCategoryNameForBackend(string:any) {
    //let categories = ["Trinkspiele", "Kartenspiele", "Würfelspiele", "Brettspiele", "Outdoorspiele", "Ballspiele"];
    //DRINKING_GAMES, CARD_GAMES, DICE_GAMES, BOARD_GAMES, OUTDOOR_GAMES, BALL_GAMES
    switch(string){
        case "Trinkspiele": return "DRINKING_GAMES";
        case "Kartenspiele": return "CARD_GAMES";
        case "Würfelspiele": return "DICE_GAMES";
        case "Brettspiele": return "BOARD_GAMES";
        case "Outdoorspiele": return "OUTDOOR_GAMES";
        case "Ballspiele": return "BALL_GAMES";
        default: return "";
    }
}

function createGameItem(dto: GameDTO, submission:boolean) {
    let content = document.getElementById("gamePageContent");

    let item = document.createElement("div");
    item.className = "gameItem";
    item.setAttribute("value", "" + dto.getId());
    let img = document.createElement("div");
    img.style.backgroundImage = checkURL(dto.getCover()) ? "url(" + dto.getCover() + ")" : "url('images/imgNotFound.png')";
    img.className = "gameImage";
    let desc = document.createElement("div");
    desc.className = "descContainer";
    desc.innerHTML = dto.getDescription() != null ? dto.getDescription().substr(0, 100) + "..." : "No description...";

    let textContainer = document.createElement("div");
    textContainer.className = "textContainer";
    let h1 = document.createElement("h1");
    h1.innerHTML = dto.getName();
    let p = document.createElement("p");
    p.innerHTML = dto.getMinPlayer() + " - " + dto.getMaxPlayer() + " Spieler";
    let p2 = document.createElement("p");
    if(dto.getCategories() != null){
        for(let i = 0; i < dto.getCategories().length; i++){
            p2.innerHTML = (i+1) < dto.getCategories().length ? p2.innerHTML + changeCategoryName(dto.getCategories()[i]) + ", " : p2.innerHTML + changeCategoryName(dto.getCategories()[i]);
        }
    }


    let starSpan:HTMLSpanElement = null;
    let rating:HTMLDivElement = null;
    if(submission == false){
        starSpan = document.createElement("span");
        starSpan.className = "icon-heart hidden";
        if (GeneralFunctions.usrAccessToken != undefined) {
            starSpan.className = "icon-heart";
            if (GeneralFunctions.isItemFavored(GeneralFunctions.userDto.favoredGames, dto.getId())) {
                starSpan.className = "icon-heart favored";
            }
        }
        starSpan.addEventListener('click', function () {
            if (GeneralFunctions.usrAccessToken != undefined) {
                if (starSpan.className != "icon-heart favored") {
                    starSpan.className = "icon-heart favored";
                    if (GeneralFunctions.userDto.favoredGames == null) {
                        GeneralFunctions.userDto.favoredGames = new Array<GameDTO>();
                    }
                    (<any>GeneralFunctions.userDto.favoredGames)[GeneralFunctions.userDto.favoredGames.length] = item;
                    post(GeneralFunctions.backendServer + "/games/favorize/" + dto.getId(), { }, function (data: any) { console.log(data); },null,GeneralFunctions.usrAccessToken);
                }
                else {
                    starSpan.className = "icon-heart";
                    removeFavoredGameFromArray(item.getAttribute("value"));
                    post(GeneralFunctions.backendServer + "/games/unfavorize/" + dto.getId(), { }, function (data: any) { console.log(data); },null,GeneralFunctions.usrAccessToken);
                }
            }
            else {
                alert("Not logged in!");
            }
        });

        rating = document.createElement("div");
        rating.className = "rating hidden";
        if (GeneralFunctions.usrAccessToken != undefined) {
            rating.className = "rating";
        }
        for (let i = 5; i >= 1; i--) {
            let ss = document.createElement("span");
            ss.innerHTML = "☆";
            if (dto.getRating() >= i) {
                ss.className = "allRating";
                if(dto.getRatedByUser() == true){
                    ss.className = "selected";
                }
            }
            ss.setAttribute("value", i + "");
            ss.addEventListener('click', function () {
                for (let c = 0; c < rating.childNodes.length; c++) {
                    (<HTMLSpanElement>rating.childNodes[c]).className = "";
                    if (+(<HTMLSpanElement>rating.childNodes[c]).getAttribute("value") < +ss.getAttribute("value")) {
                        (<HTMLSpanElement>rating.childNodes[c]).className = "selected";
                    }
                }
                ss.className = "selected";
                post(GeneralFunctions.backendServer + "/games/rate", { gameId: dto.getId(), rating: i, userId: GeneralFunctions.userDto.id }, function () { },null,GeneralFunctions.usrAccessToken);
            });
            rating.appendChild(ss);
        }
    }
    img.addEventListener('click', function() {
        let content = document.getElementsByClassName("contentContainer")[0];
        createGameItemPageFromId(dto.getId());
        content.className = content.className + " fadeOut";
        setTimeout(function() {
            content.className = "contentContainer hidden";
        }, 200);
    });

    img.appendChild(desc);
    item.appendChild(img);
    textContainer.appendChild(h1);
    textContainer.appendChild(p);
    textContainer.appendChild(p2);
    item.appendChild(textContainer);
    if(starSpan != null){
        item.appendChild(starSpan);
    }
    if(rating != null){
        item.appendChild(rating);
    }
    if(submission){
        let added = document.createElement("span");
        added.innerHTML = dto.getActivated() ? "Already activated!" : "Still pending...";
        added.className = dto.getActivated() ? "green" : "red";
        item.appendChild(added);
        var wrenchSpan = document.createElement("span");
        wrenchSpan.className = "icon-wrench";
        wrenchSpan.addEventListener('click', function() {
            let content = document.getElementsByClassName("contentContainer")[0];
            createGameCreationPage(dto);
            content.className = content.className + " fadeOut";
            setTimeout(function() {
                content.className = "contentContainer hidden";
            }, 200);
        })
        item.appendChild(wrenchSpan);
    }
    content.appendChild(item);
}
function createGameItemPageFromId(id: number){
    let gameDto:GameDTO = getGameDTOFromId(id);
    if(document.getElementsByClassName("gameItemPageContainer").length == 0){
        let page = document.getElementById("gamePageMain");
        let gameContainer = document.createElement("div");
        gameContainer.className="gameItemPageContainer";

        let backButton = document.createElement("div");
        backButton.className="icon-back";
        backButton.addEventListener('click',function(){ 
            let content = document.getElementsByClassName("contentContainer")[0];
            content.className = "contentContainer";
            gameContainer.className = gameContainer.className + " fadeOutt";
            setTimeout(function() {
                gameContainer.className = "gameItemPageContainer hidden";
            }, 200);
        });
        gameContainer.appendChild(backButton);

        let header = document.createElement("div");
        header.className="header";
        header.id="headerCover";
        header.style.backgroundImage="url('" + gameDto.getCover() +"')";
        let headerText = document.createElement("div");
        headerText.className = "headerText";

        let title = document.createElement("h1");
        title.className="title";
        title.id="headerTitle";
        title.innerHTML=gameDto.getName();
        headerText.appendChild(title);

        let players = document.createElement("h2");
        players.className="players";
        players.id="gamePlayers";
        players.innerHTML=gameDto.getMinPlayer() + " - " + gameDto.getMaxPlayer() + " Players";
        headerText.appendChild(players);
        
        let rating = document.createElement("div");
        rating.className="rating";
        rating.id="gameRating";
        rating.innerHTML=gameDto.getRating()+ "☆";
        headerText.appendChild(rating);
        
        header.appendChild(headerText);
        gameContainer.appendChild(header);

        let tabMenu = document.createElement("div");
        tabMenu.className="tabMenu";
        let descTab = document.createElement("div");
        descTab.className="tab active";
        descTab.id="descTab";
        descTab.innerHTML="Description";
        let instTab = document.createElement("div");
        instTab.className="tab";
        instTab.id="instTab";
        instTab.innerHTML="Instructions";



        let description = document.createElement("div");
        description.className="tabContent active";
        description.id="gameDescription";
        description.innerHTML=gameDto.getDescription();

        let instruction = document.createElement("div");
        instruction.className="tabContent";
        instruction.id="gameInstruction";
        instruction.innerHTML=gameDto.getInstruction();

        
        
        descTab.addEventListener('click', function() {
            descTab.className = "tab active";
            instTab.className = "tab";
            description.className = "tabContent active";
            instruction.className = "tabContent";
        });
        instTab.addEventListener('click', function() {
            descTab.className = "tab";
            instTab.className = "tab active";
            description.className = "tabContent";
            instruction.className = "tabContent active";
        });

        tabMenu.appendChild(descTab);
        tabMenu.appendChild(instTab);
        gameContainer.appendChild(tabMenu);
        
        gameContainer.appendChild(description);
        gameContainer.appendChild(instruction);

        page.appendChild(gameContainer);
    }
    else {
        document.getElementsByClassName("gameItemPageContainer")[0].className = "gameItemPageContainer";
        document.getElementById("headerCover").style.backgroundImage="url('" + gameDto.getCover() + "')";
        document.getElementById("headerTitle").innerHTML = gameDto.getName();
        document.getElementById("gameRating").innerHTML = gameDto.getRating() + "☆";
        document.getElementById("gamePlayers").innerHTML = gameDto.getMinPlayer() + " - " + gameDto.getMaxPlayer() + " Players";
        document.getElementById("gameDescription").innerHTML = gameDto.getDescription();
        document.getElementById("gameInstruction").innerHTML = gameDto.getInstruction();

    }
}
function getGameDTOFromId(id:number): GameDTO{
    for(let i = 0; i < gameList.length; i++){
        if(gameList[i].getId() == id){
            return gameList[i];
        }
    }
    return null;
}
function removeFavoredGameFromArray(iId: any) {
    let newArray = [];
    if (GeneralFunctions.userDto.favoredGames != null) {
        for (let i = 0; i < GeneralFunctions.userDto.favoredGames.length; i++) {
            if (GeneralFunctions.userDto.favoredGames[i].id != iId) {
                newArray[newArray.length] = GeneralFunctions.userDto.favoredGames[i];
            }
        }
        GeneralFunctions.userDto.favoredGames = newArray;
    }
}

function markFavoredGames(games: any) {
    if (games != null) {
        let items = document.getElementsByClassName("gameItem");
        for (let i = 0; i < items.length; i++) {
            if (GeneralFunctions.isItemFavored(games, items[i].getAttribute("value"))) {
                for (let z = 0; z < items[i].childNodes.length; z++) {
                    if ((<any>items[i].childNodes[z]).className == "icon-heart") {
                        (<any>items[i].childNodes[z]).className = (<any>items[i].childNodes[z]).className + " favored";
                    }
                }
            }
        }
    }
}

function checkURL(url: any) {
    if(url != null || url != undefined){
        return (url.match(/\.(jpeg|jpg|gif|png)$/) != null);
    }
    return false;
}

function searchForGames(inner: string) {
    deleteAllGames()
    let counter = 0;
    gameList.forEach(element => {
        if(inner == ""){
            createGameItem(element,false);
        }
        else if(element.name.toLowerCase().indexOf(inner.toLowerCase()) != -1){
            if(getSelectedCategories().length > 0){
                if(gameContainsCategories(element.getCategories(),getSelectedCategories())){
                    createGameItem(element,false);
                }
            }
            else{
                createGameItem(element,false);
            }
        }
    });
}

function deleteAllGames() {
    let content = document.getElementById("gamePageContent");
    let addBtn = document.getElementById("addGameButton");
    while (content.firstChild) {
        content.removeChild(content.firstChild);
    }
    content.appendChild(addBtn);
}

function showFavoredGames() {
    deleteAllGames();
    let holder = new Holder<GameDTO>();
    holder.result = GeneralFunctions.userDto.favoredGames;
    generateGames(holder);
    GeneralFunctions.activateIcons();
    markFavoredGames(GeneralFunctions.userDto.favoredGames);
    document.getElementById("userMenu").style.maxHeight = "0px";
}

function showSelectedCategories(){
    deleteAllGames();
    let selectedCategories = getSelectedCategories();
    if(selectedCategories.length > 0){
        for(let i = 0; i < gameList.length; i++){
            if(gameContainsCategories(gameList[i].getCategories(),selectedCategories)){
                createGameItem(gameList[i],false);
            }
        }
    }
    else{
        generateGamesFromGameList(gameList);
    }
}

function changeCategoryNames(gamecats: any){
    let newCats = [];
    if(gamecats != null){
        for(let i = 0; i < gamecats.length; i++){
            newCats[i] = changeCategoryName(gamecats[i]).toLowerCase();
        }
    }
    return newCats;
}

function gameContainsCategories(gameCategories:any,categories:any){
    if(gameCategories != null){
        gameCategories = changeCategoryNames(gameCategories);
        return categories.some(function (v:any) {
            return gameCategories.indexOf(v) >= 0;
        });
    }
    return false;
}

function gameContainsCategory(gameCategories: any, category: string){
    if(gameCategories != null){
        for(let i = 0; i < gameCategories.length; i++){
            if(changeCategoryName(gameCategories[i]).toLowerCase() == category.toLowerCase()){
                return true;
            }
        }
    }
    return false;
}

function getSelectedCategories(){
    let categories = document.getElementsByClassName("categoryTag selected");
    let catsString = [];
    for(let i = 0; i < categories.length; i++){
        catsString[catsString.length] = categories[i].id;
    }
    return catsString;
}

function showSubmissions(){
    deleteAllGames();
    GeneralFunctions.showLoadingBar();
    document.getElementById("userMenu").style.maxHeight = "0px";
    getJSON(GeneralFunctions.backendServer + "/games/mysubmissions",generateGames,GeneralFunctions.usrAccessToken);
}