//
//  GameCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 02.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class GameCell: UITableViewCell {

    
    @IBOutlet weak var categoriesLabel: UILabel!
    @IBOutlet weak var coverImage: UIImageView!
    @IBOutlet weak var gameName: UILabel!
    @IBOutlet weak var playerLabel: UILabel!
    @IBOutlet weak var favorizeButton: UIButton!
    @IBOutlet weak var ratingStars: CosmosView!
    
    var gameDto: GameDto!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
}
