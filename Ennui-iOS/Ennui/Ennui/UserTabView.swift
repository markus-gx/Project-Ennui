//
//  UserTabView.swift
//  Ennui
//
//  Created by Markus Geilehner on 08.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation
import FBSDKLoginKit

class UserTabView: UITableViewController, FBSDKLoginButtonDelegate{
    
    @IBOutlet weak var navBar: UINavigationItem!
    var userDto: UserLoginDto!;
    @IBOutlet weak var userLoadingBar: UIActivityIndicatorView!
    
    
    @IBOutlet weak var favouriteEventsCell: UITableViewCell!
    @IBOutlet weak var favouriteGamesCell: UITableViewCell!
    @IBOutlet weak var userProfileCell: UserProfileViewCellTableViewCell!
    override func viewWillAppear(_ animated: Bool) {
        
        self.navigationController?.navigationBar.barTintColor = UIColor.init(red: 53.0/255.0, green: 70.0/255.0, blue: 90.0/255.0, alpha: 1.0)
        let titleDict: NSDictionary = [NSAttributedStringKey.foregroundColor: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [NSAttributedStringKey : Any]
        self.navigationController?.navigationBar.isTranslucent = false;
        
        favouriteGamesCell.accessoryType = .disclosureIndicator;
        favouriteEventsCell.accessoryType = .disclosureIndicator;
        
        reloadUserData(notification: Notification.init(name: Notification.Name(rawValue: "userDataRecieved")));
    }
    override func viewDidLoad() {
        let fbService = FacebookService()
        let cg = CGRect(x: view.frame.width/2 - 150, y: view.frame.height - 175, width: 300, height: 50)
        fbService.createFacebookButton(view: self, delegateClass: self,cg: cg)
        print("Height: ");
        print(self.tableView.sectionHeaderHeight);
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: "userDataRecieved"), object: nil, queue: nil, using: reloadUserData)
    }
    
    func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        if(error != nil){
            //error handling
        }
        else if(result.isCancelled){
            //handle cancel
        }
        else{
            let backendService = BackendService()
            userProfileCell.userName.text = NSLocalizedString("LOADING", comment: "");
            userLoadingBar.startAnimating();
            backendService.doPost(path: "/users/login", dict: [:],token: FBSDKAccessToken.current().tokenString, callback: { (nsdict) in
                let loginUserDto = backendService.parseLoginJSON(json: nsdict);
                let userDefaults = UserDefaults.standard;
                let encoder = JSONEncoder()
                if let encoded = try? encoder.encode(loginUserDto){
                    userDefaults.set(encoded, forKey: "userDto");
                }
                userDefaults.synchronize();
                //Observer Pattern
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "userDataRecieved"), object: nil);
            })
        }
        
    }
    
    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        userProfileCell.profileImage.image = #imageLiteral(resourceName: "defaultProfile");
        userProfileCell.userName.text = NSLocalizedString("Not logged in!", comment: "")
    }
    
    func reloadUserData(notification: Notification) -> Void{
        let userDefaults = UserDefaults.standard;
        let backend = BackendService()
        if(backend.userAlreadyExist(kUsernameKey: "userDto") && FBSDKAccessToken.current() != nil){
            let decoder = JSONDecoder()
            if let dtoData = userDefaults.data(forKey: "userDto"), let usr = try? decoder.decode(UserLoginDto.self, from: dtoData){
                self.userDto = usr;
                userProfileCell.userName.text = self.userDto.firstname + " " + self.userDto.lastname;
                loadUserProfileImage(picUrl: self.userDto.profileImage);
                DispatchQueue.main.async {
                    self.tableView.reloadData();
                }
            }
        }
        else{
            self.userDto = nil;
            if(FBSDKAccessToken.current() == nil){
                userProfileCell.userName.text = NSLocalizedString("Not logged in!", comment: "")
            }
            else{
                userProfileCell.userName.text = NSLocalizedString("LOADING", comment: "")
                userLoadingBar.startAnimating();
            }
        }
    }
    
    func loadUserProfileImage(picUrl: String){
        let catPictureURL = URL(string: "https://graph.facebook.com/" + userDto.fbId + "/picture?type=normal")!
        let session = URLSession(configuration: .default)
        let downloadPicTask = session.dataTask(with: catPictureURL) { (data, response, error) in
            if let e = error {
                print("Error downloading cat picture: \(e)")
            } else {
                if (response as? HTTPURLResponse) != nil {
                    if let imageData = data {
                        DispatchQueue.main.async {
                            self.userProfileCell.profileImage.image = UIImage(data: imageData)
                        }
                    } else {
                        print("Couldn't get image: Image is nil")
                    }
                } else {
                    print("Couldn't get response code for some reason")
                }
            }
            DispatchQueue.main.async {
                self.tableView.reloadData();
                self.userLoadingBar.stopAnimating();
            }
        }
        downloadPicTask.resume()
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if identifier == "favoredEventsSegue" || identifier == "favoredGamesSegue" {
            if(FBSDKAccessToken.current() == nil){
                self.alert(message: NSLocalizedString("Not logged in!", comment: ""), title: NSLocalizedString("Not logged in!", comment: ""))
                return false;
            }
        }
        return true;
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(FBSDKAccessToken.current() != nil){
            if let dest = segue.destination as? UserFavoredEventsController{
                dest.eventItems = self.userDto.favouriteEvents;
                dest.userDto = userDto;
            }
            else if let dest = segue.destination as? UserFavoredGamesController{
                dest.gameItems = self.userDto.favouriteGames
                dest.userDto = self.userDto
            }
        }
    }
    
    public func alert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
}
