//
//  EventCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 22.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class EventCell: UITableViewCell {

    @IBOutlet weak var iconImage: UIImageView!
    @IBOutlet weak var locationLabel: UILabel!
    @IBOutlet weak var coverImage: UIImageView!
    @IBOutlet weak var eventNameLabel: UILabel!
    @IBOutlet weak var timespanLabel: UILabel!
    @IBOutlet weak var favorizeButton: FavorizeButton!
    var eventDto: EventDto? = nil;
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
