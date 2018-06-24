//
//  ReviewCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 13.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class ReviewCell: UITableViewCell {

    
    @IBOutlet weak var readMoreBtn: UIButton!
    @IBOutlet weak var reviewTextLabel: UILabel!
    @IBOutlet weak var ratingView: CosmosView!
    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var profileImage: UIImageView!
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
}
