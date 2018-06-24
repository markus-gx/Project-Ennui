import { Controller } from '../lib/Controller';
import { GeneralFunctions } from '../GeneralFunctions';

declare function post(url:String,data:any,success:Function,dataType:String): void;
declare function getJSON(addresse:string,func:Function): void;
declare function generateProgressBar(element:HTMLElement,text:String,textValue:number,maxValue:number): void;

export class StatisticController extends Controller {
        static selector: string = '#statistics';
        constructor(selection: HTMLSelectElement) {
        super(selection);
        GeneralFunctions.getUserCoordinatesSetup(startGenerateStatistics);
    }
}

function startGenerateStatistics(){
    getJSON(GeneralFunctions.backendServer + "/information/statistics",generateStatistics)
}

function generateStatistics(data:any){
    let stats = document.getElementById("statistics");

    let eventStats = document.createElement("div");
    eventStats.className = "eventStats";

    stats.appendChild(eventStats);
    let eventsInCountry = 0;
    for(let i = 0; i < data.countryWithEvents.length; i++){
        if(data.countryWithEvents[i][1] == GeneralFunctions.usrCountryCode){
            eventsInCountry = data.countryWithEvents[i][0];
            i = data.countryWithEvents.length;
        }
    }
    console.log("Found " + data.events + " Events!");
    generateProgressBar(eventStats,"Events in " + GeneralFunctions.usrCountryCode,eventsInCountry,data.events);
}