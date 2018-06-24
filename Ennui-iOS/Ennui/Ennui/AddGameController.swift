//
//  AddGameController.swift
//  Ennui
//
//  Created by Markus Geilehner on 16.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class AddGameController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {

    var categories: [String:Bool] = [NSLocalizedString("DRINKING_GAMES", comment: ""):false,NSLocalizedString("CARD_GAMES", comment: ""):false,NSLocalizedString("DICE_GAMES", comment: ""):false,NSLocalizedString("BOARD_GAMES", comment: ""):false,NSLocalizedString("OUTDOOR_GAMES", comment: ""):false,NSLocalizedString("BALL_GAMES", comment: ""):false]
    var selectedCategory: String = NSLocalizedString("All", comment: "")
    @IBOutlet weak var categoriesField: UITextField!
    @IBOutlet weak var maxPlayerField: UITextField!
    @IBOutlet weak var minPlayerField: UITextField!
    @IBOutlet weak var gameInstructionView: UITextView!
    @IBOutlet weak var gameNameField: UITextField!
    @IBOutlet weak var gameDescriptionView: UITextView!
    @IBOutlet weak var coverUrlField: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: 50))
        doneToolbar.barStyle = UIBarStyle.blackTranslucent
        
        let flexSpace = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(doneButtonAction))
        
        var items: [UIBarButtonItem] = []
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        self.maxPlayerField.inputAccessoryView = doneToolbar
        self.minPlayerField.inputAccessoryView = doneToolbar
        self.gameInstructionView.inputAccessoryView = doneToolbar
        self.gameDescriptionView.inputAccessoryView = doneToolbar
        self.gameNameField.inputAccessoryView = doneToolbar
        self.coverUrlField.inputAccessoryView = doneToolbar
        
        gameDescriptionView.isHidden = false
        gameInstructionView.isHidden = true
        // Do any additional setup after loading the view.
    }
    
    @objc func doneButtonAction(_sender: Any)
    {
        self.maxPlayerField.resignFirstResponder()
        self.minPlayerField.resignFirstResponder()
        self.gameInstructionView.resignFirstResponder()
        self.gameDescriptionView.resignFirstResponder()
        self.gameNameField.resignFirstResponder()
        self.coverUrlField.resignFirstResponder()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
    
    //Category Picker
    //UI PICKER VIEW
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if(categories[Array(categories.keys)[row]] == true){
            categories[Array(categories.keys)[row]] = false
        }
        else{
            categories[Array(categories.keys)[row]] = true
        }
        DispatchQueue.main.async {
            pickerView.reloadAllComponents()
        }
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return categories.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if(categories[Array(categories.keys)[row]] == true){
            return Array(categories.keys)[row] + "   ✓";
        }
        return Array(categories.keys)[row];
    }
    
    @objc func doneClicked(sender: UIButton!){
        self.categoriesField.resignFirstResponder()
    }
    
    @IBAction func addGame(_ sender: UIButton) {
    }
    @IBAction func swtichSegment(_ sender: UISegmentedControl) {
        if gameDescriptionView.isHidden {
            gameDescriptionView.isHidden = false
            gameInstructionView.isHidden = true
        }
        else{
            gameDescriptionView.isHidden = true
            gameInstructionView.isHidden = false
        }
    }
    
    
}
