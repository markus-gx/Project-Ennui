export class Filter{

    private place: any;
    private radius: number;
    private from: Date;
    private to: Date;
    private category: string;
    private tags: Array<string> = [];

    constructor(_place: any, _radius: number, _from: Date, _to: Date, _category: string, _tags: Array<string>){
            this.place = _place;;
            this.radius = _radius;
            this.from = _from;
            this.to = _to;
            this.category = _category;
            this.tags = _tags;
    }

    setPlace(_place:any):void{
        this.place = _place;
    }

    getPlace():any{
        return this.place;
    }

    setRadius(_rad: number):void{
        this.radius = _rad;
    }

    getRadius():number{
        return this.radius;
    }

    setFrom(num: Date):void{
        this.from = num;
    }

    getFrom():Date{
        return this.from;
    }

    setTo(num: Date):void{
        this.to = num;
    }

    getTo():Date{
        return this.to;
    }

    setCategory(_cat:string):void{
        this.category = _cat;
    }

    getCategory():string{
        return this.category;
    }

    getTags():Array<string>{
        return this.tags;
    }

    addTag(tag: string):void{
        this.tags[this.tags.length] = tag;
    }

    removeTag(tag: string):void {
        let idx = this.tags.indexOf(tag,0);
        if(idx > -1){
            this.tags.splice(idx,1);
        }
    }
}