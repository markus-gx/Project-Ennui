//
//  GameDto.swift
//  Ennui
//
//  Created by Markus Geilehner on 02.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation


struct GameDto: Codable{
    var id: Int!;
    var name: String!;
    var description: String!;
    var instruction: String!;
    var cover: String!;
    var minPlayer: Int!
    var maxPlayer: Int!
    var activated: Bool!
    var ownerId: Int!
    var categories: [String]!
    var rating: Int!
    var ratedByUser: Bool!
    var favorized: Bool!
    
    func getCategoriesAsText() -> String{
        var cats = ""
        let backend = BackendService()
        for cat in categories{
            if cat == categories.last{
                cats = cats + backend.convertGameCategoryToReadableCategory(cat: cat)
            }
            else{
                cats = cats + backend.convertGameCategoryToReadableCategory(cat: cat) + ","
            }
        }
        return cats;
    }
}
