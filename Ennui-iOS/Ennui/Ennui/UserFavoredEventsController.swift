//
//  UserFavoredEventsController.swift
//  Ennui
//
//  Created by Markus Geilehner on 24.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class UserFavoredEventsController: UITableViewController {

    let sections = ["Favorized Events"];
    var eventItems: [EventDto] = [];
    var userDto: UserLoginDto? = nil;
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    //TABLE VIEW
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sections[section] + " (" + String(eventItems.count) + ")";
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count;
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return eventItems.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "favCell", for: indexPath) as! FavEventCell;
        let dto = eventItems[indexPath.row];
        cell.label.text = dto.name;
        cell.accessoryType = .disclosureIndicator;
        cell.eventDto = dto;
        return cell;
    }
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true;
    }
    
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == UITableViewCellEditingStyle.delete{
            unFavorizeEvent(eventId: eventItems[indexPath.row].id, row: indexPath.row);
        }
    }
    
    func unFavorizeEvent(eventId: Int, row: Int){
        let backendService = BackendService()
        backendService.doPost(path: "/events/unfavorize/" + String(eventId), dict: [:],token: FBSDKAccessToken.current().tokenString, callback: {
                (nsdict) in
                //print(nsdict.value(forKey: "success") as! String);
                self.eventItems.remove(at: row)
                DispatchQueue.main.async {
                    self.tableView.reloadData();
                }
        })
        
        updateFavoredEventsInUserDefaults(eventDto: eventItems[row])
    }
    
    func updateFavoredEventsInUserDefaults(eventDto: EventDto){
        removeOrAddEventToFavoredEvents(eventDto: eventDto)
        if(userDto != nil){
            let userDefaults = UserDefaults.standard;
            let dto:UserLoginDto = self.userDto!;
            let encoder = JSONEncoder()
            if let encoded = try? encoder.encode(dto){
                userDefaults.set(encoded, forKey: "userDto");
            }
            userDefaults.synchronize();
        }
    }
    
    func removeOrAddEventToFavoredEvents(eventDto: EventDto){
        if(userDto?.favouriteEvents != nil){
            var i = -1;
            for (idx,dto) in (self.userDto?.favouriteEvents.enumerated())! {
                if dto.id == eventDto.id{
                    i = idx;
                }
            }
            if i >= 0{
                self.userDto?.favouriteEvents.remove(at: i);
            }
            else{
                self.userDto?.favouriteEvents.append(eventDto)
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? EventDetailViewController{
            if let cell = sender as? FavEventCell {
                dest.eventDto = cell.eventDto;
                dest.cover = nil;
            }
        }
    }

}
