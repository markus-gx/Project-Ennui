//
//  FavGameCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 02.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class FavGameCell: UITableViewCell {

    @IBOutlet weak var gameLabel: UILabel!
    var gameDto: GameDto! = GameDto()
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
