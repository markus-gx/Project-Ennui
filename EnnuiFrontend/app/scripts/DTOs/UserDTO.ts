export class UserDTO{
    id:number;
    fbId: string;
    profilePicture:string;
    favoredEvents:any;
    favoredGames:any;

    name:string;
    constructor(_id: number,profilePicture:string,favoredEvents:any,favoredGames:any, name:string, fbid: string){
        this.id = _id;
        this.profilePicture = profilePicture;
        this.favoredEvents = favoredEvents;
        this.favoredGames = favoredGames;
        this.name = name;
        this.fbId = fbid;
    }
}