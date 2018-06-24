//
//  Holder.swift
//  Ennui
//
//  Created by Markus G. on 14/11/2017.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct Holder<T: Codable>: Codable{
    var success: Bool!
    var message: String!
    var result: [T]!
    var recommendedResults: [T]!
    
    init(success: Bool!) {
        self.success = success
        self.message = ""
    }
}
