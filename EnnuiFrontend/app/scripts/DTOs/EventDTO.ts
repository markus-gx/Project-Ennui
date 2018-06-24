export class EventDTO{

    id: number;
    eventId: string;
    ticketUri: string;
    name: string;
    ownerName: string;
    description: string;
    coverUrl: string;
    city: string;
    placeName: string;
    street: string;
    longitude: number;
    latitude: number;
    category: string;
    starttime: number;
    endtime: number;
    country: string;
    zip: string;
    activated: boolean;

    constructor(_id: number,_web: string,_eventName: string,_eventOwner: string, _desc: string, _cover: string, 
    _city: string, _placeName: string, _street: string, _longitude: number, _latitude: number,_zip: string, _cat: string, _start: number, _end: number, _country: string, activated:boolean, eventid:string){
            this.id = _id;
            this.ticketUri = _web;
            this.name = _eventName;
            this.ownerName = _eventOwner;
            this.description = _desc;
            this.coverUrl = _cover;
            this.city = _city;
            this.placeName = _placeName;
            this.street = _street;
            this.category = _cat;
            this.starttime = _start;
            this.endtime = _end;
            this.country = _country;
            this.longitude = _longitude;
            this.latitude = _latitude;
            this.zip = _zip;
            this.activated = activated;
            this.eventId = eventid;
    }
    getEventId(){
        return this.eventId;
    }
    getActivated(){
        return this.activated;
    }
    getId(){
        return this.id;
    }

    getWebsiteURL(){
        return this.ticketUri;
    }

    getEventName(){
        return this.name;
    }

    getEventOwner(){
        return this.ownerName;
    }

    getDescription(){
        return this.description;
    }

    getCoverUrl(){
        return this.coverUrl;
    }

    getPlaceName(){
        return this.placeName;
    }

    getLongitude(){
        return this.longitude;
    }

    getLatitude(){
        return this.latitude;
    }

    getStreet(){
        return this.street;
    }

    getZip(){
        return this.zip;
    }

    getCity(){
        return this.city;
    }

    getCategories(){
        return this.category;
    }

    getStartTime(){
        return this.starttime;
    }

    getEndTime(){
        return this.endtime;
    }

    getCountry(){
        return this.country;
    }
}