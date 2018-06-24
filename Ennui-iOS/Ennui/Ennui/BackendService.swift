//
//  BackendLayer.swift
//  Ennui
//
//  Created by Markus Geilehner on 09.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

class BackendService{
    let serverUrl = "https://ennui.htl-leonding.ac.at:8443";
    let appAccessToken = "access_token=967640606679290|aCgl43Dq0ELePxHtRwxmmX3iAHM"
    let eventCategories: [String] = [NSLocalizedString("PARTY", comment: ""),NSLocalizedString("MUSIC", comment: ""),
                                     NSLocalizedString("ART", comment: ""),NSLocalizedString("LITERATUR", comment: ""),NSLocalizedString("COMEDY", comment: ""),NSLocalizedString("FOOD", comment: ""),NSLocalizedString("GAMES", comment: ""),NSLocalizedString("HEALTH", comment: ""),NSLocalizedString("SHOPPING", comment: ""),NSLocalizedString("HOME_GARDEN", comment: ""),NSLocalizedString("SPORT", comment: ""),NSLocalizedString("THEATRE", comment: ""),NSLocalizedString("OTHERS", comment: "")];
    
    public func userAlreadyExist(kUsernameKey: String) -> Bool {
        return UserDefaults.standard.object(forKey: kUsernameKey) != nil
    }
    
    public func doPost(path:String,dict: [String:Any],token: String, callback: @escaping (String) -> Void){
        if let jsonData = try? JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted) {
            let url = NSURL(string: serverUrl + path);
            let request = NSMutableURLRequest(url: url! as URL)
            request.httpMethod = "POST"
            request.httpBody = jsonData
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("bearer " + token, forHTTPHeaderField: "Authorization")
            
            let task = URLSession.shared.dataTask(with: request as URLRequest){ data,response,error in
                if error != nil{
                    print(error as Any)
                    return
                }
                
                do {
                    //let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                    if let jsonString = String(data: data!, encoding: .utf8){
                        callback(jsonString);
                    }
                }
            }          
            task.resume()
        }
    }
    
    public func doPostArray(path:String,dict: [String:Any], token: String, callback: @escaping (NSArray) -> Void){
        if let jsonData = try? JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted) {
            let url = NSURL(string: serverUrl + path);
            let request = NSMutableURLRequest(url: url! as URL)
            request.httpMethod = "POST"
            request.httpBody = jsonData
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("bearer " + token, forHTTPHeaderField: "Authorization")
            
            let task = URLSession.shared.dataTask(with: request as URLRequest){ data,response,error in
                if error != nil{
                    print(error as Any)
                    return
                }
                
                do {
                    let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSArray
                    callback(json!);
                } catch let error as NSError {
                    print(error)
                }
            }
            task.resume()
        }
    }
    
    public func doGet(path:String,token: String, callback: @escaping (String) -> Void){
            let url = NSURL(string: ((serverUrl + path).addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed))!)
            let request = NSMutableURLRequest(url: url! as URL)
            request.httpMethod = "GET"
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("bearer " + token, forHTTPHeaderField: "Authorization")
            
            let task = URLSession.shared.dataTask(with: request as URLRequest){ data,response,error in
                if error != nil{
                    print(error as Any)
                    return
                }
                
                do {
                    //let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                    if let jsonString = String(data: data!, encoding: .utf8){
                        callback(jsonString);
                    }
                }
            }
            task.resume()
    }
    
    public func doGraphGet(path:String, callback: @escaping (String) -> Void){
        let url = NSURL(string: (("https://graph.facebook.com/" + path + "&" + appAccessToken).addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed))!)
        let request = NSMutableURLRequest(url: url! as URL)
        request.httpMethod = "GET"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest){ data,response,error in
            if error != nil{
                print(error as Any)
                return
            }
            
            do {
                //let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                if let jsonString = String(data: data!, encoding: .utf8){
                    callback(jsonString);
                }
            }
        }
        task.resume()
    }
    
    public func doGetArray(path:String, token: String, callback: @escaping (NSArray) -> Void){
        let url = NSURL(string: ((serverUrl + path).addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed))!)
        let request = NSMutableURLRequest(url: url! as URL)
        request.httpMethod = "GET"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("bearer " + token, forHTTPHeaderField: "Authorization")
        
        let task = URLSession.shared.dataTask(with: request as URLRequest){ data,response,error in
            if error != nil{
                print(error as Any)
                return
            }
            
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSArray
                if(json != nil){
                    callback(json!);
                }
            } catch let error as NSError {
                print(error)
            }
        }
        task.resume()
    }
    
    
    public func parseLoginJSON(json: String) -> UserLoginDto{
        let loginDto: UserLoginDto = UserLoginDto()
            let jsonData = json.data(using: .utf8)!;
            let decoder = JSONDecoder()
            if let user = try? decoder.decode(UserLoginDto.self, from: jsonData){
                return user;
            }
        return loginDto;
    }
    
    public func parseHolder<T:Codable>(json: String) -> Holder<T>{
        let jsonData = json.data(using: .utf8)!
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .millisecondsSince1970
        if let holder = try? decoder.decode(Holder<T>.self, from: jsonData){
            return holder
        }
        return Holder<T>(success:false)
    }
    
    public func parseEventDtoJSON(arr: NSArray) -> [EventDto]{
        var eventArray: [EventDto] = [];
        for ele in arr {
            if let jsonString = ele as? String{
                let jsonData = jsonString.data(using: .utf8)!
                let decoder = JSONDecoder()
                if let event = try? decoder.decode(EventDto.self, from: jsonData){
                    eventArray.append(event);
                }
            }
            
        }
        return eventArray;
    }
    
    public func parseTaxiDtoJSON(arr: NSArray) -> [TaxiDto]{
        var taxiArray: [TaxiDto] = [];
        for ele in arr{
            if let dict = ele as? NSDictionary{
                let dto = TaxiDto()
                dto.address_components = dict.value(forKey: "address_components");
                dto.international_phone_number = dict.value(forKey: "international_phone_number") as? String;
                dto.icon = dict.value(forKey: "icon") as? String;
                dto.name = dict.value(forKey: "name") as? String;
                dto.rating = dict.value(forKey: "rating") as? Int;
                taxiArray.append(dto);
            }
        }
        return taxiArray;
    }
    
    public func getIconFromCategory(category:EventCategories) -> UIImage{
        switch category {
            case .PARTY:
                return #imageLiteral(resourceName: "partyIcon");
            case .MUSIC:
                return #imageLiteral(resourceName: "musicIcon");
            case .ART:
                return #imageLiteral(resourceName: "artIcon");
            case .LITERATUR:
                return #imageLiteral(resourceName: "literaturIcon");
            case .COMEDY:
                return #imageLiteral(resourceName: "comedyIcon");
            case .FOOD:
                return #imageLiteral(resourceName: "foodIcon");
            case .GAMES:
                return #imageLiteral(resourceName: "gameIcon");
            case .HEALTH:
                return #imageLiteral(resourceName: "heartBlue");
            case .SHOPPING:
                return #imageLiteral(resourceName: "shoppingIcon");
            case .HOME_GARDEN:
                return #imageLiteral(resourceName: "gardenIcon");
            case .SPORT:
                return #imageLiteral(resourceName: "sportIcon");
            case .THEATRE:
                return #imageLiteral(resourceName: "theatreIcon");
            case .OTHERS:
                return #imageLiteral(resourceName: "otherIcon");
        }
    }
    
    public func convertCategoryToCategoryNames(cat: EventCategories) -> String{
        switch(cat){
            case .PARTY: return NSLocalizedString("PARTY", comment: "");
            case .MUSIC: return NSLocalizedString("MUSIC", comment: "");
            case .ART: return NSLocalizedString("ART", comment: "");
            case .LITERATUR: return NSLocalizedString("LITERATUR", comment: "");
            case .COMEDY: return NSLocalizedString("COMEDY", comment: "");
            case .FOOD: return NSLocalizedString("FOOD", comment: "");
            case .GAMES: return NSLocalizedString("GAMES", comment: "");
            case .HEALTH: return NSLocalizedString("HEALTH", comment: "");
            case .SHOPPING: return NSLocalizedString("SHOPPING", comment: "");
            case .HOME_GARDEN: return NSLocalizedString("HOME_GARDEN", comment: "");
            case .SPORT: return NSLocalizedString("SPORT", comment: "");
            case .THEATRE: return NSLocalizedString("THEATRE", comment: "");
            case .OTHERS: return NSLocalizedString("OTHERS", comment: "");
        }
    }
    
    public func convertCategoryNamesToCategories(names: [String]) -> [EventCategories]{
        var cats: [EventCategories] = []
        for name in names {
            cats.append(convertCategoryNameToCategory(name: name))
        }
        return cats;
    }
    
    public func convertCategoryNameToCategory(name: String) -> EventCategories{
        switch(name){
            case NSLocalizedString("PARTY", comment: ""): return EventCategories.PARTY;
            case NSLocalizedString("MUSIC", comment: ""): return EventCategories.MUSIC;
            case NSLocalizedString("ART", comment: ""): return EventCategories.ART;
            case NSLocalizedString("LITERATUR", comment: ""): return EventCategories.LITERATUR;
            case NSLocalizedString("COMEDY", comment: ""): return EventCategories.COMEDY;
            case NSLocalizedString("FOOD", comment: ""): return EventCategories.FOOD;
            case NSLocalizedString("GAMES", comment: ""): return EventCategories.GAMES;
            case NSLocalizedString("HEALTH", comment: ""): return EventCategories.HEALTH;
            case NSLocalizedString("SHOPPING", comment: ""): return EventCategories.SHOPPING;
            case NSLocalizedString("HOME_GARDEN", comment: ""): return EventCategories.HOME_GARDEN;
            case NSLocalizedString("SPORT", comment: ""): return EventCategories.SPORT;
            case NSLocalizedString("THEATRE", comment: ""): return EventCategories.THEATRE;
            case NSLocalizedString("OTHERS", comment: ""): return EventCategories.OTHERS;
            default: return EventCategories.OTHERS;
        }
    }
    
    public func convertGameCategoryToReadableCategory(cat: String) -> String{
        return NSLocalizedString(cat, comment: "");
    }
    
    public func convertReadableCategoryToGameCategory(cat: String) -> String{
        switch(cat){
            case NSLocalizedString("DRINKING_GAMES", comment: ""): return "DRINKING_GAMES";
            case NSLocalizedString("CARD_GAMES", comment: ""): return "CARD_GAMES";
            case NSLocalizedString("DICE_GAMES", comment: ""): return "DICE_GAMES";
            case NSLocalizedString("BOARD_GAMES", comment: ""): return "BOARD_GAMES";
            case NSLocalizedString("OUTDOOR_GAMES", comment: ""): return "OUTDOOR_GAMES";
            case NSLocalizedString("BALL_GAMES", comment: ""): return "BALL_GAMES";
            default: return "";
        }
    }
    
    public func convertOfferCategoryToName(cat: String) -> String{
        switch(cat){
            case "CLIMBING": return NSLocalizedString("CLIMBING", comment: "")
            case "BOWLING": return NSLocalizedString("BOWLING", comment: "")
            case "CINEMA": return NSLocalizedString("CINEMA", comment: "")
            case "CART": return NSLocalizedString("CART", comment: "")
            case "BILLIARD": return NSLocalizedString("BILLIARD", comment: "")
            case "LASERTAG": return NSLocalizedString("LASERTAG", comment: "")
            case "SEGWAY": return NSLocalizedString("SEGWAY", comment: "")
            case "MUSEUM": return NSLocalizedString("MUSEUM", comment: "")
            case "PAINTBALL": return NSLocalizedString("PAINTBALL", comment: "")
            case "TRAMPOLIN": return NSLocalizedString("TRAMPOLIN", comment: "")
            case "CASINO": return NSLocalizedString("CASINO", comment: "")
            case "OPEN_AIR_POOL": return NSLocalizedString("OPEN_AIR_POOL", comment: "")
            case "INDOOR_POOL": return NSLocalizedString("INDOOR_POOL", comment: "")
            case "THERMAL_BATH": return NSLocalizedString("THERMAL_BATH", comment: "")
            case "HIGH_ROPE_COURSE": return NSLocalizedString("HIGH_ROPE_COURSE", comment: "")
            case "ICE_SKATE": return NSLocalizedString("ICE_SKATE", comment: "")
            case "ESCAPE_THE_ROOM": return NSLocalizedString("ESCAPE_THE_ROOM", comment: "")
            case "SKIING": return NSLocalizedString("SKIING", comment: "")
            case "WATER_SKIING": return NSLocalizedString("WATER_SKIING", comment: "")
            case "SWIMMING": return NSLocalizedString("SWIMMING", comment: "")
            default: return cat
        }
    }
    
    public func convertNameToOfferCategory(cat: String) -> String{
        switch(cat){
            case NSLocalizedString("CLIMBING", comment: ""):return "CLIMBING"
            case NSLocalizedString("BOWLING", comment: ""):return "BOWLING"
            case NSLocalizedString("CINEMA", comment: ""):return "CINEMA"
            case NSLocalizedString("CART", comment: ""):return "CART"
            case NSLocalizedString("BILLIARD", comment: ""):return "BILLIARD"
            case NSLocalizedString("LASERTAG", comment: ""):return "LASERTAG"
            case NSLocalizedString("SEGWAY", comment: ""):return "SEGWAY"
            case NSLocalizedString("MUSEUM", comment: ""):return "MUSEUM"
            case NSLocalizedString("PAINTBALL", comment: ""):return "PAINTBALL"
            case NSLocalizedString("TRAMPOLIN", comment: ""):return "TRAMPOLIN"
            case NSLocalizedString("CASINO", comment: ""):return "CASINO"
            case NSLocalizedString("OPEN_AIR_POOL", comment: ""):return "OPEN_AIR_POOL"
            case NSLocalizedString("INDOOR_POOL", comment: ""):return "INDOOR_POOL"
            case NSLocalizedString("THERMAL_BATH", comment: ""):return "THERMAL_BATH"
            case NSLocalizedString("HIGH_ROPE_COURSE", comment: ""):return "HIGH_ROPE_COURSE"
            case NSLocalizedString("ICE_SKATE", comment: ""):return "ICE_SKATE"
            case NSLocalizedString("ESCAPE_THE_ROOM", comment: ""):return "ESCAPE_THE_ROOM"
            case NSLocalizedString("SKIING", comment: ""):return "SKIING"
            case NSLocalizedString("WATER_SKIING", comment: ""):return "WATER_SKIING"
            case NSLocalizedString("SWIMMING", comment: ""):return "SWIMMING"
            default: return cat
        }
    }
    
    func alert(view: UIViewController, message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        view.present(alertController, animated: true, completion: nil)
    }
}
