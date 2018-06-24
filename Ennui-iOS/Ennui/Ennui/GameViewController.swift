//
//  GameViewController.swift
//  Ennui
//
//  Created by Markus Geilehner on 02.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class GameViewController: UITableViewController, UIPickerViewDelegate, UIPickerViewDataSource{
    
    var gameItems: [GameDto] = []
    var gameItemsToDisplay: [GameDto] = []
    var gameItemFavorBtns: [Int: Bool] = [:]
    var gameCovers: [Int:UIImage] = [:]
    let categories = [NSLocalizedString("All", comment: ""),NSLocalizedString("DRINKING_GAMES", comment: ""),NSLocalizedString("CARD_GAMES", comment: ""),NSLocalizedString("DICE_GAMES", comment: ""),NSLocalizedString("BOARD_GAMES", comment: ""),NSLocalizedString("OUTDOOR_GAMES", comment: ""),NSLocalizedString("BALL_GAMES", comment: "")]
    var selectedCategory: String = NSLocalizedString("All", comment: "")
    var userDto: UserLoginDto? = nil
    @IBOutlet weak var categorySelection: UITextField!
    
    @IBOutlet weak var loadingBr: UIActivityIndicatorView!
    
    override func viewWillAppear(_ animated: Bool) {
        //Load User and load favored items
        let userDefaults = UserDefaults.standard;
        let backend = BackendService()
        if(backend.userAlreadyExist(kUsernameKey: "userDto") && FBSDKAccessToken.current() != nil){
            let decoder = JSONDecoder()
            if let dtoData = userDefaults.data(forKey: "userDto"), let dto = try? decoder.decode(UserLoginDto.self, from: dtoData){
                self.userDto = dto;
            }
            if self.userDto?.fbId == FBSDKAccessToken.current().userID{
                gameItemFavorBtns.keys.forEach { gameItemFavorBtns[$0] = false }
                for dto in (self.userDto?.favouriteGames)! {
                    self.gameItemFavorBtns[dto.id] = true;
                }
            }
        }
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //NavigonBar Color
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: "userDataRecieved"), object: nil, queue: nil, using: reloadUserData)
        self.navigationController?.navigationBar.barTintColor = UIColor.init(red: 53.0/255.0, green: 70.0/255.0, blue: 90.0/255.0, alpha: 1.0)
        let titleDict: NSDictionary = [NSAttributedStringKey.foregroundColor: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [NSAttributedStringKey : Any]
        self.navigationController?.navigationBar.isTranslucent = false;
        if #available(iOS 11.0, *) {
            self.navigationController?.navigationBar.prefersLargeTitles = true
            self.navigationController?.navigationBar.largeTitleTextAttributes = titleDict as? [NSAttributedStringKey: Any]
        }
        self.getGames()
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Games (" + String(gameItemsToDisplay.count) + ")"
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return gameItemsToDisplay.count
    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "gameCell", for: indexPath) as! GameCell
        let dto = gameItemsToDisplay[indexPath.row];
        
        cell.gameDto = dto
        cell.gameName.text = dto.name
        cell.playerLabel.text = String(dto.minPlayer) + " - " + String(dto.maxPlayer) + " " + NSLocalizedString("Players", comment: "")
        cell.categoriesLabel.text = dto.getCategoriesAsText()
        if gameCovers[dto.id] == nil && dto.cover != nil {
            downloadImageAndSaveForKey(key: dto.id, url: dto.cover)
        }
        else{
            cell.coverImage.image = gameCovers[dto.id] ?? UIImage()
        }
        
        //Favorize System
        if(self.gameItemFavorBtns[dto.id] == nil){
            gameItemFavorBtns[dto.id] = dto.favorized;
        }
        else if(dto.favorized == true && self.gameItemFavorBtns[dto.id] == false){
            gameItemsToDisplay[indexPath.row].favorized = false;
            setGameItemFavorized(id: dto.id,favored: false)
        }
        else if(dto.favorized == false && self.gameItemFavorBtns[dto.id] == true){
            gameItemsToDisplay[indexPath.row].favorized = true;
            setGameItemFavorized(id: dto.id,favored: true)
        }
        
        if(self.gameItemFavorBtns[dto.id] == false){
            cell.favorizeButton.imageView?.image = #imageLiteral(resourceName: "heartBlue");
        }
        else{
            cell.favorizeButton.imageView?.image = #imageLiteral(resourceName: "heart");
        }
        ////////////////
        
        if(FBSDKAccessToken.current() != nil){
            cell.favorizeButton.isHidden = false;
            cell.favorizeButton.tag = dto.id;
            cell.favorizeButton.removeTarget(nil, action: nil, for: .allEvents);
            cell.favorizeButton.addTarget(self, action: #selector(favorizeGameHandler), for: .touchUpInside)
        }
        else{
            cell.favorizeButton.isHidden = true;
        }
        cell.ratingStars.rating = Double(dto.rating)
        cell.ratingStars.settings.filledColor = UIColor.init(red: 53.0/255.0, green: 70.0/255.0, blue: 90.0/255.0, alpha: 1.0)
        if dto.ratedByUser{
            cell.ratingStars.rating = Double(dto.rating)
            cell.ratingStars.settings.filledColor = UIColor.init(red: 41.0/255.0, green: 145.0/255.0, blue: 228.0/255.0, alpha: 1.0)
        }
        if FBSDKAccessToken.current() != nil{
            cell.ratingStars.settings.updateOnTouch = true
            cell.ratingStars.didFinishTouchingCosmos = {
                rating in
                let backend = BackendService()
                self.gameItemsToDisplay[indexPath.row].ratedByUser = true
                self.gameItemsToDisplay[indexPath.row].rating = Int(rating)
                //self.updateUserInUserDefaults()
                backend.doPost(path: "/games/rate", dict: ["userId": self.userDto?.id ?? -1,
                                                               "gameId": dto.id,
                                                               "rating": Int(rating)],token: FBSDKAccessToken.current().tokenString, callback: {
                                                                (nsdict) in
                                                                //print(nsdict.value(forKey: "success") as! String)
                })
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }
        }
        else{
            cell.ratingStars.settings.updateOnTouch = false
        }
        
        return cell
    }
    
    @objc func favorizeGameHandler(sender: UIButton!){
        let backendService = BackendService()
        if(self.gameItemFavorBtns[sender.tag])!{
            sender.imageView?.image = #imageLiteral(resourceName: "heartBlue");
            self.gameItemFavorBtns[sender.tag] = false;
            backendService.doPost(path: "/games/unfavorize/" + String(sender.tag), dict: ["id":sender.tag],token: FBSDKAccessToken.current().tokenString, callback: {
                (nsdict) in
               // print(nsdict.value(forKey: "success") as! String);
            })
        }
        else{
            DispatchQueue.main.async {
                sender.imageView?.image = #imageLiteral(resourceName: "heart");
                self.tableView.reloadData();
            }
            self.gameItemFavorBtns[sender.tag] = true;
            backendService.doPost(path: "/games/favorize/" + String(sender.tag), dict: ["id":sender.tag], token: FBSDKAccessToken.current().tokenString, callback: {
                (nsdict) in
               // print(nsdict.value(forKey: "success") as! String);
            })
            backendService.alert(view: self, message: NSLocalizedString("Game has been added to 'Favored Games'", comment: ""), title: NSLocalizedString("Favored!", comment: ""));
        }
        removeOrAddGameToFavoredGames(gameDto: getGameInList(id: sender.tag)!)
        self.updateUserInUserDefaults()
        DispatchQueue.main.async {
            self.tableView.reloadData();
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
    
    func updateUserInUserDefaults(){
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
    
    func getGameInList(id: Int) -> GameDto?{
        for game in gameItems {
            if game.id == id{
                return game
            }
        }
        return nil
    }
    
    func setGameItemFavorized(id: Int, favored: Bool){
        var i = -1
        for (idx,game) in gameItems.enumerated(){
            if game.id == id{
                i = idx;
            }
        }
        if i >= 0{
            gameItems[i].favorized = favored
        }
    }
    //Storyboard extensions
    
    @IBAction func clickOnCategoryFilter(_ sender: UITextField) {
        
        let inputView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width, height: 240))
        
        let categoryPickerView  : UIPickerView = UIPickerView(frame: CGRect(x: 0,y: 40,width: 0,height: 0))
        categoryPickerView.dataSource = self
        categoryPickerView.delegate = self
        inputView.addSubview(categoryPickerView)
        
        let doneButton = UIButton(frame: CGRect(x: (self.view.frame.size.width) - (100),y: 0,width: 100,height: 50))
        doneButton.setTitle("Done", for: UIControlState.normal)
        doneButton.setTitle("Done", for: UIControlState.highlighted)
        doneButton.setTitleColor(UIColor.black, for: UIControlState.normal)
        doneButton.setTitleColor(UIColor.gray, for: UIControlState.highlighted)
        
        inputView.addSubview(doneButton) // add Button to UIView
        doneButton.addTarget(self, action: #selector(doneClicked), for: UIControlEvents.touchUpInside)
        sender.inputView = inputView
    }
    
    @objc func doneClicked(_sender: Any!){
        categorySelection.resignFirstResponder()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? GameDetailController{
            if let cell = sender as? GameCell{
                dest.coverUIImage = cell.coverImage.image
                dest.gameDto = cell.gameDto
            }
        }
    }
    
    //UI PICKER VIEW
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        categorySelection.text = categories[row]
        selectedCategory = categories[row]
        self.setGameItemsToDisplay()
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return categories.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return categories[row];
    }
    
    //MAIN FUNCS
    func getGames(){
        let backend = BackendService()
        loadingBr.startAnimating()
        var path = "/games/activated";
        var token = "";
        if(FBSDKAccessToken.current() != nil){
            path = "/games/activatedlogged";
            token = FBSDKAccessToken.current().tokenString
        }
        backend.doGet(path: path,token: token, callback: {
            (arr) in
            let gameHolder:Holder<GameDto> = backend.parseHolder(json: arr)
            if gameHolder.success != nil && gameHolder.success{
                self.gameItems = gameHolder.result
                
                for (idx, _) in self.gameItems.enumerated(){
                    self.gameItems[idx].favorized = false
                }
                
                self.setGameItemsToDisplay()
                DispatchQueue.main.async {
                    self.loadingBr.stopAnimating()
                    self.tableView.reloadData()
                }
            }
        })
    }
    
    func setGameItemsToDisplay(){
        gameItemsToDisplay = []
        if selectedCategory == categories[0]{
            gameItemsToDisplay = gameItems
        }
        else{
            let backend = BackendService()
            for ele in gameItems{
                if ele.categories.contains(backend.convertReadableCategoryToGameCategory(cat: selectedCategory)){
                    gameItemsToDisplay.append(ele)
                }
            }
        }
        
    }
    
    func reloadUserData(notification: Notification) -> Void{
        let userDefaults = UserDefaults.standard;
        let backend = BackendService()
        if(backend.userAlreadyExist(kUsernameKey: "userDto") && FBSDKAccessToken.current() != nil){
            let decoder = JSONDecoder()
            if let dtoData = userDefaults.data(forKey: "userDto"), let dto = try? decoder.decode(UserLoginDto.self, from: dtoData){
                self.userDto = dto;
            }
            /*for dto in (self.userDto?.getUserFavoredGames())! {
                self.gameItemFavorBtns[dto.id] = true;
            }*/
            DispatchQueue.main.async {
                self.tableView.reloadData();
            }
        }
    }
    
    func downloadImageAndSaveForKey(key: Int, url: String){
        let catPictureURL = URL(string: url)
        if(catPictureURL != nil){
            let session = URLSession(configuration: .default)
            let downloadPicTask = session.dataTask(with: catPictureURL!) { (data, response, error) in
                if let e = error {
                    print("Error downloading cat picture: \(e)")
                    self.gameCovers[key] = UIImage()
                } else {
                    if (response as? HTTPURLResponse) != nil {
                        if let imageData = data {
                            self.gameCovers[key] = UIImage(data: imageData)
                            if self.gameCovers[key] == nil{
                                self.gameCovers[key] = UIImage()
                                print("image not set")
                            }
                        } else {
                            self.gameCovers[key] = UIImage()
                        }
                    } else {
                        self.gameCovers[key] = UIImage()
                    }
                }
                DispatchQueue.main.async {
                    self.tableView.reloadData();
                }
            }
            downloadPicTask.resume()
        }
        else{
            self.gameCovers[key] = UIImage()
        }
    }
    
    
}
