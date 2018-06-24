//
//  OfferCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 11.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class OfferCell: UITableViewCell {

    @IBOutlet weak var offerName: UILabel!
    @IBOutlet weak var openNow: UILabel!
    @IBOutlet weak var vicinity: UILabel!
    var offerDto: OfferDto!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
