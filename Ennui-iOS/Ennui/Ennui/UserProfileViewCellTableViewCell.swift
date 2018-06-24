//
//  UserProfileViewCellTableViewCell.swift
//  Ennui
//
//  Created by Markus Geilehner on 09.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class UserProfileViewCellTableViewCell: UITableViewCell {

    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var userName: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.imageView?.layoutIfNeeded();
        profileImage.layer.cornerRadius = profileImage.frame.width / 2;
        profileImage.clipsToBounds = true;
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
}
