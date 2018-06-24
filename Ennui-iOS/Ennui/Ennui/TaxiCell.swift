//
//  TaxiCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 30.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class TaxiCell: UITableViewCell {

    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var callBtn: UIButton!
    @IBOutlet weak var taxiNameLabel: UILabel!
    var number: String!;
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    @IBAction func callButtonAction(_ sender: Any) {
        number = number.replacingOccurrences(of: " ", with: "")
        number = number.replacingOccurrences(of: "-", with: "")
        number = number.replacingOccurrences(of: "+", with: "")
        if let url = URL(string: "tel://" + "+" + String(self.number)) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
}
