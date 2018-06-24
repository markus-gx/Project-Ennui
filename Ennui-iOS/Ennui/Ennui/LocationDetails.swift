//
//  LocationDetails.swift
//  Ennui
//
//  Created by Markus Geilehner on 11.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct LocationDetails: Codable{
    var street: String!
    var city: String!
    var country: String!
    var latitude: Double!
    var longitude: Double
    var vicinity: String!
}
