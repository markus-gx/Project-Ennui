//
//  UserLoginDto.swift
//  Ennui
//
//  Created by Markus Geilehner on 09.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct UserLoginDto: Codable{
    var id: Int!;
    var fbId: String!;
    var firstname: String!;
    var lastname: String!;
    var name: String!;
    var ageRange: String!;
    var email: String!;
    var gender: String!;
    var admin: Bool!;
    var profileImage: String!;
    var favouriteEvents: [EventDto]!;
    var favouriteGames: [GameDto]!;
    init() {
        
    }
}
