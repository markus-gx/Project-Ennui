//
//  TaxiTableController.swift
//  Ennui
//
//  Created by Markus Geilehner on 30.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import CoreLocation

class TaxiTableController: UITableViewController, CLLocationManagerDelegate {

    var taxiRequestSent = false;
    let locationManager = CLLocationManager()
    var taxiDtos: [TaxiDto] = [];
    
    @IBOutlet weak var loadingBar: UIActivityIndicatorView!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.locationManager.delegate = self;
        
        if CLLocationManager.authorizationStatus() != .authorizedWhenInUse || CLLocationManager.authorizationStatus() != .authorizedAlways{
            self.locationManager.requestWhenInUseAuthorization();
        }
        self.locationManager.requestLocation();
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.first {
            let lat: String = "\(location.coordinate.latitude)"
            let long: String = "\(location.coordinate.longitude)"
            getTaxis(lat: lat, long: long)
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to find user's location: \(error.localizedDescription)")
        let backend = BackendService()
        backend.alert(view: self, message: NSLocalizedString("Failed to find your location!", comment: ""), title: "Error");
    }
    
    func getTaxis(lat: String, long: String){
        if taxiRequestSent == false{
            taxiRequestSent = true;
            let backend = BackendService()
            backend.doPostArray(path: "/general/taxis", dict: ["longitude":long,"latitude":lat], token: "", callback: { (nsarray) in
                self.taxiDtos = backend.parseTaxiDtoJSON(arr: nsarray);
                DispatchQueue.main.async {
                    self.tableView.reloadData();
                    self.loadingBar.stopAnimating();
                }
            })
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Taxis (" + String(self.taxiDtos.count) + ")"
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return self.taxiDtos.count;
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "taxiCell", for: indexPath) as! TaxiCell

        cell.taxiNameLabel.text = taxiDtos[indexPath.row].name;
        cell.ratingLabel.text = "(" + String(taxiDtos[indexPath.row].rating) + "/5)";
        cell.number = taxiDtos[indexPath.row].international_phone_number;
        
        return cell
    }
    

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
