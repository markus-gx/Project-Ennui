import { Review } from './Review';
import { TimePeriod } from './TimePeriod';
import { LocationDetails } from './LocationDetails';
export class OfferDTO{
    locationDetails: LocationDetails
    id: String;
    international_phone_number: String;
    name: String;
    open_now:boolean;
    periods: Array<TimePeriod>;
    weekday_text: Array<String>;
    rating: number;
    reviews: Array<Review>;
    website: String;
    reference: String;
    constructor(){
        
    }
}