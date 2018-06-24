//
//  UserFavoredGamesController.swift
//  Ennui
//
//  Created by Markus Geilehner on 02.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class UserFavoredGamesController: UITableViewController {

    let sections = ["Favorized Games"];
    var gameItems: [GameDto] = [];
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
        return sections[section] + " (" + String(gameItems.count) + ")";
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count;
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return gameItems.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "favGameCell", for: indexPath) as! FavGameCell;
        let dto = gameItems[indexPath.row];
        cell.gameLabel.text = dto.name;
        cell.accessoryType = .disclosureIndicator;
        cell.gameDto = dto;
        return cell;
    }
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true;
    }
    
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == UITableViewCellEditingStyle.delete{
            unFavorizeGame(gameId: gameItems[indexPath.row].id, row: indexPath.row);
        }
    }
    
    func unFavorizeGame(gameId: Int, row: Int){
        let backendService = BackendService()
        backendService.doPost(path: "/games/unfavorize/" + String(gameId), dict: [:],token: FBSDKAccessToken.current().tokenString, callback: {
            (nsdict) in
          //  print(nsdict.value(forKey: "success") as! String);
            self.gameItems.remove(at: row)
            DispatchQueue.main.async {
                self.tableView.reloadData();
            }
        })
        
        updateFavoredEventsInUserDefaults(gameDto: gameItems[row])
    }
    
    func updateFavoredEventsInUserDefaults(gameDto: GameDto){
        removeOrAddGameToFavoredGames(gameDto: gameDto)
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
    
    func removeOrAddGameToFavoredGames(gameDto: GameDto){
        if(userDto?.favouriteGames != nil){
            var i = -1;
            for (idx,dto) in (self.userDto?.favouriteGames.enumerated())! {
                if dto.id == gameDto.id{
                    i = idx;
                }
            }
            if i >= 0{
                self.userDto?.favouriteGames.remove(at: i);
            }
            else{
                self.userDto?.favouriteGames.append(gameDto)
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? GameDetailController{
            if let cell = sender as? FavGameCell{
                dest.gameDto = cell.gameDto
                dest.coverUIImage = nil
            }
        }
    }

}
