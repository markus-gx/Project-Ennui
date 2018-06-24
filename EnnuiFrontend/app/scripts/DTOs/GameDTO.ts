export class GameDTO{
    id:number;
    name:string
    description:string;
    instruction:string;
    cover:string;
    minPlayer:number;
    maxPlayer:number;
    categories: Array<String>;
    rating:number;
    ratedByUser: boolean;
    activated: boolean;
    ownerId: number;

    constructor(_id:number,_name:string,_description:string,_instruction:string,_cover:string,_minPlayer:number,_maxPlayer:number,_category:Array<String>,_rating:number, ratedByUser: boolean, activated: boolean, ownerId:number){
        this.id = _id;
        this.name = _name;
        this.description = _description;
        this.instruction = _instruction;
        this.cover = _cover;
        this.minPlayer = _minPlayer;
        this.maxPlayer = _maxPlayer;
        this.categories = _category;
        this.rating = _rating;
        this.ratedByUser = ratedByUser;
        this.activated = activated;
        this.ownerId = ownerId;
    }

    public getActivated(){
        return this.activated;
    }

    public getRatedByUser(){
        return this.ratedByUser;
    }
    
    public getId(){
        return this.id;
    }

    public getName(){
        return this.name;
    }
    
    public getDescription(){
        return this.description;
    }

    public getInstruction(){
        return this.instruction;
    }

    public getCover(){
        return this.cover;
    }

    public getMinPlayer(){
        return this.minPlayer;
    }

    public getMaxPlayer(){
        return this.maxPlayer;
    }

    public getCategories(){
        return this.categories;
    }

    public getRating(){
        return this.rating;
    }
}