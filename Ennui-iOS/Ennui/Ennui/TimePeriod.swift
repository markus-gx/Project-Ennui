//
//  TimePeriod.swift
//  Ennui
//
//  Created by Markus Geilehner on 11.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct TimePeriod: Codable{
    var open: DayPeriod!
    var close: DayPeriod!
}

struct DayPeriod: Codable{
    var day: Int!
    var time: String!
}
