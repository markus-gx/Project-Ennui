//
//  OffersListController.swift
//  Ennui
//
//  Created by Markus Geilehner on 04.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import CoreLocation

class OffersListController: UITableViewController, CLLocationManagerDelegate {
    
    var activity: String!
    var offerItems: [OfferDto] = []
    let locationManger = CLLocationManager()
    var userLocation: CLLocation!
    var activityIN: UIActivityIndicatorView!

    override func viewDidLoad() {
        super.viewDidLoad()

        self.navigationItem.title = activity
        self.tableView.tableFooterView = UIView()
        activityIN = UIActivityIndicatorView(frame: CGRect(x: UIScreen.main.bounds.width/2 - 25, y: UIScreen.main.bounds.height/2 - 25, width: 50, height: 50)) as UIActivityIndicatorView
        activityIN.center = self.view.center
        activityIN.hidesWhenStopped = true
        activityIN.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
        activityIN.startAnimating()
        self.view.addSubview(activityIN)
       
        self.locationManger.delegate = self
        if CLLocationManager.authorizationStatus() != .authorizedWhenInUse || CLLocationManager.authorizationStatus() != .authorizedAlways{
            self.locationManger.requestWhenInUseAuthorization()
        }
        self.locationManger.startUpdatingLocation()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //LOCATION MANAGER
    
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.first {
            if userLocation == nil{
                userLocation = location;
                manager.stopUpdatingLocation()
                getOffers()
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to find user's location: \(error.localizedDescription)")
        let backend = BackendService()
        backend.alert(view: self, message: NSLocalizedString("Failed to find your location!", comment: ""), title: "Error");
    }
    
    func getOffers(){
        let backend = BackendService()
        backend.doGet(path: "/offers?category=" + backend.convertNameToOfferCategory(cat: activity) + "&latitude=" + String(self.userLocation.coordinate.latitude) + "&longitude=" + String(self.userLocation.coordinate.longitude), token: "") { (jsonstring) in
            let offerHolder: Holder<OfferDto> = backend.parseHolder(json: jsonstring)
            if offerHolder.success != nil && offerHolder.success{
                self.offerItems = offerHolder.result
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                    self.activityIN.stopAnimating()
                }
            }
            
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Places (" + String(offerItems.count) + ")"
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return offerItems.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "offerCell", for: indexPath) as! OfferCell
        let offer = self.offerItems[indexPath.row]
        cell.offerName.text = offer.name
        if offer.open_now{
            cell.openNow.textColor = UIColor.green
            cell.openNow.text = NSLocalizedString("OFFER_OPEN", comment: "")
        }
        else{
            cell.openNow.textColor = UIColor.red
            cell.openNow.text = NSLocalizedString("OFFER_CLOSED", comment: "")
        }
        cell.vicinity.text = offer.locationDetails.vicinity
        cell.offerDto = offer
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? OfferDetailController{
            if let cell = sender as? OfferCell{
                dest.offerDto = cell.offerDto
            }
        }
    }

}
