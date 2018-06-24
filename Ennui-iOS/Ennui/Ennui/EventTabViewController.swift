//
//  EventTabViewController.swift
//  Ennui
//
//  Created by Markus Geilehner on 22.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import CoreLocation
import UIKit
import UserNotifications

class EventTabViewController: UITableViewController, CLLocationManagerDelegate {
    
    @IBOutlet weak var eventLoadingBar: UIActivityIndicatorView!
    let sections = ["Recommended Events","Events"];
    var eventItems:[[EventDto]] = [[],[]];
    var eventItemCovers: [Int:UIImage] = [:];
    var eventItemFavorBtns: [Int:Bool] = [:];
    let locationManger = CLLocationManager()
    let eventFilter = EventFilter()
    var filterChanged = false;
    var userDto: UserLoginDto? = nil;
    var userLocation: CLLocation!;
    
    override func viewDidLoad() {
        super.viewDidLoad()
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: "userDataRecieved"), object: nil, queue: nil, using: reloadUserData)
        
        self.locationManger.delegate = self;
        
        if CLLocationManager.authorizationStatus() != .authorizedWhenInUse || CLLocationManager.authorizationStatus() != .authorizedAlways{
            self.locationManger.requestAlwaysAuthorization()
        }
        self.locationManger.startUpdatingLocation()

        self.navigationController?.navigationBar.barTintColor = UIColor.init(red: 53.0/255.0, green: 70.0/255.0, blue: 90.0/255.0, alpha: 1.0)
        let titleDict: NSDictionary = [NSAttributedStringKey.foregroundColor: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [NSAttributedStringKey : Any]
        self.navigationController?.navigationBar.isTranslucent = false;
        if #available(iOS 11.0, *) {
            self.navigationController?.navigationBar.prefersLargeTitles = true
            self.navigationController?.navigationBar.largeTitleTextAttributes = titleDict as? [NSAttributedStringKey: Any]
        }
        
        eventLoadingBar.center = self.view.center;
    }
    
    override func viewWillAppear(_ animated: Bool) {
        let userDefaults = UserDefaults.standard;
        let decoder = JSONDecoder()
        let backend = BackendService()
        if(backend.userAlreadyExist(kUsernameKey: "userDto") && FBSDKAccessToken.current() != nil){
            //let dto = NSKeyedUnarchiver.unarchiveObject(with: userDefaults.object(forKey: "userDto") as! Data) as? UserLoginDto;
            if let dtoData = userDefaults.data(forKey: "userDto"), let dto = try? decoder.decode(UserLoginDto.self, from: dtoData){
                if self.userDto?.fbId == FBSDKAccessToken.current().userID{
                    self.userDto = dto;
                    eventItemFavorBtns.keys.forEach { eventItemFavorBtns[$0] = false }
                    for dto in (self.userDto?.favouriteEvents)! {
                        self.eventItemFavorBtns[dto.id] = true;
                    }
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                    }
                }
            }
        }
        if filterChanged == true{
            //self.eventItems = [[],[]];
            DispatchQueue.main.async {
                self.eventLoadingBar.startAnimating()
            }
            if eventFilter.longitude != 0 && eventFilter.latitude != 0{
                self.getEvents(sender: "filterChanged")
            }
            else{
                self.eventsCalled = false;
                userLocation = nil;
                self.locationManger.startUpdatingLocation()
            }
            filterChanged = false;
        }
    }
    
    //LOCATION MANAGER
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if UIApplication.shared.applicationState == .active{
            if let location = locations.first {
                if(userLocation == nil){
                    eventFilter.latitude = location.coordinate.latitude;
                    eventFilter.longitude = location.coordinate.longitude
                    userLocation = location;
                    manager.stopUpdatingLocation()
                }
                else{
                    eventFilter.latitude = userLocation.coordinate.latitude;
                    eventFilter.longitude = userLocation.coordinate.longitude;
                }
                let geoCoder = CLGeocoder()
                let location = CLLocation(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
                geoCoder.reverseGeocodeLocation(location, completionHandler: { (placemarks, error) -> Void in
                    // Place details
                    var placeMark: CLPlacemark!
                    placeMark = placemarks?[0]
                    if placeMark != nil{
                        self.eventFilter.country = placeMark.isoCountryCode;
                        if self.eventFilter.country != nil{
                            self.getEvents(sender: "default");
                        }
                        else{
                            self.alert(message: NSLocalizedString("Failed to find your location!", comment: ""), title: "Error");
                        }
                        self.eventFilter.formattedAdress = (placeMark.name ?? "");
                    }
                    
                })
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to find user's location: \(error.localizedDescription)")
        alert(message: NSLocalizedString("Failed to find your location!", comment: ""), title: "Error");
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //TABLE VIEW
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sections[section] + " (" + String(eventItems[section].count) + ")";
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count;
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return eventItems[section].count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "eventCell", for: indexPath) as! EventCell;
        if(eventItems.count != 0){
            
            let dto = eventItems[indexPath.section][indexPath.row];
            cell.eventDto = dto;
            cell.eventNameLabel.text = dto.name;
            cell.locationLabel.text = dto.placeName;
            cell.timespanLabel.text = dto.getTimeSpanLabel();
            if(self.eventItemCovers[dto.id] == nil){
                cell.coverImage.image = UIImage()
                if(dto.coverUrl != nil){
                    getCoverFromFacebookAndSaveForKey(key: dto.id, fbId: dto.eventId);
                }
            }
            else{
                cell.coverImage.image = self.eventItemCovers[dto.id]!;
            }

            if(self.eventItemFavorBtns[dto.id] == nil){
                eventItemFavorBtns[dto.id] = dto.favored;
            }
            else if(dto.favored == true && self.eventItemFavorBtns[dto.id] == false){
                eventItems[indexPath.section][indexPath.row].favored = false;
            }
            else if(dto.favored == false && self.eventItemFavorBtns[dto.id] == true){
                eventItems[indexPath.section][indexPath.row].favored = true;
            }
            
            if(self.eventItemFavorBtns[dto.id] == false){
                cell.favorizeButton.imageView?.image = #imageLiteral(resourceName: "heartBlue");
            }
            else{
                cell.favorizeButton.imageView?.image = #imageLiteral(resourceName: "heart");
            }
            let backend = BackendService()
            cell.iconImage.image = backend.getIconFromCategory(category: dto.category);
            if(FBSDKAccessToken.current() != nil){
                cell.favorizeButton.isHidden = false;
                cell.favorizeButton.tag = dto.id;
                cell.favorizeButton.eventDto = dto;
                cell.favorizeButton.removeTarget(nil, action: nil, for: .allEvents);
                cell.favorizeButton.addTarget(self, action: #selector(favorizeButtonAction), for: .touchUpInside)
            }
            else{
                cell.favorizeButton.isHidden = true;
            }
        }
        return cell;
    }
    
    @objc func favorizeButtonAction(sender: FavorizeButton!){
        let backendService = BackendService()
        if(self.eventItemFavorBtns[sender.tag])!{
            sender.imageView?.image = #imageLiteral(resourceName: "heartBlue");
            sender.eventDto?.favored = false;
            self.eventItemFavorBtns[sender.tag] = false;
            backendService.doPost(path: "/events/unfavorize/" + String(sender.tag), dict: [:],token: FBSDKAccessToken.current().tokenString, callback: {
                (nsdict) in
               // print(nsdict.value(forKey: "success") as! String);
            })
            removeNotification(eventName: sender.eventDto?.name)
        }
        else{
            DispatchQueue.main.async {
                sender.imageView?.image = #imageLiteral(resourceName: "heart");
                sender.eventDto?.favored = true;
                self.tableView.reloadData();
            }
            self.eventItemFavorBtns[sender.tag] = true;
            backendService.doPost(path: "/events/favorize/" + String(sender.tag), dict: [:],token: FBSDKAccessToken.current().tokenString, callback: {
                (nsdict) in
                //print(nsdict.value(forKey: "success") as! String);
            })
            alert(message: NSLocalizedString("Event has been added to 'Favored Events'", comment: ""), title: NSLocalizedString("Favored!", comment: ""));
            addNotificationForEvent(eventName: sender.eventDto?.name ?? "Event", startTime: sender.eventDto?.starttime)
        }
        self.updateFavoredEventsInUserDefaults(eventDto: sender.eventDto!);
        DispatchQueue.main.async {
            self.tableView.reloadData();
        }
    }
    
    //OTHERS
    
    func addNotificationForEvent(eventName: String!, startTime:Date!){
        let notification = UNMutableNotificationContent()
        notification.title = eventName
        notification.body = NSLocalizedString("IS_STARTING", comment: "")
        
        var componenets = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: startTime)
        componenets.calendar = Calendar.current;
        componenets.hour = componenets.hour! - 2;
        componenets.second = 0;
        print(componenets)
        
        let notificationTrigger = UNCalendarNotificationTrigger(dateMatching: componenets, repeats: false)
        let request = UNNotificationRequest(identifier: "eventReminder" + eventName, content: notification, trigger: notificationTrigger)
        
        UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
        
    }
    
    func removeNotification(eventName: String!){
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [eventName])
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
    
    func getCoverFromFacebookAndSaveForKey(key: Int, fbId: Int){
        let backendService = BackendService()
        backendService.doGraphGet(path: String(fbId) + "?fields=cover", callback: {
            json in
            let jsonData = json.data(using: .utf8)!
            let decoder = JSONDecoder()
            decoder.dateDecodingStrategy = .millisecondsSince1970
            if let holder = try? decoder.decode(CoverDataHolder.self, from: jsonData){
                self.downloadImageAndSaveForKey(key: key, url: holder.cover.source)
            }
        })
    }
    
    func downloadImageAndSaveForKey(key: Int, url: String){
        let catPictureURL = URL(string: url)!
        let session = URLSession(configuration: .default)
        let downloadPicTask = session.dataTask(with: catPictureURL) { (data, response, error) in
            if let e = error {
                print("Error downloading cat picture: \(e)")
            } else {
                if (response as? HTTPURLResponse) != nil {
                    if let imageData = data {
                        self.eventItemCovers[key] = UIImage(data: imageData)
                    } else {
                        print("Couldn't get image: Image is nil")
                    }
                } else {
                    print("Couldn't get response code for some reason")
                }
            }
            DispatchQueue.main.async {
                self.tableView.reloadData();
            }
        }
        downloadPicTask.resume()
    }
    
    private var eventsCalled = false;
    func getEvents(sender:String){
        if(sender == "default"){
            if(eventsCalled == false){
                getEventsFromServer();
                eventsCalled = true;
            }
        }
        else{
            getEventsFromServer();
        }
    }
    
    func getEventsFromServer(){
        let backendService = BackendService()
        var token = "";
        var path = "/events";
        if FBSDKAccessToken.current() != nil{
            token = FBSDKAccessToken.current().tokenString
            path = "/events/logged";
        }
        backendService.doGet(path: path + String(eventFilter.getFilterAsRequestParameters()),token: token, callback: {
          (nsdict) in
            let eventHolder: Holder<EventDto> = backendService.parseHolder(json: nsdict)
            if eventHolder.success != nil && eventHolder.success {
                if(eventHolder.recommendedResults != nil){
                    self.eventItems[0] = eventHolder.recommendedResults
                }
                if(eventHolder.result != nil){
                    self.eventItems[1] = eventHolder.result
                }
            }
            DispatchQueue.main.async {
                self.tableView.reloadData();
                self.eventLoadingBar.stopAnimating();
            }
        })
    }
    
    func alert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func reloadUserData(notification: Notification) -> Void{
        let userDefaults = UserDefaults.standard;
        let backend = BackendService()
        if(backend.userAlreadyExist(kUsernameKey: "userDto") && FBSDKAccessToken.current() != nil){
            let decoder = JSONDecoder()
            if let dtoData = userDefaults.data(forKey: "userDto"), let usr = try? decoder.decode(UserLoginDto.self, from: dtoData){
                self.userDto = usr;
                for dto in (self.userDto?.favouriteEvents)! {
                    self.eventItemFavorBtns[dto.id] = true;
                }
                DispatchQueue.main.async {
                    self.tableView.reloadData();
                }
            }
        }
    }

    //SEGUES
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if identifier == "addFormSegue" {
            if(FBSDKAccessToken.current() == nil){
                self.alert(message: NSLocalizedString("Not logged in!", comment: ""), title: "Error")
                return false;
            }
        }
        return true;
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? EventDetailViewController{
            if let cell = sender as? EventCell {
                dest.cover = cell.coverImage.image;
                dest.eventDto = cell.eventDto;
            }
        }
        if let dest = segue.destination as? FilterViewController{
            dest.eventFilter = self.eventFilter;
            dest.delegate = self;
        }
        if let dest = segue.destination as? AddEventFormController{
            if userDto != nil{
                dest.eventHoster = (self.userDto?.firstname ?? "") + " " + (self.userDto?.lastname ?? "")
            }
        }
    }

}
