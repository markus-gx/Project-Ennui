//
//  LocationEventDetailController.swift
//  Ennui
//
//  Created by Markus Geilehner on 29.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class LocationEventDetailController: UIViewController, MKMapViewDelegate {

    @IBOutlet weak var locationMap: MKMapView!
    @IBOutlet weak var cityLabel: UILabel!
    @IBOutlet weak var countryLabel: UILabel!
    @IBOutlet weak var streetLabel: UILabel!
    @IBOutlet weak var zipLabel: UILabel!
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var openMapsBtn: UIButton!
    var eventDto: EventDto!
    var locationDetails: LocationDetails!
    var name: String!
    var installedNavigationApps : [String] = ["Apple Maps"] // Apple Maps is always installed
    
    override func viewWillAppear(_ animated: Bool) {
        var lat: Double!
        var long: Double!
        var placeName: String!
        if eventDto != nil {
            lat = eventDto.latitude
            long = eventDto.longitude
            placeName = eventDto.placeName
        }
        if locationDetails != nil{
            lat = locationDetails.latitude
            long = locationDetails.longitude
            placeName = name
        }
        self.nameLabel.text = placeName
        
        self.locationMap.centerCoordinate.latitude = lat
        self.locationMap.centerCoordinate.longitude = long
        let annotation = MKPointAnnotation()
        annotation.coordinate.latitude = lat
        annotation.coordinate.longitude = long
        annotation.title = placeName
        self.locationMap.setRegion(MKCoordinateRegionMakeWithDistance(annotation.coordinate, 100, 100), animated: true)
        self.locationMap.delegate = self;
        self.locationMap.showsUserLocation = true;
        
        let geoCoder = CLGeocoder()
        let location = CLLocation(latitude: annotation.coordinate.latitude, longitude: annotation.coordinate.longitude)
        geoCoder.reverseGeocodeLocation(location, completionHandler: { (placemarks, error) -> Void in
            // Place details
            var placeMark: CLPlacemark!
            placeMark = placemarks?[0]
            self.countryLabel.text = "Country: " + (placeMark.country ?? "");
            self.streetLabel.text = "Street: " + (placeMark.thoroughfare ?? "") + " " + (placeMark.subThoroughfare ?? "")
            self.cityLabel.text = "City: " + (placeMark.locality ?? "")
            self.zipLabel.text = "ZIP: " + (placeMark.postalCode ?? "")
            
        })
        self.locationMap.addAnnotation(annotation);
        //self.locationMap.selectAnnotation(annotation, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.checkMaps()
        self.openMapsBtn.layer.cornerRadius = 10
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func mapView(_ mapView: MKMapView, didSelect view: MKAnnotationView) {
        openMapsDialog(long: (view.annotation?.coordinate.longitude)!, lat: (view.annotation?.coordinate.latitude)!)
    }

    func getUrlForAppWithCoordinates(app:String, lat: String, long: String) -> NSURL{
        var url: NSURL;
        switch app {
        case "Apple Maps":
            url = NSURL(string: "http://maps.apple.com/?q=" + String(lat) + "," + String(long))!
        case "Google Maps":
            url = NSURL(string: "comgooglemaps://?center=" + String(lat) + "," + String(long) + "&zoom=14&views=traffic")!
        default:
            url = NSURL(string: "http://maps.apple.com/?q=" + String(lat) + "," + String(long))!;
        }
        return url;
    }
    
    func checkMaps(){
        if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
            self.installedNavigationApps.append("Google Maps")
        }
    }
    

    @IBAction func openMapsAction(_ sender: UIButton) {
        if eventDto != nil{
          openMapsDialog(long: eventDto.longitude, lat: eventDto.latitude)
        }
        else if locationDetails != nil{
            openMapsDialog(long: locationDetails.longitude, lat: locationDetails.latitude)
        }
    }
    
    func openMapsDialog(long: Double!, lat: Double){
        let alert = UIAlertController(title: "Selection", message: "Select Navigation App", preferredStyle: .actionSheet)
        for app in self.installedNavigationApps {
            let button = UIAlertAction(title: app, style: .default, handler: {
                action in
                let latstr: String = String(lat)
                let longstr: String = String(long)
                let url = self.getUrlForAppWithCoordinates(app: app, lat: latstr, long: longstr)
                if UIApplication.shared.canOpenURL(url as URL) == true {
                    UIApplication.shared.open(url as URL, options: [:], completionHandler: nil)
                }
            })
            alert.addAction(button)
        }
        alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: ""), style: UIAlertActionStyle.cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
