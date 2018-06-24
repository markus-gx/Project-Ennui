//
//  AddEventFormController.swift
//  Ennui
//
//  Created by Markus Geilehner on 04.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import GooglePlaces

class AddEventFormController: UIViewController, GMSAutocompleteResultsViewControllerDelegate, UIPickerViewDelegate, UIPickerViewDataSource {
    
    var categories: [String:Bool] = [:]
    var selectedCategory: String = "";
    @IBOutlet weak var eventNameField: UITextField!
    @IBOutlet weak var eventHosterField: UITextField!
    @IBOutlet weak var eventDescriptionView: UITextView!
    @IBOutlet weak var eventCategoriesField: UITextField!
    @IBOutlet weak var eventImageField: UITextField!
    @IBOutlet weak var eventWebsiteField: UITextField!
    @IBOutlet weak var eventStartField: UITextField!
    @IBOutlet weak var eventEndField: UITextField!
    @IBOutlet weak var addEventBtn: UIButton!
    @IBOutlet weak var searchView: UIView!
    var country: String! = ""
    var city: String! = ""
    var postal: String! = ""
    var long: Double! = 0
    var lat: Double! = 0
    var street: String! = ""
    var placeName: String! = ""
    var startDateComp: DateComponents!;
    var endDateComp: DateComponents!;
    var eventHoster: String!;
    
    //PlaceSearch
    var resultsViewController: GMSAutocompleteResultsViewController?
    var searchController: UISearchController?
    var resultView: UITextView?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Googl places search////////////////////////////////////////////////////////////////
        resultsViewController = GMSAutocompleteResultsViewController()
        resultsViewController?.delegate = self
        resultsViewController?.view.backgroundColor = UIColor(white: 1, alpha: 0.5);
        
        searchController = UISearchController(searchResultsController: resultsViewController)
        searchController?.searchResultsUpdater = resultsViewController
        
        
        let subView = UIView(frame: CGRect(x: 0, y: 0, width: self.searchView.frame.width - 5.0, height: searchView.frame.height))
        
        subView.addSubview((searchController?.searchBar)!)
        searchView.addSubview(subView)
        searchController?.searchBar.sizeToFit()
        searchController?.searchBar.barTintColor = UIColor.white;
        searchController?.searchBar.placeholder = NSLocalizedString("Enter a specific place", comment: "")
        
        navigationController?.navigationBar.isTranslucent = false
        searchController?.hidesNavigationBarDuringPresentation = false
        ////////////////////////////////////////////////////////////////////////////////////////////////
        
        let backend = BackendService()
        for cat in backend.eventCategories{
            self.categories[cat] = false
        }
        
        ///
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        view.addGestureRecognizer(tap)
        
        self.eventHosterField.text = eventHoster ?? ""
    }
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    func getCurrentSelectedCategories() -> [String]{
        var cats: [String] = []
        for (key,value) in categories{
            if value{
                cats.append(key)
            }
        }
        return cats
    }
    
    @IBAction func selectCategories(_ sender: UITextField) {
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
        doneButton.tag = 2;
        doneButton.addTarget(self, action: #selector(doneClicked), for: UIControlEvents.touchUpInside)
        sender.inputView = inputView
    }
    
    @IBAction func eventStarttimeAction(_ sender: UITextField) {
        createDatePickerAsSubview(sender: sender, tagg: 0)
    }

    @IBAction func eventEndtimeAction(_ sender: UITextField) {
        createDatePickerAsSubview(sender: sender, tagg: 1)
    }
    
    
    @IBAction func addEventButtonAction(_ sender: UIButton) {
        let backend = BackendService()
        var body: [String:Any] = [:]
        var addEvent = true
        
        addEvent = checkRequiredField(msg: NSLocalizedString("Event-Name is required!", comment: ""), field: eventNameField) ? addEvent : false
        addEvent = checkRequiredField(msg: NSLocalizedString("Event-Hoster is required", comment: ""), field: eventHosterField) ? addEvent : false
        addEvent = checkRequiredView(msg: NSLocalizedString("Event-Description is required!", comment: ""), field: eventDescriptionView) ? addEvent : false
        addEvent = checkRequiredField(msg: NSLocalizedString("You have to select categories!", comment: ""), field: eventCategoriesField) ? addEvent : false
        addEvent = checkRequiredField(msg: NSLocalizedString("Event-Starttime is required!", comment: ""), field: eventStartField) ? addEvent : false
        addEvent = checkRequiredField(msg: NSLocalizedString("Event-Endtime is required!", comment: ""), field: eventEndField) ? addEvent : false
        /*var country: String! = ""
        var city: String! = ""
        var postal: String! = ""
        var long: Double! = 0
        var lat: Double! = 0
        var street: String! = ""
        var placeName: String! = ""*/
        if country.isEmpty{
            backend.alert(view: self, message: NSLocalizedString("COUNTRY_LOCATION_ERROR", comment: ""), title: "Error")
            addEvent = false
        }
        if city.isEmpty{
            backend.alert(view: self, message: NSLocalizedString("CITY_LOCATION_ERROR", comment: ""), title: "Error")
            addEvent = false
        }
        if postal.isEmpty{
            backend.alert(view: self, message: NSLocalizedString("ZIP_LOCATION_ERROR", comment: ""), title: "Error")
            addEvent = false
        }
        if long == 0 || lat == 0{
            backend.alert(view: self, message: NSLocalizedString("LONGLAT_LOCATION_ERROR", comment: ""), title: "Error")
            addEvent = false
        }
        if startDateComp == nil || !startDateComp.isValidDate{
            backend.alert(view: self, message: NSLocalizedString("NO_START_DATE_ERROR", comment: ""), title: "Error")
            addEvent = false
        }
        if endDateComp == nil || !endDateComp.isValidDate{
            backend.alert(view: self, message: NSLocalizedString("NO_END_DATE_ERROR", comment: ""), title: "Error")
            addEvent = false
        }
        
        if startDateComp != nil && endDateComp != nil && startDateComp.isValidDate && endDateComp.isValidDate{
            if endDateComp.date! < startDateComp.date!{
                backend.alert(view: self, message: NSLocalizedString("END_DATE_EARLIER_THAN_START_DATE", comment: ""), title: "Error")
                addEvent = false
            }
        }
        
        
        if addEvent{
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "EEE, dd MMM yyyy HH:mm:ss zzz"
            body["name"] = eventNameField.text
            body["owner"] = eventHosterField.text
            body["description"] = eventDescriptionView.text
            body["category"] = backend.convertCategoryNameToCategory(name: self.selectedCategory).rawValue
            body["coverUrl"] = eventImageField.text
            body["ticketUri"] = eventWebsiteField.text
            body["country"] = country
            body["starttime"] = dateFormatter.string(from: NSCalendar.current.date(from: startDateComp)!)
            body["endtime"] = dateFormatter.string(from: NSCalendar.current.date(from: endDateComp)!)
            body["city"] = city
            body["zip"] = postal
            body["longitude"] = long
            body["latitude"] = lat
            body["street"] = street
            body["placeName"] = placeName
            backend.doPost(path: "/events/add", dict: body,token: FBSDKAccessToken.current().tokenString, callback: {
                (nsdict) in
                let eHolder:Holder<EventDto> = backend.parseHolder(json: nsdict)
                if eHolder.success {
                    backend.alert(view: self, message: NSLocalizedString("EVENT_APPROVEMENT", comment: ""), title: NSLocalizedString("EVENT_ADDED", comment: ""))
                }
                else{
                    backend.alert(view: self, message: NSLocalizedString("Something went wrong!", comment: ""), title: "Error!")
                }
                self.navigationController?.popViewController(animated: true)
            })
        }
        
    }
    
    func checkRequiredField(msg: String!, field: UITextField!) -> Bool{
        let backend = BackendService()
        if (field.text?.isEmpty)!{
            backend.alert(view: self, message: msg, title: "Error")
            field.layer.borderWidth = 1.0
            field.layer.borderColor = UIColor.red.cgColor
            return false
        }
        return true
    }
    
    func checkRequiredView(msg: String!, field: UITextView!) -> Bool{
        let backend = BackendService()
        if (field.text?.isEmpty)!{
            backend.alert(view: self, message: msg, title: "Error")
            field.layer.borderWidth = 1.0
            field.layer.borderColor = UIColor.red.cgColor
            return false
        }
        return true
    }
    
    //Category Picker
    //UI PICKER VIEW
   func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        self.selectedCategory = Array(categories.keys)[row]
        /*if(categories[Array(categories.keys)[row]] == true){
            categories[Array(categories.keys)[row]] = false
        }
        else{
            categories[Array(categories.keys)[row]] = true
        }*/
        /*DispatchQueue.main.async {
            pickerView.reloadAllComponents()
        }*/
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return categories.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        /*if(categories[Array(categories.keys)[row]] == true){
            return Array(categories.keys)[row] + "   ✓";
        }*/
        return Array(categories.keys)[row];
    }

    
    
    //Date picker
    func createDatePickerAsSubview(sender: UITextField, tagg: Int){
        let inputView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width, height: 240))
        
        let datePickerView  : UIDatePicker = UIDatePicker(frame: CGRect(x: 0,y: 40,width: 0,height: 0))
        datePickerView.datePickerMode = UIDatePickerMode.dateAndTime
        datePickerView.timeZone = TimeZone(abbreviation: "GMT")
        datePickerView.tag = tagg;
        datePickerView.date = getCurrentDefaultDate()
        inputView.addSubview(datePickerView) // add date picker to UIView
        
        let doneButton = UIButton(frame: CGRect(x: (self.view.frame.size.width) - (100),y: 0,width: 100,height: 50))
        doneButton.setTitle(NSLocalizedString("Done", comment: ""), for: UIControlState.normal)
        doneButton.setTitle(NSLocalizedString("Done", comment: ""), for: UIControlState.highlighted)
        doneButton.setTitleColor(UIColor.black, for: UIControlState.normal)
        doneButton.setTitleColor(UIColor.gray, for: UIControlState.highlighted)
        doneButton.tag = tagg;
        
        inputView.addSubview(doneButton) // add Button to UIView
        doneButton.addTarget(self, action: #selector(doneClicked), for: UIControlEvents.touchUpInside)
        sender.inputView = inputView
        datePickerView.addTarget(self, action: #selector(handleDatePicker), for: UIControlEvents.valueChanged)
        
        handleDatePicker(sender: datePickerView)
    }
    
    @objc func handleDatePicker(sender: UIDatePicker) {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        dateFormatter.timeZone = TimeZone(abbreviation: "GMT")
        let generator = UISelectionFeedbackGenerator()
        generator.prepare()
        var componenets = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: sender.date)
        componenets.calendar = Calendar.current
        if(sender.tag == 0){
            eventStartField.text = dateFormatter.string(from: sender.date)
            generator.selectionChanged()
            startDateComp = componenets
        }
        else if(sender.tag == 1){
            eventEndField.text = dateFormatter.string(from: sender.date)
            generator.selectionChanged()
            endDateComp = componenets
        }
    }
    
    func getCurrentDefaultDate() -> Date{
        let gregorian = Calendar(identifier: .gregorian)
        let now = Date()
        var components = gregorian.dateComponents([.year, .month, .day, .hour, .minute, .second], from: now)
        components.hour = 12
        components.minute = 00
        components.second = 0
        return gregorian.date(from: components)!
    }
    
    @objc func doneClicked(sender:UIButton)
    {
        if(sender.tag == 0){
            eventStartField.resignFirstResponder()
        }
        else if(sender.tag == 1){
            eventEndField.resignFirstResponder()
        }
        else if sender.tag == 2{
            eventCategoriesField.resignFirstResponder()
            eventCategoriesField.text = selectedCategory
            /*for (key,value) in categories{
                if value{
                    eventCategoriesField.text = eventCategoriesField.text! + " " + key
                }
            }*/
        }
    }
    
    //Google places
    
    func resultsController(_ resultsController: GMSAutocompleteResultsViewController, didAutocompleteWith place: GMSPlace) {
        searchController?.isActive = false
        searchController?.searchBar.text = place.formattedAddress;
        self.lat = place.coordinate.latitude
        self.long = place.coordinate.longitude
        self.placeName = place.name
        var sadd = "";
        var snr = "";
        
        for comp in place.addressComponents! {
            print(comp.type + " " + comp.name)
            if comp.type == "country"{
                self.country = NSLocale.locales1(countryName1: comp.name)
            }
            if comp.type == "locality"{
                self.city = comp.name
            }
            if comp.type == "postal_code"{
                self.postal = comp.name
            }
            if comp.type == "street_address"{
                sadd = comp.name
            }
            if comp.type == "street_number"{
                snr = comp.name
            }
        }
        self.street = sadd + " " + snr
    }
    
    func resultsController(_ resultsController: GMSAutocompleteResultsViewController,
                           didFailAutocompleteWithError error: Error){
        let backend = BackendService()
        backend.alert(view: self, message: error.localizedDescription, title: NSLocalizedString("Something went wrong!", comment: ""))
    }
    
    // Turn the network activity indicator on and off again.
    func didRequestAutocompletePredictions(forResultsController resultsController: GMSAutocompleteResultsViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
    }
    
    func didUpdateAutocompletePredictions(forResultsController resultsController: GMSAutocompleteResultsViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = false
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let detailView = segue.destination as? EventDetailViewController{
            let backend = BackendService()
            var event = EventDto()
            event.id = -1;
            event.ticketUri = self.eventWebsiteField.text;
            event.name = self.eventNameField.text;
            event.ownerName = self.eventHosterField.text;
            event.description = self.eventDescriptionView.text;
            event.coverUrl = self.eventImageField.text;
            event.placeName = self.placeName;
            event.city = self.city;
            event.latitude = self.lat;
            event.longitude = self.long;
            event.street = self.street;
            event.zip = self.postal;
            event.category = backend.convertCategoryNameToCategory(name: self.selectedCategory)
            if(startDateComp != nil && startDateComp.isValidDate){
                event.starttime = NSCalendar.current.date(from: startDateComp)
            }
            if(endDateComp != nil && endDateComp.isValidDate){
                event.endtime = NSCalendar.current.date(from: endDateComp)
            }
            event.country = NSLocale.locales1(countryName1: self.country)
            event.favored = false
            
            detailView.eventDto = event
            detailView.cover = nil
        }
    }
}
