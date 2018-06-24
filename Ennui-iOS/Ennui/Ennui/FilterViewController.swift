//
//  FilterViewController.swift
//  Ennui
//
//  Created by Markus Geilehner on 23.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import GooglePlaces

class FilterViewController: UITableViewController, GMSAutocompleteResultsViewControllerDelegate{
    var eventFilter: EventFilter!;
    weak var delegate: EventTabViewController!;
    var resultsViewController: GMSAutocompleteResultsViewController?
    var searchController: UISearchController?
    var resultView: UITextView?
    
    @IBOutlet weak var locationCell: UITableViewCell!
    @IBOutlet weak var radiusCellSlider: UISlider!
    @IBOutlet weak var radiusCellKMs: UILabel!
    @IBOutlet weak var startTimeTextBox: UITextField!
    @IBOutlet weak var endTimeTextBox: UITextField!
    @IBOutlet weak var categoryCell: UITableViewCell!
    @IBOutlet weak var categoryCollectionView: CategoryCollectionView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Event-Filter";
        
        resultsViewController = GMSAutocompleteResultsViewController()
        resultsViewController?.delegate = self
        resultsViewController?.view.backgroundColor = UIColor(white: 1, alpha: 0.5);
        
        searchController = UISearchController(searchResultsController: resultsViewController)
        searchController?.searchResultsUpdater = resultsViewController
        
        let subView = UIView(frame: CGRect(x: 0, y: self.locationCell.frame.height - 67.5, width: self.locationCell.frame.width, height: 45.0))
        
        subView.addSubview((searchController?.searchBar)!)
        locationCell.addSubview(subView)
        searchController?.searchBar.sizeToFit()
        searchController?.searchBar.barTintColor = UIColor.white;
        searchController?.searchBar.placeholder = NSLocalizedString("Enter a specific place", comment: "")
        
        navigationController?.navigationBar.isTranslucent = false
        searchController?.hidesNavigationBarDuringPresentation = false
        
        // When UISearchController presents the results view, present it in
        // this view controller, not one further up the chain.
        //definesPresentationContext = true
        
        self.radiusCellKMs.text = "(" + String(Int(radiusCellSlider.value)) + " km)";
        
        let rightButtonItem = UIBarButtonItem.init(
            title: NSLocalizedString("Clear Filter", comment: ""),
            style: .done,
            target: self,
            action: #selector(rightButtonAction)
        )
        
        self.navigationItem.rightBarButtonItem = rightButtonItem;
        
        setCategoriesSelected(cats: delegate.eventFilter.categories)
        radiusCellSlider.value = Float(eventFilter.radius)
        radiusCellKMs.text = "( " + String(eventFilter.radius) + " km)";
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss";
        if eventFilter.startTime != nil{
            startTimeTextBox.text = dateFormatter.string(from: Calendar.current.date(from: eventFilter.startTime)!)
        }
        if eventFilter.endTime != nil{
           endTimeTextBox.text = dateFormatter.string(from: Calendar.current.date(from: eventFilter.endTime)!)
        }
        searchController?.searchBar.text = eventFilter.formattedAdress;
    }
    
    @objc func rightButtonAction(sender: UIBarButtonItem){
        self.categoryCollectionView.resetCategoriesSelected()
        self.delegate.eventFilter.reset()
        self.delegate.filterChanged = true;
        self.navigationController?.popViewController(animated: true);
    }
    
    func setCategoriesSelected(cats: [EventCategories]){
        let backend = BackendService()
        for (idx,element) in categoryCollectionView.categories.enumerated() {
            for ele in delegate.eventFilter.categories {
                if backend.convertCategoryToCategoryNames(cat: ele) == element{
                    categoryCollectionView.categoriesSelected[idx] = true;
                }
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        let selectedCats = categoryCollectionView.getSelectedCategoryNames()
        if delegate.eventFilter.categories.count != selectedCats.count{
            delegate.filterChanged = true;
        }
        else{
           for (idx,ele) in selectedCats.enumerated(){
                if ele != delegate.eventFilter.categories[idx].rawValue{
                    delegate.filterChanged = true;
                }
            }
        }
        let backend = BackendService()
        self.delegate.eventFilter.categories = backend.convertCategoryNamesToCategories(names: categoryCollectionView.getSelectedCategoryNames())
    }
    
    //Handle User Selection
    
    func resultsController(_ resultsController: GMSAutocompleteResultsViewController, didAutocompleteWith place: GMSPlace) {
        searchController?.isActive = false
        searchController?.searchBar.text = place.formattedAddress;
        self.delegate.eventFilter.latitude = place.coordinate.latitude;
        self.delegate.eventFilter.longitude = place.coordinate.longitude;
        self.delegate.eventFilter.formattedAdress = place.formattedAddress;
        for comp in place.addressComponents! {
            if comp.type == "country"{
                self.delegate.eventFilter.country = NSLocale.locales1(countryName1: comp.name)
            }
        }
        self.delegate.filterChanged = true;
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

   
    @IBAction func onRadiusMove(_ sender: Any) {
        if FBSDKAccessToken.current() != nil{
           self.radiusCellKMs.text = "(" + String(Int(self.radiusCellSlider.value)) + " km)";
            self.delegate.eventFilter.radius = Int32(self.radiusCellSlider.value);
            self.delegate.filterChanged = true;
        }
        else{
            let backend = BackendService()
            backend.alert(view: self, message: NSLocalizedString("LOGIN_FOR_FEATURE", comment: ""), title: "Error")
            radiusCellSlider.value = 25;
        }
        
    }
    
    @IBAction func startTimeEditBegan(_ sender: UITextField) {
        if FBSDKAccessToken.current() != nil{
            createDatePickerAsSubview(sender: sender, tagg: 0)
        }
        else{
            let backend = BackendService()
            backend.alert(view: self, message: NSLocalizedString("LOGIN_FOR_FEATURE", comment: ""), title: "Error")
        }
        
    }
    
    @IBAction func endTimeEditBegan(_ sender: UITextField) {
        if FBSDKAccessToken.current() != nil{
            createDatePickerAsSubview(sender: sender, tagg: 1)
        }
        else{
            let backend = BackendService()
            backend.alert(view: self, message: NSLocalizedString("LOGIN_FOR_FEATURE", comment: ""), title: "Error")
        }
    }
    
    func createDatePickerAsSubview(sender: UITextField, tagg: Int){
        let inputView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width, height: 240))
        
        let datePickerView  : UIDatePicker = UIDatePicker(frame: CGRect(x: 0,y: 40,width: 0,height: 0))
        datePickerView.datePickerMode = UIDatePickerMode.dateAndTime
        datePickerView.tag = tagg;
        inputView.addSubview(datePickerView) // add date picker to UIView
        
        let doneButton = UIButton(frame: CGRect(x: (self.view.frame.size.width) - (100),y: 0,width: 100,height: 50))
        doneButton.setTitle("Done", for: UIControlState.normal)
        doneButton.setTitle("Done", for: UIControlState.highlighted)
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
        let generator = UISelectionFeedbackGenerator()
        generator.prepare()
        let componenets = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: sender.date)
        if(sender.tag == 0){
            startTimeTextBox.text = dateFormatter.string(from: sender.date)
            self.delegate.eventFilter.startTime = componenets;
            generator.selectionChanged()
        }
        else if(sender.tag == 1){
            endTimeTextBox.text = dateFormatter.string(from: sender.date)
            self.delegate.eventFilter.endTime = componenets;
            generator.selectionChanged()
        }
        self.delegate.filterChanged = true;
    }
    
    @objc func doneClicked(sender:UIButton)
    {
        if(sender.tag == 0){
            startTimeTextBox.resignFirstResponder()
        }
        else if(sender.tag == 1){
            endTimeTextBox.resignFirstResponder()
        }
    }
    
    
    //TableView
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0{
            return 75
        }
        else if indexPath.section == 2{
            categoryCollectionView.frame = CGRect(x: 0, y: 0, width: categoryCollectionView.frame.width, height: categoryCollectionView.collectionViewLayout.collectionViewContentSize.height)
            return categoryCollectionView.collectionViewLayout.collectionViewContentSize.height
        }
        return UITableViewAutomaticDimension
    }
}

extension NSLocale {
    class func locales1(countryName1 : String) -> String {
        let locales : String = ""
        for localeCode in NSLocale.isoCountryCodes {
            let countryName = (Locale.current as NSLocale).displayName(forKey: .countryCode, value: localeCode)
            if countryName1.lowercased() == countryName?.lowercased() {
                return localeCode
            }
        }
        return locales
    }
}
