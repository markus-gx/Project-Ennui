//
//  FavEventCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 24.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class FavEventCell: UITableViewCell {
    @IBOutlet weak var label: UILabel!
    var eventDto:EventDto! = EventDto()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
}
