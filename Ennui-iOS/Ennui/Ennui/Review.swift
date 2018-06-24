//
//  Review.swift
//  Ennui
//
//  Created by Markus Geilehner on 11.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct Review: Codable{
    var authorName: String!
    var language: String!
    var profile_photo_url: String!
    var rating: Int!
    var text: String!
    var time: Int!
}
