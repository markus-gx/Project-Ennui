//
//  OfferDto.swift
//  Ennui
//
//  Created by Markus Geilehner on 04.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct OfferDto: Codable{
    var locationDetails: LocationDetails!
    var international_phone_number: String!
    var id: String!
    var name: String!
    var open_now: Bool!
    var periods: [TimePeriod]!
    var weekday_text: [String]!
    var rating: Double!
    var reviews: [Review]!
    var website: String!
    var reference: String!
}
