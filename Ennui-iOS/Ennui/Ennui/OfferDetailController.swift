//
//  OfferDetailController.swift
//  Ennui
//
//  Created by Markus Geilehner on 11.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class OfferDetailController: UITableViewController {
    
    var offerDto: OfferDto!

    @IBOutlet weak var openingTimesLabel: UILabel!
    @IBOutlet weak var phoneNumber: UILabel!
    @IBOutlet weak var websiteBtn: UIButton!
    @IBOutlet weak var callBtn: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()

        if offerDto != nil && offerDto.reference != nil{
            self.navigationItem.title = offerDto.name
            let backend = BackendService()
            backend.doGet(path: "/offers/" + offerDto.reference, token: "", callback: { (jsonstring) in
                let oH: Holder<OfferDto> = backend.parseHolder(json: jsonstring)
                if oH.success != nil && oH.success{
                    self.offerDto=oH.result[0]
                    self.fillData()
                }
                else{
                    self.navigationController?.popViewController(animated: true)
                }
            })
        }
        else{
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    func fillData(){
        DispatchQueue.main.async {
            for line in self.offerDto.weekday_text{
                self.openingTimesLabel.text = self.openingTimesLabel.text! + line + "\n"
            }
            self.phoneNumber.text = self.offerDto.international_phone_number
            self.websiteBtn.setTitle(self.offerDto.website, for: .normal)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func callClick(_ sender: UIButton) {
        offerDto.international_phone_number = offerDto.international_phone_number.replacingOccurrences(of: " ", with: "")
        offerDto.international_phone_number = offerDto.international_phone_number.replacingOccurrences(of: "-", with: "")
        offerDto.international_phone_number = offerDto.international_phone_number.replacingOccurrences(of: "+", with: "")
        if let url = URL(string: "tel://" + "+" + String(self.offerDto.international_phone_number)) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
    @IBAction func websiteClick(_ sender: UIButton) {
        if(offerDto.website != nil){
            let url = URL(string: (self.offerDto.website)!)
            if url != nil{
                UIApplication.shared.open(url!, options: [:], completionHandler: nil)
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? LocationEventDetailController{
            dest.name = offerDto.name
            dest.locationDetails = offerDto.locationDetails
            dest.eventDto = nil
        }
        else if let dest = segue.destination as? OfferReviewController{
            dest.reviews = self.offerDto.reviews
        }
    }
    
}
